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
import cc.redpen.model.ListBlock;
import cc.redpen.model.Paragraph;
import cc.redpen.model.Section;
import cc.redpen.parser.BaseParserTest;
import cc.redpen.parser.LineOffset;
import cc.redpen.parser.SentenceExtractor;
import cc.redpen.validator.ValidationError;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

import static cc.redpen.parser.DocumentParser.WIKI;
import static java.util.Collections.singletonList;
import static java.util.stream.IntStream.range;
import static org.junit.Assert.assertEquals;

public class WikiParserTest extends BaseParserTest {
    @Test
    public void testBasicDocument() throws UnsupportedEncodingException {
        String sampleText = ""
            + "h1. About Gekioko.\n"
            + "Gekioko pun pun maru means very very angry.\n"
            + "\n"
            + "The word also have posive meaning.\n"
            + "h2. About Gunma.\n"
            + "\n"
            + "Gunma is located at west of Saitama.\n"
            + "- Features\n"
            + "-- Main City: Gumma City\n"
            + "-- Capical: 200 Millon\n"
            + "- Location\n"
            + "-- Japan\n"
            + "\n"
            + "The word also have posive meaning. Hower it is a bit wired.";

        Document doc = createFileContent(sampleText);
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
        assertEquals(0, secondSection.getNumberOfLists());
        assertEquals(2, secondSection.getNumberOfParagraphs());
        assertEquals(1, secondSection.getNumberOfSubsections());
        assertEquals(firstSection, secondSection.getParentSection());
        // validate paragraph in 2nd section
        assertEquals(1, secondSection.getParagraph(0).getNumberOfSentences());
        assertEquals(true, secondSection.getParagraph(0).getSentence(0).isFirstSentence());
        assertEquals(1, secondSection.getParagraph(1).getNumberOfSentences());
        assertEquals(true, secondSection.getParagraph(1).getSentence(0).isFirstSentence());

        // last section
        Section lastSection = doc.getSection(doc.size() - 1);
        assertEquals(1, lastSection.getNumberOfLists());
        assertEquals(5, lastSection.getListBlock(0).getNumberOfListElements());
        assertEquals(2, lastSection.getNumberOfParagraphs());
        assertEquals(1, lastSection.getHeaderContentsListSize());
        assertEquals(0, lastSection.getNumberOfSubsections());
        assertEquals("About Gunma.", lastSection.getHeaderContent(0).getContent());
        assertEquals(secondSection, lastSection.getParentSection());

        // validate paragraph in last section
        assertEquals(1, lastSection.getParagraph(0).getNumberOfSentences());
        assertEquals(true, lastSection.getParagraph(0).getSentence(0).isFirstSentence());
        assertEquals(2, lastSection.getParagraph(1).getNumberOfSentences());
        assertEquals(true, lastSection.getParagraph(1).getSentence(0).isFirstSentence());
        assertEquals(false, lastSection.getParagraph(1).getSentence(1).isFirstSentence());

    }

    @Test
    public void testGenerateDocumentWithList() {
        String sampleText = "Threre are several railway companies in Japan as follows.\n"
            + "- Tokyu\n"
            + "-- Toyoko Line\n"
            + "-- Denentoshi Line\n"
            + "- Keio\n"
            + "- Odakyu\n";
        Document doc = createFileContent(sampleText);
        ListBlock listBlock = doc.getSection(0).getListBlock(0);
        assertEquals(5, listBlock.getNumberOfListElements());
        assertEquals("Tokyu", listBlock.getListElement(0).getSentence(0).getContent());
        assertEquals(1, listBlock.getListElement(0).getLevel());
        assertEquals("Toyoko Line", listBlock.getListElement(1).getSentence(0).getContent());
        assertEquals(2, listBlock.getListElement(1).getLevel());
        assertEquals("Denentoshi Line", listBlock.getListElement(2).getSentence(0).getContent());
        assertEquals(2, listBlock.getListElement(2).getLevel());
        assertEquals("Keio", listBlock.getListElement(3).getSentence(0).getContent());
        assertEquals(1, listBlock.getListElement(3).getLevel());
        assertEquals("Odakyu", listBlock.getListElement(4).getSentence(0).getContent());
        assertEquals(1, listBlock.getListElement(4).getLevel());
    }

