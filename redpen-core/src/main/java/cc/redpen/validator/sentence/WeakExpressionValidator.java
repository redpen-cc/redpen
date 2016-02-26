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
import cc.redpen.validator.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Warn about grammatically weak expressions in the sentence. This is essentially a version of
 * a dictionary-lookup validator, but one that also looks up sequences of words.
 */
public class WeakExpressionValidator extends Validator {

    private static final String DEFAULT_RESOURCE_PATH = "default-resources/weak-expressions";

    // a list of weak expressions
    private List<String> weakExpressions;

    @Override
    protected void init() throws RedPenException {
        super.init();

        String defaultDictionaryFile = DEFAULT_RESOURCE_PATH + "/weak-expressions-" + getSymbolTable().getLang() + ".dat";
        weakExpressions = new DictionaryLoader<List<String>>(ArrayList::new, (list, line) -> list.add(line.trim().toLowerCase()))
          .loadCachedFromResource(defaultDictionaryFile, "weak expressions");
    }

    /**
     * Build up sequences of tokens and see if that sequence exists in the weak-expression dictionary
     *
     * @param sentence input
     */
    @Override
    public void validate(Sentence sentence) {
        TokenElement tokens[] = new TokenElement[]{null, null, null, null, null, null};

        for (int i = 0; i < sentence.getTokens().size(); i++) {

            for (int j = 0; j < tokens.length - 1; j++) {
                tokens[j] = i + j < sentence.getTokens().size() ? sentence.getTokens().get(i + j) : null;
            }

            if (tokens[0] != null) {
                // check all groups to see if they are tokenized in the dictionary
                String expression = "";
                for (int j = 0; (j < tokens.length) && (tokens[j] != null); j++) {
                    expression += ((j > 0) ? " " : "") + tokens[j].getSurface();

                    if (weakExpressions.contains(expression.toLowerCase())) {
                        addLocalizedErrorWithPosition(
                                "WeakExpression",
                                sentence,
                                tokens[0].getOffset(),
                                tokens[j].getOffset() + tokens[j].getSurface().length(),
                                expression);
                    }
                }
            }
        }
    }
}
