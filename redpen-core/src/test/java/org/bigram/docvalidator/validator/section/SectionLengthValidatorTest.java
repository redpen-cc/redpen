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
package org.bigram.docvalidator.validator.section;

import org.bigram.docvalidator.validator.section.SectionLengthValidator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.bigram.docvalidator.model.Paragraph;
import org.bigram.docvalidator.model.Section;
import org.bigram.docvalidator.ValidationError;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class SectionLengthValidatorTest {

  private static SectionLengthValidator validator;
  
  @BeforeClass
  public static void setUp() {
    validator = new SectionLengthValidator();
    validator.setMaxSectionLength(10);
  }
  
  @Test
  public void testSectionLength() {
    Section section = new Section(0, "header");
    Paragraph paragraph = new Paragraph();
    paragraph.appendSentence("it like a piece of a cake.", 0);
    section.appendParagraph(paragraph);
    List<ValidationError> errors = validator.validate(section);
    assertEquals(1, errors.size());
  }
}
