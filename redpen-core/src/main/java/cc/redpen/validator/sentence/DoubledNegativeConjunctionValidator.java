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
import java.util.regex.Pattern;

import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;

/**
 * DoubledNegativeConjunctionValidator checks if the input texts has
 * multiple negative conjuctions.
 * <br>
 * Note: this validator works only for Japanese texts.
 */
public class DoubledNegativeConjunctionValidator extends Validator {
    @Override
    public void validate(Sentence sentence) {
        List<TokenElement> vec = new ArrayList<>();

        for (TokenElement el : sentence.getTokens()) {
            final List<String> t = el.getTags();
            final String s = el.getSurface();
            if (t.get(0).equals("助詞") && t.get(1).equals("接続助詞") && s.equals("が")) {
                vec.add(el);
            }
        }

        if (vec.size() > 1) {
            addLocalizedError(sentence, vec.get(0).getSurface());
        }
    }

    @Override
    public List<String> getSupportedLanguages() {
        return singletonList(Locale.JAPANESE.getLanguage());
    }
}
