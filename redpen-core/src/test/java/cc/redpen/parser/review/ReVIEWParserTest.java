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
package cc.redpen.parser.review;

import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.model.Document;
import cc.redpen.parser.DocumentParser;
import cc.redpen.parser.SentenceExtractor;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;

public class ReVIEWParserTest {
    @Test
    public void ParseBlock() throws Exception {
        String sample = "//list[yml_sample][my.yml]{";
        ReVIEWParser parser = new ReVIEWParser();
        ReVIEWParser.ReVIEWBlock block = parser.parseBlock(new ReVIEWLine(sample, 0));
        assertEquals("list", block.type);
        assertEquals(2, block.properties.size());
        assertEquals("yml_sample", block.properties.get(0));
        assertEquals("my.yml", block.properties.get(1));
        assertTrue(block.isOpen);
    }

    @Test
    public void ParseBlockWithoutProperties() throws Exception {
        String sample = "//lead{";
        ReVIEWParser parser = new ReVIEWParser();
        ReVIEWParser.ReVIEWBlock block = parser.parseBlock(new ReVIEWLine(sample, 0));
        assertEquals("lead", block.type);
        assertEquals(0, block.properties.size());
        assertTrue(block.isOpen);
    }

    @Test
    public void testRemoveTextDecoration() throws UnsupportedEncodingException {
        String sampleText = "About @<b>{Gekioko}.\n";
        Document doc = createFileContent(sampleText);
        assertEquals(1, doc.size());
        assertEquals("About Gekioko.", doc.getSection(0).getParagraph(0).getSentence(0).getContent());
    }

    @Test
    public void testGenerateDocumentWithList() {
        String sampleText = "There are several railway companies in Japan as follows.\n";
        sampleText += "\n";
        sampleText += "* Tokyu\n";
        sampleText += "** Toyoko Line\n";
        sampleText += "** Denentoshi Line\n";
        sampleText += "* Keio\n";
        sampleText += "* Odakyu\n";

        Document doc = createFileContent(sampleText);
        assertEquals(5, doc.getSection(0).getListBlock(0).getNumberOfListElements());
        assertEquals("Tokyu", doc.getSection(0).getListBlock(0).getListElement(0).getSentence(0).getContent());
        assertEquals(1, doc.getSection(0).getListBlock(0).getListElement(0).getLevel());
        assertEquals(3, doc.getSection(0).getListBlock(0).getListElement(0).getSentence(0).getLineNumber());
        assertEquals(2, doc.getSection(0).getListBlock(0).getListElement(0).getSentence(0).getStartPositionOffset());

        assertEquals("Toyoko Line", doc.getSection(0).getListBlock(0).getListElement(1).getSentence(0).getContent());
        assertEquals(2, doc.getSection(0).getListBlock(0).getListElement(1).getLevel());
        assertEquals(4, doc.getSection(0).getListBlock(0).getListElement(1).getSentence(0).getLineNumber());
        assertEquals(3, doc.getSection(0).getListBlock(0).getListElement(1).getSentence(0).getStartPositionOffset());

        assertEquals("Denentoshi Line", doc.getSection(0).getListBlock(0).getListElement(2).getSentence(0).getContent());
        assertEquals(2, doc.getSection(0).getListBlock(0).getListElement(2).getLevel());
        assertEquals(5, doc.getSection(0).getListBlock(0).getListElement(2).getSentence(0).getLineNumber());
        assertEquals(3, doc.getSection(0).getListBlock(0).getListElement(2).getSentence(0).getStartPositionOffset());

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
    public void testLabelledList() {
        String sampleText = "= SampleDoc\n" +
                "v0.0.2, 2015-11-17\n" +
                ":last-update-label!:\n" +
                "\n" +
                "== 用語定義\n" +
                "ユビキタス言語を定義します。\n" +
                "\n" +
                ": Some word\n" +
                "なにかの意味をのせて用例をのせます。\n" +
                "\n" +
                ": リリース\n" +
                "ソフトウェアを顧客に提供することです。\n" +
                "\n" +
                ": redpen\n" +
                "RedPen はオープンソースの校正ツールです。RedPen は技術文書が文書規約に従って書かれているかを自動検査します。 現在の RedPen 日本語ドキュメントは十分検査されておりません。校正にはもう少々時間がかかる予定です。誤りなど見つかりましたら、https://github.com/redpen-cc/redpen-doc-ja に Issue 登録しておしらせ頂けると幸いです。";

        Document doc = createFileContent(sampleText);
        assertEquals(3, doc.getSection(1).getListBlock(0).getNumberOfListElements());
        assertEquals("なにかの意味をのせて用例をのせます。", doc.getSection(1).getListBlock(0).getListElement(0).getSentence(0).getContent());
        assertEquals(15, doc.getSection(1).getListBlock(0).getListElement(2).getSentence(0).getLineNumber());

    }

    private Document createFileContent(String inputDocumentString) {
        DocumentParser parser = DocumentParser.REVIEW;
        Document doc = null;
        try {
            Configuration configuration = Configuration.builder().build();
            doc = parser.parse(
                    inputDocumentString,
                    new SentenceExtractor(configuration.getSymbolTable()),
                    configuration.getTokenizer());
        } catch (RedPenException e) {
            e.printStackTrace();
            fail();
        }
        return doc;
    }

}
