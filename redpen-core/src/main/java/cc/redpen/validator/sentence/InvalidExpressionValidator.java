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

import cc.redpen.model.Sentence;
import cc.redpen.validator.DictionaryValidator;

import static java.util.stream.Stream.concat;

/**
 * Validate input sentences contain invalid expression.
 */
public final class InvalidExpressionValidator extends DictionaryValidator {
    public InvalidExpressionValidator() {
        super("invalid-expression/invalid-expression");
    }

    @Override
    public void validate(Sentence sentence) {
        concat(dictionary.stream(), getSetAttribute("list").stream()).forEach(value -> {
            int startPosition = sentence.getContent().indexOf(value);
            if (startPosition != -1) {
                addLocalizedErrorWithPosition(sentence, startPosition, startPosition + value.length(), value);
            }
        });
    }
}
