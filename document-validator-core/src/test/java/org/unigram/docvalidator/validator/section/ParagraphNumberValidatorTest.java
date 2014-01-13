/**
 * DocumentValidator
 * Copyright (c) 2013-, Takahiko Ito, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package org.unigram.docvalidator.validator.section;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.unigram.docvalidator.store.FileContent;
import org.unigram.docvalidator.store.Paragraph;
import org.unigram.docvalidator.store.Section;
import org.unigram.docvalidator.util.FakeResultDistributor;
import org.unigram.docvalidator.util.ResultDistributor;
import org.unigram.docvalidator.util.ValidationError;

class ParagraphNumberValidatorForTest extends ParagraphNumberValidator {
  public void setMaxNumber() {
    this.maxParagraphs = 3;
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

    FileContent fileContent = new FileContent();
    fileContent.appendSection(section);

    ResultDistributor distributor = new FakeResultDistributor();
    List<ValidationError> errors = validator.check(fileContent,
        distributor);
    assertEquals(1, errors.size());
  }

  @Test
  public void testSectionWithOnlyOneSection() {
    ParagraphNumberValidatorForTest validator = new ParagraphNumberValidatorForTest();
    validator.setMaxNumber();

    Section section = new Section(0);
    section.appendParagraph(new Paragraph());

    FileContent fileContent = new FileContent();
    fileContent.appendSection(section);

    ResultDistributor distributor = new FakeResultDistributor();
    List<ValidationError> errors = validator.check(fileContent,
        distributor);
    assertEquals(0, errors.size());
  }

}
