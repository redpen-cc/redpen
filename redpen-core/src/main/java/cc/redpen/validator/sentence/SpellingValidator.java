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
import cc.redpen.tokenizer.TokenElement;
import cc.redpen.util.DictionaryLoader;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SpellingValidator extends Validator {

    private static final String DEFAULT_RESOURCE_PATH = "default-resources/spellchecker";
    private static final Logger LOG =
            LoggerFactory.getLogger(SpellingValidator.class);
    private static String skipCharacters = "+~-(),\".";
    // TODO: replace more memory efficient data structure
    private Set<String> defaultDictionary;
    private Set<String> customDictionary;


    @Override
    protected void init() throws RedPenException {
        String defaultDictionaryFile = DEFAULT_RESOURCE_PATH
                + "/spellchecker-" + getSymbolTable().getLang() + ".dat";
        defaultDictionary = DictionaryLoader.WORD_LOWERCASE.loadCachedFromResource(defaultDictionaryFile, "spell dictionary");

        customDictionary = new HashSet<>();
        Optional<String> listStr = getConfigAttribute("list");
        listStr.ifPresent(f -> {
            LOG.info("User defined valid word list found.");
            customDictionary.addAll(Arrays.asList(f.split(",")));
            LOG.info("Succeeded to add elements of user defined list.");
        });

        Optional<String> userDictionaryFile = getConfigAttribute("dict");
        if (userDictionaryFile.isPresent()) {
            String f = userDictionaryFile.get();
            LOG.info("user dictionary file is " + f);
            customDictionary.addAll(DictionaryLoader.WORD_LOWERCASE.loadCachedFromFile(f, "SpellingValidator user dictionary"));
            LOG.info("Succeeded to load specified user dictionary.");
        }
    }

    @Override
    public void validate(List<ValidationError> errors, Sentence sentence) {
        for (TokenElement token : sentence.getTokens()) {
            String surface = normalize(token.getSurface());
            if (surface.length() == 0) {
                continue;
            }

            if (!this.defaultDictionary.contains(surface) && !this.customDictionary.contains(surface)) {
                errors.add(createValidationErrorFromToken(sentence, token));
            }
        }
    }

    private String normalize(String line) {
        StringBuilder builder = new StringBuilder(line.length());
        for (char ch : line.toCharArray()) {
            if (skipCharacters.indexOf(ch) == -1) {
                builder.append(Character.toLowerCase(ch));
            }
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpellingValidator that = (SpellingValidator) o;

        if (defaultDictionary != null ? !defaultDictionary.equals(that.defaultDictionary) : that.defaultDictionary != null) return false;
        return !(customDictionary != null ? !customDictionary.equals(that.customDictionary) : that.customDictionary != null);

    }

    @Override
    public int hashCode() {
        int result = defaultDictionary != null ? defaultDictionary.hashCode() : 0;
        result = 31 * result + (customDictionary != null ? customDictionary.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SpellingValidator{" +
                "defaultDictionary=" + defaultDictionary +
                ", customDictionary=" + customDictionary +
                '}';
    }
}
