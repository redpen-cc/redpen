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
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class SpaceBetweenAlphabeticalWordValidatorTest {
    @Test
    public void testNeedBeforeSpace() throws RedPenException {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SpaceBetweenAlphabeticalWord"))
                .build();

        List<Document> documents = new ArrayList<>();documents.add(
                Document.builder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("きょうはCoke を飲みたい。", 1))
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(1, errors.get(documents.get(0)).size());
    }

    @Test
    public void testNeedAfterSpace() throws RedPenException {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SpaceBetweenAlphabeticalWord"))
                .build();

        List<Document> documents = new ArrayList<>();documents.add(
                Document.builder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("きょうは Cokeを飲みたい。", 1))
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(1, errors.get(documents.get(0)).size());
    }

    @Test
    public void testNeedBeforeAndAfterSpace() throws RedPenException {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SpaceBetweenAlphabeticalWord"))
                .build();

        List<Document> documents = new ArrayList<>();documents.add(
                Document.builder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("きょうはCokeを飲みたい。", 1))
                        .build());
        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(2, errors.get(documents.get(0)).size());
    }

    @Test
    public void testNotNeedSpaces() throws RedPenException {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SpaceBetweenAlphabeticalWord"))
                .build();

        List<Document> documents = new ArrayList<>();documents.add(
                Document.builder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("This Coke is cold", 1))
                        .build());
        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void testLatinSymbolWithoutSpace() throws RedPenException {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SpaceBetweenAlphabeticalWord"))
                .build();

        List<Document> documents = new ArrayList<>();documents.add(
                Document.builder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("きょうは,コーラを飲みたい。", 1))
                        .build());
        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void testWithParenthesis() throws RedPenException {
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("SpaceBetweenAlphabeticalWord"))
                .build();

        List<Document> documents = new ArrayList<>();documents.add(
                Document.builder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("きょうは（Coke）を飲みたい。", 1))
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void testWithComma() throws RedPenException {
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("SpaceBetweenAlphabeticalWord"))
                .build();

        List<Document> documents = new ArrayList<>();documents.add(
                Document.builder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("きょうは、Coke を飲みたい。", 1))
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void testErrorBeforePosition() throws RedPenException {
        String sampleText = "きょうはCoke を飲みたい。";
        Configuration configuration = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SpaceBetweenAlphabeticalWord"))
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
        Configuration configuration = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SpaceBetweenAlphabeticalWord"))
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


    @Test
    public void testErrorAfterCommaJa() throws RedPenException {
        String sampleText = "二種類の出力（json、json2）をサポートしてます。";
        Configuration configuration = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("SpaceBetweenAlphabeticalWord"))
                .build();
        DocumentParser parser = DocumentParser.MARKDOWN;
        List<Document> documents = new ArrayList<>();
        Document document  = parser.parse(sampleText, new SentenceExtractor(configuration.getSymbolTable()),
                configuration.getTokenizer());
        documents.add(document);

        RedPen redPen = new RedPen(configuration);
        List<ValidationError> errors = redPen.validate(documents).get(documents.get(0));
        assertEquals(0, errors.size());
    }

    @Test
    public void testNoErrorAfterParencesinJapaneseText() throws RedPenException {
        String sampleText  = "現状では平文、Markdown、Textile（Wiki 記法）、AsciiDoc、LaTeX、Re:VIEW に対応している。";
        Configuration configuration = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("SpaceBetweenAlphabeticalWord"))
                .build();
        DocumentParser parser = DocumentParser.MARKDOWN;
        List<Document> documents = new ArrayList<>();
        Document document  = parser.parse(sampleText, new SentenceExtractor(configuration.getSymbolTable()),
                configuration.getTokenizer());
        documents.add(document);

        RedPen redPen = new RedPen(configuration);
        List<ValidationError> errors = redPen.validate(documents).get(documents.get(0));
        assertEquals(0, errors.size());

    }

    @Test
    public void testNeedNoBeforeAndAfterSpace() throws RedPenException {
        SpaceBetweenAlphabeticalWordValidator validator = new SpaceBetweenAlphabeticalWordValidator();
        validator.preInit(new ValidatorConfiguration("SpaceBetweenAlphabeticalWord").addProperty("forbidden", "true"), Configuration.builder().build());
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(new Sentence("きょうは Coke を飲みたい。", 0));
        assertEquals(1, errors.size());
    }

    @Test
    public void testSkipBefore() throws RedPenException {
        SpaceBetweenAlphabeticalWordValidator validator = new SpaceBetweenAlphabeticalWordValidator();
        validator.preInit(new ValidatorConfiguration("SpaceBetweenAlphabeticalWord").addProperty("skip_before", "「"), Configuration.builder().build());
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(new Sentence("きょうは「Coke 」を飲みたい。", 0));
        assertEquals(0, errors.size());
    }
    
    @Test
    public void testSkipAfter() throws RedPenException {
        SpaceBetweenAlphabeticalWordValidator validator = new SpaceBetweenAlphabeticalWordValidator();
        validator.preInit(new ValidatorConfiguration("SpaceBetweenAlphabeticalWord").addProperty("skip_after", "」"), Configuration.builder().build());
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(new Sentence("きょうは「 Coke」を飲みたい。", 0));
        assertEquals(0, errors.size());
    }
    
    @Test
    public void testSkipBeforeAndAfter() throws RedPenException {
        SpaceBetweenAlphabeticalWordValidator validator = new SpaceBetweenAlphabeticalWordValidator();
        validator.preInit(new ValidatorConfiguration("SpaceBetweenAlphabeticalWord").addProperty("skip_before", "・「").addProperty("skip_after", "・」"), Configuration.builder().build());
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(new Sentence("きょうは「Coke・Pepsi」を飲みたい。", 0));
        assertEquals(0, errors.size());
    }


    @Test
    public void testSupportedLanguages() {
        SpaceBetweenAlphabeticalWordValidator validator = new SpaceBetweenAlphabeticalWordValidator();
        final List<String> languages = validator.getSupportedLanguages();
        assertEquals(2, languages.size());
        assertEquals(Locale.JAPANESE.getLanguage(), languages.get(0));
        assertEquals(Locale.CHINESE.getLanguage(), languages.get(1));
    }
}
