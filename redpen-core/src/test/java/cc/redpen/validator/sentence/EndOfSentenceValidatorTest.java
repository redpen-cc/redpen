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
import cc.redpen.parser.LineOffset;
import cc.redpen.parser.SentenceExtractor;
import cc.redpen.tokenizer.JapaneseTokenizer;
import cc.redpen.validator.ValidationError;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class EndOfSentenceValidatorTest {
    @Test
    public void testInvalidEndOfSentence() {
        EndOfSentenceValidator validator = new EndOfSentenceValidator();
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(new Sentence("He said \"that is right\".", 0));
        assertEquals(1, errors.size());
    }

    @Test
    public void testValidEndOfSentence() {
        EndOfSentenceValidator validator = new EndOfSentenceValidator();
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(new Sentence("He said \"that is right.\"", 0));
        assertEquals(0, errors.size());
    }

    @Test
    public void testInValidEndOfSentenceWithQuestionMark() {
        EndOfSentenceValidator validator = new EndOfSentenceValidator();
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate( new Sentence("He said \"Is it right\"?", 0));
        assertEquals(1, errors.size());
    }

    @Test
    public void testVoid() {
        EndOfSentenceValidator validator = new EndOfSentenceValidator();
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(new Sentence("", 0));
        assertEquals(0, errors.size());
    }

    @Test
    public void testJapaneseInvalidEndOfSentence() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("EndOfSentence"))
                .setLanguage("ja").build();

        List<Document> documents = new ArrayList<>();documents.add(
                new Document.DocumentBuilder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence("彼は言った，“今日は誕生日”。", 1)
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(1, errors.get(documents.get(0)).size());
    }

    @Test
    public void testErrorPosition() throws RedPenException {
        String sampleText = "He said \"that is right\".";
        Configuration configuration = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(
                        new ValidatorConfiguration("EndOfSentence"))
                .build();
        DocumentParser parser = DocumentParser.MARKDOWN;
        List<Document> documents = new ArrayList<>();
        Document document  = parser.parse(sampleText, new SentenceExtractor(configuration.getSymbolTable()),
                configuration.getTokenizer());
        documents.add(document);

        RedPen redPen = new RedPen(configuration);
        List<ValidationError> errors = redPen.validate(documents).get(documents.get(0));
        assertEquals(1, errors.size());
        assertEquals("EndOfSentence", errors.get(0).getValidatorName());
        assertEquals(new LineOffset(1, 22), errors.get(0).getStartPosition().get());
        assertEquals(new LineOffset(1, 23), errors.get(0).getEndPosition().get());
    }
}
