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
        assertEquals("baz", docs.get(0).getSection(0).getHeaderContent(0).content);
        assertEquals(1, docs.get(0).getSection(0).getNumberOfParagraphs());
        assertEquals(2, docs.get(0).getSection(0).getParagraph(0).getNumberOfSentences());
        assertEquals("sentence0", docs.get(0).getSection(0).getParagraph(0).getSentence(0).content);
        assertEquals(true, docs.get(0).getSection(0).getParagraph(0).getSentence(0).isFirstSentence);
        assertEquals(0, docs.get(0).getSection(0).getParagraph(0).getSentence(0).position);
        assertEquals("sentence1", docs.get(0).getSection(0).getParagraph(0).getSentence(1).content);
        assertEquals(false, docs.get(0).getSection(0).getParagraph(0).getSentence(1).isFirstSentence);
        assertEquals(1, docs.get(0).getSection(0).getParagraph(0).getSentence(1).position);
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
        assertEquals("sec1", docs.get(0).getSection(0).getHeaderContent(0).content);
        assertEquals(0, docs.get(0).getSection(0).getLevel());
        assertEquals(1, docs.get(0).getSection(0).getNumberOfParagraphs());
        assertEquals(2, docs.get(0).getSection(0).getParagraph(0).getNumberOfSentences());
        assertEquals("sentence00", docs.get(0).getSection(0).getParagraph(0).getSentence(0).content);
        assertEquals(true, docs.get(0).getSection(0).getParagraph(0).getSentence(0).isFirstSentence);
        assertEquals(0, docs.get(0).getSection(0).getParagraph(0).getSentence(0).position);
        assertEquals("sentence01", docs.get(0).getSection(0).getParagraph(0).getSentence(1).content);
        assertEquals(false, docs.get(0).getSection(0).getParagraph(0).getSentence(1).isFirstSentence);
        assertEquals(1, docs.get(0).getSection(0).getParagraph(0).getSentence(1).position);

        // second document
        assertEquals(1, docs.get(1).size());
        assertEquals(Optional.of("doc2"), docs.get(1).getFileName());
        assertEquals("sec2", docs.get(1).getSection(0).getHeaderContent(0).content);
        assertEquals(0, docs.get(1).getSection(0).getLevel());
        assertEquals(1, docs.get(1).getSection(0).getNumberOfParagraphs());
        assertEquals(2, docs.get(1).getSection(0).getParagraph(0).getNumberOfSentences());
        assertEquals("sentence10", docs.get(1).getSection(0).getParagraph(0).getSentence(0).content);
        assertEquals(true, docs.get(1).getSection(0).getParagraph(0).getSentence(0).isFirstSentence);
        assertEquals(0, docs.get(1).getSection(0).getParagraph(0).getSentence(0).position);
        assertEquals("sentence11", docs.get(1).getSection(0).getParagraph(0).getSentence(1).content);
        assertEquals(false, docs.get(1).getSection(0).getParagraph(0).getSentence(1).isFirstSentence);
        assertEquals(1, docs.get(1).getSection(0).getParagraph(0).getSentence(1).position);
    }
}
