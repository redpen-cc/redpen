/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
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
import cc.redpen.util.StringUtils;
import cc.redpen.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;

/**
 * Validate the end hyphens of Katakana words in Japanese documents.
 * Japanese Katakana words have variations in end hyphen.
 * For example, "computer" is written in Katakana by
 * "コンピュータ (without hyphen) ", and "コンピューター (with hypen) ".
 * This validator validate if Katakana words ending format is match
 * the predefined standard. See JIS Z8301, G.6.2.2 b) G.3.
 * <p>
 * The rules in JIS Z8301 are as follows:
 * <p>
 * a) Words of 3 characters or more can not have the end hyphen.
 * b) Words of 2 characters or less can have the end hyphen.
 * c) A compound word applies a) and b) for each component.
 * d) In the cases from a) to c), the length of a syllable
 * which are represented as a hyphen, flip syllable,
 * and stuffed syllable is 1 except for Youon.
 * <p>
 * Note that KatakanaEndHyphenValidator only checks the rules a) and b).
 */
final public class KatakanaEndHyphenValidator extends Validator {
    private static final Logger LOG =
            LoggerFactory.getLogger(KatakanaEndHyphenValidator.class);

    /**
     * Default Katakana limit length without hypen.
     */
    private static final int DEFAULT_KATAKANA_LIMIT_LENGTH = 3;
    /**
     * Katakana end hyphen character.
     */
    private static final char HYPHEN = 'ー';
    /**
     * Katakana middle dot character.
     */
    private static final char KATAKANA_MIDDLE_DOT = '・';

    private Set<String> customSkipList;

    public KatakanaEndHyphenValidator() {
        super();
    }

    public static boolean isKatakanaEndHyphen(StringBuilder katakana) {
        return (DEFAULT_KATAKANA_LIMIT_LENGTH < katakana.length()
                && katakana.charAt(katakana.length() - 1) == HYPHEN);
    }

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList(Locale.JAPANESE.getLanguage());
    }

    @Override
    public void validate(Sentence sentence) {
        StringBuilder katakana = new StringBuilder("");
        for (int i = 0; i < sentence.getContent().length(); i++) {
            char c = sentence.getContent().charAt(i);
            if (StringUtils.isKatakana(c) && c != KATAKANA_MIDDLE_DOT) {
                katakana.append(c);
            } else {
                this.checkKatakanaEndHyphen(sentence, katakana, i-1);
                katakana.delete(0, katakana.length());
            }
        }
        this.checkKatakanaEndHyphen(sentence, katakana, sentence.getContent().length() - 1);
    }

    private void checkKatakanaEndHyphen(Sentence sentence,
                                                         StringBuilder katakana,
                                                         int position) {
        if ( !(customSkipList != null && customSkipList.contains(katakana.toString())) ) {
            if (isKatakanaEndHyphen(katakana)) {
                addLocalizedErrorWithPosition(sentence, position, position + 1, katakana.toString());
            }
        }
    }

    @Override
    protected void init() throws RedPenException {
        customSkipList = new HashSet<>();
        Optional<String> skipListStr = getConfigAttribute("list");
        skipListStr.ifPresent(f -> {
            LOG.info("Found user defined skip list.");
            customSkipList.addAll(Arrays.asList(f.split(",")));
            LOG.info("Succeeded to add elements of user defined skip list.");
        });

        Optional<String> confFile = getConfigAttribute("dict");
        if (confFile.isPresent()) {
            customSkipList.addAll(WORD_LIST.loadCachedFromFile(new File(confFile.get()), "KatakanaEndHyphenValidator user dictionary"));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KatakanaEndHyphenValidator that = (KatakanaEndHyphenValidator) o;

        return !(customSkipList != null ? !customSkipList.equals(that.customSkipList) : that.customSkipList != null);
    }

    @Override
    public int hashCode() {
        int result = customSkipList != null ? customSkipList.hashCode() : 0;
        return result;
    }

    @Override
    public String toString() {
        return "KatakanaEndHyphenValidator{" +
                "customSkipList=" + customSkipList +
                '}';
    }
}
