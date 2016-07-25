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

import java.util.*;

import static java.util.Collections.singletonList;


/**
 * DoubledJoshiValidator checks if the input texts has duplicated Joshi words in one setnences.
 * <br>
 * Note: this validator works only for Japanese texts.
 * Note: the min_interval was introduced following textlint-rule-no-doubled-joshi.
 */
public class DoubledJoshiValidator extends DictionaryValidator {
    public DoubledJoshiValidator() {
        super("min_interval", 1);
    }

    @Override
    public void validate(Sentence sentence) {
        Map<String, List<TokenElement>> counts = new HashMap<>();
        Map<TokenElement, Integer> positions = new HashMap<>();
        Set<String> skipList = getSet("list");
        int id = 0;
        // extract all the Joshi tokens
        for (TokenElement tokenElement : sentence.getTokens()) {
            if (tokenElement.getTags().get(0).equals("助詞") &&
                    !skipList.contains(tokenElement.getSurface())) {
                if (!counts.containsKey(tokenElement.getSurface())) {
                    counts.put(tokenElement.getSurface(), new LinkedList<>());
                }
                counts.get(tokenElement.getSurface()).add(tokenElement);
                positions.put(tokenElement, id);
                id++;
            }
        }

        // create errors
        for (String joshi : counts.keySet()) {
            if (counts.get(joshi).size() < 2) { continue; }
            TokenElement prev = null;
            for (TokenElement token : counts.get(joshi)) {
                int prevPosition = getPosition(prev, positions);
                int currentPosition = getPosition(token, positions);
                prev = token;
                if (prevPosition == -1 || currentPosition == -1) { continue; }
                if ((currentPosition - prevPosition) <= getInt("min_interval")) {
                    addLocalizedError(sentence, joshi);
                }
            }
        }
    }

    private int getPosition(TokenElement token, Map<TokenElement, Integer> positions) {
        if (token == null) { return -1; }
        return positions.get(token);
    }

    @Override
    public List<String> getSupportedLanguages() {
        return singletonList(Locale.JAPANESE.getLanguage());
    }
}
