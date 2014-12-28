/**
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
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
import cc.redpen.model.Document;
import cc.redpen.model.ListBlock;
import cc.redpen.model.Paragraph;
import cc.redpen.model.Section;
import cc.redpen.parser.DocumentParser;
import cc.redpen.parser.SentenceExtractor;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import static cc.redpen.config.SymbolType.COMMA;
import static cc.redpen.config.SymbolType.FULL_STOP;
import static org.junit.Assert.*;

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
        sampleText += "Gekioko pun pun maru means very very angry.\n";
        sampleText += "\n";
        sampleText += "The word also have posive meaning.\n";
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
        sampleText += "The word also have positive meaning. However it is a bit wired.";

        Document doc = createFileContent(sampleText);
        assertNotNull("doc is null", doc);
        assertEquals(3, doc.size());
        // first section
        final Section firstSection = doc.getSection(0);
        assertEquals(1, firstSection.getHeaderContentsListSize());
        assertEquals("", firstSection.getHeaderContent(0).content);
        assertEquals(0, firstSection.getNumberOfLists());
        assertEquals(0, firstSection.getNumberOfParagraphs());
        assertEquals(1, firstSection.getNumberOfSubsections());

        // 2nd section
        final Section secondSection = doc.getSection(1);
        assertEquals(1, secondSection.getHeaderContentsListSize());
        assertEquals("About Gekioko.", secondSection.getHeaderContent(0).content);
        assertEquals(1, secondSection.getHeaderContent(0).lineNum);
        assertEquals(0, secondSection.getNumberOfLists());
        assertEquals(2, secondSection.getNumberOfParagraphs());
        assertEquals(1, secondSection.getNumberOfSubsections());
        assertEquals(firstSection, secondSection.getParentSection());
        // validate paragraph in 2nd section
        assertEquals(1, secondSection.getParagraph(0).getNumberOfSentences());
        assertEquals(true, secondSection.getParagraph(0).getSentence(0).isFirstSentence);
        assertEquals(2, secondSection.getParagraph(0).getSentence(0).lineNum);
        assertEquals(1, secondSection.getParagraph(1).getNumberOfSentences());
        assertEquals(true, secondSection.getParagraph(1).getSentence(0).isFirstSentence);
        assertEquals(4, secondSection.getParagraph(1).getSentence(0).lineNum);

        Section lastSection = doc.getSection(doc.size() - 1);
        assertEquals(1, lastSection.getNumberOfLists());
        assertEquals(5, lastSection.getListBlock(0).getNumberOfListElements());
        assertEquals(2, lastSection.getNumberOfParagraphs());
        assertEquals(1, lastSection.getHeaderContentsListSize());
        assertEquals(0, lastSection.getNumberOfSubsections());
        assertEquals("About Gunma.", lastSection.getHeaderContent(0).content);
        assertEquals(secondSection, lastSection.getParentSection());

        // validate paragraph in last section
        assertEquals(1, lastSection.getParagraph(0).getNumberOfSentences());
        assertEquals(true, lastSection.getParagraph(0).getSentence(0).isFirstSentence);
        assertEquals(7, lastSection.getParagraph(0).getSentence(0).lineNum);
        assertEquals(2, lastSection.getParagraph(1).getNumberOfSentences());
        assertEquals(true, lastSection.getParagraph(1).getSentence(0).isFirstSentence);
        assertEquals(15, lastSection.getParagraph(1).getSentence(0).lineNum);
        assertEquals(false, lastSection.getParagraph(1).getSentence(1).isFirstSentence);
        assertEquals(15, lastSection.getParagraph(1).getSentence(1).lineNum);
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
        assertEquals("Tokyu", doc.getSection(0).getListBlock(0).getListElement(0).getSentence(0).content);
        assertEquals(1, doc.getSection(0).getListBlock(0).getListElement(0).getLevel());
        assertEquals("Toyoko Line", doc.getSection(0).getListBlock(0).getListElement(1).getSentence(0).content);
        assertEquals(2, doc.getSection(0).getListBlock(0).getListElement(1).getLevel());
        assertEquals("Denentoshi Line", doc.getSection(0).getListBlock(0).getListElement(2).getSentence(0).content);
        assertEquals(2, doc.getSection(0).getListBlock(0).getListElement(2).getLevel());
        assertEquals("Keio", doc.getSection(0).getListBlock(0).getListElement(3).getSentence(0).content);
        assertEquals(1, doc.getSection(0).getListBlock(0).getListElement(3).getLevel());
        assertEquals("Odakyu", doc.getSection(0).getListBlock(0).getListElement(4).getSentence(0).content);
        assertEquals(1, doc.getSection(0).getListBlock(0).getListElement(4).getLevel());
    }

    @Test
    public void testGenerateDocumentWithMultipleSentenceInOneSentence() {
        String sampleText =
                "Tokyu is a good railway company. The company is reliable. In addition it is rich.";
        String[] expectedResult = {"Tokyu is a good railway company.",
                " The company is reliable.", " In addition it is rich."};
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(3, firstParagraph.getNumberOfSentences());
        for (int i = 0; i < expectedResult.length; i++) {
            assertEquals(expectedResult[i], firstParagraph.getSentence(i).content);
        }
    }

    @Test
    public void testGenerateDocumentWithMultipleSentencesWithVaraiousStopCharacters() {
        String sampleText = "Is Tokyu a good railway company? The company is reliable. In addition it is rich!\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(3, firstParagraph.getNumberOfSentences());
        assertEquals("Is Tokyu a good railway company?", doc.getSection(0).getParagraph(0).getSentence(0).content);
        assertEquals(" The company is reliable.", doc.getSection(0).getParagraph(0).getSentence(1).content);
        assertEquals(" In addition it is rich!", doc.getSection(0).getParagraph(0).getSentence(2).content);
    }

    @Test
    public void testGenerateDocumentWithMultipleSentenceInMultipleSentences() {
        String sampleText = "Tokyu is a good railway company. The company is reliable. In addition it is rich.\n";
        sampleText += "I like the company. Howerver someone does not like it.";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(5, firstParagraph.getNumberOfSentences());
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
        String sampleText = "this is not a [pen], but also this is not [Google](http://google.com) either.";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(1, firstParagraph.getNumberOfSentences());
        assertEquals(1, firstParagraph.getSentence(0).links.size());
        // PegDown Parser is related to visit(RefLinkNode) method
        assertEquals("http://google.com", firstParagraph.getSentence(0).links.get(0));
        assertEquals("this is not a [pen], but also this is not Google either.",
                firstParagraph.getSentence(0).content);
    }

    @Test
    public void testPlainLinkWithSpaces() {
        // PegDown Parser is related to visit(ExpLinkNode) method
        String sampleText = "the url is not [Google]( http://google.com ).";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(1, firstParagraph.getNumberOfSentences());
        assertEquals(1, firstParagraph.getSentence(0).links.size());
        assertEquals("http://google.com", firstParagraph.getSentence(0).links.get(0));
        assertEquals("the url is not Google.",
                firstParagraph.getSentence(0).content);
    }

    @Test
    public void testLinkWithoutTag() {
        // PegDown Parser is related tovisit(AutoLinkNode) method
        String sampleText = "url of google is http://google.com.";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(1, firstParagraph.getNumberOfSentences());
        assertEquals(1, firstParagraph.getSentence(0).links.size());
        assertEquals("http://google.com", firstParagraph.getSentence(0).links.get(0));
        assertEquals("url of google is http://google.com.",
                firstParagraph.getSentence(0).content);
    }

    @Test
    public void testDocumentWithItalicWord() {
        String sampleText = "This is a *good* day.\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals("This is a good day.", firstParagraph.getSentence(0).content);
    }

    @Test
    public void testDocumentWithMultipleItalicWords() {
        String sampleText = "*This* is a _good_ day.\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals("This is a good day.", firstParagraph.getSentence(0).content);
    }

    @Test
    public void testDocumentWithMultipleNearStrongWords() {
        String sampleText = "This is **a** __good__ day.\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals("This is a good day.", firstParagraph.getSentence(0).content);
    }

    @Test
    public void testDocumentWithItalicExpression() {
        String sampleText = "This is *a good* day.\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals("This is a good day.", firstParagraph.getSentence(0).content);
    }


    @Test
    public void testDocumentWithHeaderCotainingMultipleSentences()
            throws UnsupportedEncodingException {
        String sampleText = "";
        sampleText += "# About Gunma. About Saitama.\n";
        sampleText += "Gunma is located at west of Saitama.\n";
        sampleText += "The word also have posive meaning. Hower it is a bit wired.";

        Document doc = createFileContent(sampleText);
        Section lastSection = doc.getSection(doc.size() - 1);
        assertEquals(2, lastSection.getHeaderContentsListSize());
        assertEquals("About Gunma.", lastSection.getHeaderContent(0).content);
        assertEquals(" About Saitama.", lastSection.getHeaderContent(1).content);
    }

    @Test
    public void testDocumentWithHeaderWitoutPeriod()
            throws UnsupportedEncodingException {
        String sampleText = "";
        sampleText += "# About Gunma\n";
        sampleText += "Gunma is located at west of Saitama.\n";
        sampleText += "The word also have posive meaning. Hower it is a bit wired.";

        Document doc = createFileContent(sampleText);
        Section lastSection = doc.getSection(doc.size() - 1);
        assertEquals(1, lastSection.getHeaderContentsListSize());
        assertEquals("About Gunma", lastSection.getHeaderContent(0).content);
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
                listBlock.getListElement(0).getSentence(0).content);
        assertEquals("The word also have posive meaning.",
                listBlock.getListElement(1).getSentence(0).content);
        assertEquals(" Hower it is a bit wired.",
                listBlock.getListElement(1).getSentence(1).content);
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
        assertEquals(2, listBlock.getListElement(0).getSentence(0).lineNum);
        assertEquals("Gunma is located at west of Saitama",
                listBlock.getListElement(0).getSentence(0).content);
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

        assertEquals(0, rootSection.getHeaderContent(0).lineNum);
        assertEquals(1, h1Section.getHeaderContent(0).lineNum);
        assertEquals(5, h2Section.getHeaderContent(0).lineNum);
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

        assertEquals(0, rootSection.getHeaderContent(0).lineNum);
        assertEquals(1, h1Section.getHeaderContent(0).lineNum);
        assertEquals(6, h2Section.getHeaderContent(0).lineNum);
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
                "|Cool    | Shit  |\n" +
                "|is this | really\n";

        Document doc = createFileContent(sampleText);
        Section lastSection = doc.getSection(doc.size() - 1);
        assertEquals(0, lastSection.getNumberOfParagraphs());
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
                .setSymbol(new Symbol(FULL_STOP, '．', "."))
                .setSymbol(new Symbol(COMMA, '，', "、"))
                .build();

        Document doc = createFileContent(sampleText, conf);
        Section firstSection = doc.getSection(0);
        Paragraph firstParagraph = firstSection.getParagraph(0);
        assertEquals(2, firstParagraph.getNumberOfSentences());
        assertEquals("それは異なる．",
                firstParagraph.getSentence(0).content);
        assertEquals("たとえば，\\n以下のとおりである．",
                firstParagraph.getSentence(1).content);
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
            doc = parser.parse(inputDocumentString, new SentenceExtractor(configuration.getSymbolTable()), configuration.getTokenizer());
        } catch (RedPenException e) {
            e.printStackTrace();
            fail();
        }
        return doc;
    }


}
