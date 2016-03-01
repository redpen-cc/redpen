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
import cc.redpen.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * If input sentences contain invalid expressions, this validator
 * returns the errors with corrected expressions.
 */
public final class SuggestExpressionValidator extends Validator {
    private static final Logger LOG = LoggerFactory.getLogger(SuggestExpressionValidator.class);
    private Map<String, String> synonyms = new HashMap<>();

    public SuggestExpressionValidator() {
        super("dict", "");
    }

    @Override
    public void validate(Sentence sentence) {
        synonyms.keySet().stream().forEach(value -> {
            int startPosition = sentence.getContent().indexOf(value);
            if (startPosition != -1) {
                String suggested = synonyms.get(value);
                addLocalizedErrorWithPosition(sentence, startPosition, startPosition + value.length(), value, suggested);
            }
        });
    }

    @Override
    protected void init() throws RedPenException {
        //TODO: support default dictionary.
        String confFile = getStringAttribute("dict");
        if (isNotEmpty(confFile)) {
            LOG.info("Dictionary file is " + confFile);
            synonyms = KEY_VALUE.loadCachedFromFile(findFile(confFile), "SuggestExpressionValidator dictionary");
        }
        else {
            LOG.warn("Dictionary file is not specified");
        }
    }

    protected void setSynonyms(Map<String, String> synonymMap) {
        this.synonyms = synonymMap;
    }
}
