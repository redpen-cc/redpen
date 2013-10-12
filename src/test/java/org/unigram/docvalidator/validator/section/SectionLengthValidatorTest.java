package org.unigram.docvalidator.validator.section;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;

import org.junit.Test;
import org.unigram.docvalidator.store.FileContent;
import org.unigram.docvalidator.store.Paragraph;
import org.unigram.docvalidator.store.Section;
import org.unigram.docvalidator.util.FakeResultDistributor;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.validator.section.SectionLengthValidator;

class SectionLengthValidatorForTest extends SectionLengthValidator {
  void setMaxLength() {
    this.maxSectionCharNumber = 10;
  }
}

public class SectionLengthValidatorTest {

  @Test
  public void testSectionLength() {
    SectionLengthValidatorForTest validator = new SectionLengthValidatorForTest();
    validator.setMaxLength();

    Section section = new Section(0, "");
    Paragraph paragraph = new Paragraph();
    paragraph.appendSentence("it like a piece of a cake.", 0);
    section.appendParagraph(paragraph);
    FileContent fileContent = new FileContent();
    fileContent.appendChild(section);

    List<ValidationError> errors = validator.check(fileContent, new FakeResultDistributor());
    assertEquals(1, errors.size());
  }

}
