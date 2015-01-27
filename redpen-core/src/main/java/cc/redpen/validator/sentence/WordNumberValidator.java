/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.redpen.validator.sentence;

import cc.redpen.RedPenException;
import cc.redpen.model.Sentence;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Validate input sentences have more words than specified.
 */
final public class WordNumberValidator extends Validator {
    /**
     * Default maximum number of words in one sentence.
     */
    @SuppressWarnings("WeakerAccess")
    public static final int DEFAULT_MAXIMUM_WORDS_IN_A_SENTENCE = 30;
    private static final Logger LOG =
            LoggerFactory.getLogger(WordNumberValidator.class);
    private int maxWordNumber = DEFAULT_MAXIMUM_WORDS_IN_A_SENTENCE;

    @Override
    public void validate(List<ValidationError> errors, Sentence sentence) {
        int wordNum = sentence.getTokens().size();
        if (wordNum > maxWordNumber) {
            errors.add(createValidationError(sentence, wordNum));
        }
    }

    @Override
    protected void init() throws RedPenException {
        this.maxWordNumber = getConfigAttributeAsInt("max_num", DEFAULT_MAXIMUM_WORDS_IN_A_SENTENCE);
    }

    @Override
    public String toString() {
        return "WordNumberValidator{" +
                "maxWordNumber=" + maxWordNumber +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WordNumberValidator that = (WordNumberValidator) o;

        return maxWordNumber == that.maxWordNumber;
    }

    @Override
    public int hashCode() {
        return maxWordNumber;
    }
}
