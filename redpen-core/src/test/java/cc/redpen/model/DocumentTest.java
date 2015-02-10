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
package cc.redpen.model;

import cc.redpen.tokenizer.JapaneseTokenizer;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class DocumentTest {
    @Test
    public void testCreateDocument() {
        Document doc = new Document.DocumentBuilder()
                        .setFileName("Foobar").build();
        assertEquals(0, doc.size());
    }

    @Test
    public void testCreateDocumentWithList() {
        Document doc = new Document.DocumentBuilder()
                .setFileName("Foobar")
                .addSection(0)
                .addSectionHeader("baz")
                .addParagraph()
                .addSentence("sentence0", 0)
                .addSentence("sentence1", 1)
                .addListBlock()
                .addListElement(0, "list0")
                .addListElement(0, "list1")
                .addListElement(1, "list2")
                .build();

        assertEquals(1, doc.size());
        assertEquals(0, doc.getSection(0).getLevel());
        assertEquals(Optional.of("Foobar"), doc.getFileName());
        assertEquals("baz", doc.getSection(0).getHeaderContent(0).getContent());
        assertEquals(1, doc.getSection(0).getNumberOfParagraphs());
        assertEquals("sentence0", doc.getSection(0).getParagraph(0).getSentence(0).getContent());
        assertEquals(true, doc.getSection(0).getParagraph(0).getSentence(0).isFirstSentence());
        assertEquals(0, doc.getSection(0).getParagraph(0).getSentence(0).getLineNumber());
        assertEquals("sentence1", doc.getSection(0).getParagraph(0).getSentence(1).getContent());
        assertEquals(false, doc.getSection(0).getParagraph(0).getSentence(1).isFirstSentence());
        assertEquals(1, doc.getSection(0).getParagraph(0).getSentence(1).getLineNumber());
        assertEquals(1, doc.getSection(0).getNumberOfLists());
        assertEquals(3, doc.getSection(0).getListBlock(0).getNumberOfListElements());
        assertEquals(0, doc.getSection(0).getListBlock(0).getListElement(0).getLevel());
        assertEquals("list0", doc.getSection(0).getListBlock(0).getListElement(0).getSentence(0).getContent());
        assertEquals(0, doc.getSection(0).getListBlock(0).getListElement(1).getLevel());
        assertEquals("list1", doc.getSection(0).getListBlock(0).getListElement(1).getSentence(0).getContent());
        assertEquals(1, doc.getSection(0).getListBlock(0).getListElement(2).getLevel());
        assertEquals("list2", doc.getSection(0).getListBlock(0).getListElement(2).getSentence(0).getContent());
    }

    @Test
    public void testSentenceIsTokenized() {
        Document doc = new Document.DocumentBuilder()
                .setFileName("foobar")
                .addSection(0)
                .addSectionHeader("baz")
                .addParagraph()
                .addSentence("This is a foobar.", 0)
                .build();
        assertEquals(1, doc.size());
        assertEquals(4, doc.getSection(0).getParagraph(0).getSentence(0).getTokens().size());
    }

    @Test
    public void testJapaneseSentenceIsTokenized() {
        Document doc = new Document.DocumentBuilder(new JapaneseTokenizer())
                .setFileName("今日")
                .addSection(0)
                .addSectionHeader("天気")
                .addParagraph()
                .addSentence("今日は晴天だ。", 0)
                .build();
        assertEquals(1, doc.size());
        assertEquals(5, doc.getSection(0).getParagraph(0).getSentence(0).getTokens().size());
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateParagraphBeforeSection() {
        new Document.DocumentBuilder()
                .setFileName("Foobar")
                .addParagraph()
                .addSection(0)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateListBlockBeforeSection() {
        new Document.DocumentBuilder()
                .setFileName("Foobar")
                .addListBlock()
                .addSection(0)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateListElementBeforeListBlock() {
        new Document.DocumentBuilder()
                .setFileName("Foobar")
                .addListElement(0, "foo")
                .addListBlock()
                .build();
    }
}
