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

    // 押え
    @Test
    void testInvalidValidOkuriganaWithOsae1() throws Exception {
        String sampleText = "そこを押えない。";
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
    void testInvalidValidOkuriganaWithOsae2() throws Exception {
        String sampleText = "そこを押えて、";
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
    void testInvalidValidOkuriganaWithOsae3() throws Exception {
        String sampleText = "そこを押える。";
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
    void testInvalidValidOkuriganaWithOsae4() throws Exception {
        String sampleText = "そこを押える時、";
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
    void testInvalidValidOkuriganaWithOsae5() throws Exception {
        String sampleText = "そこを押えれば、";
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
    void testInvalidValidOkuriganaWithOsae6() throws Exception {
        String sampleText = "そこを押えよ！";
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
    void testValidOkuriganaWithOsae1() throws Exception {
        String sampleText = "そこを押さえない。";
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
    void testValidOkuriganaWithOsae2() throws Exception {
        String sampleText = "そこを押さえて、";
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
    void testValidOkuriganaWithOsae3() throws Exception {
        String sampleText = "そこを押さえる。";
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
    void testValidOkuriganaWithOsae4() throws Exception {
        String sampleText = "そこを押さえる時、";
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
    void testValidOkuriganaWithOsae5() throws Exception {
        String sampleText = "そこを押さえれば、";
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
    void testValidOkuriganaWithOsae6() throws Exception {
        String sampleText = "そこを押さえよ!";
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
    void testValidOkuriganaWithOsae7() throws Exception {
        String sampleText = "花押を押す";
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
    void testValidOkuriganaWithOsae8() throws Exception {
        String sampleText = "荷物を押収する";
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

    // 起ら
    @Test
    void testInvalidValidOkuriganaWithOkora1() throws Exception {
        String sampleText = "あなたに、困難は起らない。";
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
    void testInvalidValidOkuriganaWithOkora2() throws Exception {
        String sampleText = "あなたに、困難が起る。";
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
    void testInvalidValidOkuriganaWithOkora3() throws Exception {
        String sampleText = "あなたに困難が起る時、私は助けるだろう。";
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
    void testInvalidValidOkuriganaWithOkora4() throws Exception {
        String sampleText = "あなたに困難が起れば、私は助けるだろう。";
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
    void testInvalidValidOkuriganaWithOkora5() throws Exception {
        String sampleText = "あなたに困難が起ろう。";
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
    void testInvalidValidOkuriganaWithOkora6() throws Exception {
        String sampleText = "あなたは、起ない";
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
    void testInvalidValidOkuriganaWithOkora7() throws Exception {
        String sampleText = "わたしは、起よう！";
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
    void testValidOkuriganaWithOkora1() throws Exception {
       String sampleText = "あなたに、困難は起こらない。";
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
    void testValidOkuriganaWithOkora2() throws Exception {
        String sampleText = "あなたに困難が起こって、私が助けた。";
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
    void testValidOkuriganaWithOkora3() throws Exception {
        String sampleText = "あなたに、困難が起こる。";
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
    void testValidOkuriganaWithOkora4() throws Exception {
        String sampleText = "あなたに困難が起こる時、私は助けるだろう。";
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
    void testValidOkuriganaWithOkora5() throws Exception {
        String sampleText = "あなたに、困難が起ころう。";
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
    void testValidOkuriganaWithOkora6() throws Exception {
        String sampleText = "あなたは、起きない。";
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
    void testValidOkuriganaWithOkora7() throws Exception {
        String sampleText = "わたしは、起きて、顔を洗った。";
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
    void testValidOkuriganaWithOkora8() throws Exception {
        String sampleText = "わたしは、起きる。";
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
    void testValidOkuriganaWithOkora9() throws Exception {
        String sampleText = "わたしは、起きる時、あくびをした。";
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
    void testValidOkuriganaWithOkora10() throws Exception {
        String sampleText = "わたしは、起きれば、出かけるだろう。";
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
    void testValidOkuriganaWithOkora11() throws Exception {
        String sampleText = "わたしは、起きよう！";
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
    void testValidOkuriganaWithOkora12() throws Exception {
        String sampleText = "この話には起承転結がない。";
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
    void testValidOkuriganaWithOkora13() throws Exception {
        String sampleText = "わたしは、再帰するだろう。";
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
    void testValidOkuriganaWithOkora14() throws Exception {
        String sampleText = "わたしは、喚起するだろう。";
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
    void testValidOkuriganaWithOkora15() throws Exception {
        String sampleText = "わたしは、躍起になるだろう。";
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


    // 著い
    @Test
    void testInvalidValidOkuriganaWithIchijirushi1() throws Exception {
        String sampleText = "あなたの成長は、著くない。";
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
    void testInvalidValidOkuriganaWithIchijirushi2() throws Exception {
        String sampleText = "あなたの成長が著くて、みんな喜んだ。";
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
    void testInvalidValidOkuriganaWithIchijirushi3() throws Exception {
        String sampleText = "あなたの成長は、著かろう。";
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
    void testInvalidValidOkuriganaWithIchijirushi4() throws Exception {
        String sampleText = "あなたの成長は、著かった。";
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
    void testInvalidValidOkuriganaWithIchijirushi5() throws Exception {
        String sampleText = "あなたの成長は、著い。";
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
    void testInvalidValidOkuriganaWithIchijirushi6() throws Exception {
        String sampleText = "あなたの成長が著い時、みんな喜ぶ。";
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
    void testInvalidValidOkuriganaWithIchijirushi7() throws Exception {
        String sampleText = "あなたの成長が著ければ、みんな喜ぶ。";
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
    void testValidOkuriganaWithIchijirushi1() throws Exception {
        String sampleText = "あなたの成長は、著しくない。";
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
    void testValidOkuriganaWithIchijirushi2() throws Exception {
        String sampleText = "あなたの成長が著しくて、みんな喜んだ。";
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
    void testValidOkuriganaWithIchijirushi3() throws Exception {
        String sampleText = "あなたの成長が、著しかろう。";
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
    void testValidOkuriganaWithIchijirushi4() throws Exception {
        String sampleText = "あなたの成長が、著しい。";
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
    void testValidOkuriganaWithIchijirushi5() throws Exception {
        String sampleText = "あなたの成長が著しい時、みんな喜んだ。";
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
    void testValidOkuriganaWithIchijirushi6() throws Exception {
        String sampleText = "あなたの成長が著しければ、みんな喜ぶだろう。";
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
    void testValidOkuriganaWithIchijirushi7() throws Exception {
        String sampleText = "徴候が顕著に現れる。";
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
    void testValidOkuriganaWithIchijirushi8() throws Exception {
        String sampleText = "あなたの成長が、著しかった。";
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


    // 試る
    @Test
    void testInvalidValidOkuriganaWithKokoromiru1() throws Exception {
        String sampleText = "わたしは、冬期の単独登頂を試ない。";
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
    void testInvalidValidOkuriganaWithKokoromiru2() throws Exception {
        String sampleText = "わたしは、冬期の単独登頂を試て、成功した。";
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
    void testInvalidValidOkuriganaWithKokoromiru3() throws Exception {
        String sampleText = "わたしは、冬期の単独登頂を試る。";
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
    void testInvalidValidOkuriganaWithKokoromiru4() throws Exception {
        String sampleText = "わたしが、冬期の単独登頂を試る時、応援されるだろう。";
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
    void testInvalidValidOkuriganaWithKokoromiru5() throws Exception {
        String sampleText = "わたしが、冬期の単独登頂を試れば、応援されるだろう。";
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
    void testInvalidValidOkuriganaWithKokoromiru6() throws Exception {
        String sampleText = "わたしは、冬期の単独登頂を試よう！";
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
    void testValidOkuriganaWithKokoromiru1() throws Exception {
        String sampleText = "わたしは、冬期の単独登頂を試みない。";
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
    void testValidOkuriganaWithKokoromiru2() throws Exception {
        String sampleText = "わたしは、冬期の単独登頂を試みて、成功した。";
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
    void testValidOkuriganaWithKokoromiru3() throws Exception {
        String sampleText = "わたしが、冬期の単独登頂を試みる時、応援されるだろう。";
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
    void testValidOkuriganaWithKokoromiru4() throws Exception {
        String sampleText = "わたしが、冬期の単独登頂を試みれば、応援されるだろう。";
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
    void testValidOkuriganaWithKokoromiru5() throws Exception {
        String sampleText = "わたしは、冬期の単独登頂を試みよう！";
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
    void testValidOkuriganaWithKokoromiru6() throws Exception {
        String sampleText = "開発プロセスに問題ないと判断し、入試する。";
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
    void testValidOkuriganaWithKokoromiru7() throws Exception {
        String sampleText = "試験を休んだため追試になる。";
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
    void testValidOkuriganaWithKokoromiru8() throws Exception {
        String sampleText = "入試にむけて試験勉強をする。";
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


    // 恥し
    @Test
    void testInvalidValidOkuriganaWithHazukashii1() throws Exception {
        String sampleText = "わたしはギャグがうけなくても、恥しくない。";
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
    void testInvalidValidOkuriganaWithHazukashii2() throws Exception {
        String sampleText = "わたしはギャグがうけず恥しくて、苦笑いをした。";
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
    void testInvalidValidOkuriganaWithHazukashii3() throws Exception {
        String sampleText = "あなたはギャグがうけず、恥しかろう。";
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
    void testInvalidValidOkuriganaWithHazukashii4() throws Exception {
        String sampleText = "わたしはギャグがうけず、恥しかった。";
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
    void testInvalidValidOkuriganaWithHazukashii5() throws Exception {
        String sampleText = "わたしはギャグがうけず、恥しい。";
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
    void testInvalidValidOkuriganaWithHazukashii6() throws Exception {
        String sampleText = "わたしはギャグがうけず、恥しい時、苦笑いをする。";
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
    void testInvalidValidOkuriganaWithHazukashii7() throws Exception {
        String sampleText = "あなたはギャグがうけず、恥しければ、苦笑いをする。";
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
    void testValidOkuriganaWithHazukashii1() throws Exception {
        String sampleText = "わたしはギャグがうけなくても、恥ずかしくない。";
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
    void testValidOkuriganaWithHazukashii2() throws Exception {
        String sampleText = "わたしはギャグがうけず恥ずかしくて、苦笑いをした。";
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
    void testValidOkuriganaWithHazukashii3() throws Exception {
        String sampleText = "あなたはギャグがうけず恥ずかしかろう。";
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
    void testValidOkuriganaWithHazukashii4() throws Exception {
        String sampleText = "わたしはギャグがうけず恥ずかしい。";
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
    void testValidOkuriganaWithHazukashii5() throws Exception {
        String sampleText = "わたしはギャグがうけず恥ずかしい時、苦笑いをする。";
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
    void testValidOkuriganaWithHazukashii6() throws Exception {
        String sampleText = "わたしはギャグがうけず恥ずかしければ、苦笑いをする。";
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
    void testValidOkuriganaWithHazukashii7() throws Exception {
        String sampleText = "わたしは羞恥心をもっている。";
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
    void testValidOkuriganaWithHazukashii8() throws Exception {
        String sampleText = "あなたは無恥な人です。";
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


    // 生れ
    @Test
    void testInvalidValidOkuriganaWithUmare1() throws Exception {
        String sampleText = "わたしにはアイディアが生れない。";
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
    void testInvalidValidOkuriganaWithUmare2() throws Exception {
        String sampleText = "わたしにアイディアが生れて、行動した。";
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
    void testInvalidValidOkuriganaWithUmare3() throws Exception {
        String sampleText = "わたしに良いアイディアが生れる。";
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
    void testInvalidValidOkuriganaWithUmare4() throws Exception {
        String sampleText = "わたしに良いアイディアが生れる時、行動するだろう。";
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
    void testInvalidValidOkuriganaWithUmare5() throws Exception {
        String sampleText = "わたしに良いアイディアが生れれば、行動するだろう。";
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
    void testInvalidValidOkuriganaWithUmare6() throws Exception {
        String sampleText = "わたしに良いアイディアが生れよ！";
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
    void testValidOkuriganaWithUmare1() throws Exception {
        String sampleText = "わたしにはアイデアが生まれない。";
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
    void testValidOkuriganaWithUmare2() throws Exception {
        String sampleText = "わたしにアイデアが生まれて、行動した。";
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
    void testValidOkuriganaWithUmare3() throws Exception {
        String sampleText = "わたしにアイデアが生まれる。";
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
    void testValidOkuriganaWithUmare4() throws Exception {
        String sampleText = "わたしにアイデアが生まれる時、行動するだろう。";
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
    void testValidOkuriganaWithUmare5() throws Exception {
        String sampleText = "わたしにアイデアが生まれれば、行動するだろう。";
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
    void testValidOkuriganaWithUmare6() throws Exception {
        String sampleText = "わたしにアイデアが生まれよ！";
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
    void testValidOkuriganaWithUmare7() throws Exception {
        String sampleText = "わたしの一生の思い出。";
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
    void testValidOkuriganaWithUmare8() throws Exception {
        String sampleText = "わたしは更生するだろう。";
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


    // 妨ない
    @Test
    void testInvalidValidOkuriganaWithSamatagenai1() throws Exception {
        String sampleText = "あなたの業務を妨ない。";
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
    void testInvalidValidOkuriganaWithSamatagenai2() throws Exception {
        String sampleText = "あなたの業務を妨て、怒られた。";
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
    void testInvalidValidOkuriganaWithSamatagenai3() throws Exception {
        String sampleText = "あなたの業務を妨る。";
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
    void testInvalidValidOkuriganaWithSamatagenai4() throws Exception {
        String sampleText = "あなたの業務を妨る時、怒られるだろう。";
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
    void testInvalidValidOkuriganaWithSamatagenai5() throws Exception {
        String sampleText = "あなたの業務を妨れば、怒られるだろう。";
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
    void testInvalidValidOkuriganaWithSamatagenai6() throws Exception {
        String sampleText = "あなたの業務を妨よ！";
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
    void testValidOkuriganaWithSamatagenai1() throws Exception {
        String sampleText = "あなたの業務を妨げない。";
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
    void testValidOkuriganaWithSamatagenai2() throws Exception {
        String sampleText = "あなたの業務を妨げて、怒られた。";
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
    void testValidOkuriganaWithSamatagenai3() throws Exception {
        String sampleText = "あなたの業務を妨げる。";
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
    void testValidOkuriganaWithSamatagenai4() throws Exception {
        String sampleText = "あなたの業務を妨げる時、怒られるだろう。";
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
    void testValidOkuriganaWithSamatagenai5() throws Exception {
        String sampleText = "あなたの業務を妨げれば、怒られるだろう。";
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
    void testValidOkuriganaWithSamatagenai6() throws Exception {
        String sampleText = "あなたの業務を妨げよ！";
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
    void testValidOkuriganaWithSamatagenai7() throws Exception {
        String sampleText = "あなたの業務を妨害する。";
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
    void testValidOkuriganaWithSamatagenai8() throws Exception {
        String sampleText = "あなたは進路妨害をした。";
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

    // 進る
    @Test
    void testInvalidValidOkuriganaWithSusumeru1() throws Exception {
        String sampleText = "あなたはプロジェクトを進ない。";
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
    void testInvalidValidOkuriganaWithSusumeru2() throws Exception {
        String sampleText = "あなたはプロジェクトを進ていくだろ。";
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
    void testInvalidValidOkuriganaWithSusumeru3() throws Exception {
        String sampleText = "あなたはプロジェクトを進る。";
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
    void testInvalidValidOkuriganaWithSusumeru4() throws Exception {
        String sampleText = "あなたがプロジェクトを進る時、わたしはサポートする。";
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
    void testInvalidValidOkuriganaWithSusumeru5() throws Exception {
        String sampleText = "あなたがプロジェクトを進れば、わたしはサポートする。";
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
    void testInvalidValidOkuriganaWithSusumeru6() throws Exception {
        String sampleText = "あなたがプロジェクトを進よ！";
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
    void testValidOkuriganaWithSusumeru1() throws Exception {
        String sampleText = "あなたはプロジェクトを進めない。";
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
    void testValidOkuriganaWithSusumeru2() throws Exception {
        String sampleText = "あなたはプロジェクトを進めていくだろう。";
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
    void testValidOkuriganaWithSusumeru3() throws Exception {
        String sampleText = "あなたはプロジェクトを進める。";
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
    void testValidOkuriganaWithSusumeru4() throws Exception {
        String sampleText = "あなたはプロジェクトを進める時、わたしはサポートする。";
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
    void testValidOkuriganaWithSusumeru5() throws Exception {
        String sampleText = "あなたはプロジェクトを進めれば、わたしはサポートする。";
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
    void testValidOkuriganaWithSusumeru6() throws Exception {
        String sampleText = "あなたはプロジェクトを進めよ！";
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
    void testValidOkuriganaWithSusumeru7() throws Exception {
        String sampleText = "あなたは行進する。";
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
    void testValidOkuriganaWithSusumeru8() throws Exception {
        String sampleText = "あなたは躍進した。";
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
    void testValidOkuriganaWithSusumeru9() throws Exception {
        String sampleText = "あなたの性格は猪突猛進タイプだ。";
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

    // 勧ない
    @Test
    void testInvalidValidOkuriganaWithSusumenai1() throws Exception {
        String sampleText = "あなたにダイエットを勧ない。";
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
    void testInvalidValidOkuriganaWithSusumenai2() throws Exception {
        String sampleText = "あなたにダイエットを勧て、わたしも一緒にやってみよう。";
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
    void testInvalidValidOkuriganaWithSusumenai3() throws Exception {
        String sampleText = "あなたにダイエットを勧る。";
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
    void testInvalidValidOkuriganaWithSusumenai4() throws Exception {
        String sampleText = "あなたにダイエットを勧る時、わたしも一緒にやってみよう。";
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
    void testInvalidValidOkuriganaWithSusumenai5() throws Exception {
        String sampleText = "あなたにダイエットを勧れば、わたしも一緒にやってみる。";
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
    void testInvalidValidOkuriganaWithSusumenai6() throws Exception {
        String sampleText = "あなたにダイエットを勧よ！";
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
    void testValidOkuriganaWithSusumenai1() throws Exception {
        String sampleText = "あなたにダイエットを勧めない。";
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
    void testValidOkuriganaWithSusumenai2() throws Exception {
        String sampleText = "あなたにダイエットを勧めて、わたしも一緒にやってみよう。";
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
    void testValidOkuriganaWithSusumenai3() throws Exception {
        String sampleText = "あなたにダイエットを勧める。";
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
    void testValidOkuriganaWithSusumenai4() throws Exception {
        String sampleText = "あなたにダイエットを勧める時、わたしも一緒にやってみよう。";
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
    void testValidOkuriganaWithSusumenai5() throws Exception {
        String sampleText = "あなたにダイエットを勧めれば、わたしも一緒にやってみよう。";
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
    void testValidOkuriganaWithSusumenai6() throws Exception {
        String sampleText = "あなたにダイエットを勧めよ！";
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
    void testValidOkuriganaWithSusumenai7() throws Exception {
        String sampleText = "わたしは勧善懲悪なドラマが好きです。";
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


    // 考る
    @Test
    void testInvalidValidOkuriganaWithKangaeru1() throws Exception {
        String sampleText = "あなたは将来のことを考ない。";
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
    void testInvalidValidOkuriganaWithKangaeru2() throws Exception {
        String sampleText = "あなたは将来のことを考て、勉強する。";
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
    void testInvalidValidOkuriganaWithKangaeru3() throws Exception {
        String sampleText = "あなたは将来のことを考る。";
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
    void testInvalidValidOkuriganaWithKangaeru4() throws Exception {
        String sampleText = "あなたは将来のことを考る時、勉強するだろう。";
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
    void testInvalidValidOkuriganaWithKangaeru5() throws Exception {
        String sampleText = "あなたは将来のことを考れば、勉強するだろう。";
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
    void testInvalidValidOkuriganaWithKangaeru6() throws Exception {
        String sampleText = "あなたは将来のことを考よ！";
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
    void testValidOkuriganaWithKangaeru1() throws Exception {
        String sampleText = "あなたは将来のことを考えない。";
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
    void testValidOkuriganaWithKangaeru2() throws Exception {
        String sampleText = "あなたは将来のことを考えて、勉強するだろう。";
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
    void testValidOkuriganaWithKangaeru3() throws Exception {
        String sampleText = "あなたは将来のことを考える。";
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
    void testValidOkuriganaWithKangaeru4() throws Exception {
        String sampleText = "あなたは将来のことを考える時、勉強するだろう。";
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
    void testValidOkuriganaWithKangaeru5() throws Exception {
        String sampleText = "あなたは将来のことを考えれば、勉強するだろう。";
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
    void testValidOkuriganaWithKangaeru6() throws Exception {
        String sampleText = "あなたは将来のことを考えよ！";
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
    void testValidOkuriganaWithKangaeru7() throws Exception {
        String sampleText = "あなたは将来のことを再考する。";
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
    void testValidOkuriganaWithKangaeru8() throws Exception {
        String sampleText = "あなたは過去のことを参考にする。";
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
    void testValidOkuriganaWithKangaeru9() throws Exception {
        String sampleText = "これは参考情報です。";
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
    void testValidOkuriganaWithKangaeru10() throws Exception {
        String sampleText = "あなたはどのような思考回路を持っているのだろう？";
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


    // 行なう
    @Test
    void testInvalidValidOkuriganaWithOkonau1() throws Exception {
        String sampleText = "あなたは決めたことを行なわない。";
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
    void testInvalidValidOkuriganaWithOkonau2() throws Exception {
        String sampleText = "あなたは決めたことを行なう。";
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
    void testInvalidValidOkuriganaWithOkonau3() throws Exception {
        String sampleText = "あなたは決めたことを行なう時、信頼されるだろう。";
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
    void testInvalidValidOkuriganaWithOkonau4() throws Exception {
        String sampleText = "あなたは決めたことを行なえば、信頼されるだろう。";
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
    void testValidOkuriganaWithOkonau1() throws Exception {
        String sampleText = "あなたは決めたことを行わない。";
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
    void testValidOkuriganaWithOkonau2() throws Exception {
        String sampleText = "あなたは決めたことを行わない。";
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
    void testValidOkuriganaWithOkonau3() throws Exception {
        String sampleText = "あなたは決めたことを行う。";
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
    void testValidOkuriganaWithOkonau4() throws Exception {
        String sampleText = "あなたは決めたことを行う時、信頼されるだろう。";
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
    void testValidOkuriganaWithOkonau5() throws Exception {
        String sampleText = "あなたは決めたことを行えば、信頼されるだろう。";
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
    void testValidOkuriganaWithOkonau6() throws Exception {
        String sampleText = "あなたの周りで不正が横行する。";
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
    void testValidOkuriganaWithOkonau7() throws Exception {
        String sampleText = "あなたは蛇行運転が得意です。";
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
    void testValidOkuriganaWithOkonau8() throws Exception {
        String sampleText = "あなたは旅行に行く。";
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


    // 表わ
    @Test
    void testInvalidValidOkuriganaWithArawa1() throws Exception {
        String sampleText = "あなたは感情を顔に表わさない。";
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
    void testInvalidValidOkuriganaWithArawa2() throws Exception {
        String sampleText = "あなたは感情を顔に表わす。";
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
    void testInvalidValidOkuriganaWithArawa3() throws Exception {
        String sampleText = "あなたが感情を顔に表わす時、意図が伝わるだろう。";
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
    void testInvalidValidOkuriganaWithArawa4() throws Exception {
        String sampleText = "あなたが感情を顔に表わせば、意図が伝わるだろう。";
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
    void testValidOkuriganaWithArawa1() throws Exception {
        String sampleText = "あなたは感情を顔に表さない。";
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
    void testValidOkuriganaWithArawa2() throws Exception {
        String sampleText = "あなたは感情を顔に表す。";
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
    void testValidOkuriganaWithArawa3() throws Exception {
        String sampleText = "あなたは感情を顔に表す時、意図が伝わるだろう。";
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
    void testValidOkuriganaWithArawa4() throws Exception {
        String sampleText = "あなたは感情を顔に表せば、意図が伝わるだろう。";
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
    void testValidOkuriganaWithArawa5() throws Exception {
        String sampleText = "あなたは意表を突くのが得意だ。";
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
    void testValidOkuriganaWithArawa6() throws Exception {
        String sampleText = "あなたは秘密を公表する。";
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

    // 現わ
    @Test
    void testInvalidValidOkuriganaWithAraware1() throws Exception {
        String sampleText = "あなたが、現われない。";
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
    void testInvalidValidOkuriganaWithAraware2() throws Exception {
        String sampleText = "あなたが、現われて、会話した。";
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
    void testInvalidValidOkuriganaWithAraware3() throws Exception {
        String sampleText = "あなたが、現われる。";
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
    void testInvalidValidOkuriganaWithAraware4() throws Exception {
        String sampleText = "あなたが、現われる時、会話するだろう。";
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
    void testInvalidValidOkuriganaWithAraware5() throws Exception {
        String sampleText = "あなたが、現われれば、会話するだろう。";
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
    void testInvalidValidOkuriganaWithAraware6() throws Exception {
        String sampleText = "あなたが、現われよ！";
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
    void testValidOkuriganaWithAraware1() throws Exception {
        String sampleText = "あなたが現れない。";
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
    void testValidOkuriganaWithAraware2() throws Exception {
        String sampleText = "あなたが現れて、会話した。";
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
    void testValidOkuriganaWithAraware3() throws Exception {
        String sampleText = "あなたが現れる。";
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
    void testValidOkuriganaWithAraware4() throws Exception {
        String sampleText = "あなたが現れる時、会話するだろう。";
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
    void testValidOkuriganaWithAraware5() throws Exception {
        String sampleText = "あなたが現れれば、会話するだろう。";
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
    void testValidOkuriganaWithAraware6() throws Exception {
        String sampleText = "あなたが現れよ！";
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
    void testValidOkuriganaWithAraware7() throws Exception {
        String sampleText = "あなたの夢が実現する。";
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
    void testValidOkuriganaWithAraware8() throws Exception {
        String sampleText = "あなたが発言した。";
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

    // 断わ
    @Test
    void testInvalidValidOkuriganaWithKotowa1() throws Exception {
        String sampleText = "あなたは、断わらない。";
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
    void testInvalidValidOkuriganaWithKotowa2() throws Exception {
        String sampleText = "あなたは、断わる。";
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
    void testInvalidValidOkuriganaWithKotowa3() throws Exception {
        String sampleText = "あなたが断わる時、困るだろう。";
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
    void testInvalidValidOkuriganaWithKotowa4() throws Exception {
        String sampleText = "あなたが断われば、困るだろう。";
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
    void testInvalidValidOkuriganaWithKotowa5() throws Exception {
        String sampleText = "あなたに断わろう。";
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
    void testValidOkuriganaWithKotowa1() throws Exception {
        String sampleText = "あなたは断らない。";
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
    void testValidOkuriganaWithKotowa2() throws Exception {
        String sampleText = "あなたが断って、困った。";
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
    void testValidOkuriganaWithKotowa3() throws Exception {
        String sampleText = "あなたが断る。";
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
    void testValidOkuriganaWithKotowa4() throws Exception {
        String sampleText = "あなたが断る時、困るだろう。";
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
    void testValidOkuriganaWithKotowa5() throws Exception {
        String sampleText = "あなたに断ろう。";
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
    void testValidOkuriganaWithKotowa6() throws Exception {
        String sampleText = "あなたは横断歩道を渡る。";
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
    void testValidOkuriganaWithKotowa7() throws Exception {
        String sampleText = "あなたは決断する。";
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
    void testValidOkuriganaWithKotowa8() throws Exception {
        String sampleText = "あなたは判断した。";
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

	//聞え
    @Test
    void testInvalidValidOkuriganaWithKikoe1() throws Exception {
        String sampleText = "私には聞えない。";
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
    void testInvalidValidOkuriganaWithKikoe2() throws Exception {
        String sampleText = "私には聞えて、";
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
    void testInvalidValidOkuriganaWithKikoe3() throws Exception {
        String sampleText = "私には聞える。";
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
    void testInvalidValidOkuriganaWithKikoe4() throws Exception {
        String sampleText = "私には聞える時、";
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
    void testInvalidValidOkuriganaWithKikoe5() throws Exception {
        String sampleText = "私には聞えれば、";
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
    void testInvalidValidOkuriganaWithKikoe6() throws Exception {
        String sampleText = "私には聞えよ！";
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
    void testValidOkuriganaWithKikoe1() throws Exception {
        String sampleText = "私には聞こえない。";
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
    void testValidOkuriganaWithKikoe2() throws Exception {
        String sampleText = "私には聞こえて、";
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
    void testValidOkuriganaWithKikoe3() throws Exception {
        String sampleText = "私には聞こえる。";
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
    void testValidOkuriganaWithKikoe4() throws Exception {
        String sampleText = "私には聞こえる時、";
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
    void testValidOkuriganaWithKikoe5() throws Exception {
        String sampleText = "私には聞こえれば、";
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
    void testValidOkuriganaWithKikoe6() throws Exception {
        String sampleText = "私には聞こえよ！";
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
    void testValidOkuriganaWithKikoe7() throws Exception {
        String sampleText = "新聞を読む";
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
    void testValidOkuriganaWithKikoe8() throws Exception {
        String sampleText = "百聞は一見にしかず";
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

	//当る
    @Test
    void testInvalidValidOkuriganaWithAtaru1() throws Exception {
        String sampleText = "宝くじが当らない。";
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
    void testInvalidValidOkuriganaWithAtaru2() throws Exception {
        String sampleText = "宝くじが当る。";
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
    void testInvalidValidOkuriganaWithAtaru3() throws Exception {
        String sampleText = "宝くじが当る時、";
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
    void testInvalidValidOkuriganaWithAtaru4() throws Exception {
        String sampleText = "宝くじが当れば、";
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
    void testInvalidValidOkuriganaWithAtaru5() throws Exception {
        String sampleText = "宝くじに当ろう。";
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
    void testValidOkuriganaWithAtaru1() throws Exception {
        String sampleText = "宝くじが当たらない。";
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
    void testValidOkuriganaWithAtaru2() throws Exception {
        String sampleText = "宝くじが当たって、";
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
    void testValidOkuriganaWithAtaru3() throws Exception {
        String sampleText = "宝くじが当たる。";
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
    void testValidOkuriganaWithAtaru4() throws Exception {
        String sampleText = "宝くじが当たる時、";
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
    void testValidOkuriganaWithAtaru5() throws Exception {
        String sampleText = "宝くじが当たれば、";
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
    void testValidOkuriganaWithAtaru6() throws Exception {
        String sampleText = "宝くじに当たろう。";
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
    void testValidOkuriganaWithAtaru7() throws Exception {
        String sampleText = "私が該当する。";
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
    void testValidOkuriganaWithAtaru8() throws Exception {
        String sampleText = "見当違いしている";
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
    void testValidOkuriganaWithAtaru9() throws Exception {
        String sampleText = "私が担当した。";
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

	//落さない
    @Test
    void testInvalidValidOkuriganaWithOtosa1() throws Exception {
        String sampleText = "荷物を落さない";
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
    void testInvalidValidOkuriganaWithOtosa2() throws Exception {
        String sampleText = "荷物を落して";
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
    void testInvalidValidOkuriganaWithOtosa3() throws Exception {
        String sampleText = "荷物を落す。";
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
    void testInvalidValidOkuriganaWithOtosa4() throws Exception {
        String sampleText = "荷物を落す時、";
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
    void testInvalidValidOkuriganaWithOtosa5() throws Exception {
        String sampleText = "荷物を落せば、";
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
    void testInvalidValidOkuriganaWithOtosa6() throws Exception {
        String sampleText = "荷物を落そう。";
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
    void testValidOkuriganaWithOtosa1() throws Exception {
        String sampleText = "荷物を落とさない。";
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
    void testValidOkuriganaWithOtosa2() throws Exception {
        String sampleText = "荷物を落とさして、";
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
    void testValidOkuriganaWithOtosa3() throws Exception {
        String sampleText = "荷物を落として、";
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
    void testValidOkuriganaWithOtosa4() throws Exception {
        String sampleText = "荷物を落とす。";
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
    void testValidOkuriganaWithOtosa5() throws Exception {
        String sampleText = "荷物を落とす時、";
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
    void testValidOkuriganaWithOtosa6() throws Exception {
        String sampleText = "荷物を落とす。";
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
    void testValidOkuriganaWithOtosa7() throws Exception {
        String sampleText = "荷物を落とそう。";
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
    void testValidOkuriganaWithOtosa8() throws Exception {
        String sampleText = "感情が欠落している。";
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
    void testValidOkuriganaWithOtosa9() throws Exception {
        String sampleText = "先頭から脱落する";
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

	//終る
    @Test
    void testInvalidValidOkuriganaWithowaru1() throws Exception {
        String sampleText = "勉強を終らさない。";
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
    void testInvalidValidOkuriganaWithowaru2() throws Exception {
        String sampleText = "勉強が終る。";
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
    void testInvalidValidOkuriganaWithowaru3() throws Exception {
        String sampleText = "勉強が終る時、";
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
    void testInvalidValidOkuriganaWithowaru4() throws Exception {
        String sampleText = "勉強が終れば、";
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
    void testInvalidValidOkuriganaWithowaru5() throws Exception {
        String sampleText = "勉強を終ろう。";
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
    void testValidOkuriganaWithowaru1() throws Exception {
        String sampleText = "勉強を終わらさない。";
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
    void testValidOkuriganaWithowaru2() throws Exception {
        String sampleText = "勉強が終わって、";
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
    void testValidOkuriganaWithowaru3() throws Exception {
        String sampleText = "勉強が終わる。";
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
    void testValidOkuriganaWithowaru4() throws Exception {
        String sampleText = "勉強が終わる時、";
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
    void testValidOkuriganaWithowaru5() throws Exception {
        String sampleText = "勉強が終われば、";
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
    void testValidOkuriganaWithowaru6() throws Exception {
        String sampleText = "勉強を終わろう。";
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
    void testValidOkuriganaWithowaru7() throws Exception {
        String sampleText = "最終電車に乗る";
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
    void testValidOkuriganaWithowaru8() throws Exception {
        String sampleText = "有終の美を飾る";
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

	//果す
    @Test
    void testInvalidValidOkuriganaWithhatasu1() throws Exception {
        String sampleText = "約束を果さない。";
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
    void testInvalidValidOkuriganaWithhatasu2() throws Exception {
        String sampleText = "約束を果す。";
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
    void testInvalidValidOkuriganaWithhatasu3() throws Exception {
        String sampleText = "約束を果す時、";
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
    void testInvalidValidOkuriganaWithhatasu4() throws Exception {
        String sampleText = "約束を果せば、";
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
    void testInvalidValidOkuriganaWithhatasu5() throws Exception {
        String sampleText = "約束を果そう。";
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
    void testValidOkuriganaWithhatasu1() throws Exception {
        String sampleText = "約束を果たさない。";
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
    void testValidOkuriganaWithhatasu2() throws Exception {
        String sampleText = "約束を果たして、";
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
    void testValidOkuriganaWithhatasu3() throws Exception {
        String sampleText = "約束を果たす。";
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
    void testValidOkuriganaWithhatasu4() throws Exception {
        String sampleText = "約束を果たす時、";
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
    void testValidOkuriganaWithhatasu5() throws Exception {
        String sampleText = "約束を果たそう。";
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
    void testValidOkuriganaWithhatasu6() throws Exception {
        String sampleText = "因果関係を明らかにする";
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
    void testValidOkuriganaWithhatasu8() throws Exception {
        String sampleText = "結果を確認する";
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
    void testValidOkuriganaWithhatasu9() throws Exception {
        String sampleText = "成果をあげる";
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

	//変る
    @Test
    void testInvalidValidOkuriganaWithkawaru1() throws Exception {
        String sampleText = "性格は変らない。";
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
    void testInvalidValidOkuriganaWithkawaru2() throws Exception {
        String sampleText = "性格は変らして、";
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
    void testInvalidValidOkuriganaWithkawaru3() throws Exception {
        String sampleText = "性格は変る。";
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
    void testInvalidValidOkuriganaWithkawaru4() throws Exception {
        String sampleText = "性格が変るとき、";
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
    void testInvalidValidOkuriganaWithkawaru5() throws Exception {
        String sampleText = "性格を変らせれば、";
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
    void testValidOkuriganaWithkawaru1() throws Exception {
        String sampleText = "性格が変わらない。";
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
    void testValidOkuriganaWithkawaru2() throws Exception {
        String sampleText = "性格を変わらして、";
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
    void testValidOkuriganaWithkawaru3() throws Exception {
        String sampleText = "性格を変わる。";
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
    void testValidOkuriganaWithkawaru4() throws Exception {
        String sampleText = "性格が変わるとき、";
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
    void testValidOkuriganaWithkawaru5() throws Exception {
        String sampleText = "性格が変わらせれば、";
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
    void testValidOkuriganaWithkawaru6() throws Exception {
        String sampleText = "異変に気付く。";
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
    void testValidOkuriganaWithkawaru7() throws Exception {
        String sampleText = "臨機応変に対応する";
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
    void testValidOkuriganaWithkawaru8() throws Exception {
        String sampleText = "病状が急変した";
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

	//買る
    @Test
    void testInvalidValidOkuriganaWithkaeru1() throws Exception {
        String sampleText = "テレビを買ない。";
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
    void testInvalidValidOkuriganaWithkaeru2() throws Exception {
        String sampleText = "テレビを買て、";
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
    void testInvalidValidOkuriganaWithkaeru3() throws Exception {
        String sampleText = "テレビを買る。";
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
    void testInvalidValidOkuriganaWithkaeru4() throws Exception {
        String sampleText = "テレビを買る時、";
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
    void testInvalidValidOkuriganaWithkaeru5() throws Exception {
        String sampleText = "テレビを買れば、";
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
    void testInvalidValidOkuriganaWithkaeru6() throws Exception {
        String sampleText = "テレビを買よ！";
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
    void testValidOkuriganaWithkaeru1() throws Exception {
        String sampleText = "テレビを買えない。";
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
    void testValidOkuriganaWithkaeru2() throws Exception {
        String sampleText = "テレビを買えて、";
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
    void testValidOkuriganaWithkaeru3() throws Exception {
        String sampleText = "テレビを買える。";
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
    void testValidOkuriganaWithkaeru4() throws Exception {
        String sampleText = "テレビを買える時、";
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
    void testValidOkuriganaWithkaeru5() throws Exception {
        String sampleText = "テレビを買えれば、";
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
    void testValidOkuriganaWithkaeru6() throws Exception {
        String sampleText = "テレビを買えよ！";
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
    void testValidOkuriganaWithkaeru7() throws Exception {
        String sampleText = "購買意欲が沸く";
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
    void testValidOkuriganaWithkaeru8() throws Exception {
        String sampleText = "テレビを売買する。";
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
    void testValidOkuriganaWithkaeru9() throws Exception {
        String sampleText = "仲買人から買う";
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

	//上る
    @Test
    void testInvalidValidOkuriganaWithagaru1() throws Exception {
        String sampleText = "階段を上らない。";
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
    void testInvalidValidOkuriganaWithagaru2() throws Exception {
        String sampleText = "階段を上る。";
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
    void testInvalidValidOkuriganaWithagaru3() throws Exception {
        String sampleText = "階段を上る時、";
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
    void testInvalidValidOkuriganaWithagaru4() throws Exception {
        String sampleText = "階段を上れば、";
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
    void testValidOkuriganaWithagaru1() throws Exception {
        String sampleText = "階段を上がらない。";
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
    void testValidOkuriganaWithagaru2() throws Exception {
        String sampleText = "階段を上がって、";
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
    void testValidOkuriganaWithagaru3() throws Exception {
        String sampleText = "階段を上がる。";
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
    void testValidOkuriganaWithagaru4() throws Exception {
        String sampleText = "階段を上がる時、";
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
    void testValidOkuriganaWithagaru5() throws Exception {
        String sampleText = "階段を上がれば、";
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
    void testValidOkuriganaWithagaru6() throws Exception {
        String sampleText = "屋上に移動する。";
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
    void testValidOkuriganaWithagaru7() throws Exception {
        String sampleText = "陸上競技場に行く";
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
    void testValidOkuriganaWithagaru8() throws Exception {
        String sampleText = "能力が向上する。";
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

	//費す
    @Test
    void testInvalidValidOkuriganaWithtsuiyasu1() throws Exception {
        String sampleText = "お金を費さない。";
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
    void testInvalidValidOkuriganaWithtsuiyasu2() throws Exception {
        String sampleText = "お金を費して、";
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
    void testInvalidValidOkuriganaWithtsuiyasu3() throws Exception {
        String sampleText = "お金を費す。";
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
    void testInvalidValidOkuriganaWithtsuiyasu4() throws Exception {
        String sampleText = "お金を費す時、";
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
    void testInvalidValidOkuriganaWithtsuiyasu5() throws Exception {
        String sampleText = "お金を費せば、";
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
    void testValidOkuriganaWithtsuiyasu1() throws Exception {
        String sampleText = "お金を費やさない。";
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
    void testValidOkuriganaWithtsuiyasu2() throws Exception {
        String sampleText = "お金を費やして、";
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
    void testValidOkuriganaWithtsuiyasu3() throws Exception {
        String sampleText = "お金を費やす。";
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
    void testValidOkuriganaWithtsuiyasu4() throws Exception {
        String sampleText = "お金を費やすとき、";
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
    void testValidOkuriganaWithtsuiyasu5() throws Exception {
        String sampleText = "お金を費やせば、";
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
    void testValidOkuriganaWithtsuiyasu6() throws Exception {
        String sampleText = "会費を徴収する";
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
    void testValidOkuriganaWithtsuiyasu7() throws Exception {
        String sampleText = "自費で参加する";
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

	//危い
    @Test
    void testInvalidValidOkuriganaWithayaui1() throws Exception {
        String sampleText = "自分の身が危くない。";
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
    void testInvalidValidOkuriganaWithayaui2() throws Exception {
        String sampleText = "自分の身が危く、";
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
    void testInvalidValidOkuriganaWithayaui3() throws Exception {
        String sampleText = "自分の身が危い。";
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
    void testInvalidValidOkuriganaWithayaui4() throws Exception {
        String sampleText = "自分の身が危い時、";
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
    void testValidOkuriganaWithayaui1() throws Exception {
        String sampleText = "自分の身が危うくない。";
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
    void testValidOkuriganaWithayaui2() throws Exception {
        String sampleText = "自分の身が危うく、";
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
    void testValidOkuriganaWithayaui3() throws Exception {
        String sampleText = "自分の身が危うかった。";
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
    void testValidOkuriganaWithayaui4() throws Exception {
        String sampleText = "自分の身が危うい。";
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
    void testValidOkuriganaWithayaui5() throws Exception {
        String sampleText = "自分の身が危うい時、";
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
    void testValidOkuriganaWithayaui6() throws Exception {
        String sampleText = "自分の身が危うければ、";
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

	//逸す
    @Test
    void testInvalidValidOkuriganaWithsorasu1() throws Exception {
        String sampleText = "後ろに逸さない。";
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
    void testInvalidValidOkuriganaWithsorasu2() throws Exception {
        String sampleText = "後ろに逸して、";
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
    void testInvalidValidOkuriganaWithsorasu3() throws Exception {
        String sampleText = "後ろに逸す。";
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
    void testInvalidValidOkuriganaWithsorasu4() throws Exception {
        String sampleText = "後ろに逸す時、";
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
    void testValidOkuriganaWithsorasu1() throws Exception {
        String sampleText = "後ろに逸らさない。";
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
    void testValidOkuriganaWithsorasu2() throws Exception {
        String sampleText = "後ろに逸らして、";
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
    void testValidOkuriganaWithsorasu3() throws Exception {
        String sampleText = "後ろに逸らす。";
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
    void testValidOkuriganaWithsorasu4() throws Exception {
        String sampleText = "後ろに逸らす時、";
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

	//反す
    @Test
    void testInvalidValidOkuriganaWithsora1() throws Exception {
        String sampleText = "体を反さない";
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
    void testInvalidValidOkuriganaWithsora2() throws Exception {
        String sampleText = "体を反して、";
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
    void testInvalidValidOkuriganaWithsora3() throws Exception {
        String sampleText = "体を反す。";
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
    void testInvalidValidOkuriganaWithsora4() throws Exception {
        String sampleText = "体を反す時、";
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
    void testInvalidValidOkuriganaWithsora5() throws Exception {
        String sampleText = "体を反せば、";
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
    void testInvalidValidOkuriganaWithsora6() throws Exception {
        String sampleText = "体を反そう。";
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
    void testValidOkuriganaWithsora1() throws Exception {
        String sampleText = "体を反らさない。";
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
    void testValidOkuriganaWithsora2() throws Exception {
        String sampleText = "体を反らして、";
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
    void testValidOkuriganaWithsora3() throws Exception {
        String sampleText = "体を反らす。";
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
    void testValidOkuriganaWithsora4() throws Exception {
        String sampleText = "体を反らす時、";
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
    void testValidOkuriganaWithsora5() throws Exception {
        String sampleText = "体を反らせば、";
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
    void testValidOkuriganaWithsora6() throws Exception {
        String sampleText = "体を反らそう。";
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
    void testValidOkuriganaWithsora7() throws Exception {
        String sampleText = "ルールに違反する。";
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
    void testValidOkuriganaWithsora8() throws Exception {
        String sampleText = "謀反をおこす。";
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

	//過す
    @Test
    void testInvalidValidOkuriganaWithsugosu1() throws Exception {
        String sampleText = "家で過さない。";
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
    void testInvalidValidOkuriganaWithsugosu2() throws Exception {
        String sampleText = "家で過して、";
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
    void testInvalidValidOkuriganaWithsugosu3() throws Exception {
        String sampleText = "家で過す。";
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
    void testInvalidValidOkuriganaWithsugosu4() throws Exception {
        String sampleText = "家で過す時、";
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
    void testInvalidValidOkuriganaWithsugosu5() throws Exception {
        String sampleText = "家で過せば、";
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
    void testInvalidValidOkuriganaWithsugosu6() throws Exception {
        String sampleText = "家で過そう。";
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
    void testValidOkuriganaWithsugosu1() throws Exception {
        String sampleText = "家で過ごさない。";
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
    void testValidOkuriganaWithsugosu2() throws Exception {
        String sampleText = "家で過ごして、";
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
    void testValidOkuriganaWithsugosu3() throws Exception {
        String sampleText = "家で過ごす。";
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
    void testValidOkuriganaWithsugosu4() throws Exception {
        String sampleText = "家で過ごす時、";
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
    void testValidOkuriganaWithsugosu5() throws Exception {
        String sampleText = "家で過ごせば、";
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
    void testValidOkuriganaWithsugosu6() throws Exception {
        String sampleText = "家で過ごそう。";
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
    void testValidOkuriganaWithsugosu7() throws Exception {
        String sampleText = "時間が経過する。";
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
    void testValidOkuriganaWithsugosu8() throws Exception {
        String sampleText = "通過点をとおる。";
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
