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
 * Check if the input sentence start with a capital letter.
 */
final public class StartWithCapitalLetterValidator extends CloneableValidator {
    private static final String DEFAULT_RESOURCE_PATH = "default-resources/capital-letter-exception-list";
    private static final Logger LOG =
            LoggerFactory.getLogger(SpellingValidator.class);
    private Set<String> whiteList;

    public StartWithCapitalLetterValidator() {
        this.whiteList = new HashSet<>();
    }

    public boolean addWhiteList(String item) {
        return whiteList.add(item);
    }

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList(Locale.ENGLISH.getLanguage());
    }

    @Override
    public void validate(List<ValidationError> errors, Sentence sentence) {
        String content = sentence.getContent();
        List<TokenElement> tokens = sentence.getTokens();
        String headWord = "";
        for (TokenElement token : tokens) {
            if (!token.getSurface().equals("")) { // skip white space
                headWord = token.getSurface();
                break;
            }
        }

        if (tokens.size() == 0 || this.whiteList.contains(headWord)) {
            return;
        }

        char headChar = '≡';
        for (char ch : content.toCharArray()) {
            if (ch != ' ') {
                headChar = ch;
                break;
            }
        }

        if (headChar == '≡') {
            return;
        }

        if (Character.isLowerCase(headChar)) {
            errors.add(createValidationError(sentence, headChar));
        }
    }

    @Override
    protected void init() throws RedPenException {
        WordListExtractor extractor = new WordListExtractor();

        LOG.info("Loading default capital letter exception dictionary ");
        String defaultDictionaryFile = DEFAULT_RESOURCE_PATH
                + "/default-capital-case-exception-list.dat";
        try {
            extractor.loadFromResource(defaultDictionaryFile);
        } catch (IOException e) {
            throw new RedPenException("Failed to load default dictionary.", e);
        }
        LOG.info("Succeeded to load default dictionary.");

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

        whiteList = extractor.get();
    }

    @Override
    public String toString() {
        return "StartWithCapitalLetterValidator{" +
                "whiteList=" + whiteList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StartWithCapitalLetterValidator that = (StartWithCapitalLetterValidator) o;

        return !(whiteList != null ? !whiteList.equals(that.whiteList) : that.whiteList != null);

    }

    @Override
    public int hashCode() {
        return whiteList != null ? whiteList.hashCode() : 0;
    }
}
