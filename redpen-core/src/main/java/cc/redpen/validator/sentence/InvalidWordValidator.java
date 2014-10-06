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
import cc.redpen.tokenizer.TokenElement;
import cc.redpen.util.ResourceLoader;
import cc.redpen.util.WordListExtractor;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Detect invalid word occurrences.
 */
final public class InvalidWordValidator extends Validator<Sentence> {
    private static final String DEFAULT_RESOURCE_PATH =
            "default-resources/invalid-word";
    private static final Logger LOG =
            LoggerFactory.getLogger(InvalidWordValidator.class);
    private Set<String> invalidWords = new HashSet<>();

    public List<ValidationError> validate(Sentence line) {
        List<ValidationError> result = new ArrayList<>();
        String content = line.content;
        //NOTE: only Ascii white space since this validator works for european languages.
        List<String> words = Arrays.asList(content.split(" "));
        for (TokenElement token : line.tokens) {
            if (invalidWords.contains(token.getSurface())) {
                result.add(createValidationError(line, token.getSurface()));
            }
        }
        return result;
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
        ResourceLoader loader = new ResourceLoader(extractor);

        LOG.info("Loading default invalid word dictionary for " +
                "\"" + lang + "\".");
        String defaultDictionaryFile = DEFAULT_RESOURCE_PATH
                + "/invalid-word-" + lang + ".dat";
        try {
            loader.loadInternalResource(defaultDictionaryFile);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            LOG.error("Failed to load default dictionary.");
            throw new RedPenException(e);
        }
        LOG.info("Succeeded to load default dictionary.");

        Optional<String> confFile = getConfigAttribute("dictionary");
        confFile.ifPresent(f -> {
            LOG.info("user dictionary file is " + f);
            try {
                loader.loadExternalFile(f);
            } catch (IOException e) {
                LOG.error(e.getMessage());
                LOG.error("Failed to load user dictionary.");
                return;
            }
            LOG.info("Succeeded to load specified user dictionary.");
        });

        invalidWords = extractor.get();
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