    @Test
    public void testGenerateDocumentWithNumberedList() {
        String sampleText = "Threre are several railway companies in Japan as follows.\n"
            + "# Tokyu\n"
            + "## Toyoko Line\n"
            + "## Denentoshi Line\n"
            + "# Keio\n"
            + "# Odakyu\n";
        Document doc = createFileContent(sampleText);
        ListBlock listBlock = doc.getSection(0).getListBlock(0);
        assertEquals(5, listBlock.getNumberOfListElements());
        assertEquals("Tokyu", listBlock.getListElement(0).getSentence(0).getContent());
        assertEquals(1, listBlock.getListElement(0).getLevel());
        assertEquals("Toyoko Line", listBlock.getListElement(1).getSentence(0).getContent());
        assertEquals(2, listBlock.getListElement(1).getLevel());
        assertEquals("Denentoshi Line", listBlock.getListElement(2).getSentence(0).getContent());
        assertEquals(2, listBlock.getListElement(2).getLevel());
        assertEquals("Keio", listBlock.getListElement(3).getSentence(0).getContent());
        assertEquals(1, listBlock.getListElement(3).getLevel());
        assertEquals("Odakyu", listBlock.getListElement(4).getSentence(0).getContent());
        assertEquals(1, listBlock.getListElement(4).getLevel());
    }

    @Test
    public void testGenerateDocumentWithOneLineComment() {
        String sampleText = "There are various tests.\n"
            + "[!-- The following should be exmples --]\n"
            + "Most common one is unit test.\n"
            + "Integration test is also common.\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(3, firstParagraph.getNumberOfSentences());
    }

    @Test
    public void testGenerateDocumentWithMultiLinesComment() {
        String sampleText = "There are various tests.\n"
            + "[!-- \n"
            + "The following should be exmples\n"
            + "--]\n"
            + "Most common one is unit test.\n"
            + "Integration test is also common.\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(3, firstParagraph.getNumberOfSentences());
    }

    @Test
    public void testGenerateDocumentWithMultiLinesComment2() {
        String sampleText = "There are various tests.\n"
            + "[!-- \n"
            + "The following should be exmples\n"
            + "In addition the histories should be described\n"
            + "--]\n"
            + "Most common one is unit test.\n"
            + "Integration test is also common.\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(3, firstParagraph.getNumberOfSentences());
    }

    @Test
    public void testGenerateDocumentWithVoidComment() {
        String sampleText = "There are various tests.\n"
            + "[!----]\n"
            + "Most common one is unit test.\n"
            + "Integration test is also common.\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(3, firstParagraph.getNumberOfSentences());
    }

    @Test
    public void testGenerateDocumentWithOnlySpaceComment() {
        String sampleText = "There are various tests.\n"
            + "[!-- --]\n"
            + "Most common one is unit test.\n"
            + "Integration test is also common.\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(3, firstParagraph.getNumberOfSentences());
    }

    @Test
    public void testGenerateDocumentWithCommentHavingHeadSpace() {
        String sampleText = "There are various tests.\n"
            + " [!-- BLAH BLAH --]\n"
            + "Most common one is unit test.\n"
            + "Integration test is also common.\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(3, firstParagraph.getNumberOfSentences());
    }

    @Test
    public void testGenerateDocumentWithCommentHavingTailingSpace() {
        String sampleText = "There are various tests.\n"
            + "[!-- BLAH BLAH --] \n"
            + "Most common one is unit test.\n"
            + "Integration test is also common.\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(3, firstParagraph.getNumberOfSentences());
    }

