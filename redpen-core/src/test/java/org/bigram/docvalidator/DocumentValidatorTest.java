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
package org.bigram.docvalidator;

import org.apache.commons.io.input.ReaderInputStream;
import org.bigram.docvalidator.DocumentValidator;
import org.bigram.docvalidator.DocumentValidatorException;
import org.bigram.docvalidator.ValidationError;
import org.junit.Test;
import org.bigram.docvalidator.config.Configuration;
import org.bigram.docvalidator.config.ValidationConfigurationLoader;
import org.bigram.docvalidator.config.ValidatorConfiguration;
import org.bigram.docvalidator.model.Document;
import org.bigram.docvalidator.model.DocumentCollection;
import org.bigram.docvalidator.model.Paragraph;
import org.bigram.docvalidator.model.Section;
import org.bigram.docvalidator.model.Sentence;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

public class DocumentValidatorTest {

  @Test
  public void testEmptyValidator() throws DocumentValidatorException {

    DocumentCollection documents = new DocumentCollection();

    Document document = new Document();

    Section section1 = new Section(1);

    Paragraph paragraph1 = new Paragraph();
    paragraph1.appendSentence(
        new Sentence(
            "In a land far away, there once was as a hungry programmer.",
            1
        )
    );
    paragraph1.appendSentence(
        new Sentence("He was hungry for programming and programmed all day - "
            + " - in Java, Python, C++, etc.",
            2
        )
    );

    paragraph1.appendSentence(
        new Sentence("Whe he wasn't programming, he was eating noodles.",
            3
        )
    );

    Paragraph paragraph2 = new Paragraph();
    paragraph1.appendSentence(
        new Sentence("One day while programming, he got a new idea.",
            4
        )
    );

    section1.appendParagraph(paragraph1);
    section1.appendParagraph(paragraph2);


    document.appendSection(section1);

    documents.addDocument(document);


    ValidatorConfiguration validatorConfig = new ValidatorConfiguration(
        "<?xml version=\"1.0\"?>\n" +
            "<character-table></character-table>"
    );
    Configuration configuration = new Configuration(
        validatorConfig); // = ValidatorConfiguration + CharacterTable

    DocumentValidator validator = new DocumentValidator.Builder()
        .setConfiguration(configuration)
//        .setConfig(...)
//        .setCharacterTable()
        .build();

    List<ValidationError> errors = validator.check(documents);

    assertEquals(0, errors.size());
  }


  @Test
  public void testSimpleDocument() throws DocumentValidatorException {

    DocumentCollection documents = new DocumentCollection();

    Document document = new Document();
    document.setFileName("tested file");
    Section section0 = new Section(0);
    Paragraph paragraph0 = new Paragraph();
    paragraph0.appendSentence(
        new Sentence("it is a piece of a cake.", 0)
    );
    paragraph0.appendSentence(
        new Sentence("that is also a piece of a cake.", 1)
    );
    section0.appendParagraph(paragraph0);
    document.appendSection(section0);
    documents.addDocument(document);

    DocumentValidator validator = getDocumentValidator();

    List<ValidationError> errors = validator.check(documents);

    // validate the errors
    assertEquals(2, errors.size());
    for (ValidationError error : errors) {
      assertThat(error.getValidatorName(), is("SentenceLength"));
      assertThat(error.getMessage(),
          containsString("The length of the line exceeds the maximum "));
    }
  }

  private DocumentValidator getDocumentValidator() throws
      DocumentValidatorException {
    ValidatorConfiguration validatorConfig =
        ValidationConfigurationLoader.loadConfiguration(
            new ReaderInputStream(new StringReader("<?xml version=\"1.0\"?>\n" +
                "<component name=\"Validator\">" +
                "  <component name=\"SentenceIterator\">" +
                "    <component name=\"SentenceLength\">\n" +
                "      <property name=\"max_length\" value=\"5\"/>\n" +
                "    </component>" +
                "  </component>" +
                "</component>"
            ))
        );

    Configuration configuration = new Configuration(validatorConfig);

    return new DocumentValidator.Builder()
        .setConfiguration(configuration)
        .build();
  }

  @Test
  public void testDocumentWithHeader() throws DocumentValidatorException {

    DocumentCollection documents = new DocumentCollection();

    Document document = new Document();
    document.setFileName("tested file");
    Section section0 = new Section(0);
    Paragraph paragraph0 = new Paragraph();
    paragraph0.appendSentence(
        new Sentence("it is a piece of a cake.", 0)
    );
    paragraph0.appendSentence(
        new Sentence("that is also a piece of a cake.", 1)
    );
    section0.appendParagraph(paragraph0);
    List<Sentence> headers = new ArrayList<Sentence>(1);
    headers.add(new Sentence("this is it", 0));
    section0.appendHeaderContent(headers);
    document.appendSection(section0);
    documents.addDocument(document);

    DocumentValidator validator = getDocumentValidator();

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
    DocumentCollection documents = new DocumentCollection();

    Document document = new Document();
    document.setFileName("tested file");
    Section section0 = new Section(0);
    Paragraph paragraph0 = new Paragraph();
    paragraph0.appendSentence(
        new Sentence("it is a piece of a cake.", 0)
    );
    paragraph0.appendSentence(
        new Sentence("that is also a piece of a cake.", 1)
    );
    section0.appendParagraph(paragraph0);

    List<Sentence> headers = new ArrayList<Sentence>(1);
    headers.add(new Sentence("this is it", 0));
    section0.appendHeaderContent(headers);

    List<Sentence> listElements = new ArrayList<Sentence>(1);
    listElements.add(new Sentence("this is a list.", 0));
    section0.appendListBlock();
    section0.appendListElement(0, listElements);

    document.appendSection(section0);
    documents.addDocument(document);

    DocumentValidator validator = getDocumentValidator();

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
    DocumentCollection documents = new DocumentCollection();

    Document document = new Document();
    document.setFileName("tested file");
    documents.addDocument(document);

    DocumentValidator validator = getDocumentValidator();

    List<ValidationError> errors = validator.check(documents);

    // validate the errors
    assertEquals(0, errors.size());

  }
}
