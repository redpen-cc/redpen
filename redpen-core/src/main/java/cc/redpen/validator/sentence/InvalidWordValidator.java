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
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Detect invalid word occurrences.
 */
final public class InvalidWordValidator extends Validator {
    private static final String DEFAULT_RESOURCE_PATH =
            "default-resources/invalid-word";
    private static final Logger LOG =
            LoggerFactory.getLogger(InvalidWordValidator.class);
    private Set<String> invalidWords;
    private Set<String> customInvalidWords = new HashSet<>();

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList(Locale.ENGLISH.getLanguage());
    }

    @Override
    public void validate(List<ValidationError> errors, Sentence sentence) {
        //NOTE: only Ascii white space since this validator works for european languages.
        for (TokenElement token : sentence.getTokens()) {
            if (invalidWords.contains(token.getSurface().toLowerCase())
                    || customInvalidWords.contains(token.getSurface().toLowerCase())) {
                errors.add(createValidationErrorFromToken(sentence, token));
            }
        }
    }

    @Override
    protected void init() throws RedPenException {
        String lang = getSymbolTable().getLang();
        String defaultDictionaryFile = DEFAULT_RESOURCE_PATH
                + "/invalid-word-" + lang + ".dat";
        invalidWords = WordListExtractor.loadWordListFromResource(defaultDictionaryFile, "invalid word", false);

        WordListExtractor extractor = new WordListExtractor();
        getConfigAttribute("list").ifPresent((f -> {
            LOG.info("User defined invalid expression list found.");
            customInvalidWords.addAll(Arrays.asList(f.split(",")));
            LOG.info("Succeeded to add elements of user defined list.");
        }));

        Optional<String> confFile = getConfigAttribute("dict");
        if(confFile.isPresent()){
            LOG.info("user dictionary file is " + confFile.get());
            try {
                customInvalidWords.addAll(extractor.load(new FileInputStream(confFile.get())));
                LOG.info("Succeeded to load specified user dictionary.");
            } catch (IOException e) {
                throw new RedPenException("Failed to load user dictionary.", e);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvalidWordValidator that = (InvalidWordValidator) o;

        if (invalidWords != null ? !invalidWords.equals(that.invalidWords) : that.invalidWords != null) return false;
        return !(customInvalidWords != null ? !customInvalidWords.equals(that.customInvalidWords) : that.customInvalidWords != null);

    }

    @Override
    public int hashCode() {
        int result = invalidWords != null ? invalidWords.hashCode() : 0;
        result = 31 * result + (customInvalidWords != null ? customInvalidWords.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "InvalidWordValidator{" +
                "invalidWords=" + invalidWords +
                ", customInvalidWords=" + customInvalidWords +
                '}';
    }
}
