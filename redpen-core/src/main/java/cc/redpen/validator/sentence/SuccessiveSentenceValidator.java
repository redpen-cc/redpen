/*
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
import cc.redpen.util.LevenshteinDistance;
import cc.redpen.validator.Validator;

public class SuccessiveSentenceValidator extends Validator {
    private Sentence prevSentence;

    public SuccessiveSentenceValidator() {
        super(
                "dist", 3,// default threshold of minimum distance
                "min_len", 5); // default threshold of minimum sentence length
        prevSentence = new Sentence("", 1, 0);
    }

    @Override
    protected void init() throws RedPenException {
        super.init();
    }

    @Override
    public void validate(Sentence sentence) {
        if (isSame(sentence, prevSentence)) {
            addLocalizedError(sentence, prevSentence.getContent(), sentence.getContent());
        }
        prevSentence = sentence;
    }

    private boolean isSame(Sentence sentence, Sentence prevSentence) {
        // pre check
        if (sentence.getContent().length() < getInt("min_len")) {
            return false;
        }
        if (sentence.getContent().toLowerCase().equals(prevSentence.getContent().toLowerCase())) {
            return true;
        }
        // check with edit distance
        if (LevenshteinDistance.getDistance(sentence.getContent(), prevSentence.getContent()) < getInt("dist")) {
            return true;
        } else {
            return false;
        }
    }
}
