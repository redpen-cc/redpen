package cc.redpen.validator.section;

import cc.redpen.RedPenException;
import cc.redpen.model.Paragraph;
import cc.redpen.model.Section;
import cc.redpen.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
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
public class EmptySectionValidator extends Validator {
    private static final Logger LOG = LoggerFactory.getLogger(EmptySectionValidator.class);
    private int sectionLevelLimit = 5;

    @Override
    public void validate(Section section) {
        if (section.getLevel() >= sectionLevelLimit) {
            return;
        }

        if (section.getLevel() == 0) {
            return; // hot fix for auto generated level 0 sections.
        }
        if (section.getNumberOfParagraphs() == 0) {
            addLocalizedError(section.getJoinedHeaderContents());
        } else {
            for (Paragraph p : section.getParagraphs()) {
                if (p.getNumberOfSentences() == 0) {
                    addLocalizedError(section.getJoinedHeaderContents());
                    break;
                }
            }
        }
    }

    @Override
    protected void init() throws RedPenException {
        Optional<String> limit = getConfigAttribute("limit");
        if (limit.isPresent()) {
            sectionLevelLimit = Integer.parseInt(limit.get());
        }
    }
}
