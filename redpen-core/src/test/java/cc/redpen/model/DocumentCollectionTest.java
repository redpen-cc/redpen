package cc.redpen.model;

import cc.redpen.tokenizer.JapaneseTokenizer;
import cc.redpen.tokenizer.WhiteSpaceTokenizer;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class DocumentCollectionTest {
    @Test
    public void testCreateDocumentCollection() {
        DocumentCollection doc = new DocumentCollection.Builder().addDocument(
                new Document.DocumentBuilder(new WhiteSpaceTokenizer())
                        .setFileName("Foobar")
                        .addSection(0)
                        .addSectionHeader("baz")
                        .addParagraph()
                        .addSentence("sentence0", 0)
                        .addSentence("sentence1", 1)
                        .build()).build();

        assertEquals(1, doc.getNumberOfDocuments());
        assertEquals(1, doc.getDocument(0).getNumberOfSections());
        assertEquals(0, doc.getDocument(0).getSection(0).getLevel());
        assertEquals(Optional.of("Foobar"), doc.getDocument(0).getFileName());
        assertEquals("baz", doc.getDocument(0).getSection(0).getHeaderContent(0).content);
        assertEquals(1, doc.getDocument(0).getSection(0).getNumberOfParagraphs());
        assertEquals(2, doc.getDocument(0).getSection(0).getParagraph(0).getNumberOfSentences());
        assertEquals("sentence0", doc.getDocument(0).getSection(0).getParagraph(0).getSentence(0).content);
        assertEquals(true, doc.getDocument(0).getSection(0).getParagraph(0).getSentence(0).isFirstSentence);
        assertEquals(0, doc.getDocument(0).getSection(0).getParagraph(0).getSentence(0).position);
        assertEquals("sentence1", doc.getDocument(0).getSection(0).getParagraph(0).getSentence(1).content);
        assertEquals(false, doc.getDocument(0).getSection(0).getParagraph(0).getSentence(1).isFirstSentence);
        assertEquals(1, doc.getDocument(0).getSection(0).getParagraph(0).getSentence(1).position);
    }

    @Test
    public void testVoidCreateDocument() {
        DocumentCollection doc = new DocumentCollection.Builder().addDocument(
                new Document.DocumentBuilder(new WhiteSpaceTokenizer())
                        .setFileName("Foobar").build()).build();
        assertEquals(1, doc.getNumberOfDocuments());
        assertEquals(0, doc.getDocument(0).getNumberOfSections());
    }

    @Test
    public void testDocumentCollectionWithMultipleDocument() {
        DocumentCollection doc = new DocumentCollection.Builder().addDocument(
                new Document.DocumentBuilder(new WhiteSpaceTokenizer())
                        .setFileName("doc1")
                        .addSection(0)
                        .addSectionHeader("sec1")
                        .addParagraph()
                        .addSentence("sentence00", 0)
                        .addSentence("sentence01", 1).build())
                .addDocument(new Document.DocumentBuilder(new WhiteSpaceTokenizer())
                        .setFileName("doc2")
                        .addSection(0)
                        .addSectionHeader("sec2")
                        .addParagraph()
                        .addSentence("sentence10", 0)
                        .addSentence("sentence11", 1)
                        .build()).build();

        assertEquals(2, doc.getNumberOfDocuments());

        // first document
        assertEquals(1, doc.getDocument(0).getNumberOfSections());
        assertEquals(Optional.of("doc1"), doc.getDocument(0).getFileName());
        assertEquals("sec1", doc.getDocument(0).getSection(0).getHeaderContent(0).content);
        assertEquals(0, doc.getDocument(0).getSection(0).getLevel());
        assertEquals(1, doc.getDocument(0).getSection(0).getNumberOfParagraphs());
        assertEquals(2, doc.getDocument(0).getSection(0).getParagraph(0).getNumberOfSentences());
        assertEquals("sentence00", doc.getDocument(0).getSection(0).getParagraph(0).getSentence(0).content);
        assertEquals(true, doc.getDocument(0).getSection(0).getParagraph(0).getSentence(0).isFirstSentence);
        assertEquals(0, doc.getDocument(0).getSection(0).getParagraph(0).getSentence(0).position);
        assertEquals("sentence01", doc.getDocument(0).getSection(0).getParagraph(0).getSentence(1).content);
        assertEquals(false, doc.getDocument(0).getSection(0).getParagraph(0).getSentence(1).isFirstSentence);
        assertEquals(1, doc.getDocument(0).getSection(0).getParagraph(0).getSentence(1).position);

        // second document
        assertEquals(1, doc.getDocument(1).getNumberOfSections());
        assertEquals(Optional.of("doc2"), doc.getDocument(1).getFileName());
        assertEquals("sec2", doc.getDocument(1).getSection(0).getHeaderContent(0).content);
        assertEquals(0, doc.getDocument(1).getSection(0).getLevel());
        assertEquals(1, doc.getDocument(1).getSection(0).getNumberOfParagraphs());
        assertEquals(2, doc.getDocument(1).getSection(0).getParagraph(0).getNumberOfSentences());
        assertEquals("sentence10", doc.getDocument(1).getSection(0).getParagraph(0).getSentence(0).content);
        assertEquals(true, doc.getDocument(1).getSection(0).getParagraph(0).getSentence(0).isFirstSentence);
        assertEquals(0, doc.getDocument(1).getSection(0).getParagraph(0).getSentence(0).position);
        assertEquals("sentence11", doc.getDocument(1).getSection(0).getParagraph(0).getSentence(1).content);
        assertEquals(false, doc.getDocument(1).getSection(0).getParagraph(0).getSentence(1).isFirstSentence);
        assertEquals(1, doc.getDocument(1).getSection(0).getParagraph(0).getSentence(1).position);
    }

    @Test
    public void testCreateDocumentWithList() {
        DocumentCollection doc = new DocumentCollection.Builder().addDocument(
                new Document.DocumentBuilder(new WhiteSpaceTokenizer())
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
                        .build()).build();

        assertEquals(1, doc.getNumberOfDocuments());
        assertEquals(1, doc.getDocument(0).getNumberOfSections());
        assertEquals(0, doc.getDocument(0).getSection(0).getLevel());
        assertEquals(Optional.of("Foobar"), doc.getDocument(0).getFileName());
        assertEquals("baz", doc.getDocument(0).getSection(0).getHeaderContent(0).content);
        assertEquals(1, doc.getDocument(0).getSection(0).getNumberOfParagraphs());
        assertEquals("sentence0", doc.getDocument(0).getSection(0).getParagraph(0).getSentence(0).content);
        assertEquals(true, doc.getDocument(0).getSection(0).getParagraph(0).getSentence(0).isFirstSentence);
        assertEquals(0, doc.getDocument(0).getSection(0).getParagraph(0).getSentence(0).position);
        assertEquals("sentence1", doc.getDocument(0).getSection(0).getParagraph(0).getSentence(1).content);
        assertEquals(false, doc.getDocument(0).getSection(0).getParagraph(0).getSentence(1).isFirstSentence);
        assertEquals(1, doc.getDocument(0).getSection(0).getParagraph(0).getSentence(1).position);
        assertEquals(1, doc.getDocument(0).getSection(0).getNumberOfLists());
        assertEquals(3, doc.getDocument(0).getSection(0).getListBlock(0).getNumberOfListElements());
        assertEquals(0, doc.getDocument(0).getSection(0).getListBlock(0).getListElement(0).getLevel());
        assertEquals("list0", doc.getDocument(0).getSection(0).getListBlock(0).getListElement(0).getSentence(0).content);
        assertEquals(0, doc.getDocument(0).getSection(0).getListBlock(0).getListElement(1).getLevel());
        assertEquals("list1", doc.getDocument(0).getSection(0).getListBlock(0).getListElement(1).getSentence(0).content);
        assertEquals(1, doc.getDocument(0).getSection(0).getListBlock(0).getListElement(2).getLevel());
        assertEquals("list2", doc.getDocument(0).getSection(0).getListBlock(0).getListElement(2).getSentence(0).content);
    }

    @Test
    public void testSentenceIsTokenized() {
        DocumentCollection doc = new DocumentCollection.Builder().addDocument(
                new Document.DocumentBuilder(new WhiteSpaceTokenizer())
                        .setFileName("foobar")
                        .addSection(0)
                        .addSectionHeader("baz")
                        .addParagraph()
                        .addSentence("This is a foobar.", 0)
                        .build()).build();
        assertEquals(1, doc.getNumberOfDocuments());
        assertEquals(1, doc.getDocument(0).getNumberOfSections());
        assertEquals(4, doc.getDocument(0).getSection(0).getParagraph(0).getSentence(0).tokens.size());
    }

    @Test
    public void testJapaneseSentenceIsTokenized() {
        DocumentCollection doc = new DocumentCollection.Builder().addDocument(
                new Document.DocumentBuilder(new JapaneseTokenizer())
                        .setFileName("今日")
                        .addSection(0)
                        .addSectionHeader("天気")
                        .addParagraph()
                        .addSentence("今日は晴天だ。", 0)
                        .build()).build();
        assertEquals(1, doc.getNumberOfDocuments());
        assertEquals(1, doc.getDocument(0).getNumberOfSections());
        assertEquals(5, doc.getDocument(0).getSection(0).getParagraph(0).getSentence(0).tokens.size());
    }

}
