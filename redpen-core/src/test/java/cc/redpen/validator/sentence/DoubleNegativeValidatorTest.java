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
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DoubleNegativeValidatorTest {
    @Test
    void testDetectDoubleNegative() throws Exception {
        String sampleText = "そういう話なら、理解できないこともない。";
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("DoubleNegative"))
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
    void testNotDetectSingleNegative() throws Exception {
        String sampleText =
                "そういう話は理解できない。";
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("DoubleNegative"))
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
    void testNotDetectPositiveType() throws Exception {
        String sampleText =
                "そういう話は理解できる。";
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("DoubleNegative"))
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
    void testDetectFuzzyDoubleNegative() throws Exception {
        String sampleText =
                "そういう話なら、ないことないでしょう。";
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("DoubleNegative"))
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
    void testDetectEnDoubleNegative() throws Exception {
        String sampleText = "We believe it, unless it is not true";
        Configuration config = Configuration.builder()
                .addValidatorConfig(
                        new ValidatorConfiguration("DoubleNegative"))

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
    void testDetectEnDoubleNegativeWithDistance() throws Exception {
        String sampleText = "unless that is not true, I will go there.";
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("DoubleNegative"))
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
