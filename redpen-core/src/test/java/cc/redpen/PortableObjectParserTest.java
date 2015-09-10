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

public class PortableObjectParserTest {

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
        return generateDocument(sampleText, "ja");
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
        parser = DocumentParser.PO;
    }

    @Test
    public void testEmptyMsgidIsIgnored() {
        String sampleText = "";
        sampleText += "#\n";
        sampleText += "\n";
        sampleText += "msgid \"\"\n";
        sampleText += "msgstr \"\"\n";
        sampleText += "\n";
        sampleText += "msgid \"What is RedPen?\"\n";
        sampleText += "msgstr \"\"\n";
        sampleText += "\"RedPenとは？\"\n";
        sampleText += "\n";
        Document doc = generateDocument(sampleText);
        Section section = doc.getLastSection();
        assertEquals(1, getTotalSentenceCount(section));
        assertEquals(1, extractParagraphs(section).size());

        assertEquals(1, section.getParagraph(0).getNumberOfSentences());
        assertEquals(7, section.getParagraph(0).getSentence(0).getLineNumber());
        assertEquals("RedPenとは？", section.getParagraph(0).getSentence(0).getContent());

    }

    @Test
    public void testLastMsgstrIsHandled() {
        String sampleText = "";
        sampleText += "msgid \"What is RedPen?\"\n";
        sampleText += "msgstr \"\"\n";
        sampleText += "\"RedPenとは？\"\n";
        sampleText += "\n";
        sampleText += "msgid \"RedPen is a proofreading tool.\"\n";
        sampleText += "msgstr \"\"\n";
        sampleText += "\"RedPenは文章の校正ツールです。\"\n";

        Document doc = generateDocument(sampleText);
        Section section = doc.getLastSection();
        assertEquals(2, getTotalSentenceCount(section));
        assertEquals(2, extractParagraphs(section).size());

        assertEquals(1, section.getParagraph(0).getNumberOfSentences());
        assertEquals(2, section.getParagraph(0).getSentence(0).getLineNumber());
        assertEquals("RedPenとは？", section.getParagraph(0).getSentence(0).getContent());

        assertEquals(1, section.getParagraph(1).getNumberOfSentences());
        assertEquals(6, section.getParagraph(1).getSentence(0).getLineNumber());
        assertEquals("RedPenは文章の校正ツールです。", section.getParagraph(1).getSentence(0).getContent());
    }

    @Test
    public void testExtractedMultipleSentences() {
        String sampleText = "";
        sampleText += "msgid \"\"\n";
        sampleText += "\"RedPen is a proofreading tool.\"\n";
        sampleText += "\"It helps programmers who write technical documents \"\n";
        sampleText += "\"that need to adhere to a writing standard.\"\n";
        sampleText += "msgstr \"\"\n";
        sampleText += "\"RedPenは文章の校正ツールです。プログラマが技術文書を規約にしたがって記述するのを支援します。\"\n";

        Document doc = generateDocument(sampleText);
        Section section = doc.getLastSection();
        assertEquals(2, getTotalSentenceCount(section));
        assertEquals(1, extractParagraphs(section).size());

        assertEquals(2, section.getParagraph(0).getNumberOfSentences());
        assertEquals(5, section.getParagraph(0).getSentence(0).getLineNumber());
        assertEquals("RedPenは文章の校正ツールです。", section.getParagraph(0).getSentence(0).getContent());
        assertEquals("プログラマが技術文書を規約にしたがって記述するのを支援します。", section.getParagraph(0).getSentence(1).getContent());
    }

}
