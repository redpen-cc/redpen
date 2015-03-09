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
import cc.redpen.tokenizer.TokenElement;
import cc.redpen.util.WordListExtractor;
import cc.redpen.validator.CloneableValidator;
import cc.redpen.validator.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Detect invalid word occurrences.
 */
final public class InvalidWordValidator extends CloneableValidator {
    private static final String DEFAULT_RESOURCE_PATH =
            "default-resources/invalid-word";
    private static final Logger LOG =
            LoggerFactory.getLogger(InvalidWordValidator.class);
    private Set<String> invalidWords = new HashSet<>();

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList(Locale.ENGLISH.getLanguage());
    }

    @Override
    public void validate(List<ValidationError> errors, Sentence sentence) {
        //NOTE: only Ascii white space since this validator works for european languages.
        for (TokenElement token : sentence.getTokens()) {
            if (invalidWords.contains(token.getSurface().toLowerCase())) {
                errors.add(createValidationError(sentence, token.getSurface()));
            }
        }
    }

    /**
     * Add invalid element. This method is used for testing
     *
     * @param invalid invalid word to be added the list
     */
    public void addInvalid(String invalid) {
        invalidWords.add(invalid);
    }

    @Override
    protected void init() throws RedPenException {
        String lang = getSymbolTable().getLang();
        WordListExtractor extractor = new WordListExtractor();

        LOG.info("Loading default invalid word dictionary for " +
                "\"" + lang + "\".");
        String defaultDictionaryFile = DEFAULT_RESOURCE_PATH
                + "/invalid-word-" + lang + ".dat";
        try {
            extractor.loadFromResource(defaultDictionaryFile);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            LOG.error("Failed to load default dictionary.");
            throw new RedPenException(e);
        }
        LOG.info("Succeeded to load default dictionary.");

        Optional<String> listStr = getConfigAttribute("list");
        listStr.ifPresent(f -> {
            LOG.info("User defined invalid expression list found.");
            invalidWords.addAll(Arrays.asList(f.split(",")));
            LOG.info("Succeeded to add elements of user defined list.");
        });

        Optional<String> confFile = getConfigAttribute("dict");
        confFile.ifPresent(f -> {
            LOG.info("user dictionary file is " + f);
            try {
                extractor.load(new FileInputStream(f));
            } catch (IOException e) {
                LOG.error(e.getMessage());
                LOG.error("Failed to load user dictionary.");
                return;
            }
            LOG.info("Succeeded to load specified user dictionary.");
        });

        invalidWords.addAll(extractor.get());
    }

    @Override
    public String toString() {
        return "InvalidWordValidator{" +
                "invalidWords=" + invalidWords +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvalidWordValidator that = (InvalidWordValidator) o;

        if (invalidWords != null ? !invalidWords.equals(that.invalidWords) : that.invalidWords != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return invalidWords != null ? invalidWords.hashCode() : 0;
    }
}
