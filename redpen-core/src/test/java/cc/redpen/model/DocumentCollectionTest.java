package cc.redpen.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class DocumentCollectionTest {
    @Test
    public void testCreateDocumentCollection() {
        List<Document> doc = new ArrayList<>();
        doc.add(new Document.DocumentBuilder()
                .setFileName("Foobar")
                .addSection(0)
                .addSectionHeader("baz")
                .addParagraph()
                .addSentence("sentence0", 0)
                .addSentence("sentence1", 1)
                .build());

        assertEquals(1, doc.size());
        assertEquals(1, doc.get(0).size());
        assertEquals(0, doc.get(0).getSection(0).getLevel());
        assertEquals(Optional.of("Foobar"), doc.get(0).getFileName());
        assertEquals("baz", doc.get(0).getSection(0).getHeaderContent(0).content);
        assertEquals(1, doc.get(0).getSection(0).getNumberOfParagraphs());
        assertEquals(2, doc.get(0).getSection(0).getParagraph(0).getNumberOfSentences());
        assertEquals("sentence0", doc.get(0).getSection(0).getParagraph(0).getSentence(0).content);
        assertEquals(true, doc.get(0).getSection(0).getParagraph(0).getSentence(0).isFirstSentence);
        assertEquals(0, doc.get(0).getSection(0).getParagraph(0).getSentence(0).position);
        assertEquals("sentence1", doc.get(0).getSection(0).getParagraph(0).getSentence(1).content);
        assertEquals(false, doc.get(0).getSection(0).getParagraph(0).getSentence(1).isFirstSentence);
        assertEquals(1, doc.get(0).getSection(0).getParagraph(0).getSentence(1).position);
    }

    @Test
    public void testDocumentCollectionWithMultipleDocument() {
        List<Document> doc = new ArrayList<>();
        doc.add(new Document.DocumentBuilder()
                .setFileName("doc1")
                .addSection(0)
                .addSectionHeader("sec1")
                .addParagraph()
                .addSentence("sentence00", 0)
                .addSentence("sentence01", 1).build());
        doc.add(new Document.DocumentBuilder()
                .setFileName("doc2")
                .addSection(0)
                .addSectionHeader("sec2")
                .addParagraph()
                .addSentence("sentence10", 0)
                .addSentence("sentence11", 1)
                .build());

        assertEquals(2, doc.size());

        // first document
        assertEquals(1, doc.get(0).size());
        assertEquals(Optional.of("doc1"), doc.get(0).getFileName());
        assertEquals("sec1", doc.get(0).getSection(0).getHeaderContent(0).content);
        assertEquals(0, doc.get(0).getSection(0).getLevel());
        assertEquals(1, doc.get(0).getSection(0).getNumberOfParagraphs());
        assertEquals(2, doc.get(0).getSection(0).getParagraph(0).getNumberOfSentences());
        assertEquals("sentence00", doc.get(0).getSection(0).getParagraph(0).getSentence(0).content);
        assertEquals(true, doc.get(0).getSection(0).getParagraph(0).getSentence(0).isFirstSentence);
        assertEquals(0, doc.get(0).getSection(0).getParagraph(0).getSentence(0).position);
        assertEquals("sentence01", doc.get(0).getSection(0).getParagraph(0).getSentence(1).content);
        assertEquals(false, doc.get(0).getSection(0).getParagraph(0).getSentence(1).isFirstSentence);
        assertEquals(1, doc.get(0).getSection(0).getParagraph(0).getSentence(1).position);

        // second document
        assertEquals(1, doc.get(1).size());
        assertEquals(Optional.of("doc2"), doc.get(1).getFileName());
        assertEquals("sec2", doc.get(1).getSection(0).getHeaderContent(0).content);
        assertEquals(0, doc.get(1).getSection(0).getLevel());
        assertEquals(1, doc.get(1).getSection(0).getNumberOfParagraphs());
        assertEquals(2, doc.get(1).getSection(0).getParagraph(0).getNumberOfSentences());
        assertEquals("sentence10", doc.get(1).getSection(0).getParagraph(0).getSentence(0).content);
        assertEquals(true, doc.get(1).getSection(0).getParagraph(0).getSentence(0).isFirstSentence);
        assertEquals(0, doc.get(1).getSection(0).getParagraph(0).getSentence(0).position);
        assertEquals("sentence11", doc.get(1).getSection(0).getParagraph(0).getSentence(1).content);
        assertEquals(false, doc.get(1).getSection(0).getParagraph(0).getSentence(1).isFirstSentence);
        assertEquals(1, doc.get(1).getSection(0).getParagraph(0).getSentence(1).position);
    }
}
