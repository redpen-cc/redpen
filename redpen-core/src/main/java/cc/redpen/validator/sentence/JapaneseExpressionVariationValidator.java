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
import cc.redpen.validator.Validator;

import java.util.*;

import static java.util.Collections.singletonList;

public class JapaneseExpressionVariationValidator extends Validator {
    private Map<String, List<TokenElement>> words = new HashMap<>();

    @Override
    public void validate(Sentence sentence) {
        for (TokenElement token : sentence.getTokens()) {
            String reading = token.getTags().get(7);
            if (this.words.containsKey(reading)) {
                List<TokenElement> tokens = this.words.get(reading);
                for (TokenElement candidate : tokens) {
                    if (candidate != token && !token.getSurface().equals(candidate.getSurface())) {
                        addLocalizedErrorFromToken(sentence, token);
                    }
                }
            }
        }
    }

    @Override
    public void preValidate(Sentence sentence) {
        for (TokenElement token : sentence.getTokens()) {
            String reading = token.getTags().get(7);
            if (!this.words.containsKey(reading)) {
                this.words.put(reading, new LinkedList<TokenElement>());
            }
            this.words.get(reading).add(token);

        }
    }

    @Override
    public List<String> getSupportedLanguages() {
        return singletonList(Locale.JAPANESE.getLanguage());
    }
}
