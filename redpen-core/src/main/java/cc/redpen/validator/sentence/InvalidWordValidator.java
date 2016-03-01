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
import cc.redpen.validator.Validator;

import java.util.*;

import static java.util.Collections.singletonList;

/**
 * Detect invalid word occurrences.
 */
final public class InvalidWordValidator extends Validator {
    private static final String DEFAULT_RESOURCE_PATH = "default-resources/invalid-word";
    private Set<String> invalidWords;

    public InvalidWordValidator() {
        super("list", new HashSet<>());
    }

    @Override
    public List<String> getSupportedLanguages() {
        return singletonList(Locale.ENGLISH.getLanguage());
    }

    @Override
    public void validate(Sentence sentence) {
        for (TokenElement token : sentence.getTokens()) {
            if (invalidWords.contains(token.getSurface().toLowerCase())
                    || getSetAttribute("list").contains(token.getSurface().toLowerCase())) {
                addLocalizedErrorFromToken(sentence, token);
            }
        }
    }

    @Override
    protected void init() throws RedPenException {
        String lang = getSymbolTable().getLang();
        String defaultDictionaryResource = DEFAULT_RESOURCE_PATH + "/invalid-word-" + lang + ".dat";
        invalidWords = WORD_LIST.loadCachedFromResource(defaultDictionaryResource, "invalid word");

        Optional<String> confFile = getConfigAttribute("dict");
        if (confFile.isPresent()) {
            getSetAttribute("list").addAll(WORD_LIST.loadCachedFromFile(findFile(confFile.get()), "InvalidWordValidator user dictionary"));
        }
    }
}
