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
    // NOTE: throw a exception when input sentence when sentence is zero length.
    if ("".equals(sentence.content)) {
      throw new RuntimeException("The content of input sentence is null");
    }
    sentenceStrings.add(sentence.content);
    List<ValidationError> errors = new ArrayList<ValidationError>();
    errors.add(new ValidationError(this.getClass(), "Error occurs", sentence));
    return errors;
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
    Document document = generateFileContent("tested file");
    String [] sentences = {"it is a piece of a cake.",
        "that is also a piece of a cake."};
    addSentences(document, sentences);

    DummyValidator validator = new DummyValidator();
    SentenceIterator sentenceIterator = generateSentenceIterator(validator);
    List<ValidationError> errors =
        sentenceIterator.check(document, new FakeResultDistributor());

    // check the iterated sentences
    assertEquals(2, validator.getSentenceStrings().size());
    assertEquals("it is a piece of a cake.",
        validator.getSentenceStrings().get(0));
    assertEquals("that is also a piece of a cake.",
        validator.getSentenceStrings().get(1));

    // check the errors
    assertEquals(2, errors.size());
    for (ValidationError error : errors) {
      assertEquals("tested file", error.getFileName());
      assertEquals("Error occurs",error.getMessage());
    }
  }

  @Test
  public void testFileContentWithHeader() {
    Document document = generateFileContent("tested file");
    String [] sentences = {"it is a piece of a cake.",
        "that is also a piece of a cake."};
    addSentences(document, sentences);
    addHeader(document, "this is it");

    DummyValidator validator = new DummyValidator();
    SentenceIterator sentenceIterator = generateSentenceIterator(validator);
    List<ValidationError> errors =
        sentenceIterator.check(document, new FakeResultDistributor());

    assertEquals(3, validator.getSentenceStrings().size());
    assertEquals("it is a piece of a cake.",
        validator.getSentenceStrings().get(0));
    assertEquals("that is also a piece of a cake.",
        validator.getSentenceStrings().get(1));
    assertEquals("this is it",
        validator.getSentenceStrings().get(2));

    assertEquals(3, errors.size());
    for (ValidationError error : errors) {
      assertEquals("tested file", error.getFileName());
      assertEquals("Error occurs",error.getMessage());
    }
  }

  @Test
  public void testFileContentWithList() {
    Document document = generateFileContent("tested file");
    String [] sentences = {"it is a piece of a cake.",
        "that is also a piece of a cake."};
    addSentences(document, sentences);
    addHeader(document, "this is it");
    String [] lists = {"this is a list."};
    addList(document, lists);

    DummyValidator validator = new DummyValidator();
    SentenceIterator sentenceIterator = generateSentenceIterator(validator);
    List<ValidationError> errors =
        sentenceIterator.check(document, new FakeResultDistributor());

    assertEquals(4, validator.getSentenceStrings().size());
    assertEquals("it is a piece of a cake.",
        validator.getSentenceStrings().get(0));
    assertEquals("that is also a piece of a cake.",
        validator.getSentenceStrings().get(1));
    assertEquals("this is it",
        validator.getSentenceStrings().get(2));
    assertEquals("this is a list.",
        validator.getSentenceStrings().get(3));

    assertEquals(4, errors.size());
    for (ValidationError error : errors) {
      assertEquals("tested file", error.getFileName());
      assertEquals("Error occurs",error.getMessage());
    }
  }

  @Test
  public void testFileContentWithoutContent() {
    Document document = generateFileContent("tested file");
    String [] sentences = {};
    addSentences(document, sentences);

    DummyValidator validator = new DummyValidator();
    SentenceIterator sentenceIterator = generateSentenceIterator(validator);
    sentenceIterator.check(document, new FakeResultDistributor());

    assertEquals(0, validator.getSentenceStrings().size());
  }

  @Test
  public void testNoExceptionFromSentenceValidator() {
    Document document = generateFileContent("tested file");
    String [] sentences = {""};
    addSentences(document, sentences);

    DummyValidator validator = new DummyValidator();
    SentenceIterator sentenceIterator = generateSentenceIterator(validator);
    try {
      sentenceIterator.check(document, new FakeResultDistributor());
    } catch (Throwable e) {
      fail();
    }
    assertEquals(0, validator.getSentenceStrings().size());
  }

  private SentenceIterator generateSentenceIterator(SentenceValidator validator) {
    SentenceIteratorForTest sentenceIterator = new SentenceIteratorForTest();
    List<SentenceValidator> validatorList = new ArrayList<SentenceValidator>();
    validatorList.add(validator);
    sentenceIterator.appendValidators(validatorList);
    return sentenceIterator;
  }

  private void addSentences(Document document, String[] sentences) {
    if (document.getNumberOfSections() == 0) {
      document.appendSection(new Section(0));
    }
    Section section = document.getSection(0);
    Paragraph paragraph = new Paragraph();
    for (int i = 0; i < sentences.length; i++) {
      paragraph.appendSentence(sentences[i], i);
    }
    section.appendParagraph(paragraph);
  }

  private void addHeader(Document document, String header) {
    if (document.getNumberOfSections() == 0) {
      document.appendSection(new Section(0));
    }
    Section section = document.getSection(0);
    List<Sentence> headers = new ArrayList<Sentence>();
    headers.add(new Sentence(header, 0));
    section.appendHeaderContent(headers);
  }

  private void addList(Document document, String[] listElements) {
    if (document.getNumberOfSections() == 0) {
      document.appendSection(new Section(0));
    }
    Section section = document.getSection(0);
    List<Sentence> listContents = new ArrayList<Sentence>();
    for (int i = 0; i < listElements.length; i++) {
      listContents.add(new Sentence(listElements[i],i));
    }
    section.appendListBlock();
    section.appendListElement(0, listContents);
  }

  private Document generateFileContent(String fileName) {
    Document document = new Document();
    document.setFileName(fileName);
    return document;
  }
}
