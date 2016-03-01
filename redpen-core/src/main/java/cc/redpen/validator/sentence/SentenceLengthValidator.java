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

/**
 * Validate input sentences contain more characters more than specified.
 */
public final class SentenceLengthValidator extends Validator {
    public SentenceLengthValidator() {
        super("max_len", 120);
    }

    @Override
    public void validate(Sentence sentence) {
        int maxLength = getInt("max_len");
        if (sentence.getContent().length() > maxLength) {
            addLocalizedError(sentence, sentence.getContent().length(), maxLength);
        }
    }
}
