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
package cc.redpen.parser;

import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.model.DocumentCollection;
import cc.redpen.model.Paragraph;
import cc.redpen.model.Section;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PlainTextParserTest {

    private Parser parser = null;

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
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(sampleText.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e1) {
            fail();
        }
        Document doc = null;
        try {
            doc = parser.generateDocument(is);
        } catch (RedPenException e) {
            fail();
        }
        return doc;
    }

    @Before
    public void setup() {
        Configuration configuration = new Configuration.Builder()
                .addValidatorConfig(
                        new ValidatorConfiguration("SentenceLength").addAttribute("max_length", "10"))
                .build();
        try {
            parser = DocumentParserFactory.generate(Parser.Type.PLAIN, configuration, new DocumentCollection.Builder());
        } catch (RedPenException e1) {
            e1.printStackTrace();
            fail();
        }
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
        sampleText += "Happ life.\n";
        sampleText += "Happy home.\n";
        sampleText += "Tama Home.\n";
        Document doc = generateDocument(sampleText);
        Section section = doc.getLastSection();
        assertEquals(7, calcLineNum(section));
        assertEquals(3, extractParagraphs(section).size());
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
        assertEquals(0, section.getHeaderContent(0).position);
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
        assertEquals(0, section.getHeaderContent(0).position);
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

    @Test(expected = RedPenException.class)
    public void testNullInitialize() throws Exception {
        DocumentParserFactory.generate(Parser.Type.PLAIN, null,
                new DocumentCollection.Builder());
    }

    @Test(expected = RedPenException.class)
    public void testNullFileName() throws Exception {
        parser.generateDocument("no_exist_files");
    }
}
