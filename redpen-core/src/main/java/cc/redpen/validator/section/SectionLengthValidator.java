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
 * Validate the length of one section.
 */
final public class SectionLengthValidator extends Validator {
    public SectionLengthValidator() {
        super("max_num", 1000);
    }

    @Override
    public void validate(Section section) {
        int sectionCharNumber = 0;

        for (Paragraph currentParagraph : section.getParagraphs()) {
            for (Sentence sentence : currentParagraph.getSentences()) {
                sectionCharNumber += sentence.getContent().length();
            }
        }

        if (sectionCharNumber > getInt("max_num")) {
            addLocalizedError(section.getJoinedHeaderContents(), sectionCharNumber, getInt("max_num"));
        }
    }
}
