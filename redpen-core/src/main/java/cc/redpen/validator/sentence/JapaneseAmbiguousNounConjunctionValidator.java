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

import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;

/**
 * JapaneseAmbiguousNounConjunctionValidator looks for ambiguous conjuctions among nouns.
 * <br>
 * Note: this validator works only for Japanese texts.
 */
public class JapaneseAmbiguousNounConjunctionValidator extends Validator {
    public JapaneseAmbiguousNounConjunctionValidator() {
        super("list", emptySet());
    }

    @Override
    public void validate(Sentence sentence) {
        int st = 0;
        final List<String> q = new LinkedList<>();

        for (TokenElement tokenElement : sentence.getTokens()) {
            final List<String> tags = tokenElement.getTags();
            switch (st) {
            case 0:
                if (tags.get(0).equals("助詞") && tags.get(6).equals("の")) {
                    q.add(tags.get(6));
                    st = 1;
                }
                break;
            case 1:
                if (tags.get(0).equals("名詞")) {
                    q.add(tags.get(6));
                } else {
                    if (tags.get(0).equals("助詞") && tags.get(6).equals("の")) {
                        q.add(tags.get(6));
                        st = 2;
                    } else {
                        q.clear();
                        st = 0;
                    }
                }
                break;
            case 2:
                addLocalizedError(sentence, String.join("", q));
                st = 0;
                break;
            }
        }
    }

    @Override
    public List<String> getSupportedLanguages() {
        return singletonList(Locale.JAPANESE.getLanguage());
    }
}
