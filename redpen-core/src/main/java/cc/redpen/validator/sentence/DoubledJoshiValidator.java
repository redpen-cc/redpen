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
import cc.redpen.util.Pair;

import java.util.*;

import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;

/**
 * DoubledJoshiValidator checks if the input texts has duplicated Kakujoshi words in one setnences.
 * <br>
 * Note: this validator works only for Japanese texts.
 */
public class DoubledJoshiValidator extends Validator {
    public DoubledJoshiValidator() {
        super();
        addDefaultProperties("list", emptySet());
        addDefaultProperties("min_dist", Integer.MAX_VALUE);
        addDefaultProperties("relaxed", false);
    }

    @Override
    public void validate(Sentence sentence) {
        final boolean relaxed = getBoolean("relaxed");
        final Set<String> skipList = getSet("list");
        final List<Pair<String, Integer>> vec = new ArrayList<>();

        int i = 0;
        for (TokenElement tokenElement : sentence.getTokens()) {
            final List<String> t = tokenElement.getTags();
            final String s = tokenElement.getSurface();
            if (t.get(0).equals("助詞")) {
                if (!skipList.contains(s)) {
                    if (relaxed) {
                        if ("連体化".equals(t.get(1)) && "の".equals(s)) {
                            continue;
                        }
                        if ("格助詞".equals(t.get(1)) && "を".equals(s)) {
                            continue;
                        }
                        if ("接続助詞".equals(t.get(1)) && "て".equals(s)) {
                            continue;
                        }
                    }
                    vec.add(new Pair<>(s, i));
                }
            }
            ++i;
        }

        final Map<String, Integer> seen = new HashMap<>();
        final int mininumDistance = getInt("min_dist");

        for (Pair<String, Integer> e : vec) {
            final String p = e.first;
            final int q = e.second;
            if (seen.containsKey(p)) {
                if ((q - seen.get(p)) < mininumDistance) {
                    addLocalizedError(sentence, p);
                }
            }
            seen.put(p, q);
        }
    }

    @Override
    public List<String> getSupportedLanguages() {
        return singletonList(Locale.JAPANESE.getLanguage());
    }
}
