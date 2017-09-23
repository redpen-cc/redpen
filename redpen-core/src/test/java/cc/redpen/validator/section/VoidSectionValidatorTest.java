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
package cc.redpen.validator.section;

import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.model.Sentence;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;
import cc.redpen.validator.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VoidSectionValidatorTest {
    private VoidSectionValidator validator = new VoidSectionValidator();

    @Test
    void testInvalid() {
        Document document =
                Document.builder()
                        .addSection(4)
                        .addSectionHeader("Abstract")
                        .build();

        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(document.getSection(0));

        assertEquals(1, errors.size());
    }

    @Test
    void testInvalidWithVoidSentence() {
        Document document =
                Document.builder()
                        .addSection(1)
                        .addSectionHeader("Abstract")
                        .addParagraph()
                        .build();

        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(document.getSection(0));

        assertEquals(1, errors.size());
    }

    @Test
    void testValid() {
        Document document =
                Document.builder()
                        .addSection(1)
                        .addSectionHeader("Abstract")
                        .addParagraph()
                        .addSentence(new Sentence("he is a super man.", 1))
                        .build();

        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(document.getSection(0));

        assertEquals(0, errors.size());
    }

    @Test
    void testInvalidButSmallSection() {
        Document document =
                Document.builder()
                        .addSection(7)
                        .addSectionHeader("Abstract")
                        .addParagraph()
                        .build();

        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(document.getSection(0));
        assertEquals(0, errors.size());
    }

    @Test
    void testChangeLimit() throws RedPenException {
        Document document =
                Document.builder()
                        .addSection(4)
                        .addSectionHeader("Abstract")
                        .addParagraph()
                        .build();

        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("VoidSection").addProperty("limit", "3"))
                .build();
        Validator validator = ValidatorFactory.getInstance(config.getValidatorConfigs().get(0), config);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(document.getSection(0));
        assertEquals(0, errors.size());
    }
}
