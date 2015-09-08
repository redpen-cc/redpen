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
import cc.redpen.util.SpellingUtils;
import cc.redpen.validator.Validator;

/**
 * Ensure groups of words that are hyphenated in the dictionary are hyphenated in the sentence
 */
public class HyphenationValidator extends Validator {

    @Override
    public void validate(Sentence sentence) {
        String lang = getSymbolTable().getLang();

        // consider hyphenated words of this array's length
        TokenElement tokens[] = new TokenElement[]{null, null, null, null};

        // combine sequences of tokens together with hyphens and test
        // for their presence in the dictionary
        for (int i = 0; i < sentence.getTokens().size(); i++) {
            for (int j = 0; j < tokens.length - 1; j++) {
                tokens[j] = i + j < sentence.getTokens().size() ? sentence.getTokens().get(i + j) : null;
            }

            if (tokens[0] != null) {
                // check all groups to see if they are tokenized in the dictionary
                String hyphenatedForm = tokens[0].getSurface();
                for (int j = 1; (j < tokens.length) && (tokens[j] != null); j++) {
                    hyphenatedForm += "-" + tokens[j].getSurface();
                    if (SpellingUtils.getDictionary(lang).contains(hyphenatedForm.toLowerCase())) {
                        addLocalizedErrorWithPosition(
                                "HyphenatedInDictionary",
                                sentence,
                                tokens[0].getOffset(),
                                tokens[j].getOffset() + tokens[j].getSurface().length(),
                                hyphenatedForm);
                        break;
                    }
                }
            }
        }
    }
}
