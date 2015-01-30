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

import cc.redpen.model.Document;
import cc.redpen.parser.DocumentParser;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SampleDocumentGeneratorTest {
    @Test
    public void testGenerateSimplePlainDocument() throws RedPenException {
        String sampleText = "";
        sampleText += "Gekioko pun pun maru means very very angry.\n";
        List<Document> docs = SampleDocumentGenerator.generateOneFileDocument(sampleText,
                DocumentParser.PLAIN);
        assertNotNull(docs);
        assertEquals(1, docs.size());
        assertEquals(1, docs.get(0).size());
        assertEquals(1, docs.get(0).getSection(0).getNumberOfParagraphs());
        assertEquals(1, docs.get(0).getSection(0).getParagraph(0)
                .getNumberOfSentences());
        assertEquals("Gekioko pun pun maru means very very angry.", docs.get(0).getSection(0)
                .getParagraph(0).getSentence(0).getContent());
    }

    @Test
    public void testGenerateSimpleWikiDocument() throws RedPenException {
        String sampleText = "";
        sampleText += "h1. About Gekioko.\n";
        sampleText += "Gekioko pun pun maru means very very angry.\n";
        List<Document> docs = SampleDocumentGenerator.generateOneFileDocument(sampleText,
                DocumentParser.WIKI);
        assertNotNull(docs);
        assertEquals(1, docs.size());
        assertEquals(2, docs.get(0).size());
        assertEquals("About Gekioko.", docs.get(0).getSection(1).getHeaderContent(0).getContent());
        assertEquals(1, docs.get(0).getSection(1).getNumberOfParagraphs());
        assertEquals(1, docs.get(0).getSection(1).getParagraph(0)
                .getNumberOfSentences());
        assertEquals("Gekioko pun pun maru means very very angry.", docs.get(0).getSection(1)
                .getParagraph(0).getSentence(0).getContent());
    }

    @Test
    public void testGenerateSimpleMarkdownDocument() throws RedPenException {
        String sampleText = "";
        sampleText += "# About Gekioko.\n";
        sampleText += "Gekioko pun pun maru means very very angry.\n";
        List<Document> docs = SampleDocumentGenerator.generateOneFileDocument(sampleText,
                DocumentParser.MARKDOWN);
        assertNotNull(docs);
        assertEquals(1, docs.size());
        assertEquals(2, docs.get(0).size());
        assertEquals("About Gekioko.", docs.get(0).getSection(1).getHeaderContent(0).getContent());
        assertEquals(1, docs.get(0).getSection(1).getNumberOfParagraphs());
        assertEquals(1, docs.get(0).getSection(1).getParagraph(0)
                .getNumberOfSentences());
        assertEquals("Gekioko pun pun maru means very very angry.", docs.get(0).getSection(1)
                .getParagraph(0).getSentence(0).getContent());
    }

    @Test(expected = NullPointerException.class)
    public void testInputNullDocument() throws RedPenException {
        SampleDocumentGenerator.generateOneFileDocument(null, DocumentParser.MARKDOWN);
    }
}
