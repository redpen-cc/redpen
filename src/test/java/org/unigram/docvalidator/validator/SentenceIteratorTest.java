package org.unigram.docvalidator.validator;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.unigram.docvalidator.store.FileContent;
import org.unigram.docvalidator.store.Paragraph;
import org.unigram.docvalidator.store.Section;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.Configuration;
import org.unigram.docvalidator.util.FakeResultDistributor;
import org.unigram.docvalidator.util.ValidationError;

class SentenceIteratorForTest extends SentenceIterator {
  public SentenceIteratorForTest() {
    super();
  }

  public void appendValidators(List<SentenceValidator> validators) {
    for (SentenceValidator validator : validators) {
      this.addSentenceValidator(validator);
    }
  }
}

class DummyValidator implements SentenceValidator {
  public List<ValidationError> check(Sentence sentence) {
    sentenceStrings.add(sentence.content);
    return new ArrayList<ValidationError>();
  }

  public DummyValidator() {
    super();
    sentenceStrings = new ArrayList<String>();
  }

  public boolean initialize(Configuration conf,
      CharacterTable characterTable) {
    return true;
  }

  public List<String> getSentenceStrings() {
    return sentenceStrings;
  }

  private List<String> sentenceStrings;
}

public class SentenceIteratorTest {

  @Test
  public void testSimpleDocument() {
    Section section = new Section(0);
    Paragraph paragraph = new Paragraph();
    paragraph.appendSentence("it is a piece of a cake.", 0);
    paragraph.appendSentence("that is also a piece of a cake.", 1);
    section.appendParagraph(paragraph);
    FileContent fileContent = new FileContent();
    fileContent.appendSection(section);

    SentenceIteratorForTest sentenceIterator = new SentenceIteratorForTest();
    List<SentenceValidator> validatorList = new ArrayList<SentenceValidator>();
    DummyValidator validator = new DummyValidator();
    validatorList.add(validator);
    sentenceIterator.appendValidators(validatorList);
    sentenceIterator.check(fileContent, new FakeResultDistributor());
    assertEquals(2, validator.getSentenceStrings().size());
    assertEquals("it is a piece of a cake.",
        validator.getSentenceStrings().get(0));
    assertEquals("that is also a piece of a cake.",
        validator.getSentenceStrings().get(1));
  }
}
