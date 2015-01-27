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

public class SpaceBetweenAlphabeticalWordValidatorTest {
    @Test
    public void testNeedBeforeSpace() {
        SpaceBetweenAlphabeticalWordValidator validator = new SpaceBetweenAlphabeticalWordValidator();
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, new Sentence("きょうはCoke を飲みたい。", 0));
        assertEquals(1, errors.size());
    }

    @Test
    public void testNeedAfterSpace() {
        SpaceBetweenAlphabeticalWordValidator validator = new SpaceBetweenAlphabeticalWordValidator();
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, new Sentence("きょうは Cokeを飲みたい。", 0));
        assertEquals(1, errors.size());
    }

    @Test
    public void testNeedBeforeAndAfterSpace() {
        SpaceBetweenAlphabeticalWordValidator validator = new SpaceBetweenAlphabeticalWordValidator();
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, new Sentence("きょうはCokeを飲みたい。", 0));
        assertEquals(2, errors.size());
    }

    @Test
    public void testNotNeedSpaces() {
        SpaceBetweenAlphabeticalWordValidator validator = new SpaceBetweenAlphabeticalWordValidator();
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, new Sentence("This Coke is cold", 0));
        assertEquals(0, errors.size());
    }

    @Test
    public void testLatinSymbolWithoutSpace() {
        SpaceBetweenAlphabeticalWordValidator validator = new SpaceBetweenAlphabeticalWordValidator();
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, new Sentence("きょうは,コーラを飲みたい。", 0));
        assertEquals(0, errors.size());
    }

    @Test
    public void testWithParenthesis() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("SpaceBetweenAlphabeticalWord"))
                .setLanguage("ja").build();

        List<Document> documents = new ArrayList<>();documents.add(
                new Document.DocumentBuilder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence("きょうは（Coke）を飲みたい。", 1)
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void testWithComma() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("SpaceBetweenAlphabeticalWord"))
                .setLanguage("ja").build();

        List<Document> documents = new ArrayList<>();documents.add(
                new Document.DocumentBuilder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence("きょうは、Coke を飲みたい。", 1)
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void testErrorBeforePosition() throws RedPenException {
        String sampleText = "きょうはCoke を飲みたい。";
        Configuration configuration = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(
                        new ValidatorConfiguration("SpaceBetweenAlphabeticalWord"))
                .build();
        DocumentParser parser = DocumentParser.MARKDOWN;
        List<Document> documents = new ArrayList<>();
        Document document  = parser.parse(sampleText, new SentenceExtractor(configuration.getSymbolTable()),
                configuration.getTokenizer());
        documents.add(document);

        RedPen redPen = new RedPen(configuration);
        List<ValidationError> errors = redPen.validate(documents).get(documents.get(0));
        assertEquals(1, errors.size());
        assertEquals("SpaceBetweenAlphabeticalWord", errors.get(0).getValidatorName());
        assertEquals(new LineOffset(1, 4), errors.get(0).getStartPosition().get());
        assertEquals(new LineOffset(1, 5), errors.get(0).getEndPosition().get());
    }

    @Test
    public void testErrorAfterPosition() throws RedPenException {
        String sampleText = "きょうは Cokeを飲みたい。";
        Configuration configuration = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(
                        new ValidatorConfiguration("SpaceBetweenAlphabeticalWord"))
                .build();
        DocumentParser parser = DocumentParser.MARKDOWN;
        List<Document> documents = new ArrayList<>();
        Document document  = parser.parse(sampleText, new SentenceExtractor(configuration.getSymbolTable()),
                configuration.getTokenizer());
        documents.add(document);

        RedPen redPen = new RedPen(configuration);
        List<ValidationError> errors = redPen.validate(documents).get(documents.get(0));
        assertEquals(1, errors.size());
        assertEquals("SpaceBetweenAlphabeticalWord", errors.get(0).getValidatorName());
        assertEquals(new LineOffset(1, 9), errors.get(0).getStartPosition().get());
        assertEquals(new LineOffset(1, 10), errors.get(0).getEndPosition().get());
    }
}
