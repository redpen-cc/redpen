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

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.unigram.docvalidator.store.Document;
import org.unigram.docvalidator.store.Paragraph;
import org.unigram.docvalidator.store.Section;
import org.unigram.docvalidator.util.FakeResultDistributor;
import org.unigram.docvalidator.util.ResultDistributor;
import org.unigram.docvalidator.util.ValidationError;

class ParagraphNumberValidatorForTest extends ParagraphNumberValidator {
  public void setMaxNumber() {
    this.setMaxParagraphNumber(3);
  }
}


public class ParagraphNumberValidatorTest {

  @Test
  public void testSectionWithManySection() {
    ParagraphNumberValidatorForTest validator = new ParagraphNumberValidatorForTest();
    validator.setMaxNumber();
    Section section = new Section(0, "header");

    section.appendParagraph(new Paragraph());
    section.appendParagraph(new Paragraph());
    section.appendParagraph(new Paragraph());
    section.appendParagraph(new Paragraph());

    Document document = new Document();
    document.appendSection(section);

    ResultDistributor distributor = new FakeResultDistributor();
    List<ValidationError> errors = validator.check(document,
        distributor);
    assertEquals(1, errors.size());
  }

  @Test
  public void testSectionWithOnlyOneSection() {
    ParagraphNumberValidatorForTest validator = new ParagraphNumberValidatorForTest();
    validator.setMaxNumber();

    Section section = new Section(0);
    section.appendParagraph(new Paragraph());

    Document document = new Document();
    document.appendSection(section);

    ResultDistributor distributor = new FakeResultDistributor();
    List<ValidationError> errors = validator.check(document,
        distributor);
    assertEquals(0, errors.size());
  }

}
