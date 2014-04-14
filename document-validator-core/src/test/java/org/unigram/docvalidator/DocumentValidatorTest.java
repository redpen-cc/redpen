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
package org.unigram.docvalidator;

import org.junit.Test;
import org.unigram.docvalidator.model.Document;
import org.unigram.docvalidator.model.DocumentCollection;
import org.unigram.docvalidator.model.Paragraph;
import org.unigram.docvalidator.model.Section;
import org.unigram.docvalidator.model.Sentence;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.util.ValidatorConfiguration;

import java.util.List;

import static org.junit.Assert.assertEquals;

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


    ValidatorConfiguration config = new ValidatorConfiguration(
        "<?xml version=\"1.0\"?>\n" +
            "<character-table></character-table>"
    );
    DVResource resource = new DVResource(config); // = ValidatorConfiguration + CharacterTable

    DocumentValidator validator = new DocumentValidator.Builder()
        .setResource(resource)
//        .setConfig(...)
//        .setCharacterTable()
        .build();

    List<ValidationError> errors = validator.check(documents);

    assertEquals(0, errors.size());
  }
}
