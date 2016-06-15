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
 * JapaneseBrokenExpressionValidator detects certain forms of "broken" japanese expressions.
 * <br>
 * Note: this validator works only for Japanese texts.
 */
public class JapaneseBrokenExpressionValidator extends Validator {
    @Override
    public void validate(Sentence sentence) {
        final List<TokenElement> tokens = sentence.getTokens();

        for (int i = 0; i < (tokens.size() - 1); ++i) {
            final TokenElement p = tokens.get(i);
            final List<String> ptags = p.getTags();
            if (ptags.get(0).equals("動詞") && ptags.get(1).equals("自立") && ptags.get(4).equals("一段") && ptags.get(5).equals("未然形")) {
                final TokenElement q = tokens.get(i+1);
                final List<String> qtags = q.getTags();
                if (qtags.get(0).equals("動詞") && qtags.get(1).equals("接尾") && qtags.get(4).equals("一段") && qtags.get(5).equals("未然形") && qtags.get(6).equals("られる")) {
                } else {
                    addLocalizedError(sentence, p.getSurface());
                }
            }
        }
    }

    @Override
    public List<String> getSupportedLanguages() {
        return singletonList(Locale.JAPANESE.getLanguage());
    }
}
