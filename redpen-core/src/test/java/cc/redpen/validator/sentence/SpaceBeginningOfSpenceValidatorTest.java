/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
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
package cc.redpen.validator.sentence;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.validator.ValidationError;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SpaceBeginningOfSpenceValidatorTest {
    @Test
    public void testProcessSentenceWithoutEndSpace() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("SpaceBeginningOfSentence"))
                .setLanguage("en").build();

        List<Document> documents = new ArrayList<>();
                documents.add(new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence("this is a test.", 1) // ok since the sentence begins with the beginning of the line 1
                        .addSentence("this is a test.", 1) // error in second sentence (need space)
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(1, errors.get(documents.get(0)).size());
    }

    @Test
    public void testProcessFirstSpace() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("SpaceBeginningOfSentence"))
                .setLanguage("en").build();

        List<Document> documents = new ArrayList<>();
                documents.add(new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence("this is a test", 1)
                        .addSentence(" this is a test", 1)
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void testProcessHeadSentenceInAParagraph() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("SpaceBeginningOfSentence"))
                .setLanguage("en").build();

        List<Document> documents = new ArrayList<>();
                documents.add(new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence("This is a test", 0)
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void testProcessZeroLengthSentence() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("SpaceBeginningOfSentence"))
                .setLanguage("en").build();

        List<Document> documents = new ArrayList<>();
                documents.add(new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence("", 0)
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void testProcessNewLineSentenceWithoutEndSpace() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("SpaceBeginningOfSentence"))
                .setLanguage("en").build();

        List<Document> documents = new ArrayList<>();
                documents.add(new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence("this is a test.", 1)
                        .addSentence("this is a test.", 2) // ok since the sentence start from the beginning of the line
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void testProcessVoidSentence() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("SpaceBeginningOfSentence"))
                .setLanguage("en").build();

        List<Document> documents = new ArrayList<>();
                documents.add(new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence("", 1)
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.get(0)).size());
    }
}
