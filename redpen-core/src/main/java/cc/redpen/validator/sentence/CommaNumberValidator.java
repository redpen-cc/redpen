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
import cc.redpen.validator.Validator;

import static cc.redpen.config.SymbolType.COMMA;

/**
 * Validate the number of commas in one sentence.
 */
public final class CommaNumberValidator extends Validator {

    public CommaNumberValidator() {
        super("max_num", 3);
    }

    private char comma;

    private int getMaxNum() {
        return getIntAttribute("max_num");
    }

    @Override
    public void validate(Sentence sentence) {
        String content = sentence.getContent();
        int commaCount = 0;
        int position = 0;
        while (position != -1) {
            position = content.indexOf(this.comma);
            commaCount++;
            content = content.substring(position + 1, content.length());
        }
        if (getMaxNum() < commaCount) {
            addLocalizedError(sentence, commaCount, getMaxNum());
        }
    }

    @Override
    protected void init() throws RedPenException {
        this.comma = getSymbolTable().getValueOrFallbackToDefault(COMMA);
    }
}
