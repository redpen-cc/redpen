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

import cc.redpen.RedPenException;
import cc.redpen.model.Paragraph;
import cc.redpen.model.Section;
import cc.redpen.model.Sentence;
import cc.redpen.validator.Validator;

/**
 * Validate whether paragraph start as specified.
 */
final public class ParagraphStartWithValidator extends Validator {
    /**
     * Default matter paragraph start with.
     */
    @SuppressWarnings("WeakerAccess")
    public static final String DEFAULT_PARAGRAPH_START_WITH = " ";
    private String beginningOfParagraph = DEFAULT_PARAGRAPH_START_WITH;

    @Override
    public void validate(Section section) {
        for (Paragraph currentParagraph : section.getParagraphs()) {
            if (currentParagraph.getNumberOfSentences() == 0) {
                continue;
            }
            Sentence firstSentence = currentParagraph.getSentence(0);
            if (firstSentence.getContent().indexOf(this.beginningOfParagraph) != 0) {
                addValidationError(section.getJoinedHeaderContents(),
                        firstSentence.getContent().charAt(0));
            }
        }
    }

    @Override
    protected void init() throws RedPenException {
        this.beginningOfParagraph = getConfigAttribute("start_from", DEFAULT_PARAGRAPH_START_WITH);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParagraphStartWithValidator that = (ParagraphStartWithValidator) o;

        return !(beginningOfParagraph != null ? !beginningOfParagraph.equals(that.beginningOfParagraph) : that.beginningOfParagraph != null);

    }

    @Override
    public int hashCode() {
        return beginningOfParagraph != null ? beginningOfParagraph.hashCode() : 0;
    }
}
