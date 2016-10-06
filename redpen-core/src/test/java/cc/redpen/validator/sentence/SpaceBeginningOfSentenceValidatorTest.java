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
import cc.redpen.model.Sentence;
import cc.redpen.parser.DocumentParser;
import cc.redpen.parser.SentenceExtractor;
import cc.redpen.validator.ValidationError;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SpaceBeginningOfSentenceValidatorTest {
    @Test
    public void testProcessSentenceWithoutEndSpace() throws RedPenException {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SpaceBeginningOfSentence"))
                .build();

        List<Document> documents = new ArrayList<>();
                documents.add(Document.builder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("this is a test.", 1)) // ok since the sentence begins with the beginning of the line 1
                        .addSentence(new Sentence("this is a test.", 1)) // error in second sentence (need space)
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(1, errors.get(documents.get(0)).size());
    }

    @Test
    public void testProcessFirstSpace() throws RedPenException {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SpaceBeginningOfSentence"))
                .build();

        List<Document> documents = new ArrayList<>();
                documents.add(Document.builder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("this is a test", 1))
                        .addSentence(new Sentence(" this is a test", 1))
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void testProcessHeadSentenceInAParagraph() throws RedPenException {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SpaceBeginningOfSentence"))
                .build();

        List<Document> documents = new ArrayList<>();
                documents.add(Document.builder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("This is a test", 1))
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void testProcessZeroLengthSentence() throws RedPenException {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SpaceBeginningOfSentence"))
                .build();

        List<Document> documents = new ArrayList<>();
        documents.add(Document.builder()
                .addSection(1)
                .addParagraph()
                .addSentence(new Sentence("", 0))
                .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void testProcessBasicSentences() throws RedPenException {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SpaceBeginningOfSentence"))
                .build();

        List<Document> documents = new ArrayList<>();
        documents.add(Document.builder()
                .addSection(1)
                .addParagraph()
                .addSentence(new Sentence("", 0))
                .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void testProcessNewLineSentenceWithoutEndSpace() throws RedPenException {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SpaceBeginningOfSentence"))
                .build();

        List<Document> documents = new ArrayList<>();
                documents.add(Document.builder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("this is a test.", 1))
                        .addSentence(new Sentence("this is a test.", 2)) // ok since the sentence start from the beginning of the line
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void testProcessVoidSentence() throws RedPenException {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SpaceBeginningOfSentence"))
                .build();

        List<Document> documents = new ArrayList<>();
                documents.add(Document.builder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("", 1))
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void testWithMarkdownParser() throws Exception {
        String sampleText =
                "This is a pen.this is a pen.";
        Configuration conf = Configuration.
                builder("en")
                .addValidatorConfig(new ValidatorConfiguration("SpaceBeginningOfSentence")).
                build();
        List<Document> documents = new ArrayList<>();
        try {
            documents.add(createFileContent(sampleText, conf));
        } catch (Exception e) {
            fail();
        }
        RedPen redPen = new RedPen(conf);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(1, errors.get(documents.get(0)).size());
    }

    private Document createFileContent(String inputDocumentString, Configuration conf) {
        DocumentParser parser = DocumentParser.MARKDOWN;
        Document doc = null;
        try {
            doc = parser.parse(inputDocumentString, new SentenceExtractor(conf.getSymbolTable()),
                    conf.getTokenizer());
        } catch (RedPenException e) {
            e.printStackTrace();
            fail();
        }
        return doc;
    }
}
