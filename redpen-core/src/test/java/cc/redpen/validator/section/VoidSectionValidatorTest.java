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
import cc.redpen.model.Section;
import cc.redpen.model.Sentence;
import cc.redpen.validator.ValidationError;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class VoidSectionValidatorTest {
    private VoidSectionValidator validator = new VoidSectionValidator();

    @Test
    public void testInvalid() {
        Document document =
                Document.builder()
                        .addSection(1)
                        .addSectionHeader("Abstract")
                        .build();

        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(document.getSection(0));

        assertEquals(1, errors.size());
    }

    @Test
    public void testValid() {
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
}
