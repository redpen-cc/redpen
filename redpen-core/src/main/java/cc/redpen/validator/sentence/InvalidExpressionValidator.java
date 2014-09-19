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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validate input sentences contain invalid expression.
 */
final public class InvalidExpressionValidator extends Validator<Sentence> {
    private static final String DEFAULT_RESOURCE_PATH = "default-resources/invalid-expression";
    private static final Logger LOG =
            LoggerFactory.getLogger(InvalidExpressionValidator.class);
    private Set<String> invalidExpressions = new HashSet<>();

    public List<ValidationError> validate(Sentence line) {
        List<ValidationError> validationErrors = new ArrayList<>();
        String str = line.content;
        validationErrors.addAll(invalidExpressions.stream().filter(str::contains)
                .map(w -> createValidationError(line, w)).collect(Collectors.toList()));
        return validationErrors;
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
        try {
            loader.loadInternalResource(defaultDictionaryFile);
        } catch (IOException e) {
            LOG.error("Failed to load default dictionary.");
            LOG.error("InvalidExpressionValidator does not support dictionary for "
                    + "\"" + lang + "\".");
            throw new RedPenException(e);
        }
        LOG.info("Succeeded to load default dictionary.");

        Optional<String> confFile = getConfigAttribute("dictionary");
        confFile.ifPresent(f -> {
            LOG.info("user dictionary file is " + f);
            try {
                loader.loadExternalFile(f);
            } catch (IOException e) {
                LOG.error("Failed to load user dictionary.");
                return;
            }
            LOG.info("Succeeded to load specified user dictionary.");
        });

        invalidExpressions = extractor.get();
    }

    @Override
    public String toString() {
        return "InvalidExpressionValidator{" +
                "invalidExpressions=" + invalidExpressions +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvalidExpressionValidator that = (InvalidExpressionValidator) o;

        return !(invalidExpressions != null ? !invalidExpressions.equals(that.invalidExpressions) : that.invalidExpressions != null);

    }

    @Override
    public int hashCode() {
        return invalidExpressions != null ? invalidExpressions.hashCode() : 0;
    }
}
