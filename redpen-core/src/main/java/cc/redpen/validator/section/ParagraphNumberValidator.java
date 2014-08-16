/**
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
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
import cc.redpen.ValidationError;
import cc.redpen.config.SymbolTable;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Section;
import cc.redpen.validator.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Validate paragraph number. If a section has paragraphs more than specified,
 * This validator reports it.
 */
public class ParagraphNumberValidator implements Validator<Section> {
    /**
     * Default maximum number of paragraphs in a section.
     */
    @SuppressWarnings("WeakerAccess")
    public static final int DEFAULT_MAX_PARAGRAPHS_IN_A_SECTION = 5;
    private int maxParagraphs;

    @Override
    public List<ValidationError> validate(Section section) {
        List<ValidationError> validationErrors = new ArrayList<>();
        int paragraphNumber = section.getNumberOfParagraphs();
        if (maxParagraphs < paragraphNumber) {
            validationErrors.add(new ValidationError(
                    this.getClass(),
                    "The number of the paragraphs exceeds the maximum "
                            + String.valueOf(paragraphNumber), section.getHeaderContent(0)));
            return validationErrors;
        }
        return validationErrors;
    }

    @Override
    public void init(ValidatorConfiguration config, SymbolTable symbolTable) throws RedPenException {
        if (config.getAttribute("max_paragraph_num") == null) {
            this.maxParagraphs = DEFAULT_MAX_PARAGRAPHS_IN_A_SECTION;
        } else {
            this.maxParagraphs = Integer.valueOf(config.getAttribute("max_paragraph_num"));
        }
    }

    protected void setMaxParagraphNumber(int max) {
        this.maxParagraphs = max;
    }
}
