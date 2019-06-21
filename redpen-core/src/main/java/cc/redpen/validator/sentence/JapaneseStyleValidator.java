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

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.singletonList;

/**
 * Validate Japanese document if it contains both Desumasu and Dearu styles.
 */
public class JapaneseStyleValidator extends Validator {
    private static final Pattern DEARU_PATTERN = Pattern.compile("である|のだが|であった|あるが|あった|だった");
    private static final Pattern DESUMASU_PATTERN = Pattern.compile("ですね|でした|ました|でしたが|でしたので|ですので|ですが|です|ます");

    private int dearuCount = 0;
    private int desumasuCount = 0;

    public JapaneseStyleValidator() {
        super("ForceDearu", false);		// Use autodetection of DEARU/DESUMASU
    }

    @Override
    public void preValidate(Sentence sentence) {
        // match content
        dearuCount += countMatch(sentence, DEARU_PATTERN);
        desumasuCount += countMatch(sentence, DESUMASU_PATTERN);
    }

    private int countMatch(Sentence sentence, Pattern pattern) {
        String content = sentence.getContent();
        Matcher mat = pattern.matcher(content);
        int count = 0;
        while(mat.find()) {
            count += 1;
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
        boolean forceDearu = getBoolean("ForceDearu");

        if (dearuCount > desumasuCount || forceDearu ) {
            detectPattern(sentence, DESUMASU_PATTERN);
        } else {
            detectPattern(sentence, DEARU_PATTERN);
        }
    }

    private void detectPattern(Sentence sentence, Pattern pattern) {
        Matcher mat = pattern.matcher(sentence.getContent());
        while(mat.find()){
            addLocalizedErrorWithPosition(sentence,
                    mat.start(),
                    mat.end(),
                    mat.group());
        }
    }

    @Override
    public List<String> getSupportedLanguages() {
        return singletonList(Locale.JAPANESE.getLanguage());
    }
}
