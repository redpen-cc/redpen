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
import cc.redpen.model.Sentence;
import cc.redpen.util.WordListExtractor;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Validate input sentences contain invalid expression.
 */
final public class InvalidExpressionValidator extends Validator {
    private static final String DEFAULT_RESOURCE_PATH = "default-resources/invalid-expression";
    private static final Logger LOG =
            LoggerFactory.getLogger(InvalidExpressionValidator.class);
    private Set<String> invalidExpressions = new HashSet<>();

    @Override
    public void validate(List<ValidationError> errors, Sentence sentence) {
        String str = sentence.content;
        errors.addAll(invalidExpressions.stream().filter(str::contains)
                .map(w -> createValidationError(sentence, w)).collect(Collectors.toList()));
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
        LOG.info("Loading default invalid expression dictionary for " +
                "\"" + lang + "\".");
        String defaultDictionaryFile = DEFAULT_RESOURCE_PATH
                + "/invalid-expression-" + lang + ".dat";
        try {
            extractor.loadFromResource(defaultDictionaryFile);
        } catch (IOException e) {
            LOG.error("Failed to load default dictionary.");
            LOG.error("InvalidExpressionValidator does not support dictionary for "
                    + "\"" + lang + "\".");
            throw new RedPenException(e);
        }
        LOG.info("Succeeded to load default dictionary.");

        Optional<String> listStr = getConfigAttribute("list");
        listStr.ifPresent(f -> {
            LOG.info("User defined invalid expression list found.");
            invalidExpressions.addAll(Arrays.asList(f.split(",")));
            LOG.info("Succeeded to add elements of user defined list.");
        });

        Optional<String> confFile = getConfigAttribute("dict");
        confFile.ifPresent(f -> {
            LOG.info("user dictionary file is " + f);
            try {
                extractor.load(new FileInputStream(f));
            } catch (IOException e) {
                LOG.error("Failed to load user dictionary.");
                return;
            }
            LOG.info("Succeeded to load specified user dictionary.");
        });

        invalidExpressions.addAll(extractor.get());
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
