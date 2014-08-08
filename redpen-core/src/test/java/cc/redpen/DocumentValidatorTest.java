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
package cc.redpen;

import cc.redpen.DocumentValidator;
import cc.redpen.DocumentValidatorException;
import cc.redpen.ValidationError;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.distributor.FakeResultDistributor;
import cc.redpen.model.DocumentCollection;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

public class DocumentValidatorTest {

  @Test
  public void testEmptyValidator() throws DocumentValidatorException {

    DocumentCollection documents = new DocumentCollection.Builder()
        .addDocument("")
        .addSection(1, new ArrayList<>())
        .addParagraph()
        .addSentence(
            "In a land far away, there once was as a hungry programmer.",
            1)
        .addSentence(
            "He was hungry for programming and programmed all day - "
            + " - in Java, Python, C++, etc.", 2)
        .addSentence(
            "Whe he wasn't programming, he was eating noodles.",
            3)
        .addParagraph()
        .addSentence(
            "One day while programming, he got a new idea.", 4)
        .build();

    Configuration configuration = new Configuration.Builder().build();
    DocumentValidator validator = new DocumentValidator.Builder()
        .setConfiguration(configuration)
        .setResultDistributor(new FakeResultDistributor())
        .build();

    List<ValidationError> errors = validator.check(documents);
    assertEquals(0, errors.size());
  }


  @Test
  public void testSentenceValidatorWithSimpleDocument()
      throws DocumentValidatorException {
    DocumentCollection documents = new DocumentCollection.Builder()
        .addDocument("tested file")
        .addSection(0, new ArrayList<>())
        .addParagraph()
        .addSentence("it is a piece of a cake.", 0)
        .addSentence("that is also a piece of a cake.", 1)
        .build();

    DocumentValidator validator = getValidaorWithSentenceValidator();

    List<ValidationError> errors = validator.check(documents);

    // validate the errors
    assertEquals(2, errors.size());
    for (ValidationError error : errors) {
      assertThat(error.getValidatorName(), is("SentenceLength"));
      assertThat(error.getMessage(),
          containsString("The length of the line exceeds the maximum "));
    }
  }

  @Test
  public void testSectionValidatorWithSimpleDocument()
      throws DocumentValidatorException {
    DocumentCollection documents = new DocumentCollection.Builder()
        .addDocument("tested file")
        .addSection(0, new ArrayList<>())
        .addSectionHeader("foobar")
        .addParagraph()
        .addSentence("it is a piece of a cake.", 0)
        .addSentence("that is also a piece of a cake.", 1)
        .build();

    DocumentValidator validator = getValidaorWithSectionValidator();
    List<ValidationError> errors = validator.check(documents);

    // validate the errors
    assertEquals(1, errors.size());
    for (ValidationError error : errors) {
      assertThat(error.getValidatorName(), is("SectionLength"));
      assertThat(error.getMessage(),
          containsString("The number of the character in the section exceeds the maximum"));
    }
  }

  @Test
  public void testDocumentWithHeader() throws DocumentValidatorException {
    DocumentCollection documents = new DocumentCollection.Builder()
        .addDocument("tested file")
        .addSection(0)
        .addSectionHeader("this is it.")
        .addParagraph()
        .addSentence("it is a piece of a cake.", 0)
        .addSentence("that is also a piece of a cake.", 1)
        .build();

    DocumentValidator validator = getValidaorWithSentenceValidator();
    List<ValidationError> errors = validator.check(documents);

    // validate the errors
    assertEquals(3, errors.size());
    for (ValidationError error : errors) {
      assertThat(error.getValidatorName(), is("SentenceLength"));
      assertThat(error.getMessage(),
          containsString("The length of the line exceeds the maximum "));
    }
  }

  @Test
  public void testDocumentWithList() throws DocumentValidatorException {
    DocumentCollection documents = new DocumentCollection.Builder()
        .addDocument("tested file")
        .addSection(0, new ArrayList<>())
        .addSectionHeader("this is it")
        .addParagraph()
        .addSentence("it is a piece of a cake.", 0)
        .addSentence("that is also a piece of a cake.", 1)
        .addListBlock()
        .addListElement(0, "this is a list.")
        .build();

    DocumentValidator validator = getValidaorWithSentenceValidator();
    List<ValidationError> errors = validator.check(documents);

    // validate the errors
    assertEquals(4, errors.size());
    for (ValidationError error : errors) {
      assertThat(error.getValidatorName(), is("SentenceLength"));
      assertThat(error.getMessage(),
          containsString("The length of the line exceeds the maximum "));
    }
  }

  @Test
  public void testDocumentWithoutContent() throws DocumentValidatorException {
    DocumentCollection documents = new DocumentCollection.Builder()
        .addDocument("tested file")
        .build();

    DocumentValidator validator = getValidaorWithSentenceValidator();
    List<ValidationError> errors = validator.check(documents);

    // validate the errors
    assertEquals(0, errors.size());

  }

  private DocumentValidator getValidaorWithSentenceValidator() throws
      DocumentValidatorException {

    Configuration configuration = new Configuration.Builder()
        .addValidatorConfig(
            new ValidatorConfiguration("SentenceLength").addAttribute("max_length", "5"))
        .build();
    return new DocumentValidator.Builder()
        .setConfiguration(configuration)
        .setResultDistributor(new FakeResultDistributor())
        .build();
  }

  private DocumentValidator getValidaorWithSectionValidator() throws
      DocumentValidatorException {
    Configuration configuration = new Configuration.Builder()
        .addValidatorConfig(
            new ValidatorConfiguration("SectionLength").addAttribute("max_char_num", "5"))
        .build();
    return new DocumentValidator.Builder()
        .setConfiguration(configuration)
        .setResultDistributor(new FakeResultDistributor())
        .build();
  }
}
