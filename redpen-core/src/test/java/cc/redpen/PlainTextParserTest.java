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
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.model.Paragraph;
import cc.redpen.model.Section;
import cc.redpen.parser.DocumentParser;
import cc.redpen.parser.SentenceExtractor;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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

    private int calcLineNum(Section section) {
        int lineNum = 0;

        for (Paragraph paragraph : section.getParagraphs()) {
            lineNum += paragraph.getNumberOfSentences();
        }
        return lineNum;
    }

    private Document generateDocument(String sampleText) {
        Document doc = null;
        Configuration configuration = new Configuration.ConfigurationBuilder().build();
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
        assertEquals(7, calcLineNum(section));
        assertEquals(3, extractParagraphs(section).size());

        assertEquals(2, section.getParagraph(0).getNumberOfSentences());
        assertEquals(1, section.getParagraph(0).getSentence(0).lineNum);
        assertEquals(2, section.getParagraph(0).getSentence(1).lineNum);

        assertEquals(2, section.getParagraph(1).getNumberOfSentences());
        assertEquals(4, section.getParagraph(1).getSentence(0).lineNum);
        assertEquals(5, section.getParagraph(1).getSentence(1).lineNum);

        assertEquals(3, section.getParagraph(2).getNumberOfSentences());
        assertEquals(7, section.getParagraph(2).getSentence(0).lineNum);
        assertEquals(8, section.getParagraph(2).getSentence(1).lineNum);
        assertEquals(9, section.getParagraph(2).getSentence(2).lineNum);
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
        assertEquals(5, calcLineNum(section));
        Paragraph paragraph = paragraphs.get(paragraphs.size() - 1);
        for (int i = 0; i < expectedResult.length; i++) {
            assertEquals(expectedResult[i], paragraph.getSentence(i).content);
        }
        assertEquals(0, section.getHeaderContent(0).lineNum);
        assertEquals("", section.getHeaderContent(0).content);
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
        assertEquals(3, calcLineNum(section));
        Paragraph paragraph = paragraphs.get(paragraphs.size() - 1);
        for (int i = 0; i < expectedResult.length; i++) {
            assertEquals(expectedResult[i], paragraph.getSentence(i).content);
        }
        assertEquals(0, section.getHeaderContent(0).lineNum);
        assertEquals("", section.getHeaderContent(0).content);
    }

    @Test
    public void testGenerateDocumentWithNoContent() {
        String sampleText = "";
        Document doc = generateDocument(sampleText);
        Section section = doc.getLastSection();
        List<Paragraph> paragraphs = extractParagraphs(section);
        assertEquals(1, paragraphs.size());
        assertEquals(0, calcLineNum(section));
    }

}
