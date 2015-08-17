/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.redpen.validator.sentence;

import cc.redpen.RedPen;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.parser.DocumentParser;
import cc.redpen.parser.SentenceExtractor;
import cc.redpen.validator.ValidationError;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class DoubleNegativeValidatorTest {
    @Test
    public void testDetectDoubleNegative() throws Exception {
        String sampleText = "そういう話なら、理解できないこともない。";
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(
                        new ValidatorConfiguration("DoubleNegative"))
                .setLanguage("ja")
                .build();

        DocumentParser parser = DocumentParser.MARKDOWN;
        List<Document> documents = new ArrayList<>();
        Document document  = parser.parse(sampleText,
                new SentenceExtractor(config.getSymbolTable()),
                config.getTokenizer());
        documents.add(document);

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);

        assertEquals(1, errors.get(documents.get(0)).size());
        assertEquals(1, errors.get(documents.get(0)).get(0).getLineNumber());
        assertEquals("DoubleNegative", errors.get(documents.get(0)).get(0).getValidatorName());
    }

    @Test
    public void testNotDetectSingleNegative() throws Exception {
        String sampleText =
                "そういう話は理解できない。";
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(
                        new ValidatorConfiguration("DoubleNegative"))
                .setLanguage("ja")
                .build();
        DocumentParser parser = DocumentParser.MARKDOWN;
        List<Document> documents = new ArrayList<>();
        Document document  = parser.parse(sampleText,
                new SentenceExtractor(config.getSymbolTable()),
                config.getTokenizer());
        documents.add(document);

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);

        assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void testNotDetectPositiveType() throws Exception {
        String sampleText =
                "そういう話は理解できる。";
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(
                        new ValidatorConfiguration("DoubleNegative"))
                .setLanguage("ja")
                .build();
        DocumentParser parser = DocumentParser.MARKDOWN;
        List<Document> documents = new ArrayList<>();
        Document document  = parser.parse(sampleText, new SentenceExtractor(config.getSymbolTable()),
                config.getTokenizer());
        documents.add(document);

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);

        assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void testDetectFuzzyDoubleNegative() throws Exception {
        String sampleText =
                "そういう話なら、ないことないでしょう。";
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(
                        new ValidatorConfiguration("DoubleNegative"))
                .setLanguage("ja")
                .build();
        DocumentParser parser = DocumentParser.MARKDOWN;
        List<Document> documents = new ArrayList<>();
        Document document  = parser.parse(sampleText, new SentenceExtractor(config.getSymbolTable()),
                config.getTokenizer());
        documents.add(document);

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);

        assertEquals(1, errors.get(documents.get(0)).size());
        assertEquals(1, errors.get(documents.get(0)).get(0).getLineNumber());
        assertEquals("DoubleNegative", errors.get(documents.get(0)).get(0).getValidatorName());
    }

    @Test
    public void testDetectEnDoubleNegative() throws Exception {
        String sampleText = "We believe it, unless not that is true";
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(
                        new ValidatorConfiguration("DoubleNegative"))
                .setLanguage("en")
                .build();

        DocumentParser parser = DocumentParser.MARKDOWN;
        List<Document> documents = new ArrayList<>();
        Document document  = parser.parse(sampleText,
                new SentenceExtractor(config.getSymbolTable()),
                config.getTokenizer());
        documents.add(document);

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);

        assertEquals(1, errors.get(documents.get(0)).size());
        assertEquals(1, errors.get(documents.get(0)).get(0).getLineNumber());
        assertEquals("DoubleNegative", errors.get(documents.get(0)).get(0).getValidatorName());
    }


    @Test
    public void testDetectEnDoubleNegativeWithDistance() throws Exception {
        String sampleText = "unless that is not true, I will go there.";
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(
                        new ValidatorConfiguration("DoubleNegative"))
                .setLanguage("en")
                .build();

        DocumentParser parser = DocumentParser.MARKDOWN;
        List<Document> documents = new ArrayList<>();
        Document document  = parser.parse(sampleText,
                new SentenceExtractor(config.getSymbolTable()),
                config.getTokenizer());
        documents.add(document);

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);

        assertEquals(1, errors.get(documents.get(0)).size());
        assertEquals(1, errors.get(documents.get(0)).get(0).getLineNumber());
        assertEquals("DoubleNegative", errors.get(documents.get(0)).get(0).getValidatorName());
    }

}
