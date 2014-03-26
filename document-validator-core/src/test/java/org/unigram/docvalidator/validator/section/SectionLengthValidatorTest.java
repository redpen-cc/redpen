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
package org.unigram.docvalidator.validator.section;

import org.junit.Ignore;
import org.junit.Test;
import org.unigram.docvalidator.model.Document;
import org.unigram.docvalidator.model.Paragraph;
import org.unigram.docvalidator.model.Section;
import org.unigram.docvalidator.util.ValidationError;

import java.util.List;

import static org.junit.Assert.assertEquals;

class SectionLengthValidatorForTest extends SectionLengthValidator {
  void setMaxLength() {
    this.setMaxSectionLength(10);
  }
}

public class SectionLengthValidatorTest {

  @Test
  public void testSectionLength() {
    SectionLengthValidatorForTest validator = new SectionLengthValidatorForTest();
    validator.setMaxLength();

    Section section = new Section(0, "header");
    Paragraph paragraph = new Paragraph();
    paragraph.appendSentence("it like a piece of a cake.", 0);
    section.appendParagraph(paragraph);
//    Document document = new Document();
//    document.appendSection(section);
    List<ValidationError> errors = validator.validate(section);
    assertEquals(1, errors.size());
  }

}
