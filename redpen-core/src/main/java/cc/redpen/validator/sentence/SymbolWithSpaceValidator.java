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

import java.util.Set;

import static java.lang.Character.isLetterOrDigit;
import static java.lang.Character.isWhitespace;

/**
 * Validate symbol has before and after symbols. Needed spaces is depend on
 * the symbol and defined in DVCharacterTable.
 */
public class SymbolWithSpaceValidator extends Validator {

    @Override
    public void validate(Sentence sentence) {
        Set<SymbolType> symbolTypes = getSymbolTable().getNames();
        for (SymbolType symbolType : symbolTypes) {
            validateSymbol(sentence, symbolType);
        }
    }

    private ValidationError validateSymbol(Sentence sentence, SymbolType symbolType) {
        String sentenceStr = sentence.getContent();
        Symbol symbol = getSymbolTable().getSymbol(symbolType);
        if (!symbol.isNeedAfterSpace() && !symbol.isNeedBeforeSpace()) {
            return null;
        }

        char target = symbol.getValue();
        int position = sentenceStr.indexOf(target);
        if (position != -1) {
            String key = "";

            if (position > 0 && symbol.isNeedBeforeSpace() && !isWhitespace(sentenceStr.charAt(position - 1))) {
                key = "Before";
            }

            if (position < sentenceStr.length() - 1 && symbol.isNeedAfterSpace() && isLetterOrDigit(sentenceStr.charAt(position + 1))) {
                key += "After";
            }

            if (!key.isEmpty()) {
                addLocalizedErrorWithPosition(key, sentence,
                  position,
                  position + 1,
                  sentenceStr.charAt(position));
            }
        }
        return null;
    }
}
