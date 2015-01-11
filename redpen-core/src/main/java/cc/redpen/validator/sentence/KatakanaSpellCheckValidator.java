/**
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
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
import cc.redpen.util.WordListExtractor;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

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
final public class KatakanaSpellCheckValidator extends Validator {
    /**
     * The default similarity ratio between the length and the distance.
     */
    private static final float SIMILARITY_RATIO = 0.3f;
    /**
     * The default threshold value for the length of Katakana word
     * to ignore.
     */
    private static final int MAX_IGNORE_KATAKANA_LENGTH = 3;
    /**
     * Default dictionary for Katakana spell checking.
     */
    private static final String DEFAULT_RESOURCE_PATH = "default-resources/katakana";
    /**
     * Logger
     */
    private static final Logger LOG =
            LoggerFactory.getLogger(KatakanaSpellCheckValidator.class);
    /**
     * Katakana word dic with line number.
     */
    private HashMap<String, Integer> dic = new HashMap<>();
    /**
     * Exception word list.
     */
    private Set<String> exceptions = new HashSet<>();

    @Override
    public void validate(List<ValidationError> errors, Sentence sentence) {
        StringBuilder katakana = new StringBuilder();
        for (int i = 0; i < sentence.getContent().length(); i++) {
            char c = sentence.getContent().charAt(i);
            if (StringUtils.isKatakana(c)) {
                katakana.append(c);
            } else {
                this.checkKatakanaSpell(sentence, katakana.toString(), errors);
                katakana.delete(0, katakana.length());
            }
        }
        checkKatakanaSpell(sentence, katakana.toString(), errors);
    }

    private void checkKatakanaSpell(Sentence sentence, String katakana
            ,List<ValidationError> validationErrors) {
        if (katakana.length() <= MAX_IGNORE_KATAKANA_LENGTH) {
            return;
        }
        if (dic.containsKey(katakana) || exceptions.contains(katakana)) {
            return;
        }
        final int minLsDistance = Math.round(katakana.length() * SIMILARITY_RATIO);
        boolean found = false;
        List<ValidationError> errors = new ArrayList<>();
        for (String key : dic.keySet()) {
            if (LevenshteinDistance.getDistance(key, katakana) <= minLsDistance) {
                found = true;
                validationErrors.add(createValidationError(sentence, katakana, key, dic.get(key).toString()));
            }
        }
        if (!found) {
            dic.put(katakana, sentence.getLineNum());
        }
    }

    @Override
    protected void init() throws RedPenException {
        WordListExtractor extractor = new WordListExtractor();

        LOG.info("Loading default katakana word dictionary");
        String defaultDictionaryFile = DEFAULT_RESOURCE_PATH
                + "/katakana-spellcheck.dat";
        try {
            extractor.loadFromResource(defaultDictionaryFile);
            LOG.info("Succeeded to load default dictionary.");
        } catch (IOException e) {
            throw new RedPenException("Failed to load default dictionary.", e);
        }

        Optional<String> confFile = getConfigAttribute("dict");
        confFile.ifPresent(e -> {
            LOG.info("User dictionary file is " + e);
            try {
                extractor.load(new FileInputStream(e));
            } catch (IOException e1) {
                LOG.error("Failed to load user dictionary");
            }
        });
        this.exceptions = extractor.get();

        //TODO : configurable SIMILARITY_RATIO.
        //TODO : configurable MAX_IGNORE_KATAKANA_LENGTH.
    }

    @Override
    public String toString() {
        return "KatakanaSpellCheckValidator{" +
                "dic=" + dic +
                ", exceptions=" + exceptions +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KatakanaSpellCheckValidator that = (KatakanaSpellCheckValidator) o;

        if (dic != null ? !dic.equals(that.dic) : that.dic != null) return false;
        if (exceptions != null ? !exceptions.equals(that.exceptions) : that.exceptions != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = dic != null ? dic.hashCode() : 0;
        result = 31 * result + (exceptions != null ? exceptions.hashCode() : 0);
        return result;
    }
}
