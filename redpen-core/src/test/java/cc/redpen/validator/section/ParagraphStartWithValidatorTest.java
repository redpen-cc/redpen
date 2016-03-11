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

import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Paragraph;
import cc.redpen.model.Section;
import cc.redpen.validator.ValidationError;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class ParagraphStartWithValidatorTest {
    ParagraphStartWithValidator validator = new ParagraphStartWithValidator();

    @Before
    public void setUp() throws Exception {
        validator.preInit(new ValidatorConfiguration("ParagraphStartWith").addProperty("start_from", " "), Configuration.builder().build());
    }

    @Test
    public void startWithoutSpace() {
        assertEquals(1, validateParagraphs(new Paragraph().appendSentence("it like a piece of a cake.", 1)).size());
    }

    @Test
    public void startWithSpace() {
        assertEquals(0, validateParagraphs(new Paragraph().appendSentence(" it like a piece of a cake.", 1)).size());
    }

    @Test
    public void twoParagraphs() {
        List<ValidationError> errors = validateParagraphs(
          new Paragraph().appendSentence("p1.", 1),
          new Paragraph().appendSentence("p2.", 2)
        );
        assertEquals(2, errors.size());
        assertEquals("p1.", errors.get(0).getSentence().getContent());
        assertEquals("p2.", errors.get(1).getSentence().getContent());
    }

    @Test
    public void voidParagraph() {
        assertEquals(0, validateParagraphs(new Paragraph()).size());
    }

    private List<ValidationError> validateParagraphs(Paragraph...paragraphs) {
        Section section = new Section(0);
        Stream.of(paragraphs).forEach(section::appendParagraph);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(section);
        return errors;
    }
}
