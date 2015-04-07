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
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JapaneseStyleValidator extends Validator {
    private static final Pattern FUTUU_PATTERN = Pattern.compile("のだが|したが|したので|ないかと");
    private static final Pattern TEINEI_PATTERN = Pattern.compile("でしたが|でしたので|ですので|ですが");
    private static final Pattern FUTUU_END_PATTERN = Pattern.compile("(だ|である|った|ではない｜ないか)$");
    private static final Pattern TEINEI_END_PATTERN = Pattern.compile("(です|ます|ました|ません|ですね|でしょうか)$");

    private int futuuCount = 0;
    private int teineiCount = 0;

    @Override
    public void preValidate(Sentence sentence) {
        // match content
        futuuCount += countMatch(sentence, FUTUU_PATTERN);
        teineiCount+= countMatch(sentence, TEINEI_PATTERN);

        // match end content
        futuuCount += countEndMatch(sentence, FUTUU_END_PATTERN);
        teineiCount += countEndMatch(sentence, TEINEI_END_PATTERN);
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
    public void validate(List<ValidationError> errors, Sentence sentence) {
        if (futuuCount > teineiCount) {
            detectPattern(sentence, TEINEI_PATTERN, errors);
            detectPattern(sentence, TEINEI_END_PATTERN, errors);
        } else {
            detectPattern(sentence, FUTUU_PATTERN, errors);
            detectPattern(sentence, FUTUU_END_PATTERN, errors
            );
        }
    }

    private void detectPattern(Sentence sentence, Pattern pattern, List<ValidationError> errors) {
        Matcher mat = pattern.matcher(sentence.getContent());
        while(mat.find()){
            errors.add(createValidationErrorWithPosition(sentence,
                    sentence.getOffset(mat.start()),
                    sentence.getOffset(mat.end()),
                    mat.group()));
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

        if (futuuCount != that.futuuCount) return false;
        if (teineiCount != that.teineiCount) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = futuuCount;
        result = 31 * result + teineiCount;
        return result;
    }

    @Override
    public String toString() {
        return "JapaneseStyleValidator{" +
                "futuuCount=" + futuuCount +
                ", teineiCount=" + teineiCount +
                '}';
    }
}
