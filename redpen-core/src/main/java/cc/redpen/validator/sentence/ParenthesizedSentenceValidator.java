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

/**
 * Warn if too many (or overly long (or nested parenthesized sentences (where you do this))) are used in a sentence
 */
public class ParenthesizedSentenceValidator extends Validator {
    private static final String OPEN_PARENS = "(（";
    private static final String CLOSE_PARENS = ")）";

    public ParenthesizedSentenceValidator() {
        super("max_nesting_level", 1, // the limit on how many parenthesized expressions are permitted
              "max_count", 1,  // the number of parenthesized expressions allowed
              "max_length", 10); // the maximum number of words in a parenthesized expression
    }

    /**
     * Look for parenthesized expressions in the sentence and generate an error where appropriate
     * @param sentence input
     */
    @Override
    public void validate(Sentence sentence) {
        int nestingLevel = 0;
        int subsentenceLength = 0;
        int subsentenceCount = 0;

        for (TokenElement token : sentence.getTokens()) {
            if (token.getSurface().length() == 1) {
                if (OPEN_PARENS.indexOf(token.getSurface().charAt(0)) != -1) {
                    nestingLevel++;
                    if (nestingLevel > getInt("max_nesting_level")) {
                        addLocalizedErrorWithPosition(
                                "NestingLevelTooDeep",
                                sentence,
                                token.getOffset(),
                                token.getOffset() + token.getSurface().length());
                    }
                } else if (CLOSE_PARENS.indexOf(token.getSurface().charAt(0)) != -1) {
                    nestingLevel = Math.max(0, nestingLevel - 1);
                    if (nestingLevel == 0) {
                        subsentenceCount++;
                        if (subsentenceLength > getInt("max_length")) {
                            addLocalizedErrorWithPosition(
                                    "SubsentenceTooLong",
                                    sentence,
                                    token.getOffset(),
                                    token.getOffset() + token.getSurface().length());
                        }
                        subsentenceLength = 0;
                    }
                }
            }

            if (nestingLevel > 0) {
                subsentenceLength++;
            }
        }

        if (subsentenceCount > getInt("max_count")) {
            addLocalizedError("SubsentenceTooFrequent", sentence);
        }
    }
}
