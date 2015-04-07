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
import cc.redpen.config.Symbol;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.model.ListBlock;
import cc.redpen.model.Paragraph;
import cc.redpen.model.Section;
import cc.redpen.parser.DocumentParser;
import cc.redpen.parser.LineOffset;
import cc.redpen.parser.SentenceExtractor;
import cc.redpen.validator.ValidationError;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static cc.redpen.config.SymbolType.COMMA;
import static cc.redpen.config.SymbolType.FULL_STOP;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class MarkdownParserTest {

    @Before
    public void setup() {
    }

    @Test(expected = NullPointerException.class)
    public void testNullDocument() throws Exception {
        Configuration configuration = new Configuration.ConfigurationBuilder().build();
        DocumentParser parser = DocumentParser.MARKDOWN;
        InputStream is = null;
        parser.parse(is, new SentenceExtractor(configuration.getSymbolTable()), configuration.getTokenizer());
    }

    @Test
    public void testBasicDocument() throws UnsupportedEncodingException {
        String sampleText = "";
        sampleText += "# About Gekioko.\n";
        sampleText += "Gekioko pun pun maru means _very very_ angry.\n";
        sampleText += "\n";
        sampleText += "The word also has a positive meaning.\n";
        sampleText += "## About Gunma.\n";
        sampleText += "\n";
        sampleText += "Gunma is located at west of Saitama.\n";
        sampleText += "\n";
        sampleText += "* Features\n";
        sampleText += "    * Main City: Gumma City\n";
        sampleText += "    * Capical: 200 Millon\n";
        sampleText += "* Location\n";
        sampleText += "    * Japan\n";
        sampleText += "\n";
        sampleText += "The word also have positive meaning. However it is a bit weird.";

        Document doc = createFileContent(sampleText);

        assertNotNull("doc is null", doc);
        assertEquals(3, doc.size());
        // first section
        final Section firstSection = doc.getSection(0);
        assertEquals(1, firstSection.getHeaderContentsListSize());
        assertEquals("", firstSection.getHeaderContent(0).getContent());
        assertEquals(0, firstSection.getNumberOfLists());
        assertEquals(0, firstSection.getNumberOfParagraphs());
        assertEquals(1, firstSection.getNumberOfSubsections());

        // 2nd section
        final Section secondSection = doc.getSection(1);
        assertEquals(1, secondSection.getHeaderContentsListSize());
        assertEquals("About Gekioko.", secondSection.getHeaderContent(0).getContent());
        assertEquals(1, secondSection.getHeaderContent(0).getLineNumber());
        assertEquals(2, secondSection.getHeaderContent(0).getStartPositionOffset());
        assertEquals(0, secondSection.getNumberOfLists());
        assertEquals(2, secondSection.getNumberOfParagraphs());
        assertEquals(1, secondSection.getNumberOfSubsections());
        assertEquals(firstSection, secondSection.getParentSection());

        // validate paragraph in 2nd section
        assertEquals(1, secondSection.getParagraph(0).getNumberOfSentences());
        assertEquals(true, secondSection.getParagraph(0).getSentence(0).isFirstSentence());
        assertEquals(2, secondSection.getParagraph(0).getSentence(0).getLineNumber());
        assertEquals(0, secondSection.getParagraph(0).getSentence(0).getStartPositionOffset());
        assertEquals(1, secondSection.getParagraph(1).getNumberOfSentences());
        assertEquals(true, secondSection.getParagraph(1).getSentence(0).isFirstSentence());
        assertEquals(4, secondSection.getParagraph(1).getSentence(0).getLineNumber());
        assertEquals(0, secondSection.getParagraph(1).getSentence(0).getStartPositionOffset());

        // 3rd section
        Section lastSection = doc.getSection(doc.size() - 1);
        assertEquals(1, lastSection.getNumberOfLists());
        assertEquals(5, lastSection.getListBlock(0).getNumberOfListElements());
        assertEquals(2, lastSection.getNumberOfParagraphs());
        assertEquals(1, lastSection.getHeaderContentsListSize());
        assertEquals(0, lastSection.getNumberOfSubsections());
        assertEquals("About Gunma.", lastSection.getHeaderContent(0).getContent());
        assertEquals(3, lastSection.getHeaderContent(0).getStartPositionOffset());
        assertEquals(secondSection, lastSection.getParentSection());

        // validate paragraphs in last section
        assertEquals(1, lastSection.getParagraph(0).getNumberOfSentences());
        assertEquals(true, lastSection.getParagraph(0).getSentence(0).isFirstSentence());
        assertEquals(7, lastSection.getParagraph(0).getSentence(0).getLineNumber());
        assertEquals(0, lastSection.getParagraph(0).getSentence(0).getStartPositionOffset());
        assertEquals(2, lastSection.getParagraph(1).getNumberOfSentences());
        assertEquals(true, lastSection.getParagraph(1).getSentence(0).isFirstSentence());
        assertEquals(15, lastSection.getParagraph(1).getSentence(0).getLineNumber());
        assertEquals(false, lastSection.getParagraph(1).getSentence(1).isFirstSentence());
        assertEquals(15, lastSection.getParagraph(1).getSentence(1).getLineNumber());
        assertEquals(36, lastSection.getParagraph(1).getSentence(1).getStartPositionOffset());
    }


    @Test
    public void testGenerateDocumentWithHeaderedDocument() {
        String sampleText = "# Validator\n"
                + "Validator class is a abstract class in RedPen project.\n"
                + "Functions provided by RedPen class are implemented with validator class.\n";
        Document doc = createFileContent(sampleText);

        final Section secondSection = doc.getSection(1);
        assertEquals(1, secondSection.getHeaderContentsListSize());
        assertEquals("Validator", secondSection.getHeaderContent(0).getContent());
        assertEquals(1, secondSection.getHeaderContent(0).getLineNumber());
        assertEquals(2, secondSection.getHeaderContent(0).getStartPositionOffset());
        assertEquals(0, secondSection.getNumberOfLists());
        assertEquals(1, secondSection.getNumberOfParagraphs());

        // validate paragraph in 2nd section
        assertEquals(2, secondSection.getParagraph(0).getNumberOfSentences());
        assertEquals(true, secondSection.getParagraph(0).getSentence(0).isFirstSentence());
        assertEquals(2, secondSection.getParagraph(0).getSentence(0).getLineNumber());
        assertEquals(0, secondSection.getParagraph(0).getSentence(0).getStartPositionOffset());
        assertEquals("Validator class is a abstract class in RedPen project.",
                secondSection.getParagraph(0).getSentence(0).getContent());

        assertEquals(false, secondSection.getParagraph(0).getSentence(1).isFirstSentence());
        assertEquals(" Functions provided by RedPen class are implemented with validator class.",
                secondSection.getParagraph(0).getSentence(1).getContent());
        assertEquals(3, secondSection.getParagraph(0).getSentence(1).getLineNumber());
        assertEquals(0, secondSection.getParagraph(0).getSentence(1).getStartPositionOffset());

        // NOTE: both linebreak and first character of the offsets are 0
        List<LineOffset> expectedOffsets = initializeMappingTable(
                new LineOffset(3, 0), // LineBreak NOTE: lineBreak is exist but the offset is "0"
                new LineOffset(3, 0), // F NOTE: even when there is a Linebreak, sentence start from offset "0".
                new LineOffset(3, 1)  // u
        );
        for (int i = 0; i < expectedOffsets.size(); i++) {
            assertEquals(expectedOffsets.get(i), secondSection.getParagraph(0).getSentence(1).getOffset(i).get());
        }

    }

    @Test
    public void testGenerateDocumentWithList() {
        String sampleText =
                "Threre are several railway companies in Japan as follows.\n";
        sampleText += "\n";
        sampleText += "- Tokyu\n";
        sampleText += "    - Toyoko Line\n";
        sampleText += "    - Denentoshi Line\n";
        sampleText += "- Keio\n";
        sampleText += "- Odakyu\n";

        Document doc = createFileContent(sampleText);
        assertEquals(5, doc.getSection(0).getListBlock(0).getNumberOfListElements());
        assertEquals("Tokyu", doc.getSection(0).getListBlock(0).getListElement(0).getSentence(0).getContent());
        assertEquals(1, doc.getSection(0).getListBlock(0).getListElement(0).getLevel());
        assertEquals(3, doc.getSection(0).getListBlock(0).getListElement(0).getSentence(0).getLineNumber());
        assertEquals(2, doc.getSection(0).getListBlock(0).getListElement(0).getSentence(0).getStartPositionOffset());

        assertEquals("Toyoko Line", doc.getSection(0).getListBlock(0).getListElement(1).getSentence(0).getContent());
        assertEquals(2, doc.getSection(0).getListBlock(0).getListElement(1).getLevel());
        assertEquals(4, doc.getSection(0).getListBlock(0).getListElement(1).getSentence(0).getLineNumber());
        assertEquals(6, doc.getSection(0).getListBlock(0).getListElement(1).getSentence(0).getStartPositionOffset());

        assertEquals("Denentoshi Line", doc.getSection(0).getListBlock(0).getListElement(2).getSentence(0).getContent());
        assertEquals(2, doc.getSection(0).getListBlock(0).getListElement(2).getLevel());
        assertEquals(5, doc.getSection(0).getListBlock(0).getListElement(2).getSentence(0).getLineNumber());
        assertEquals(6, doc.getSection(0).getListBlock(0).getListElement(2).getSentence(0).getStartPositionOffset());

        assertEquals("Keio", doc.getSection(0).getListBlock(0).getListElement(3).getSentence(0).getContent());
        assertEquals(1, doc.getSection(0).getListBlock(0).getListElement(3).getLevel());
        assertEquals(6, doc.getSection(0).getListBlock(0).getListElement(3).getSentence(0).getLineNumber());
        assertEquals(2, doc.getSection(0).getListBlock(0).getListElement(3).getSentence(0).getStartPositionOffset());

        assertEquals("Odakyu", doc.getSection(0).getListBlock(0).getListElement(4).getSentence(0).getContent());
        assertEquals(1, doc.getSection(0).getListBlock(0).getListElement(4).getLevel());
        assertEquals(7, doc.getSection(0).getListBlock(0).getListElement(4).getSentence(0).getLineNumber());
        assertEquals(2, doc.getSection(0).getListBlock(0).getListElement(4).getSentence(0).getStartPositionOffset());
    }

    @Test
    public void testGenerateDocumentWithMultipleSentenceInOneSentence() {
        String sampleText =
                "Tokyu is a good railway company. The company is reliable. In addition it is rich.";

        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(3, firstParagraph.getNumberOfSentences());

        assertEquals("Tokyu is a good railway company.", firstParagraph.getSentence(0).getContent());
        assertEquals(1, firstParagraph.getSentence(0).getLineNumber());
        assertEquals(0, firstParagraph.getSentence(0).getStartPositionOffset());

        assertEquals(" The company is reliable.", firstParagraph.getSentence(1).getContent());
        assertEquals(1, firstParagraph.getSentence(1).getLineNumber());
        assertEquals(32, firstParagraph.getSentence(1).getStartPositionOffset());

        assertEquals(" In addition it is rich.", firstParagraph.getSentence(2).getContent());
        assertEquals(1, firstParagraph.getSentence(2).getLineNumber());
        assertEquals(57, firstParagraph.getSentence(2).getStartPositionOffset());
    }

    @Test
    public void testGenerateDocumentWithMultipleSentencesWithVaraiousStopCharacters() {
        String sampleText = "Is Tokyu a good railway company? The company is reliable. In addition it is rich!\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(3, firstParagraph.getNumberOfSentences());

        assertEquals("Is Tokyu a good railway company?", doc.getSection(0).getParagraph(0).getSentence(0).getContent());
        assertEquals(1, doc.getSection(0).getParagraph(0).getSentence(0).getLineNumber());
        assertEquals(0, doc.getSection(0).getParagraph(0).getSentence(0).getStartPositionOffset());

        assertEquals(" The company is reliable.", doc.getSection(0).getParagraph(0).getSentence(1).getContent());
        assertEquals(1, doc.getSection(0).getParagraph(0).getSentence(1).getLineNumber());
        assertEquals(32, doc.getSection(0).getParagraph(0).getSentence(1).getStartPositionOffset());

        assertEquals(" In addition it is rich!", doc.getSection(0).getParagraph(0).getSentence(2).getContent());
        assertEquals(1, doc.getSection(0).getParagraph(0).getSentence(2).getLineNumber());
        assertEquals(57, doc.getSection(0).getParagraph(0).getSentence(2).getStartPositionOffset());
    }

    @Test
    public void testGenerateDocumentWithMultipleSentenceInMultipleSentences() {
        String sampleText = "Tokyu is a good railway company. The company is reliable. In addition it\n";
        sampleText += "is rich. I like the company. However someone does not like it.";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(5, firstParagraph.getNumberOfSentences());

        assertEquals("Tokyu is a good railway company.", doc.getSection(0).getParagraph(0).getSentence(0).getContent());
        assertEquals(1, doc.getSection(0).getParagraph(0).getSentence(0).getLineNumber());
        assertEquals(0, doc.getSection(0).getParagraph(0).getSentence(0).getStartPositionOffset());

        assertEquals(" The company is reliable.", doc.getSection(0).getParagraph(0).getSentence(1).getContent());
        assertEquals(1, doc.getSection(0).getParagraph(0).getSentence(1).getLineNumber());
        assertEquals(32, doc.getSection(0).getParagraph(0).getSentence(1).getStartPositionOffset());

        assertEquals(" In addition it is rich.", doc.getSection(0).getParagraph(0).getSentence(2).getContent());
        assertEquals(1, doc.getSection(0).getParagraph(0).getSentence(2).getLineNumber());
        assertEquals(57, doc.getSection(0).getParagraph(0).getSentence(2).getStartPositionOffset());

        assertEquals(" I like the company.", doc.getSection(0).getParagraph(0).getSentence(3).getContent());
        assertEquals(2, doc.getSection(0).getParagraph(0).getSentence(3).getLineNumber());
        assertEquals(8, doc.getSection(0).getParagraph(0).getSentence(3).getStartPositionOffset());

        assertEquals(" However someone does not like it.", doc.getSection(0).getParagraph(0).getSentence(4).getContent());
        assertEquals(2, doc.getSection(0).getParagraph(0).getSentence(4).getLineNumber());
        assertEquals(28, doc.getSection(0).getParagraph(0).getSentence(4).getStartPositionOffset());
    }

    @Test
    public void testGenerateDocumentWithTwoSentencesWithMultipleShortLine() {
        String sampleText = "Tokyu\n" +
                "is a good\n" +
                "railway company. But there\n" +
                "are competitors.";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(2, firstParagraph.getNumberOfSentences());

        assertEquals("Tokyu is a good railway company.", doc.getSection(0).getParagraph(0).getSentence(0).getContent());
        assertEquals(1, doc.getSection(0).getParagraph(0).getSentence(0).getLineNumber());
        assertEquals(0, doc.getSection(0).getParagraph(0).getSentence(0).getStartPositionOffset());
        assertEquals(32, doc.getSection(0).getParagraph(0).getSentence(0).getOffsetMapSize());

        assertEquals(" But there are competitors.", doc.getSection(0).getParagraph(0).getSentence(1).getContent());
        assertEquals(3, doc.getSection(0).getParagraph(0).getSentence(1).getLineNumber());
        assertEquals(16, doc.getSection(0).getParagraph(0).getSentence(1).getStartPositionOffset());
    }

    @Test
    public void testMappingTableWithShortSentence() {
        String sampleText = "Tsu is a city.";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(1, firstParagraph.getNumberOfSentences());
        assertEquals("Tsu is a city.", doc.getSection(0).getParagraph(0).getSentence(0).getContent());

        assertEquals(1, doc.getSection(0).getParagraph(0).getSentence(0).getLineNumber());
        assertEquals(0, doc.getSection(0).getParagraph(0).getSentence(0).getStartPositionOffset());
        assertEquals(doc.getSection(0).getParagraph(0).getSentence(0).getContent().length(),
                doc.getSection(0).getParagraph(0).getSentence(0).getOffsetMapSize());

    }

    @Test
    public void testGenerateDocumentWitVoidContent() {
        String sampleText = "";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        assertEquals(0, firstSections.getParagraphs().size());
//    assertEquals(false, firstSections.getParagraphs().hasNext());
    }

    @Test
    public void testGenerateDocumentWithPeriodInSuccession() {
        String sampleText = "...";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(1, firstParagraph.getNumberOfSentences());
    }


    @Test
    public void testGenerateDocumentWitoutPeriodInLastSentence() {
        String sampleText = "Hongo is located at the west of Tokyo. Saitama is located at the north";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(2, firstParagraph.getNumberOfSentences());
    }

    @Test
    public void testGenerateDocumentWithSentenceLongerThanOneLine() {
        String sampleText = "This is a good day.\n";
        sampleText += "Hongo is located at the west of Tokyo ";
        sampleText += "which is the capital of Japan ";
        sampleText += "which is not located in the south of the earth.";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(2, firstParagraph.getNumberOfSentences());
    }

    @Test
    public void testPlainLink() {
        String sampleText = "It is not [Google](http://google.com).";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(1, firstParagraph.getNumberOfSentences());
        assertEquals(1, firstParagraph.getSentence(0).getLinks().size()); // TODO: refactor Sentence method
        // PegDown Parser is related to visit(RefLinkNode) method
        assertEquals("http://google.com", firstParagraph.getSentence(0).getLinks().get(0));
        assertEquals("It is not Google.",
                firstParagraph.getSentence(0).getContent());
        assertEquals(firstParagraph.getSentence(0).getContent().length(),
                firstParagraph.getSentence(0).getOffsetMapSize());

        List<LineOffset> expectedOffsets = initializeMappingTable(
                new LineOffset(1, 0),
                new LineOffset(1, 1),
                new LineOffset(1, 2),
                new LineOffset(1, 3),
                new LineOffset(1, 4),
                new LineOffset(1, 5),
                new LineOffset(1, 6),
                new LineOffset(1, 7),
                new LineOffset(1, 8),
                new LineOffset(1, 9),
                new LineOffset(1, 11),
                new LineOffset(1, 12),
                new LineOffset(1, 13),
                new LineOffset(1, 14),
                new LineOffset(1, 15),
                new LineOffset(1, 16),
                new LineOffset(1, 37));

        assertEquals(expectedOffsets.size(), firstParagraph.getSentence(0).getOffsetMapSize());
        for (int i = 0; i < expectedOffsets.size(); i++) {
            assertEquals(expectedOffsets.get(i), firstParagraph.getSentence(0).getOffset(i).get());
        }
    }

    @Test
    public void testPlainLinkWithSpaces() {
        // PegDown Parser is related to visit(ExpLinkNode) method
        String sampleText = "the url is not [Google]( http://google.com ).";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(1, firstParagraph.getNumberOfSentences());
        assertEquals(1, firstParagraph.getSentence(0).getLinks().size());
        assertEquals("http://google.com", firstParagraph.getSentence(0).getLinks().get(0));
        assertEquals("the url is not Google.",
                firstParagraph.getSentence(0).getContent());
    }

    @Test
    public void testPlainTwoLinkWithinOneLine() {
        // PegDown Parser is related tovisit(AutoLinkNode) method
        String sampleText = "url of google is http://google.com. http://yahoo.com is Yahoo url.";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(2, firstParagraph.getNumberOfSentences());
        assertEquals(1, firstParagraph.getSentence(0).getLinks().size());
        assertEquals("http://google.com", firstParagraph.getSentence(0).getLinks().get(0));
        assertEquals("url of google is http://google.com.",
                firstParagraph.getSentence(0).getContent());
        assertEquals("http://yahoo.com", firstParagraph.getSentence(1).getLinks().get(0));
        assertEquals(" http://yahoo.com is Yahoo url.",
                firstParagraph.getSentence(1).getContent());
    }

    @Test
    public void testPlainTwoLinkWithinOneSentence() {
        String sampleText = "http://yahoo.com and http://google.com is Google and Yahoo urls.";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(1, firstParagraph.getNumberOfSentences());
        assertEquals(2, firstParagraph.getSentence(0).getLinks().size());
        assertEquals("http://yahoo.com", firstParagraph.getSentence(0).getLinks().get(1));
        assertEquals("http://google.com", firstParagraph.getSentence(0).getLinks().get(0));
        assertEquals("http://yahoo.com and http://google.com is Google and Yahoo urls.",
                firstParagraph.getSentence(0).getContent());

    }

    @Test
    public void testLinkWithoutTag() {
        // PegDown Parser is related tovisit(AutoLinkNode) method
        String sampleText = "url of google is http://google.com.";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(1, firstParagraph.getNumberOfSentences());
        assertEquals(1, firstParagraph.getSentence(0).getLinks().size());
        assertEquals("http://google.com", firstParagraph.getSentence(0).getLinks().get(0));
        assertEquals("url of google is http://google.com.",
                firstParagraph.getSentence(0).getContent());
    }

    @Test
    public void testDocumentWithSimpleSentence() {
        String sampleText = "It is a good day.";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals("It is a good day.", firstParagraph.getSentence(0).getContent());
        assertEquals(17, firstParagraph.getSentence(0).getContent().length());
        assertEquals(1, firstParagraph.getSentence(0).getLineNumber());
        List<LineOffset> expectedOffsets = initializeMappingTable(
                new LineOffset(1, 0),
                new LineOffset(1, 1),
                new LineOffset(1, 2),
                new LineOffset(1, 3),
                new LineOffset(1, 4),
                new LineOffset(1, 5),
                new LineOffset(1, 6),
                new LineOffset(1, 7),
                new LineOffset(1, 8),
                new LineOffset(1, 9),
                new LineOffset(1, 10),
                new LineOffset(1, 11),
                new LineOffset(1, 12),
                new LineOffset(1, 13),
                new LineOffset(1, 14),
                new LineOffset(1, 15),
                new LineOffset(1, 16));

        assertEquals(expectedOffsets.size(), firstParagraph.getSentence(0).getOffsetMapSize());
        for (int i = 0; i < expectedOffsets.size(); i++) {
            assertEquals(expectedOffsets.get(i), firstParagraph.getSentence(0).getOffset(i).get());
        }
    }

    @Test
    public void testDocumentWithItalicWord() {
        String sampleText = "It is a *good* day.";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals("It is a good day.", firstParagraph.getSentence(0).getContent());
        List<LineOffset> expectedOffsets = initializeMappingTable(
                new LineOffset(1, 0),
                new LineOffset(1, 1),
                new LineOffset(1, 2),
                new LineOffset(1, 3),
                new LineOffset(1, 4),
                new LineOffset(1, 5),
                new LineOffset(1, 6),
                new LineOffset(1, 7),
                new LineOffset(1, 9),
                new LineOffset(1, 10),
                new LineOffset(1, 11),
                new LineOffset(1, 12),
                new LineOffset(1, 14),
                new LineOffset(1, 15),
                new LineOffset(1, 16),
                new LineOffset(1, 17),
                new LineOffset(1, 18));

        assertEquals(expectedOffsets.size(), firstParagraph.getSentence(0).getOffsetMapSize());
        for (int i = 0; i < expectedOffsets.size(); i++) {
            assertEquals(expectedOffsets.get(i), firstParagraph.getSentence(0).getOffset(i).get());
        }
    }

    @Test
    public void testDocumentWithMultipleItalicWords() {
        String sampleText = "*It* is a _good_ day.\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals("It is a good day.", firstParagraph.getSentence(0).getContent());
        List<LineOffset> expectedOffsets = initializeMappingTable(
                new LineOffset(1, 1),
                new LineOffset(1, 2),
                new LineOffset(1, 4),
                new LineOffset(1, 5),
                new LineOffset(1, 6),
                new LineOffset(1, 7),
                new LineOffset(1, 8),
                new LineOffset(1, 9),
                new LineOffset(1, 11),
                new LineOffset(1, 12),
                new LineOffset(1, 13),
                new LineOffset(1, 14),
                new LineOffset(1, 16),
                new LineOffset(1, 17),
                new LineOffset(1, 18),
                new LineOffset(1, 19),
                new LineOffset(1, 20));

        assertEquals(expectedOffsets.size(), firstParagraph.getSentence(0).getOffsetMapSize());
        for (int i = 0; i < expectedOffsets.size(); i++) {
            assertEquals(expectedOffsets.get(i), firstParagraph.getSentence(0).getOffset(i).get());
        }
    }

    @Test
    public void testDocumentWithMultipleNearStrongWords() {
        String sampleText = "It is **a** __good__ day.\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals("It is a good day.", firstParagraph.getSentence(0).getContent());
        List<LineOffset> expectedOffsets = initializeMappingTable(
                new LineOffset(1, 0),
                new LineOffset(1, 1),
                new LineOffset(1, 2),
                new LineOffset(1, 3),
                new LineOffset(1, 4),
                new LineOffset(1, 5),
                new LineOffset(1, 8),
                new LineOffset(1, 11),
                new LineOffset(1, 14),
                new LineOffset(1, 15),
                new LineOffset(1, 16),
                new LineOffset(1, 17),
                new LineOffset(1, 20),
                new LineOffset(1, 21),
                new LineOffset(1, 22),
                new LineOffset(1, 23),
                new LineOffset(1, 24));
        assertEquals(expectedOffsets.size(), firstParagraph.getSentence(0).getOffsetMapSize());
        for (int i = 0; i < expectedOffsets.size(); i++) {
            assertEquals(expectedOffsets.get(i), firstParagraph.getSentence(0).getOffset(i).get());
        }
    }

    @Test
    public void testDocumentWithHeaderContainingMultipleSentences()
            throws UnsupportedEncodingException {
        String sampleText = "";
        sampleText += "# About Gunma. About Saitama.\n";
        sampleText += "Gunma is located at west of Saitama.\n";
        sampleText += "The word also have positive meaning. However it is a bit weird.";

        Document doc = createFileContent(sampleText);
        Section lastSection = doc.getSection(doc.size() - 1);
        assertEquals(2, lastSection.getHeaderContentsListSize());

        assertEquals("About Gunma.", lastSection.getHeaderContent(0).getContent());
        assertEquals(1, lastSection.getHeaderContent(0).getLineNumber());
        assertEquals(2, lastSection.getHeaderContent(0).getStartPositionOffset());

        List<LineOffset> expectedOffsets1 = initializeMappingTable(
                new LineOffset(1, 2),
                new LineOffset(1, 3),
                new LineOffset(1, 4),
                new LineOffset(1, 5),
                new LineOffset(1, 6),
                new LineOffset(1, 7),
                new LineOffset(1, 8),
                new LineOffset(1, 9),
                new LineOffset(1, 10),
                new LineOffset(1, 11),
                new LineOffset(1, 12),
                new LineOffset(1, 13));

        assertEquals(expectedOffsets1.size(), lastSection.getHeaderContent(0).getOffsetMapSize());
        for (int i = 0; i < expectedOffsets1.size(); i++) {
            assertEquals(expectedOffsets1.get(i),
                    lastSection.getHeaderContent(0).getOffset(i).get());
        }

        List<LineOffset> expectedOffsets2 = initializeMappingTable(
                new LineOffset(1, 14),
                new LineOffset(1, 15),
                new LineOffset(1, 16),
                new LineOffset(1, 17),
                new LineOffset(1, 18),
                new LineOffset(1, 19),
                new LineOffset(1, 20),
                new LineOffset(1, 21),
                new LineOffset(1, 22),
                new LineOffset(1, 23),
                new LineOffset(1, 24),
                new LineOffset(1, 25),
                new LineOffset(1, 26),
                new LineOffset(1, 27),
                new LineOffset(1, 28));

        assertEquals(" About Saitama.", lastSection.getHeaderContent(1).getContent());
        assertEquals(1, lastSection.getHeaderContent(1).getLineNumber());
        assertEquals(14, lastSection.getHeaderContent(1).getStartPositionOffset());

        assertEquals(expectedOffsets2.size(), lastSection.getHeaderContent(1).getOffsetMapSize());
        for (int i = 0; i < expectedOffsets2.size(); i++) {
            assertEquals(expectedOffsets2.get(i),
                    lastSection.getHeaderContent(1).getOffset(i).get());
        }
    }

    @Test
    public void testDocumentWithHeaderWithoutPeriods()
            throws UnsupportedEncodingException {
        String sampleText = "";
        sampleText += "# About Gunma\n";
        sampleText += "Gunma is located at west of Saitama.\n";
        sampleText += "The word also have posive meaning. Hower it is a bit wired.";

        Document doc = createFileContent(sampleText);
        Section lastSection = doc.getSection(doc.size() - 1);
        assertEquals(1, lastSection.getHeaderContentsListSize());
        assertEquals("About Gunma", lastSection.getHeaderContent(0).getContent());
        assertEquals(2, lastSection.getHeaderContent(0).getStartPositionOffset());

        List<LineOffset> expectedOffsets = initializeMappingTable(
                new LineOffset(1, 2),
                new LineOffset(1, 3),
                new LineOffset(1, 4),
                new LineOffset(1, 5),
                new LineOffset(1, 6),
                new LineOffset(1, 7),
                new LineOffset(1, 8),
                new LineOffset(1, 9),
                new LineOffset(1, 10),
                new LineOffset(1, 11),
                new LineOffset(1, 12));
        assertEquals(expectedOffsets.size(), lastSection.getHeaderContent(0).getOffsetMapSize());
        for (int i = 0; i < expectedOffsets.size(); i++) {
            assertEquals(expectedOffsets.get(i),
                    lastSection.getHeaderContent(0).getOffset(i).get());
        }
    }

    @Test
    public void testDocumentWithList()
            throws UnsupportedEncodingException {
        String sampleText = "";
        sampleText += "# About Gunma. About Saitama.\n";
        sampleText += "* Gunma is located at west of Saitama.\n";
        sampleText += "* The word also have posive meaning. Hower it is a bit wired.";

        Document doc = createFileContent(sampleText);
        Section lastSection = doc.getSection(doc.size() - 1);
        ListBlock listBlock = lastSection.getListBlock(0);
        assertEquals(2, listBlock.getNumberOfListElements());
        assertEquals(1, listBlock.getListElement(0).getNumberOfSentences());
        assertEquals("Gunma is located at west of Saitama.",
                listBlock.getListElement(0).getSentence(0).getContent());
        assertEquals("The word also have posive meaning.",
                listBlock.getListElement(1).getSentence(0).getContent());
        assertEquals(" Hower it is a bit wired.",
                listBlock.getListElement(1).getSentence(1).getContent());
    }

    @Test
    public void testDocumentWithListWithoutPeriod()
            throws UnsupportedEncodingException {
        String sampleText = "";
        sampleText += "# About Gunma. About Saitama.\n";
        sampleText += "* Gunma is located at west of Saitama\n";

        Document doc = createFileContent(sampleText);
        Section lastSection = doc.getSection(doc.size() - 1);
        ListBlock listBlock = lastSection.getListBlock(0);
        assertEquals(1, listBlock.getNumberOfListElements());
        assertEquals(1, listBlock.getListElement(0).getNumberOfSentences());
        assertEquals(2, listBlock.getListElement(0).getSentence(0).getLineNumber());
        assertEquals("Gunma is located at west of Saitama",
                listBlock.getListElement(0).getSentence(0).getContent());
    }

    @Test
    public void testDocumentWithMultipleSections()
            throws UnsupportedEncodingException {
        String sampleText = "# Prefectures in Japan.\n";
        sampleText += "There are 47 prefectures in Japan.\n";
        sampleText += "\n";
        sampleText += "Each prefectures has its features.\n";
        sampleText += "## Gunma \n";
        sampleText += "Gumma is very beautiful";

        Document doc = createFileContent(sampleText);
        assertEquals(3, doc.size());
        Section rootSection = doc.getSection(0);
        Section h1Section = doc.getSection(1);
        Section h2Section = doc.getSection(2);

        assertEquals(0, rootSection.getLevel());
        assertEquals(1, h1Section.getLevel());
        assertEquals(2, h2Section.getLevel());

        assertEquals(rootSection.getSubSection(0), h1Section);
        assertEquals(h1Section.getParentSection(), rootSection);
        assertEquals(h2Section.getParentSection(), h1Section);
        assertEquals(rootSection.getParentSection(), null);

        assertEquals(0, rootSection.getHeaderContent(0).getLineNumber());
        assertEquals(1, h1Section.getHeaderContent(0).getLineNumber());
        assertEquals(5, h2Section.getHeaderContent(0).getLineNumber());
    }

    @Test
    public void testDocumentWithUnderlineSections()
            throws UnsupportedEncodingException {
        String sampleText = "Prefectures in Japan.\n";
        sampleText += "====\n";
        sampleText += "There are 47 prefectures in Japan.\n";
        sampleText += "\n";
        sampleText += "Each prefectures has its features.\n";
        sampleText += "Gunma\n";
        sampleText += "----\n";
        sampleText += "Gumma is very beautiful";

        Document doc = createFileContent(sampleText);
        assertEquals(3, doc.size());
        Section rootSection = doc.getSection(0);
        Section h1Section = doc.getSection(1);
        Section h2Section = doc.getSection(2);

        assertEquals(0, rootSection.getLevel());
        assertEquals(1, h1Section.getLevel());
        assertEquals(2, h2Section.getLevel());

        assertEquals(rootSection.getSubSection(0), h1Section);
        assertEquals(h1Section.getParentSection(), rootSection);
        assertEquals(h2Section.getParentSection(), h1Section);
        assertEquals(rootSection.getParentSection(), null);

        assertEquals(0, rootSection.getHeaderContent(0).getLineNumber());
        assertEquals(1, h1Section.getHeaderContent(0).getLineNumber());
        assertEquals(6, h2Section.getHeaderContent(0).getLineNumber());
    }

    /**
     * Note: currently redpen just skip the contents of table.
     * In the future, we will add validators on table size or table contents.
     */
    @Test
    public void testDocumentWithListWithTable()
            throws UnsupportedEncodingException {
        String sampleText = "";
        sampleText += "# Table\n\n" +
                "|--------|-------|\n" +
                "|Cool    | Stuff  |\n" +
                "|is this | really\n";

        Document doc = createFileContent(sampleText);
        Section lastSection = doc.getSection(doc.size() - 1);
        assertEquals(0, lastSection.getNumberOfParagraphs());
    }

    @Test
    public void testJapaneseDocumentSentences() throws UnsupportedEncodingException {
        String sampleText = "";
        sampleText += "# 僕の事。\n";
        sampleText += "わたしはカラオケが大好きです。\n";
        sampleText += "わたしもお寿司が大好きです。\n";
        sampleText += "\n";

        Document doc = createFileContent(sampleText, new Configuration.ConfigurationBuilder().setLanguage("ja").build());

        assertNotNull("doc is null", doc);
        assertEquals(2, doc.size());
        // first section
        final Section firstSection = doc.getSection(0);
        assertEquals(1, firstSection.getHeaderContentsListSize());
        assertEquals("", firstSection.getHeaderContent(0).getContent());
        assertEquals(0, firstSection.getNumberOfLists());
        assertEquals(0, firstSection.getNumberOfParagraphs());
        assertEquals(1, firstSection.getNumberOfSubsections());

        // 2nd section
        final Section secondSection = doc.getSection(1);
        assertEquals(1, secondSection.getHeaderContentsListSize());
        assertEquals("僕の事。", secondSection.getHeaderContent(0).getContent());
        assertEquals(1, secondSection.getHeaderContent(0).getLineNumber());
        assertEquals(2, secondSection.getHeaderContent(0).getStartPositionOffset());
        assertEquals(0, secondSection.getNumberOfLists());
        assertEquals(1, secondSection.getNumberOfParagraphs());
        assertEquals(2, secondSection.getParagraph(0).getNumberOfSentences());
        assertEquals("わたしもお寿司が大好きです。", secondSection.getParagraph(0).getSentence(1).getContent());
        assertEquals(firstSection, secondSection.getParentSection());
    }

    @Test
    public void testGenerateJapaneseDocument() {
        String sampleText = "埼玉は東京の北に存在する。";
        sampleText += "大きなベッドタウンであり、多くの人が住んでいる。";
        Configuration conf = new Configuration.ConfigurationBuilder()
                .setLanguage("ja").build();

        Document doc = createFileContent(sampleText, conf);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(2, firstParagraph.getNumberOfSentences());
    }

    @Test
    public void testGenerateJapaneseWithMultipleSentencesInOneLine() {
        String sampleText = "それは異なる．たとえば，\\n" +
                "以下のとおりである．";
        Configuration conf = new Configuration.ConfigurationBuilder()
                .setLanguage("ja")
                .addSymbol(new Symbol(FULL_STOP, '．', "."))
                .addSymbol(new Symbol(COMMA, '，', "、"))
                .build();

        Document doc = createFileContent(sampleText, conf);
        Section firstSection = doc.getSection(0);
        Paragraph firstParagraph = firstSection.getParagraph(0);
        assertEquals(2, firstParagraph.getNumberOfSentences());
        assertEquals("それは異なる．",
                firstParagraph.getSentence(0).getContent());
        assertEquals("たとえば，\\n以下のとおりである．",
                firstParagraph.getSentence(1).getContent());
    }


    @Test
    public void testErrorPositionOfMarkdownParser() throws RedPenException {
        String sampleText = "This is a good day。\n"; // invalid end of sentence symbol
        Configuration conf = new Configuration.ConfigurationBuilder()
                .setLanguage("en")
                .build();
        List<Document> documents = new ArrayList<>();
        documents.add(createFileContent(sampleText, conf));

        Configuration configuration = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(
                        new ValidatorConfiguration("InvalidSymbol"))
                .build();

        RedPen redPen = new RedPen(configuration);
        List<ValidationError> errors = redPen.validate(documents).get(documents.get(0));
        assertEquals(1, errors.size());
        assertEquals("InvalidSymbol", errors.get(0).getValidatorName());
        assertEquals(19, errors.get(0).getSentence().getContent().length());
        assertEquals(new LineOffset(1, 18), errors.get(0).getStartPosition().get());
        assertEquals(new LineOffset(1, 19), errors.get(0).getEndPosition().get());
    }

    private Document createFileContent(String inputDocumentString,
                                       Configuration config) {
        DocumentParser parser = DocumentParser.MARKDOWN;

        try {
            return parser.parse(inputDocumentString, new SentenceExtractor(config.getSymbolTable()), config.getTokenizer());
        } catch (RedPenException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Document createFileContent(String inputDocumentString) {
        DocumentParser parser = DocumentParser.MARKDOWN;
        Document doc = null;
        try {
            Configuration configuration = new Configuration.ConfigurationBuilder().build();
            doc = parser.parse(inputDocumentString, new SentenceExtractor(configuration.getSymbolTable()),
                    configuration.getTokenizer());
        } catch (RedPenException e) {
            e.printStackTrace();
            fail();
        }
        return doc;
    }

    private static List<LineOffset> initializeMappingTable(LineOffset... offsets) {
        List<LineOffset> offsetTable = new ArrayList<>();
        for (LineOffset offset : offsets) {
            offsetTable.add(offset);
        }
        return offsetTable;
    }
}
