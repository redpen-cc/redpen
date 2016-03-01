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

import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.TokenElement;
import cc.redpen.validator.DictionaryValidator;

import java.util.HashSet;
import java.util.Set;

final public class DoubledWordValidator extends DictionaryValidator {

    public DoubledWordValidator() {
        super("doubled-word/doubled-word-skiplist");
    }

    @Override
    public void validate(Sentence sentence) {
        Set<String> surfaces = new HashSet<>();
        for (TokenElement token : sentence.getTokens()) {
            String currentSurface = token.getSurface().toLowerCase();
            if (surfaces.contains(currentSurface) && !defaultList.contains(currentSurface)
              && !getSetAttribute("list").contains(currentSurface)) {
                addLocalizedErrorFromToken(sentence, token);
            }
            surfaces.add(currentSurface);
        }
    }
}
