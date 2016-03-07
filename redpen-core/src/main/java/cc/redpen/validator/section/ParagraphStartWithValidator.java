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
package cc.redpen.validator.section;

import cc.redpen.model.Paragraph;
import cc.redpen.model.Section;
import cc.redpen.model.Sentence;
import cc.redpen.validator.Validator;

/**
 * Validate whether paragraph start as specified.
 */
public final class ParagraphStartWithValidator extends Validator {
    public ParagraphStartWithValidator() {
        super("start_from", ""); // Default matter paragraph start with.
    }

    @Override
    public void validate(Section section) {
        for (Paragraph currentParagraph : section.getParagraphs()) {
            if (currentParagraph.getNumberOfSentences() == 0) {
                continue;
            }
            Sentence firstSentence = currentParagraph.getSentence(0);
            if (firstSentence.getContent().indexOf(getStringAttribute("start_from")) != 0) {
                addLocalizedError(section.getJoinedHeaderContents(),
                        firstSentence.getContent().charAt(0));
            }
        }
    }
}
