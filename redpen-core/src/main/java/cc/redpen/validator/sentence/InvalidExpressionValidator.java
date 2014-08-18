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
import cc.redpen.util.ResourceLoader;
import cc.redpen.util.WordListExtractor;
import cc.redpen.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Validate input sentences contain invalid expression.
 */
public class InvalidExpressionValidator extends Validator<Sentence> {
    private static final String DEFAULT_RESOURCE_PATH = "default-resources/invalid-expression";
    private static final Logger LOG =
            LoggerFactory.getLogger(InvalidExpressionValidator.class);
    private Set<String> invalidExpressions = new HashSet<>();

    public List<ValidationError> validate(Sentence line) {
        List<ValidationError> result = new ArrayList<>();
        String str = line.content;
        for (String w : invalidExpressions) {
            if (str.contains(w)) {
                result.add(new ValidationError(
                        this.getClass(),
                        "Found invalid expression: \"" + w + "\"", line));
            }
        }
        return result;
    }

    /**
     * Add invalid element. This method is used for testing
     *
     * @param invalid invalid expression to be added the list
     */
    public void addInvalid(String invalid) {
        invalidExpressions.add(invalid);
    }

    @Override
    protected void init() throws RedPenException {
        String lang = getSymbolTable().getLang();
        WordListExtractor extractor = new WordListExtractor();
        ResourceLoader loader = new ResourceLoader(extractor);

        LOG.info("Loading default invalid expression dictionary for " +
                "\"" + lang + "\".");
        String defaultDictionaryFile = DEFAULT_RESOURCE_PATH
                + "/invalid-expression-" + lang + ".dat";
        if (loader.loadInternalResource(defaultDictionaryFile)) {
            LOG.info("Succeeded to load default dictionary.");
        } else {
            LOG.info("Failed to load default dictionary.");
        }

        Optional<String> confFile = getConfigAttribute("dictionary");
        confFile.ifPresent(e -> {
            LOG.info("user dictionary file is " + e);
            if (loader.loadExternalFile(e)) {
                LOG.info("Succeeded to load specified user dictionary.");
            } else {
                LOG.error("Failed to load user dictionary.");
            }
        });

        invalidExpressions = extractor.get();
    }
}
