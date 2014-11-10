package cc.redpen.model;

import org.junit.Test;

public class DocumentTest {
    @Test(expected = IllegalStateException.class)
    public void testCreateParagraphBeforeSection() {
        DocumentCollection doc = new DocumentCollection.Builder().addDocument(
                new Document.DocumentBuilder()
                        .setFileName("Foobar")
                        .addParagraph()
                        .addSection(0)
                        .build()).build();
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateListBlockBeforeSection() {
        DocumentCollection doc = new DocumentCollection.Builder().addDocument(
                new Document.DocumentBuilder()
                        .setFileName("Foobar")
                        .addListBlock()
                        .addSection(0)
                        .build()).build();
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateListElementBeforeListBlock() {
        DocumentCollection doc = new DocumentCollection.Builder().addDocument(
                new Document.DocumentBuilder()
                        .setFileName("Foobar")
                        .addListElement(0, "foo")
                        .addListBlock()
                        .build()).build();
    }
}
