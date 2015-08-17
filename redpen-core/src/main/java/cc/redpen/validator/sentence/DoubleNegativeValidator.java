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
import cc.redpen.validator.ExpressionRule;
import cc.redpen.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Detect double negative expressions in Japanese texts.
 */
public class DoubleNegativeValidator extends Validator {
    private static final String DEFAULT_RESOURCE_PATH = "default-resources/double-negative/double-negative-rule-";
    private static final Logger LOG =
            LoggerFactory.getLogger(DoubleNegativeValidator.class);
    private Set<ExpressionRule> invalidPatterns;

    @Override
    public void validate(Sentence sentence) {
        for (ExpressionRule rule : invalidPatterns) {
            if (rule.match(sentence.getTokens())) {
                addValidationError(sentence, rule.toString());
            }
        }
    }

    @Override
    protected void init() throws RedPenException {
        invalidPatterns = RULE.loadCachedFromResource(
                DEFAULT_RESOURCE_PATH + getSymbolTable().getLang() +".dat",
                "double negative rules");
    }

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList(Locale.JAPANESE.getLanguage());
    }
}
