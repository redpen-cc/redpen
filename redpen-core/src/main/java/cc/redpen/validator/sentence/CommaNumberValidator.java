/**
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
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
import cc.redpen.ValidationError;
import cc.redpen.model.Sentence;
import cc.redpen.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Validate the number of commas in one sentence.
 */
public class CommaNumberValidator extends Validator<Sentence> {
    /**
     * Default maximum number of comma.
     */
    public static final int DEFAULT_MAX_COMMA_NUMBER = 3;

    /**
     * Default comma character.
     */
    public static final String DEFAULT_COMMA = ",";
    private static final Logger LOG =
            LoggerFactory.getLogger(CommaNumberValidator.class);
    private int maxCommaNum = DEFAULT_MAX_COMMA_NUMBER;
    private String comma = DEFAULT_COMMA;

    public List<ValidationError> validate(Sentence line) {
        List<ValidationError> result = new ArrayList<>();
        String content = line.content;
        int commaCount = 0;
        int position = 0;
        while (position != -1) {
            position = content.indexOf(this.comma);
            commaCount++;
            content = content.substring(position + 1, content.length());
        }
        if (maxCommaNum < commaCount) {
            result.add(new ValidationError(
                    this.getClass(),
                    "The number of comma is exceeds the maximum \""
                            + String.valueOf(commaCount) + "\".", line));
        }
        return result;
    }

    @Override
    protected void init() throws RedPenException {
        //TODO search parent configurations to get comma settings...
        this.maxCommaNum = getConfigAttributeAsInt("max_comma_num", DEFAULT_MAX_COMMA_NUMBER);

        this.comma = DEFAULT_COMMA;
        if (getSymbolTable().containsSymbol("COMMA")) {
            this.comma = getSymbolTable().getSymbol("COMMA").getValue();
            LOG.info("comma is set to \"" + this.comma + "\"");
        }
    }
}
