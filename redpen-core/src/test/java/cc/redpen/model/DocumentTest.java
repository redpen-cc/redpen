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
        assertEquals("baz", doc.getSection(0).getHeaderContent(0).content);
        assertEquals(1, doc.getSection(0).getNumberOfParagraphs());
        assertEquals("sentence0", doc.getSection(0).getParagraph(0).getSentence(0).content);
        assertEquals(true, doc.getSection(0).getParagraph(0).getSentence(0).isFirstSentence);
        assertEquals(0, doc.getSection(0).getParagraph(0).getSentence(0).lineNum);
        assertEquals("sentence1", doc.getSection(0).getParagraph(0).getSentence(1).content);
        assertEquals(false, doc.getSection(0).getParagraph(0).getSentence(1).isFirstSentence);
        assertEquals(1, doc.getSection(0).getParagraph(0).getSentence(1).lineNum);
        assertEquals(1, doc.getSection(0).getNumberOfLists());
        assertEquals(3, doc.getSection(0).getListBlock(0).getNumberOfListElements());
        assertEquals(0, doc.getSection(0).getListBlock(0).getListElement(0).getLevel());
        assertEquals("list0", doc.getSection(0).getListBlock(0).getListElement(0).getSentence(0).content);
        assertEquals(0, doc.getSection(0).getListBlock(0).getListElement(1).getLevel());
        assertEquals("list1", doc.getSection(0).getListBlock(0).getListElement(1).getSentence(0).content);
        assertEquals(1, doc.getSection(0).getListBlock(0).getListElement(2).getLevel());
        assertEquals("list2", doc.getSection(0).getListBlock(0).getListElement(2).getSentence(0).content);
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
        assertEquals(4, doc.getSection(0).getParagraph(0).getSentence(0).tokens.size());
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
        assertEquals(5, doc.getSection(0).getParagraph(0).getSentence(0).tokens.size());
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
