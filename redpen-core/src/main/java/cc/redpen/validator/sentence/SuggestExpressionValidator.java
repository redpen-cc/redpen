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
import cc.redpen.validator.KeyValueDictionaryValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static cc.redpen.util.StringUtils.isProbablyJapanese;
import static java.lang.Character.isLetter;

/**
 * If input sentences contain invalid expressions, this validator
 * returns the errors with corrected expressions.
 */
public final class SuggestExpressionValidator extends KeyValueDictionaryValidator {
    private static final Logger LOG = LoggerFactory.getLogger(SuggestExpressionValidator.class);

    public SuggestExpressionValidator() {
        super("map", new HashMap<>(), "dict", "");
    }

    @Override
    public void validate(Sentence sentence) {
        String text = sentence.getContent();
        getMap("map").keySet().stream().forEach(value -> {
                int start = text.indexOf(value);
                if (start < 0) return;
                int end = start + value.length();
                boolean hasWordBoundaries = (start == 0 || !isLetter(text.charAt(start - 1))) && (end == text.length() || !isLetter(text.charAt(end)));
                if (isProbablyJapanese(text.charAt(start)) || hasWordBoundaries) {
                    addLocalizedErrorWithPosition(sentence, start, end, value, getMap("map").get(value));
                }
            });
    }

    @Override
    protected void init() throws RedPenException {
        super.init();
    }

}
