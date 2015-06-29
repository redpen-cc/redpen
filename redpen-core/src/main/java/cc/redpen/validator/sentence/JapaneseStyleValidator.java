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

import cc.redpen.model.Sentence;
import cc.redpen.validator.Validator;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validate Japanese document if it contains both Desumasu and Dearu styles.
 * 
 */
public class JapaneseStyleValidator extends Validator {
    private static final Pattern DEARU_PATTERN = Pattern.compile("のだが|したが|したので|ないかと|してきた|であるから");
    private static final Pattern DEARU_END_PATTERN = Pattern.compile("(だ|である|った|ではない｜ないか|しろ|しなさい|いただきたい|いただく|ならない|あろう|られる)$");

    private static final Pattern DESUMASU_PATTERN = Pattern.compile("でしたが|でしたので|ですので|ですが");
    private static final Pattern DESUMASU_END_PATTERN = Pattern.compile("(です|ます|ました|ません|ですね|でしょうか|ください|ませ)$");

    private int dearuCount = 0;
    private int desumasuCount = 0;

    @Override
    public void preValidate(Sentence sentence) {
        // match content
        dearuCount += countMatch(sentence, DEARU_PATTERN);
        desumasuCount += countMatch(sentence, DESUMASU_PATTERN);

        // match end content
        dearuCount += countEndMatch(sentence, DEARU_END_PATTERN);
        desumasuCount += countEndMatch(sentence, DESUMASU_END_PATTERN);
    }

    private int countMatch(Sentence sentence, Pattern pattern) {
        String content = sentence.getContent();
        Matcher mat = pattern.matcher(content);
        int count = 0;
        while(mat.find()){
            count +=1;
        }
        return count;
    }

    private int countEndMatch(Sentence sentence, Pattern pattern) {
        String content = sentence.getContent();
        if (content.length() < 2) {
            return 0;
        }
        int count = 0;
        Matcher mat = pattern.matcher(content);
        mat.region(0, content.length()-2);
        while(mat.find()){
            count +=1;
        }
        return count;
    }

    @Override
    public void validate(Sentence sentence) {
        if (dearuCount > desumasuCount) {
            detectPattern(sentence, DESUMASU_PATTERN);
            detectPattern(sentence, DESUMASU_END_PATTERN);
        } else {
            detectPattern(sentence, DEARU_PATTERN);
            detectPattern(sentence, DEARU_END_PATTERN);
        }
    }

    private void detectPattern(Sentence sentence, Pattern pattern) {
        Matcher mat = pattern.matcher(sentence.getContent());
        while(mat.find()){
            addValidationErrorWithPosition(sentence,
                    sentence.getOffset(mat.start()),
                    sentence.getOffset(mat.end()),
                    mat.group());
        }
    }

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList(Locale.JAPANESE.getLanguage());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JapaneseStyleValidator that = (JapaneseStyleValidator) o;

        if (dearuCount != that.dearuCount) return false;
        if (desumasuCount != that.desumasuCount) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = dearuCount;
        result = 31 * result + desumasuCount;
        return result;
    }

    @Override
    public String toString() {
        return "JapaneseStyleValidator{" +
                "dearuCount=" + dearuCount +
                ", desumasuCount=" + desumasuCount +
                '}';
    }
}
