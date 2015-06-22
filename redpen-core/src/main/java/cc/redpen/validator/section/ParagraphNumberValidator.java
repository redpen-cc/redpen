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
import cc.redpen.model.Section;
import cc.redpen.validator.Validator;

/**
 * Validate paragraph number. If a section has paragraphs more than specified,
 * This validator reports it.
 */
final public class ParagraphNumberValidator extends Validator {
    /**
     * Default maximum number of paragraphs in a section.
     */
    @SuppressWarnings("WeakerAccess")
    public static final int DEFAULT_MAX_PARAGRAPHS_IN_A_SECTION = 5;
    private int maxParagraphs;

    @Override
    public void validate(Section section) {
        int paragraphNumber = section.getNumberOfParagraphs();
        if (maxParagraphs < paragraphNumber) {
            addValidationError(section.getJoinedHeaderContents(), paragraphNumber);
        }
    }

    @Override
    protected void init() throws RedPenException {
        this.maxParagraphs = getConfigAttributeAsInt("max_num", DEFAULT_MAX_PARAGRAPHS_IN_A_SECTION);
    }

    @Override
    public String toString() {
        return "ParagraphNumberValidator{" +
                "maxParagraphs=" + maxParagraphs +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParagraphNumberValidator that = (ParagraphNumberValidator) o;

        return maxParagraphs == that.maxParagraphs;
    }

    @Override
    public int hashCode() {
        return maxParagraphs;
    }

    protected void setMaxParagraphNumber(int max) {
        this.maxParagraphs = max;
    }
}
