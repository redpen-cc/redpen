package org.unigram.docvalidator.validator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.unigram.docvalidator.store.*;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.util.ValidatorConfiguration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class DocumentValidatorForTest extends DocumentValidator {
  public DocumentValidatorForTest() {
    super();
  }

  public void addValidator(Validator validator) {
    this.validators.add(validator);
  }
}

class ValidatorForTest extends SectionValidator {
  public ValidatorForTest() {
    sentenceNum = 0;
  }

  @Override
  public boolean loadConfiguration(ValidatorConfiguration conf,
                                   CharacterTable characterTable) {
    return false;
  }

  @Override
  public List<ValidationError> check(Section section) {
    Iterator<Paragraph> paragraphs = section.getParagraphs();
    while(paragraphs.hasNext()) {
      Paragraph paragraph = paragraphs.next();
      sentenceNum += paragraph.getNumberOfSentences();
    }
    return new ArrayList<ValidationError>();
  }

  public int getProcessedSentenceNum() {
    return sentenceNum;
  }

  private int sentenceNum;
}

class ValidatorThrowExceptionInCheck extends SectionValidator {
  public ValidatorThrowExceptionInCheck() {}

  @Override
  public boolean loadConfiguration(ValidatorConfiguration conf,
                                   CharacterTable characterTable) {
    return false;
  }

  @Override
  public List<ValidationError> check(Section section) {
    throw new RuntimeException("Error occurs");
  }
}

class ValidatorReturnsNull extends SectionValidator {
  public ValidatorReturnsNull() {}

  @Override
  public boolean loadConfiguration(ValidatorConfiguration conf,
                                   CharacterTable characterTable) {
    return false;
  }

  @Override
  public List<ValidationError> check(Section section) {
    return null;
  }
}

public class DocumentValidatorTest {
  @Test
  public void testRunValidators() {
    Document document = createDocument(2);
    DocumentValidatorForTest documentValidator;
    documentValidator = new DocumentValidatorForTest();
    ValidatorForTest validator = new ValidatorForTest();
    documentValidator.addValidator(validator);
    documentValidator.check(document);
    // Check if two sections in the input files are iterated
    assertEquals(2, validator.getProcessedSentenceNum());
  }

  @Test
  public void testRunValidatorsWithoutFile() {
    Document document = createDocument(0);
    DocumentValidatorForTest documentValidator;
    documentValidator = new DocumentValidatorForTest();
    ValidatorForTest validator = new ValidatorForTest();
    documentValidator.addValidator(validator);
    documentValidator.check(document);
    assertEquals(0, validator.getProcessedSentenceNum());
  }

  @Test
  public void testRunValidatorsThrowExceptionInCheck() {
    Document document = createDocument(1);
    DocumentValidatorForTest documentValidator;
    documentValidator = new DocumentValidatorForTest();
    ValidatorThrowExceptionInCheck validator = new ValidatorThrowExceptionInCheck();
    documentValidator.addValidator(validator);
    try {
      documentValidator.check(document);
    } catch (Throwable e) {
      fail();
    }
  }

  @Test
  public void testRunValidatorsReturnNull() {
    Document document = createDocument(1);
    DocumentValidatorForTest documentValidator;
    documentValidator = new DocumentValidatorForTest();
    ValidatorReturnsNull validator = new ValidatorReturnsNull();
    documentValidator.addValidator(validator);
    List<ValidationError> errors = documentValidator.check(document);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }

  private Document createDocument(int fileNum) {
    Document document = new Document();
    for (int i = 0; i < fileNum; i++) {
      document.appendFile(createFileContent("title" + String.valueOf(i),
          "content" + String.valueOf(i)));
   }
    return document;
  }

  private FileContent createFileContent(String title, String content) {
    FileContent fileContent = new FileContent();
    fileContent.setFileName(title);
    fileContent.appendSection(appendSection(content));
    return fileContent;
  }

  private Section appendSection(String content) {
    Section section = new Section(0, "header");
    Paragraph paragraph = new Paragraph();
    paragraph.appendSentence(new Sentence(content,0));
    section.appendParagraph(paragraph);
    return section;
  }
}
