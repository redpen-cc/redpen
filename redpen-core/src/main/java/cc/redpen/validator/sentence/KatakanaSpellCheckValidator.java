/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.redpen.validator.sentence;

import cc.redpen.RedPenException;
import cc.redpen.model.Sentence;
import cc.redpen.util.LevenshteinDistance;
import cc.redpen.util.StringUtils;
import cc.redpen.validator.DictionaryValidator;

import java.util.*;

import static java.util.Collections.singletonList;

/**
 * Validate the correctness of Katakana word spelling.
 * Japanese Katakana words have orthographical variations.
 * For example, "index" is written in Katakana by
 * "インデクス" or "インデックス".
 * Unfortunately, we can not detect whether is correct or not
 * because there is no specific rule.
 * So our strategy is to validate whether a Katakana word is
 * very similar to the other Katakana word in previous sentences
 * or not.
 * The similarity is defined as Levenshtein distance.
 * We heuristically detecit a pair as similar if the distance is
 * smaller than the 30% of the length of the target string.
 * Moreover, if the length of Katakana word is small,
 * mostly the pairs are detected as similar.
 * To avoid the noisy detection, we set a threshold of the
 * length of Katakana word. And if the length of a Katakana
 * word is smaller than the threshold, we do not detect
 * the similarity.
 */
 public final class KatakanaSpellCheckValidator extends DictionaryValidator {
    /**
     * Default dictionary for Katakana spell checking.
     */
    private static final String DEFAULT_RESOURCE_PATH = "default-resources/katakana";

    /**
     * Katakana word dic with line number.
     */
    private HashMap<String, Integer> dic = new HashMap<>();
    /**
     * Exception word list.
     */
    private Set<String> exceptions = new HashSet<>();

    private Map<String, Integer> katakanaWordFrequencies = new HashMap<>();

    public KatakanaSpellCheckValidator() {
        super("min_ratio", 0.3f, // The default threshold of similarity ratio between the length and the distance. The similarities are computed by edit distance.
              "min_freq", 5, // The default threshold of word frequencies of Katakana Words.
              "max_ignore_len", 3, // The default threshold value for the length of Katakana word to ignore
              "disable-default", false);
    }

    @Override
    public List<String> getSupportedLanguages() {
        return singletonList(Locale.JAPANESE.getLanguage());
    }

    @Override
    public void preValidate(Sentence sentence) {
        // collect katakana words
        StringBuilder katakana = new StringBuilder();
        for (int i = 0; i < sentence.getContent().length(); i++) {
            char c = sentence.getContent().charAt(i);
            if (StringUtils.isKatakana(c)) {
                katakana.append(c);
            } else {
                String katakanaWord = katakana.toString();
                addKatakana(katakanaWord);
                katakana.delete(0, katakana.length());
            }
        }
        if (katakana.length() > 0) {
            addKatakana(katakana.toString());
        }
    }

    private void addKatakana(String katakanaWord) {
        if (katakanaWordFrequencies.get(katakanaWord) == null) {
            katakanaWordFrequencies.put(katakanaWord, 0);
        }
        katakanaWordFrequencies.put(katakanaWord,
                katakanaWordFrequencies.get(katakanaWord)+1);
    }

    @Override
    public void validate(Sentence sentence) {
        StringBuilder katakana = new StringBuilder();
        for (int i = 0; i < sentence.getContent().length(); i++) {
            char c = sentence.getContent().charAt(i);
            if (StringUtils.isKatakana(c)) {
                katakana.append(c);
            } else {
                this.checkKatakanaSpell(sentence, katakana.toString());
                katakana.delete(0, katakana.length());
            }
        }
        checkKatakanaSpell(sentence, katakana.toString());
    }

    private void checkKatakanaSpell(Sentence sentence, String katakana) {
        if (katakana.length() <= getIntAttribute("max_ignore_len")) {
            return;
        }
        if (dic.containsKey(katakana) || exceptions.contains(katakana)
                || getSetAttribute("list").contains(katakana) ||
                (katakanaWordFrequencies.get(katakana) != null
                        && katakanaWordFrequencies.get(katakana) > getIntAttribute("min_freq"))) {
            return;
        }
        int minLsDistance = Math.round(katakana.length() * getFloatAttribute("min_ratio"));
        boolean found = false;
        for (String key : dic.keySet()) {
            if (LevenshteinDistance.getDistance(key, katakana) <= minLsDistance) {
                found = true;
                addLocalizedError(sentence, katakana, key, dic.get(key).toString());
            }
        }
        if (!found) {
            dic.put(katakana, sentence.getLineNumber());
        }
    }

    @Override
    protected void init() throws RedPenException {
        super.init();
        if (!getBooleanAttribute("disable-default")) {
            String defaultDictionaryFile = DEFAULT_RESOURCE_PATH + "/katakana-spellcheck.dat";
            exceptions = WORD_LIST.loadCachedFromResource(defaultDictionaryFile, "katakana word dictionary");
        }
    }
}
