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
import cc.redpen.config.Symbol;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.parser.DocumentParser;
import cc.redpen.parser.LineOffset;
import cc.redpen.parser.SentenceExtractor;
import cc.redpen.validator.ValidationError;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cc.redpen.config.SymbolType.*;
import static org.junit.Assert.assertEquals;

public class SymbolWithSpaceValidatorTest {
    @Test
    public void testNotNeedSpace() throws RedPenException {
        List<Document> documents = new ArrayList<>();
        documents.add(
                new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence("I like apple/orange", 1)
                        .build());

        Configuration conf = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("SymbolWithSpace"))
                .setLanguage("en")
                .setSymbol(new Symbol(SLASH, '/'))
                .build();

        RedPen redPen = new RedPen(conf);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void testNeedAfterSpace() throws RedPenException {
        List<Document> documents = new ArrayList<>();
        documents.add(
                new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence("I like her:yes it is", 1)
                        .build());

        Configuration conf = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("SymbolWithSpace"))
                .setLanguage("en")
                .setSymbol(new Symbol(COLON, ':', "", false, true))
                .build();

        RedPen redPen = new RedPen(conf);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(1, errors.get(documents.get(0)).size());
    }

    @Test
    public void testNeedBeforeSpace() throws RedPenException {
        List<Document> documents = new ArrayList<>();
        documents.add(
                new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence("I like her(Nancy) very much.", 1)
                        .build());

        Configuration conf = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("SymbolWithSpace"))
                .setLanguage("en")
                .setSymbol(new Symbol(LEFT_PARENTHESIS, '(', "", true, false))
                .build();

        RedPen redPen = new RedPen(conf);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(1, errors.get(documents.get(0)).size());
    }

    @Test
    public void testNeedSpaceInMultiplePosition() throws RedPenException {
        List<Document> documents = new ArrayList<>();
        documents.add(
                new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence("I like her(Nancy)very much.", 1)
                        .build());

        Configuration conf = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("SymbolWithSpace"))
                .setLanguage("en")
                .setSymbol(new Symbol(LEFT_PARENTHESIS, '(', "", true, false))
                .setSymbol(new Symbol(RIGHT_PARENTHESIS, ')', "", false, true))
                .build();

        RedPen redPen = new RedPen(conf);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(2, errors.get(documents.get(0)).size());
    }

    @Test
    public void testReturnOnlyOneForHitBothBeforeAndAfter() throws RedPenException {
        List<Document> documents = new ArrayList<>();
        documents.add(
                new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence("I like 1*10.", 1)
                        .build());

        Configuration conf = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("SymbolWithSpace"))
                .setLanguage("en")
                .setSymbol(new Symbol(ASTERISK, '*', "", true, true))
                .build();

        RedPen redPen = new RedPen(conf);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(1, errors.get(documents.get(0)).size());
    }

    @Test
    public void testErrorBeforePosition() throws RedPenException {
        String sampleText = "I like her(Nancy) very much.";
        Configuration configuration = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(
                        new ValidatorConfiguration("SymbolWithSpace"))
                .build();
        DocumentParser parser = DocumentParser.MARKDOWN;
        List<Document> documents = new ArrayList<>();
        Document document  = parser.parse(
                sampleText, new SentenceExtractor(configuration.getSymbolTable()),
                configuration.getTokenizer());
        documents.add(document);

        RedPen redPen = new RedPen(configuration);
        List<ValidationError> errors = redPen.validate(documents).get(documents.get(0));
        assertEquals(1, errors.size());
        assertEquals("SymbolWithSpace", errors.get(0).getValidatorName());
        assertEquals(new LineOffset(1, 10), errors.get(0).getStartPosition().get());
        assertEquals(new LineOffset(1, 11), errors.get(0).getEndPosition().get());
    }

    @Test
    public void testErrorAfterPosition() throws RedPenException {
        String sampleText = "I like her (Nancy)very much.";
        Configuration configuration = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(
                        new ValidatorConfiguration("SymbolWithSpace"))
                .build();
        DocumentParser parser = DocumentParser.MARKDOWN;
        List<Document> documents = new ArrayList<>();
        Document document  = parser.parse(
                sampleText, new SentenceExtractor(configuration.getSymbolTable()),
                configuration.getTokenizer());
        documents.add(document);

        RedPen redPen = new RedPen(configuration);
        List<ValidationError> errors = redPen.validate(documents).get(documents.get(0));
        assertEquals(1, errors.size());
        assertEquals("SymbolWithSpace", errors.get(0).getValidatorName());
        assertEquals(new LineOffset(1, 17), errors.get(0).getStartPosition().get());
        assertEquals(new LineOffset(1, 18), errors.get(0).getEndPosition().get());
    }
}
