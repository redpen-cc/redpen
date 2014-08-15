package cc.redpen.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DocumentCollectionTest {
    @Test
    public void testCreateDocumentCollection() {
        DocumentCollection doc = new DocumentCollection.Builder().addDocument("Foobar")
                .addSection(0)
                .addSectionHeader("baz")
                .addParagraph()
                .addSentence("sentence0", 0)
                .addSentence("sentence1", 1)
                .build();

        assertEquals(1, doc.getNumberOfDocuments());
        assertEquals(1, doc.getFile(0).getNumberOfSections());
        assertEquals(0, doc.getFile(0).getSection(0).getLevel());
        assertEquals("Foobar", doc.getFile(0).getFileName());
        assertEquals("baz", doc.getFile(0).getSection(0).getHeaderContent(0).content);
        assertEquals(1, doc.getFile(0).getSection(0).getNumberOfParagraphs());
        assertEquals(2, doc.getFile(0).getSection(0).getParagraph(0).getNumberOfSentences());
        assertEquals("sentence0", doc.getFile(0).getSection(0).getParagraph(0).getSentence(0).content);
        assertEquals(true, doc.getFile(0).getSection(0).getParagraph(0).getSentence(0).isFirstSentence);
        assertEquals(0, doc.getFile(0).getSection(0).getParagraph(0).getSentence(0).position);
        assertEquals("sentence1", doc.getFile(0).getSection(0).getParagraph(0).getSentence(1).content);
        assertEquals(false, doc.getFile(0).getSection(0).getParagraph(0).getSentence(1).isFirstSentence);
        assertEquals(1, doc.getFile(0).getSection(0).getParagraph(0).getSentence(1).position);
    }

    @Test
    public void testVoidCreateDocument() {
        DocumentCollection doc = new DocumentCollection
                .Builder().addDocument("Foobar").build();
        assertEquals(1, doc.getNumberOfDocuments());
        assertEquals(0, doc.getFile(0).getNumberOfSections());
    }

    @Test
    public void testDocumentCollectionWithMultipleDocument() {
        DocumentCollection doc = new DocumentCollection.Builder()
                .addDocument("doc1")
                .addSection(0)
                .addSectionHeader("sec1")
                .addParagraph()
                .addSentence("sentence00", 0)
                .addSentence("sentence01", 1)
                .addDocument("doc2")
                .addSection(0)
                .addSectionHeader("sec2")
                .addParagraph()
                .addSentence("sentence10", 0)
                .addSentence("sentence11", 1)
                .build();

        assertEquals(2, doc.getNumberOfDocuments());

        // first document
        assertEquals(1, doc.getFile(0).getNumberOfSections());
        assertEquals("doc1", doc.getFile(0).getFileName());
        assertEquals("sec1", doc.getFile(0).getSection(0).getHeaderContent(0).content);
        assertEquals(0, doc.getFile(0).getSection(0).getLevel());
        assertEquals(1, doc.getFile(0).getSection(0).getNumberOfParagraphs());
        assertEquals(2, doc.getFile(0).getSection(0).getParagraph(0).getNumberOfSentences());
        assertEquals("sentence00", doc.getFile(0).getSection(0).getParagraph(0).getSentence(0).content);
        assertEquals(true, doc.getFile(0).getSection(0).getParagraph(0).getSentence(0).isFirstSentence);
        assertEquals(0, doc.getFile(0).getSection(0).getParagraph(0).getSentence(0).position);
        assertEquals("sentence01", doc.getFile(0).getSection(0).getParagraph(0).getSentence(1).content);
        assertEquals(false, doc.getFile(0).getSection(0).getParagraph(0).getSentence(1).isFirstSentence);
        assertEquals(1, doc.getFile(0).getSection(0).getParagraph(0).getSentence(1).position);

        // second document
        assertEquals(1, doc.getFile(1).getNumberOfSections());
        assertEquals("doc2", doc.getFile(1).getFileName());
        assertEquals("sec2", doc.getFile(1).getSection(0).getHeaderContent(0).content);
        assertEquals(0, doc.getFile(1).getSection(0).getLevel());
        assertEquals(1, doc.getFile(1).getSection(0).getNumberOfParagraphs());
        assertEquals(2, doc.getFile(1).getSection(0).getParagraph(0).getNumberOfSentences());
        assertEquals("sentence10", doc.getFile(1).getSection(0).getParagraph(0).getSentence(0).content);
        assertEquals(true, doc.getFile(1).getSection(0).getParagraph(0).getSentence(0).isFirstSentence);
        assertEquals(0, doc.getFile(1).getSection(0).getParagraph(0).getSentence(0).position);
        assertEquals("sentence11", doc.getFile(1).getSection(0).getParagraph(0).getSentence(1).content);
        assertEquals(false, doc.getFile(1).getSection(0).getParagraph(0).getSentence(1).isFirstSentence);
        assertEquals(1, doc.getFile(1).getSection(0).getParagraph(0).getSentence(1).position);
    }

    @Test
    public void testCreateDocumentWithList() {
        DocumentCollection doc = new DocumentCollection.Builder().addDocument("Foobar")
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

        assertEquals(1, doc.getNumberOfDocuments());
        assertEquals(1, doc.getFile(0).getNumberOfSections());
        assertEquals(0, doc.getFile(0).getSection(0).getLevel());
        assertEquals("Foobar", doc.getFile(0).getFileName());
        assertEquals("baz", doc.getFile(0).getSection(0).getHeaderContent(0).content);
        assertEquals(1, doc.getFile(0).getSection(0).getNumberOfParagraphs());
        assertEquals("sentence0", doc.getFile(0).getSection(0).getParagraph(0).getSentence(0).content);
        assertEquals(true, doc.getFile(0).getSection(0).getParagraph(0).getSentence(0).isFirstSentence);
        assertEquals(0, doc.getFile(0).getSection(0).getParagraph(0).getSentence(0).position);
        assertEquals("sentence1", doc.getFile(0).getSection(0).getParagraph(0).getSentence(1).content);
        assertEquals(false, doc.getFile(0).getSection(0).getParagraph(0).getSentence(1).isFirstSentence);
        assertEquals(1, doc.getFile(0).getSection(0).getParagraph(0).getSentence(1).position);
        assertEquals(1, doc.getFile(0).getSection(0).getNumberOfLists());
        assertEquals(3, doc.getFile(0).getSection(0).getListBlock(0).getNumberOfListElements());
        assertEquals(0, doc.getFile(0).getSection(0).getListBlock(0).getListElement(0).getLevel());
        assertEquals("list0", doc.getFile(0).getSection(0).getListBlock(0).getListElement(0).getSentence(0).content);
        assertEquals(0, doc.getFile(0).getSection(0).getListBlock(0).getListElement(1).getLevel());
        assertEquals("list1", doc.getFile(0).getSection(0).getListBlock(0).getListElement(1).getSentence(0).content);
        assertEquals(1, doc.getFile(0).getSection(0).getListBlock(0).getListElement(2).getLevel());
        assertEquals("list2", doc.getFile(0).getSection(0).getListBlock(0).getListElement(2).getSentence(0).content);
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateSectionBeforeDocument() {
        DocumentCollection doc = new DocumentCollection.Builder()
                .addSection(0)
                .addDocument("Foobar")
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateParagraphBeforeSection() {
        DocumentCollection doc = new DocumentCollection.Builder()
                .addDocument("Foobar")
                .addParagraph()
                .addSection(0)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateListBlockBeforeSection() {
        DocumentCollection doc = new DocumentCollection.Builder()
                .addDocument("Foobar")
                .addListBlock()
                .addSection(0)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateListElementBeforeListBlock() {
        DocumentCollection doc = new DocumentCollection.Builder()
                .addDocument("Foobar")
                .addListElement(0, "foo")
                .addListBlock()
                .build();
    }
}
