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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

final public class DoubledWordValidator extends Validator {
    private static final String DEFAULT_RESOURCE_PATH = "default-resources/doubled-word";

    private Set<String> skipList;

    public DoubledWordValidator() {
        super("list", new HashSet<>());
    }

    @Override
    public void validate(Sentence sentence) {
        Set<String> surfaces = new HashSet<>();
        for (TokenElement token : sentence.getTokens()) {
            String currentSurface = token.getSurface().toLowerCase();
            if (surfaces.contains(currentSurface) && !skipList.contains(currentSurface)
              && !getSetAttribute("list").contains(currentSurface)) {
                addLocalizedErrorFromToken(sentence, token);
            }
            surfaces.add(currentSurface);
        }
    }

    @Override
    protected void init() throws RedPenException {
        String defaultDictionaryFile = DEFAULT_RESOURCE_PATH + "/doubled-word-skiplist-" + getSymbolTable().getLang() + ".dat";
        skipList = WORD_LIST.loadCachedFromResource(defaultDictionaryFile, "doubled word skip list");

        Optional<String> confFile = getConfigAttribute("dict");
        if (confFile.isPresent()) {
            getSetAttribute("list").addAll(WORD_LIST.loadCachedFromFile(findFile(confFile.get()), "DoubledWordValidator user dictionary"));
        }
    }
}
