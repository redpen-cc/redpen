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
package org.unigram.docvalidator.validator;

import org.junit.Test;
import org.unigram.docvalidator.DocumentValidator;
import org.unigram.docvalidator.model.Document;
import org.unigram.docvalidator.model.DocumentCollection;
import org.unigram.docvalidator.model.Paragraph;
import org.unigram.docvalidator.model.Section;
import org.unigram.docvalidator.model.Sentence;
import org.unigram.docvalidator.ValidationError;
import org.unigram.docvalidator.validator.section.AbstractSectionValidator;
import org.unigram.docvalidator.validator.section.SectionValidator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

class DocumentValidatorForTest extends DocumentValidator {
  public DocumentValidatorForTest() {
    super();
  }

  public void addValidator(Validator validator) {
    this.appendValidator(validator);
  }

  public void addSectionValidator(SectionValidator validator) {
    this.appendSectionValidator(validator);
  }
}

class ValidatorForTest extends AbstractSectionValidator {
  public ValidatorForTest() {
    sentenceNum = 0;
  }

  @Override
  public List<ValidationError> validate(Section section) {

    for (Paragraph paragraph : section.getParagraphs()) {
      sentenceNum += paragraph.getNumberOfSentences();
    }
    return new ArrayList<ValidationError>();
  }

  public int getProcessedSentenceNum() {
    return sentenceNum;
  }

  private int sentenceNum;
}

class ValidatorThrowExceptionInCheck extends AbstractSectionValidator {
  public ValidatorThrowExceptionInCheck() {
  }

  @Override
  public List<ValidationError> validate(Section section) {
    throw new RuntimeException("Error occurs");
  }
}

class ValidatorReturnsNull extends AbstractSectionValidator {
  public ValidatorReturnsNull() {
  }

  @Override
  public List<ValidationError> validate(Section section) {
    return new ArrayList<ValidationError>();
  }
}

public class DocumentValidatorTest {

  @Test
  public void testRunValidators() {
    DocumentCollection documentCollection = createDocument(2);
    DocumentValidatorForTest documentValidator;
    documentValidator = new DocumentValidatorForTest();
    ValidatorForTest validator = new ValidatorForTest();
    documentValidator.addSectionValidator(validator);
    documentValidator.check(documentCollection);
    // Check if two sections in the input files are iterated
    assertEquals(2, validator.getProcessedSentenceNum());
  }

  @Test
  public void testRunValidatorsWithoutFile() {
    DocumentCollection documentCollection = createDocument(0);
    DocumentValidatorForTest documentValidator;
    documentValidator = new DocumentValidatorForTest();
    ValidatorForTest validator = new ValidatorForTest();
    documentValidator.addSectionValidator(validator);
    documentValidator.check(documentCollection);
    assertEquals(0, validator.getProcessedSentenceNum());
  }


  @Test(expected = RuntimeException.class)
  public void testRunValidatorsThrowExceptionInCheck() {
    DocumentCollection documentCollection = createDocument(1);
    DocumentValidatorForTest documentValidator;
    documentValidator = new DocumentValidatorForTest();
    ValidatorThrowExceptionInCheck validator = new
      ValidatorThrowExceptionInCheck();
    documentValidator.addSectionValidator(validator);
    documentValidator.check(documentCollection);
  }

  @Test
  public void testRunValidatorsReturnNull() {
    DocumentCollection documentCollection = createDocument(1);
    DocumentValidatorForTest documentValidator;
    documentValidator = new DocumentValidatorForTest();
    ValidatorReturnsNull validator = new ValidatorReturnsNull();
    documentValidator.addSectionValidator(validator);
    List<ValidationError> errors = documentValidator.check(documentCollection);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }

  private DocumentCollection createDocument(int fileNum) {
    DocumentCollection documentCollection = new DocumentCollection();
    for (int i = 0; i < fileNum; i++) {
      documentCollection.addDocument(createFileContent("title" + String.valueOf(i),
          "content" + String.valueOf(i)));
   }
    return documentCollection;
  }

  private Document createFileContent(String title, String content) {
    Document document = new Document();
    document.setFileName(title);
    document.appendSection(appendSection(content));
    return document;
  }

  private Section appendSection(String content) {
    Section section = new Section(0, "header");
    Paragraph paragraph = new Paragraph();
    paragraph.appendSentence(new Sentence(content, 0));
    section.appendParagraph(paragraph);
    return section;
  }
}
