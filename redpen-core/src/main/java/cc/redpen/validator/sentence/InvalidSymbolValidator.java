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

import cc.redpen.config.Symbol;
import cc.redpen.config.SymbolType;
import cc.redpen.model.Sentence;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;

import java.util.List;
import java.util.Set;

/**
 * Validate if there is invalid characters in sentences.
 */
final public class InvalidSymbolValidator extends Validator {

    @Override
    public void validate(List<ValidationError> errors, Sentence sentence) {
        Set<SymbolType> symbolTypes = getSymbolTable().getNames();
        for (SymbolType symbolType : symbolTypes) {
            ValidationError error = validateSymbol(sentence, symbolType);
            if (error != null) {
                errors.add(error);
            }
        }
    }

    private ValidationError validateSymbol(Sentence sentence, SymbolType symbolType) {
        String sentenceStr = sentence.getContent();
        Symbol symbol = getSymbolTable().getSymbol(symbolType);
        for (char invalidChar : symbol.getInvalidChars()) {
            int startPosition = sentenceStr.indexOf(invalidChar);
            if (startPosition != -1) {
                return createValidationErrorWithPosition(sentence,
                        sentence.getOffset(startPosition),
                        sentence.getOffset(startPosition+1), invalidChar);
            }
        }
        return null;
    }
}
