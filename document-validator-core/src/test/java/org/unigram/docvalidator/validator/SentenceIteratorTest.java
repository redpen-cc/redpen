package org.unigram.docvalidator.validator;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.unigram.docvalidator.store.*;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.FakeResultDistributor;
import org.unigram.docvalidator.util.ValidatorConfiguration;
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

  public boolean initialize(ValidatorConfiguration conf,
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
  public void testSimpleFileContent() {
    FileContent fileContent = new FileContent();
    String [] sentences = {"it is a piece of a cake.",
        "that is also a piece of a cake."};
    addSentences(fileContent, sentences);

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

  @Test
  public void testDocumentWithHeader() {
    FileContent fileContent = new FileContent();
    String [] sentences = {"it is a piece of a cake.",
        "that is also a piece of a cake."};
    addSentences(fileContent, sentences);
    addHeader(fileContent, "this is it");

    SentenceIteratorForTest sentenceIterator = new SentenceIteratorForTest();
    List<SentenceValidator> validatorList = new ArrayList<SentenceValidator>();
    DummyValidator validator = new DummyValidator();
    validatorList.add(validator);
    sentenceIterator.appendValidators(validatorList);
    sentenceIterator.check(fileContent, new FakeResultDistributor());

    assertEquals(3, validator.getSentenceStrings().size());
  }

  @Test
  public void testDocumentWithList() {
    FileContent fileContent = new FileContent();
    String [] sentences = {"it is a piece of a cake.",
        "that is also a piece of a cake."};
    addSentences(fileContent, sentences);
    addHeader(fileContent, "this is it");
    String [] lists = {"this is a list."};
    addList(fileContent, lists);

    DummyValidator validator = new DummyValidator();
    SentenceIterator sentenceIterator = generateSentenceIterator(validator);
    sentenceIterator.check(fileContent, new FakeResultDistributor());

    assertEquals(4, validator.getSentenceStrings().size());
  }

  private SentenceIterator generateSentenceIterator(SentenceValidator validator) {
    SentenceIteratorForTest sentenceIterator = new SentenceIteratorForTest();
    List<SentenceValidator> validatorList = new ArrayList<SentenceValidator>();
    validatorList.add(validator);
    sentenceIterator.appendValidators(validatorList);
    return sentenceIterator;
  }

  private void addSentences(FileContent fileContent, String[] sentences) {
    if (fileContent.getNumberOfSections() == 0) {
      fileContent.appendSection(new Section(0));
    }
    Section section = fileContent.getSection(0);
    Paragraph paragraph = new Paragraph();
    for (int i = 0; i < sentences.length; i++) {
      paragraph.appendSentence(sentences[i], i);
    }
    section.appendParagraph(paragraph);
  }

  private void addHeader(FileContent fileContent, String header) {
    if (fileContent.getNumberOfSections() == 0) {
      fileContent.appendSection(new Section(0));
    }
    Section section = fileContent.getSection(0);
    List<Sentence> headers = new ArrayList<Sentence>();
    headers.add(new Sentence(header, 0));
    section.setHeaderContent(headers);
  }

  private void addList(FileContent fileContent, String[] listElements) {
    if (fileContent.getNumberOfSections() == 0) {
      fileContent.appendSection(new Section(0));
    }
    Section section = fileContent.getSection(0);
    List<Sentence> listContents = new ArrayList<Sentence>();
    for (int i = 0; i < listElements.length; i++) {
      listContents.add(new Sentence(listElements[i],i));
    }
    section.appendListBlock();
    section.appendListElement(0, listContents);
  }
}
