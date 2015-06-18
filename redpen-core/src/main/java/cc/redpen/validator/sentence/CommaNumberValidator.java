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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static cc.redpen.config.SymbolType.COMMA;

/**
 * Validate the number of commas in one sentence.
 */
final public class CommaNumberValidator extends Validator {
    private static final Logger LOG = LoggerFactory.getLogger(CommaNumberValidator.class);
    /**
     * Default maximum number of comma.
     */
    private static final int DEFAULT_MAX_COMMA_NUMBER = 3;
    private int maxCommaNum = DEFAULT_MAX_COMMA_NUMBER;
    private char comma;

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
        if (maxCommaNum < commaCount) {
            addValidationError(sentence, commaCount, maxCommaNum);
        }
    }

    @Override
    protected void init() throws RedPenException {
        this.maxCommaNum = getConfigAttributeAsInt("max_num", DEFAULT_MAX_COMMA_NUMBER);
        this.comma = getSymbolTable().getValueOrFallbackToDefault(COMMA);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommaNumberValidator that = (CommaNumberValidator) o;

        if (comma != that.comma) return false;
        if (maxCommaNum != that.maxCommaNum) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = maxCommaNum;
        result = 31 * result + (int) comma;
        return result;
    }

    @Override
    public String toString() {
        return "CommaNumberValidator{" +
                "maxCommaNum=" + maxCommaNum +
                ", comma=" + comma +
                '}';
    }
}
