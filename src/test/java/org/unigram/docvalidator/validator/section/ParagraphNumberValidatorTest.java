package org.unigram.docvalidator.validator.section;

import static org.junit.Assert.*;

import java.util.Vector;

import org.junit.Test;
import org.unigram.docvalidator.store.FileContent;
import org.unigram.docvalidator.store.Paragraph;
import org.unigram.docvalidator.store.Section;
import org.unigram.docvalidator.util.FakeResultDistributor;
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
    Section section = new Section(0, "");

    section.appendParagraph(new Paragraph());
    section.appendParagraph(new Paragraph());
    section.appendParagraph(new Paragraph());
    section.appendParagraph(new Paragraph());

    FileContent fileContent = new FileContent();
    fileContent.appendChild(section);

    Vector<ValidationError> errors = validator.check(fileContent,
        new FakeResultDistributor());
    assertEquals(1, errors.size());
  }

  @Test
  public void testSectionWithOnlyOneSection() {
    ParagraphNumberValidatorForTest validator = new ParagraphNumberValidatorForTest();
    validator.setMaxNumber();

    Section section = new Section(0, "");
    section.appendParagraph(new Paragraph());

    FileContent fileContent = new FileContent();
    fileContent.appendChild(section);

    Vector<ValidationError> errors = validator.check(fileContent,
        new FakeResultDistributor());
    assertEquals(0, errors.size());
  }

}
