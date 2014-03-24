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
    this.appendValidator(validator);
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
    DocumentCollection documentCollection = createDocument(2);
    DocumentValidatorForTest documentValidator;
    documentValidator = new DocumentValidatorForTest();
    ValidatorForTest validator = new ValidatorForTest();
    documentValidator.addValidator(validator);
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
    documentValidator.addValidator(validator);
    documentValidator.check(documentCollection);
    assertEquals(0, validator.getProcessedSentenceNum());
  }

  @Test
  public void testRunValidatorsThrowExceptionInCheck() {
    DocumentCollection documentCollection = createDocument(1);
    DocumentValidatorForTest documentValidator;
    documentValidator = new DocumentValidatorForTest();
    ValidatorThrowExceptionInCheck validator = new ValidatorThrowExceptionInCheck();
    documentValidator.addValidator(validator);
    try {
      documentValidator.check(documentCollection);
    } catch (Throwable e) {
      fail();
    }
  }

  @Test
  public void testRunValidatorsReturnNull() {
    DocumentCollection documentCollection = createDocument(1);
    DocumentValidatorForTest documentValidator;
    documentValidator = new DocumentValidatorForTest();
    ValidatorReturnsNull validator = new ValidatorReturnsNull();
    documentValidator.addValidator(validator);
    List<ValidationError> errors = documentValidator.check(documentCollection);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }

  private DocumentCollection createDocument(int fileNum) {
    DocumentCollection documentCollection = new DocumentCollection();
    for (int i = 0; i < fileNum; i++) {
      documentCollection.appendFile(createFileContent("title" + String.valueOf(i),
          "content" + String.valueOf(i)));
   }
    return documentCollection;
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
