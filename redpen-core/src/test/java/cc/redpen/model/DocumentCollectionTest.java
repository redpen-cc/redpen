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

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class DocumentCollectionTest {
    @Test
    public void testCreateDocumentCollection() {
        List<Document> docs = new ArrayList<>();
        docs.add(new Document.DocumentBuilder()
                .setFileName("Foobar")
                .addSection(0)
                .addSectionHeader("baz")
                .addParagraph()
                .addSentence("sentence0", 0)
                .addSentence("sentence1", 1)
                .build());

        assertEquals(1, docs.size());
        assertEquals(1, docs.get(0).size());
        assertEquals(0, docs.get(0).getSection(0).getLevel());
        assertEquals(Optional.of("Foobar"), docs.get(0).getFileName());
        assertEquals("baz", docs.get(0).getSection(0).getHeaderContent(0).getContent());
        assertEquals(1, docs.get(0).getSection(0).getNumberOfParagraphs());
        assertEquals(2, docs.get(0).getSection(0).getParagraph(0).getNumberOfSentences());
        assertEquals("sentence0", docs.get(0).getSection(0).getParagraph(0).getSentence(0).getContent());
        assertEquals(true, docs.get(0).getSection(0).getParagraph(0).getSentence(0).isFirstSentence());
        assertEquals(0, docs.get(0).getSection(0).getParagraph(0).getSentence(0).getLineNumber());
        assertEquals("sentence1", docs.get(0).getSection(0).getParagraph(0).getSentence(1).getContent());
        assertEquals(false, docs.get(0).getSection(0).getParagraph(0).getSentence(1).isFirstSentence());
        assertEquals(1, docs.get(0).getSection(0).getParagraph(0).getSentence(1).getLineNumber());
    }

    @Test
    public void testDocumentCollectionWithMultipleDocument() {
        List<Document> docs = new ArrayList<>();
        docs.add(new Document.DocumentBuilder()
                .setFileName("doc1")
                .addSection(0)
                .addSectionHeader("sec1")
                .addParagraph()
                .addSentence("sentence00", 0)
                .addSentence("sentence01", 1).build());
        docs.add(new Document.DocumentBuilder()
                .setFileName("doc2")
                .addSection(0)
                .addSectionHeader("sec2")
                .addParagraph()
                .addSentence("sentence10", 0)
                .addSentence("sentence11", 1)
                .build());

        assertEquals(2, docs.size());

        // first document
        assertEquals(1, docs.get(0).size());
        assertEquals(Optional.of("doc1"), docs.get(0).getFileName());
        assertEquals("sec1", docs.get(0).getSection(0).getHeaderContent(0).getContent());
        assertEquals(0, docs.get(0).getSection(0).getLevel());
        assertEquals(1, docs.get(0).getSection(0).getNumberOfParagraphs());
        assertEquals(2, docs.get(0).getSection(0).getParagraph(0).getNumberOfSentences());
        assertEquals("sentence00", docs.get(0).getSection(0).getParagraph(0).getSentence(0).getContent());
        assertEquals(true, docs.get(0).getSection(0).getParagraph(0).getSentence(0).isFirstSentence());
        assertEquals(0, docs.get(0).getSection(0).getParagraph(0).getSentence(0).getLineNumber());
        assertEquals("sentence01", docs.get(0).getSection(0).getParagraph(0).getSentence(1).getContent());
        assertEquals(false, docs.get(0).getSection(0).getParagraph(0).getSentence(1).isFirstSentence());
        assertEquals(1, docs.get(0).getSection(0).getParagraph(0).getSentence(1).getLineNumber());

        // second document
        assertEquals(1, docs.get(1).size());
        assertEquals(Optional.of("doc2"), docs.get(1).getFileName());
        assertEquals("sec2", docs.get(1).getSection(0).getHeaderContent(0).getContent());
        assertEquals(0, docs.get(1).getSection(0).getLevel());
        assertEquals(1, docs.get(1).getSection(0).getNumberOfParagraphs());
        assertEquals(2, docs.get(1).getSection(0).getParagraph(0).getNumberOfSentences());
        assertEquals("sentence10", docs.get(1).getSection(0).getParagraph(0).getSentence(0).getContent());
        assertEquals(true, docs.get(1).getSection(0).getParagraph(0).getSentence(0).isFirstSentence());
        assertEquals(0, docs.get(1).getSection(0).getParagraph(0).getSentence(0).getLineNumber());
        assertEquals("sentence11", docs.get(1).getSection(0).getParagraph(0).getSentence(1).getContent());
        assertEquals(false, docs.get(1).getSection(0).getParagraph(0).getSentence(1).isFirstSentence());
        assertEquals(1, docs.get(1).getSection(0).getParagraph(0).getSentence(1).getLineNumber());
    }
}
