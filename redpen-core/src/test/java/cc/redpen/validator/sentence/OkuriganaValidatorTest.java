/*
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

class OkuriganaValidatorTest {
    @Test
    void testInvalidOkurigana() throws Exception {
        String sampleText = "このタスクに長い年月を費してきた。";
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("Okurigana"))
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
        assertEquals("Okurigana", errors.get(documents.get(0)).get(0).getValidatorName());
    }

    @Test
    void testNoInvalidOkurigana() throws Exception {
        String sampleText = "このタスクに長い年月を費やしてきた。";
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("Okurigana"))
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
    void testInvalidOkuriganaWithRule() throws Exception {
        String sampleText = "彼に合せた。";
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("Okurigana"))
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
        assertEquals("Okurigana", errors.get(documents.get(0)).get(0).getValidatorName());
    }


    // Fix Issue #517 (https://github.com/redpen-cc/redpen/issues/517)
    @Test
    void testValidOkuriganaWithRule() throws Exception {
        String sampleText = "それとは競合している。";
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("Okurigana"))
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
}
