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
package cc.redpen;

import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.model.Paragraph;
import cc.redpen.model.Section;
import cc.redpen.model.Sentence;
import cc.redpen.parser.DocumentParser;
import cc.redpen.parser.LineOffset;
import cc.redpen.parser.SentenceExtractor;
import cc.redpen.tokenizer.TokenElement;
import cc.redpen.validator.ValidationError;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PlainTextParserTest {

    private DocumentParser parser = null;

    private List<Paragraph> extractParagraphs(Section section) {
        List<Paragraph> paragraphs = new ArrayList<>();
        for (Paragraph paragraph1 : section.getParagraphs()) {
            paragraphs.add(paragraph1);
        }
        return paragraphs;
    }

    private int getTotalSentenceCount(Section section) {
        int lineNum = 0;

        for (Paragraph paragraph : section.getParagraphs()) {
            lineNum += paragraph.getNumberOfSentences();
        }
        return lineNum;
    }

    private Document generateDocument(String sampleText) {
        return generateDocument(sampleText, "en");
    }

    private Document generateDocument(String sampleText, String lang) {
        Document doc = null;
        Configuration configuration = new Configuration.ConfigurationBuilder().setLanguage(lang).build();

        try {
            doc = parser.parse(sampleText, new SentenceExtractor(configuration.getSymbolTable()), configuration.getTokenizer());
        } catch (RedPenException e) {
            fail();
        }
        return doc;
    }

    @Before
    public void setup() {
        Configuration configuration = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(
                        new ValidatorConfiguration("SentenceLength").addAttribute("max_length", "10"))
                .build();
        parser = DocumentParser.PLAIN;
    }

    @Test
    public void testGenerateDocument() {
        String sampleText = "";
        sampleText += "This is a pen.\n";
        sampleText += "That is a orange.\n";
        sampleText += "\n";
        sampleText += "However, pen is not oranges.\n";
        sampleText += "We need to be peisient.\n";
        sampleText += "\n";
        sampleText += "Happy life.\n";
        sampleText += "Happy home.\n";
        sampleText += "Tama Home.\n";
        Document doc = generateDocument(sampleText);
        Section section = doc.getLastSection();
        assertEquals(7, getTotalSentenceCount(section));
        assertEquals(3, extractParagraphs(section).size());

        assertEquals(2, section.getParagraph(0).getNumberOfSentences());
        assertEquals(1, section.getParagraph(0).getSentence(0).getLineNumber());
        assertEquals(2, section.getParagraph(0).getSentence(1).getLineNumber());

        assertEquals(2, section.getParagraph(1).getNumberOfSentences());
        assertEquals(4, section.getParagraph(1).getSentence(0).getLineNumber());
        assertEquals(5, section.getParagraph(1).getSentence(1).getLineNumber());

        assertEquals(3, section.getParagraph(2).getNumberOfSentences());
        assertEquals(7, section.getParagraph(2).getSentence(0).getLineNumber());
        assertEquals(8, section.getParagraph(2).getSentence(1).getLineNumber());
        assertEquals(9, section.getParagraph(2).getSentence(2).getLineNumber());
    }

    @Test
    public void testGenerateDocumentWithTailingReturns() {
        String sampleText = "";
        sampleText += "This is a pen.\n";
        sampleText += "That is a orange.\n";
        sampleText += "\n";
        sampleText += "However, pen is not oranges.\n";
        sampleText += "We need to be peisient.\n";
        sampleText += "\n";
        sampleText += "\n";
        Document doc = generateDocument(sampleText);
        Section section = doc.getLastSection();
        assertEquals(4, extractParagraphs(section).size());
        assertEquals(2, extractParagraphs(section).get(0).getNumberOfSentences());
        assertEquals(2, extractParagraphs(section).get(1).getNumberOfSentences());
        assertEquals(0, extractParagraphs(section).get(2).getNumberOfSentences());
        assertEquals(0, extractParagraphs(section).get(3).getNumberOfSentences());
    }

    @Test
    public void testGenerateDocumentWithMultipleSentenceInOneLine() {
        String sampleText = "Tokyu is a good railway company. ";
        sampleText += "The company is reliable. In addition it is rich. ";
        sampleText += "I like the company. Howerver someone does not like it.";
        String[] expectedResult = {"Tokyu is a good railway company.",
                " The company is reliable.", " In addition it is rich.",
                " I like the company.", " Howerver someone does not like it."};
        Document doc = generateDocument(sampleText);
        Section section = doc.getLastSection();
        List<Paragraph> paragraphs = extractParagraphs(section);
        assertEquals(1, paragraphs.size());
        assertEquals(5, getTotalSentenceCount(section));
        Paragraph paragraph = paragraphs.get(paragraphs.size() - 1);
        for (int i = 0; i < expectedResult.length; i++) {
            assertEquals(expectedResult[i], paragraph.getSentence(i).getContent());
        }
        assertEquals(0, section.getHeaderContent(0).getLineNumber());
        assertEquals("", section.getHeaderContent(0).getContent());
    }

    @Test
    public void testGenerateDocumentWithMultipleSentenceContainsVariousStopCharacters() {
        String sampleText = "Is Tokyu a good railway company? ";
        sampleText += "Yes it is. In addition it is rich!";
        String[] expectedResult = {"Is Tokyu a good railway company?",
                " Yes it is.", " In addition it is rich!"};
        Document doc = generateDocument(sampleText);
        Section section = doc.getLastSection();
        List<Paragraph> paragraphs = extractParagraphs(section);
        assertEquals(1, paragraphs.size());
        assertEquals(3, getTotalSentenceCount(section));
        Paragraph paragraph = paragraphs.get(paragraphs.size() - 1);
        for (int i = 0; i < expectedResult.length; i++) {
            assertEquals(expectedResult[i], paragraph.getSentence(i).getContent());
        }
        assertEquals(0, section.getHeaderContent(0).getLineNumber());
        assertEquals("", section.getHeaderContent(0).getContent());
    }

    @Test
    public void testPlainTextDocumentOffsets() {
        String sampleText = "Is Tokyu a good railway company? It is indeed. Additionally, its cash reserves\nwould fill " +
                "a small \ncrater on the\nmoon! Yes it would\n\nAnother paragraph resides here.";
        String[] expectedParagraph1Sentences = {
                "Is Tokyu a good railway company?",
                " It is indeed.",
                " Additionally, its cash reserves would fill a small  crater on the moon!",
                " Yes it would"
        };
        Document doc = generateDocument(sampleText);
        Section section = doc.getLastSection();
        List<Paragraph> paragraphs = extractParagraphs(section);
        assertEquals(2, paragraphs.size());
        assertEquals(5, getTotalSentenceCount(section));
        Paragraph paragraph1 = paragraphs.get(0);
        Paragraph paragraph2 = paragraphs.get(1);

        assertEquals(4, paragraph1.getNumberOfSentences());
        assertEquals(1, paragraph2.getNumberOfSentences());

        // check the sentence text
        for (int i = 0; i < expectedParagraph1Sentences.length; i++) {
            assertEquals(expectedParagraph1Sentences[i], paragraph1.getSentence(i).getContent());
        }

        // make sure the sentences tokenized correctly
        assertEquals(6, paragraph1.getSentence(0).getTokens().size());
        assertEquals(3, paragraph1.getSentence(1).getTokens().size());
        assertEquals(12, paragraph1.getSentence(2).getTokens().size());
        assertEquals(3, paragraph1.getSentence(3).getTokens().size());

        // ensure that we can recover the original position of a token
        TokenElement token = paragraph1.getSentence(2).getTokens().get(0); // line 1, offset 47, first word of sentence, "Additionally"
        assertEquals("Additionally", token.getSurface());
        assertEquals(1, token.getOffset());
        assertEquals(new LineOffset(1, 47), paragraph1.getSentence(2).getOffset(token.getOffset()).get());

        token = paragraph1.getSentence(2).getTokens().get(8); // line 3, offset 0, "crater"
        assertEquals("crater", token.getSurface());
        assertEquals(53, token.getOffset());
        assertEquals(new LineOffset(3, 0), paragraph1.getSentence(2).getOffset(token.getOffset()).get());

        token = paragraph1.getSentence(3).getTokens().get(1); // line 4, offset 5, "it"
        assertEquals("it", token.getSurface());
        assertEquals(5, token.getOffset());
        assertEquals(new LineOffset(4, 10), paragraph1.getSentence(3).getOffset(token.getOffset()).get());


        assertEquals(0, section.getHeaderContent(0).getLineNumber());
        assertEquals("", section.getHeaderContent(0).getContent());
    }

    @Test
    public void testPlainTextJapaneseDocumentOffsets() {
        String sampleText = "お祖母さんの鉛筆は田の\n中にあります。お祖母さんの鉛筆が中にあるの\n田はどこですか？私の家\nの後ろあります\n\nつぎだんらくです。";
        String[] expectedParagraph1Sentences = {
                "お祖母さんの鉛筆は田の中にあります。",
                "お祖母さんの鉛筆が中にあるの田はどこですか？",
                "私の家の後ろあります"
        };
        Document doc = generateDocument(sampleText, "ja");
        Section section = doc.getLastSection();
        List<Paragraph> paragraphs = extractParagraphs(section);
        assertEquals(2, paragraphs.size());
        assertEquals(4, getTotalSentenceCount(section));
        Paragraph paragraph1 = paragraphs.get(0);
        Paragraph paragraph2 = paragraphs.get(1);

        assertEquals(3, paragraph1.getNumberOfSentences());
        assertEquals(1, paragraph2.getNumberOfSentences());

        // check the sentence text
        for (int i = 0; i < expectedParagraph1Sentences.length; i++) {
            assertEquals(expectedParagraph1Sentences[i], paragraph1.getSentence(i).getContent());
        }

        // make sure the sentences tokenized correctly
        assertEquals(11, paragraph1.getSentence(0).getTokens().size());
        assertEquals(14, paragraph1.getSentence(1).getTokens().size());
        assertEquals(7, paragraph1.getSentence(2).getTokens().size());

        // ensure that we can recover the original position of a token
        TokenElement token = paragraph1.getSentence(0).getTokens().get(6); // line 2, offset 0, "middle"
        assertEquals("中", token.getSurface());
        assertEquals(11, token.getOffset());
        assertEquals(new LineOffset(2, 0), paragraph1.getSentence(0).getOffset(token.getOffset()).get());

        token = paragraph1.getSentence(1).getTokens().get(10); // line 3, offset 2, "where"
        assertEquals("どこ", token.getSurface());
        assertEquals(16, token.getOffset());
        assertEquals(new LineOffset(3, 2), paragraph1.getSentence(1).getOffset(token.getOffset()).get());

        token = paragraph1.getSentence(2).getTokens().get(0); // line 3, offset 8, first word of sentence, "I"
        assertEquals("私", token.getSurface());
        assertEquals(0, token.getOffset());
        assertEquals(new LineOffset(3, 8), paragraph1.getSentence(2).getOffset(token.getOffset()).get());


        assertEquals(0, section.getHeaderContent(0).getLineNumber());
        assertEquals("", section.getHeaderContent(0).getContent());
    }

    @Test
    public void testPlainTextReverseOffsets() {
        String sampleText = "お祖母さんの鉛筆は田の\n中にあります。お祖母さんの鉛筆が中にあるの\n田はどこですか？私の家\nの後ろあります";
        Document doc = generateDocument(sampleText, "ja");
        Section section = doc.getLastSection();
        List<Paragraph> paragraphs = extractParagraphs(section);
        assertEquals(1, paragraphs.size());
        Paragraph paragraph1 = paragraphs.get(0);

        assertEquals(3, paragraph1.getNumberOfSentences());

        for (Sentence s : paragraph1.getSentences()) {
            for (TokenElement t : s.getTokens()) {
                assertEquals(true, s.getOffset(t.getOffset()).isPresent());
                assertEquals(t.getOffset(), s.getOffsetPosition(s.getOffset(t.getOffset()).get()));
            }
        }
    }

    @Test
    public void testGenerateDocumentWithNoContent() {
        String sampleText = "";
        Document doc = generateDocument(sampleText);
        Section section = doc.getLastSection();
        List<Paragraph> paragraphs = extractParagraphs(section);
        assertEquals(1, paragraphs.size());
        assertEquals(0, getTotalSentenceCount(section));
    }

    @Test
    public void testErrorPositionOfPlainTextParser() throws RedPenException {
        String sampleText = "This is a good day。\n"; // invalid end of sentence symbol
        Configuration conf = new Configuration.ConfigurationBuilder()
                .setLanguage("en")
                .build();
        List<Document> documents = new ArrayList<>();

        DocumentParser parser = DocumentParser.PLAIN;
        Document doc = parser.parse(sampleText, new SentenceExtractor(conf.getSymbolTable()), conf.getTokenizer());
        documents.add(doc);

        Configuration configuration = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(
                        new ValidatorConfiguration("InvalidSymbol"))
                .build();

        RedPen redPen = new RedPen(configuration);
        List<ValidationError> errors = redPen.validate(documents).get(documents.get(0));
        assertEquals(1, errors.size());
        assertEquals("InvalidSymbol", errors.get(0).getValidatorName());
        assertEquals(19, errors.get(0).getSentence().getContent().length());
        // plain text parser does not support error position.
        assertEquals(Optional.of(new LineOffset(1, 18)), errors.get(0).getStartPosition());
        assertEquals(Optional.of(new LineOffset(1, 19)), errors.get(0).getEndPosition());
    }
}
