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

import cc.redpen.model.Sentence;
import cc.redpen.validator.Validator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Validate input sentences except for first sentence of a paragraph start with
 * a space.
 */

/**
 * This validator became Deprecated see the following pull request
 *
 * https://github.com/redpen-cc/redpen/pull/720
 */
@Deprecated
public final class SpaceBeginningOfSentenceValidator extends Validator {
    private Map<Integer, List<Sentence>> sentencePositions = new HashMap<>();

    private boolean isFirstInLine(Sentence sentence) {
        return sentence.isFirstSentence() || sentencePositions.get(sentence.getLineNumber()).get(0) == sentence;
    }

    @Override
    public void validate(Sentence sentence) {
        String content = sentence.getContent();
        if (!isFirstInLine(sentence) && content.length() > 0 && content.charAt(0) != ' ') {
            addLocalizedErrorWithPosition(sentence, 0, 1);
        }
    }

    @Override
    public void preValidate(Sentence sentence) {
        if (!sentencePositions.containsKey(sentence.getLineNumber())) {
            sentencePositions.put(sentence.getLineNumber(), new LinkedList<>());
        }
        List<Sentence> list = sentencePositions.get(sentence.getLineNumber());
        list.add(sentence);
    }
}
