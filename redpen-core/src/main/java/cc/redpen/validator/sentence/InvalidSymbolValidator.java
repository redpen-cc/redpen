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
import cc.redpen.validator.Validator;

import java.util.Set;

/**
 * Validate if there is invalid characters in sentences.
 */
public final class InvalidSymbolValidator extends Validator {
    @Override
    public void validate(Sentence sentence) {
        Set<SymbolType> symbolTypes = getSymbolTable().getNames();
        for (SymbolType symbolType : symbolTypes) {
            validateSymbol(sentence, symbolType);
        }
    }

    private void validateSymbol(Sentence sentence, SymbolType symbolType) {
        String sentenceStr = sentence.getContent();
        Symbol symbol = getSymbolTable().getSymbol(symbolType);
        for (char invalidChar : symbol.getInvalidChars()) {
            detectSymbol(sentence, sentenceStr, invalidChar);
        }
    }

    private void detectSymbol(Sentence sentence, String sentenceStr, char invalidChar) {
        int startPosition = sentenceStr.indexOf(invalidChar);
        if (startPosition == -1) { return; }
        if (invalidChar != '.' || !isDigitPeriod(startPosition, sentenceStr)) {
            addLocalizedErrorWithPosition(sentence, startPosition,
                    startPosition + 1, invalidChar);
        }
    }

    /**
     * NOTE: Even when selecting Japanese or Chinese style period such as '。', '．', the Ascii period
     * is used in floating numbers (Ex. Ubuntu v1.04 or 200.00).
     */
    private boolean isDigitPeriod(int startPosition, String sentenceStr) {
        if (startPosition == sentenceStr.length() -1 || startPosition == 0) { return false; }

        return Character.isDigit(sentenceStr.charAt(startPosition - 1))
                && Character.isDigit(sentenceStr.charAt(startPosition + 1));
    }
}