    @Test
    public void testGenerateDocumentWithMultiLinesCommentHavingSpaces() {
        String sampleText = "There are various tests.\n"
            + " [!-- \n"
            + "The following should be exmples\n"
            + "In addition the histories should be described\n"
            + "--] \n"
            + "Most common one is unit test.\n"
            + "Integration test is also common.\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(3, firstParagraph.getNumberOfSentences());
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
            assertEquals(expectedResult[i], firstParagraph.getSentence(i).getContent());
        }
    }

    @Test
    public void testGenerateDocumentWithMultipleSentences() {
        String sampleText = "Tokyu is a good railway company. The company is reliable. In addition it is rich.\n"
            + "I like the company. Howerver someone does not like it.";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(5, firstParagraph.getNumberOfSentences());
    }

    @Test
    public void testGenerateDocumentWithMultipleSentencesWithVaraiousStopCharacters() {
        String sampleText = "Is Tokyu a good railway company? The company is reliable. In addition it is rich!\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph paragraph = firstSections.getParagraph(0);
        assertEquals(3, paragraph.getNumberOfSentences());
        assertEquals("Is Tokyu a good railway company?", paragraph.getSentence(0).getContent());
        assertEquals(offsets(1, range(0, 32)), paragraph.getSentence(0).getOffsetMap());
        assertEquals(" The company is reliable.", paragraph.getSentence(1).getContent());
        assertEquals(offsets(1, range(32, 57)), paragraph.getSentence(1).getOffsetMap());
        assertEquals(" In addition it is rich!", paragraph.getSentence(2).getContent());
        assertEquals(offsets(1, range(57, 81)), paragraph.getSentence(2).getOffsetMap());
    }

    @Test
    public void testGenerateDocumentWitVoidContent() {
        Document doc = createFileContent("");
        assertEquals(0, doc.getSection(0).getParagraphs().size());
    }

    @Test
    public void testGenerateDocumentWithPeriodInSuccession() {
        Document doc = createFileContent("...");
        assertEquals(1, doc.getSection(0).getParagraph(0).getNumberOfSentences());
    }

    @Test
    public void testGenerateDocumentWithoutPeriodInLastSentence() {
        Document doc = createFileContent("Hongo is located at the west of Tokyo. Saitama is located at the north");
        Paragraph paragraph = doc.getSection(0).getParagraph(0);
        assertEquals(2, paragraph.getNumberOfSentences());
        assertEquals(offsets(1, range(0, 38)), paragraph.getSentence(0).getOffsetMap());
        assertEquals(offsets(1, range(38, 70)), paragraph.getSentence(1).getOffsetMap());
    }

    @Test
    public void testGenerateOffsetesForMultiLineSentence() {
        Document doc = createFileContent("OK! Saitama\n is located at the nor\nth. OK!");
        Paragraph paragraph = doc.getSection(0).getParagraph(0);
        assertEquals(3, paragraph.getNumberOfSentences());
        List<LineOffset> offsets = offsets(1, range(3, 11));
        offsets.addAll(offsets(2, range(0, 22)));
        offsets.addAll(offsets(3, range(0, 3)));
        assertEquals(offsets, paragraph.getSentence(1).getOffsetMap());
    }

    @Test
    public void testGenerateDocumentWithSentenceLongerThanOneLine() {
        String sampleText = "This is a good day.\n"
            + "Hongo is located at the west of Tokyo "
            + "which is the capital of Japan "
            + "which is not located in the south of the earth.";
        Document doc = createFileContent(sampleText);
        assertEquals(2, doc.getSection(0).getParagraph(0).getNumberOfSentences());
    }

    @Test
    public void testPlainLink() {
        Document doc = createFileContent("this is not a [[pen]], but also this is not [[Google|http://google.com]] either.");
        Paragraph paragraph = doc.getSection(0).getParagraph(0);
        assertEquals(1, paragraph.getNumberOfSentences());
        assertEquals(2, paragraph.getSentence(0).getLinks().size());
        assertEquals("pen", paragraph.getSentence(0).getLinks().get(0));
        assertEquals("http://google.com", paragraph.getSentence(0).getLinks().get(1));
        assertEquals("this is not a pen, but also this is not Google either.",
                paragraph.getSentence(0).getContent());
    }

    @Test
    public void testPlainLinkWithSpaces() {
        Document doc = createFileContent("the url is not [[Google | http://google.com ]].");
        Paragraph paragraph = doc.getSection(0).getParagraph(0);
        assertEquals(1, paragraph.getNumberOfSentences());
        assertEquals(1, paragraph.getSentence(0).getLinks().size());
        assertEquals("http://google.com", paragraph.getSentence(0).getLinks().get(0));
        assertEquals("the url is not Google.",
                paragraph.getSentence(0).getContent());
    }

    @Test
    public void testLinkWithoutTag() {
        Document doc = createFileContent("url of google is [[http://google.com]].");
        Paragraph paragraph = doc.getSection(0).getParagraph(0);
        assertEquals(1, paragraph.getNumberOfSentences());
        assertEquals(1, paragraph.getSentence(0).getLinks().size());
        assertEquals("http://google.com", paragraph.getSentence(0).getLinks().get(0));
        assertEquals("url of google is http://google.com.",
                paragraph.getSentence(0).getContent());
    }

    @Test
    public void testIncompleteLink() {
        Document doc = createFileContent("url of google is [[http://google.com.");
        Paragraph paragraph = doc.getSection(0).getParagraph(0);
        assertEquals(1, paragraph.getNumberOfSentences());
        assertEquals(0, paragraph.getSentence(0).getLinks().size());
        assertEquals("url of google is [[http://google.com.",
                paragraph.getSentence(0).getContent());
    }

    @Test
    public void testPlainLinkWithThreeBlock() {
        Document doc = createFileContent("this is not a pen, but also this is not [[Google|http://google.com|dummy]] either.");
        Paragraph paragraph = doc.getSection(0).getParagraph(0);
        assertEquals(1, paragraph.getNumberOfSentences());
        assertEquals(1, paragraph.getSentence(0).getLinks().size());
        assertEquals("http://google.com", paragraph.getSentence(0).getLinks().get(0));
        assertEquals("this is not a pen, but also this is not Google either.",
                paragraph.getSentence(0).getContent());
    }

    @Test
    public void testVacantListBlock() {
        Document doc = createFileContent("this is not a pen, but also this is not [[]] Google either.");
        Paragraph paragraph = doc.getSection(0).getParagraph(0);
        assertEquals(1, paragraph.getNumberOfSentences());
        assertEquals(1, paragraph.getSentence(0).getLinks().size());
        assertEquals("", paragraph.getSentence(0).getLinks().get(0));
        assertEquals("this is not a pen, but also this is not  Google either.",
                paragraph.getSentence(0).getContent());
    }

    @Test
    public void testDocumentWithItalicWord() {
        Document doc = createFileContent("This is a //good// day.\n");
        assertEquals("This is a good day.", doc.getSection(0).getParagraph(0).getSentence(0).getContent());
    }

    @Test
    public void testDocumentWithMultipleItalicWords() {
        Document doc = createFileContent("//This// is a //good// day.\n");
        assertEquals("This is a good day.", doc.getSection(0).getParagraph(0).getSentence(0).getContent());
    }

    @Test
    public void testDocumentWithMultipleNearItalicWords() {
        Document doc = createFileContent("This is //a// //good// day.\n");
        assertEquals("This is a good day.", doc.getSection(0).getParagraph(0).getSentence(0).getContent());
    }

    @Test
    public void testDocumentWithItalicExpression() {
        Document doc = createFileContent("This is //a good// day.\n");
        assertEquals("This is a good day.", doc.getSection(0).getParagraph(0).getSentence(0).getContent());
    }

    @Test
    public void testDocumentWithHeaderCotainingMultipleSentences() {
        String sampleText = "h1. About Gunma. About Saitama.\n"
            + "Gunma is located at west of Saitama.\n"
            + "The word also have posive meaning. Hower it is a bit wired.";

        Document doc = createFileContent(sampleText);
        Section lastSection = doc.getSection(doc.size() - 1);
        assertEquals(2, lastSection.getHeaderContentsListSize());
        assertEquals("About Gunma.", lastSection.getHeaderContent(0).getContent());
        assertEquals(" About Saitama.", lastSection.getHeaderContent(1).getContent());
    }

    @Test
    public void testDocumentWithHeaderWitoutPeriod() {
        String sampleText = "h1. About Gunma\n"
            + "Gunma is located at west of Saitama.\n"
            + "The word also have posive meaning. Hower it is a bit wired.";
        Document doc = createFileContent(sampleText);
        Section lastSection = doc.getSection(doc.size() - 1);
        assertEquals(1, lastSection.getHeaderContentsListSize());
        assertEquals("About Gunma", lastSection.getHeaderContent(0).getContent());
        assertEquals(offsets(1, range(4, 15)), lastSection.getHeaderContent(0).getOffsetMap());
    }

    @Test
    public void testDocumentWithList() {
        String sampleText = "h1. About Gunma. About Saitama.\n"
            + "- Gunma is located at west of Saitama.\n"
            + "- The word also have posive meaning. Hower it is a bit wired.";

        Document doc = createFileContent(sampleText);
        Section lastSection = doc.getSection(doc.size() - 1);
        ListBlock listBlock = lastSection.getListBlock(0);
        assertEquals(2, listBlock.getNumberOfListElements());
        assertEquals(1, listBlock.getListElement(0).getNumberOfSentences());
        assertEquals("Gunma is located at west of Saitama.", listBlock.getListElement(0).getSentence(0).getContent());
        assertEquals("The word also have posive meaning.", listBlock.getListElement(1).getSentence(0).getContent());
        assertEquals(" Hower it is a bit wired.", listBlock.getListElement(1).getSentence(1).getContent());
    }

    @Test
    public void testDocumentWithListWithoutPeriod() {
        String sampleText = "h1. About Gunma. About Saitama.\n"
            + "- Gunma is located at west of Saitama\n";

        Document doc = createFileContent(sampleText);
        Section lastSection = doc.getSection(doc.size() - 1);
        ListBlock listBlock = lastSection.getListBlock(0);
        assertEquals(1, listBlock.getNumberOfListElements());
        assertEquals(1, listBlock.getListElement(0).getNumberOfSentences());
        assertEquals("Gunma is located at west of Saitama",
                listBlock.getListElement(0).getSentence(0).getContent());
    }

    @Test
    public void testDocumentWithMultipleSections() {
        String sampleText = "h1. Prefectures in Japan.\n"
            + "There are 47 prefectures in Japan.\n"
            + "\n"
            + "Each prefectures has its features.\n"
            + "h2. Gunma \n"
            + "Gumma is very beautiful";

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

        assertEquals(1, rootSection.getHeaderContent(0).getLineNumber());
        assertEquals(0, rootSection.getNumberOfParagraphs());

        assertEquals(offsets(1, range(4, 25)), h1Section.getHeaderContent(0).getOffsetMap());
        assertEquals(2, h1Section.getNumberOfParagraphs());
        assertEquals(1, h1Section.getParagraph(0).getNumberOfSentences());
        assertEquals(2, h1Section.getParagraph(0).getSentence(0).getLineNumber());
        assertEquals(1, h1Section.getParagraph(1).getNumberOfSentences());
        assertEquals(4, h1Section.getParagraph(1).getSentence(0).getLineNumber());

        assertEquals(offsets(5, range(4, 10)), h2Section.getHeaderContent(0).getOffsetMap());
        assertEquals(1, h2Section.getNumberOfParagraphs());
        assertEquals(1, h2Section.getParagraph(0).getNumberOfSentences());
        assertEquals(6, h2Section.getParagraph(0).getSentence(0).getLineNumber());
    }

    @Test
    public void testDocumentWithoutLastPeriod() {
        String sampleText = "h1. Prefectures in Japan.\n"
            + "There are 47 prefectures in Japan\n"; // no last period

        Document doc = createFileContent(sampleText);
        assertEquals(2, doc.size());
        Section rootSection = doc.getSection(0);
        Section h1Section = doc.getSection(1);

        assertEquals(0, rootSection.getLevel());
        assertEquals(1, h1Section.getLevel());

        assertEquals(rootSection.getSubSection(0), h1Section);
        assertEquals(h1Section.getParentSection(), rootSection);
        assertEquals(rootSection.getParentSection(), null);

        assertEquals(1, rootSection.getHeaderContent(0).getLineNumber());
        assertEquals(0, rootSection.getNumberOfParagraphs());

        assertEquals(1, h1Section.getHeaderContent(0).getLineNumber());
        assertEquals(1, h1Section.getNumberOfParagraphs());
        assertEquals(1, h1Section.getParagraph(0).getNumberOfSentences());
        assertEquals(2, h1Section.getParagraph(0).getSentence(0).getLineNumber());
    }

    @Test
    public void testGenerateJapaneseDocument() {
        String sampleText = "埼玉は東京の北に存在する。大きなベッドタウンであり、多くの人が住んでいる。";
        Document doc = createFileContent(sampleText, Configuration.builder("ja").build());
        assertEquals(2, doc.getSection(0).getParagraph(0).getNumberOfSentences());
    }

    @Test
    public void testErrorPositionOfWikiParser() throws RedPenException {
        String sampleText = "This is a good day。\n"; // invalid end of sentence symbol
        Configuration conf = Configuration.builder().build();
        Document doc = createFileContent(sampleText, conf);

        Configuration configuration = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("InvalidSymbol"))
                .build();

        RedPen redPen = new RedPen(configuration);
        List<ValidationError> errors = redPen.validate(singletonList(doc)).get(doc);
        assertEquals(1, errors.size());
        assertEquals("InvalidSymbol", errors.get(0).getValidatorName());
        assertEquals(19, errors.get(0).getSentence().getContent().length());
        assertEquals(Optional.of(new LineOffset(1, 18)), errors.get(0).getStartPosition());
        assertEquals(Optional.of(new LineOffset(1, 19)), errors.get(0).getEndPosition());
    }

    private Document createFileContent(String inputDocumentString, Configuration conf) {
        try {
            return WIKI.parse(inputDocumentString, new SentenceExtractor(conf.getSymbolTable()), conf.getTokenizer());
        } catch (RedPenException e) {
            throw new RuntimeException(e);
        }
    }

    private Document createFileContent(String inputDocumentString) {
        return createFileContent(inputDocumentString, Configuration.builder().build());
    }
}
