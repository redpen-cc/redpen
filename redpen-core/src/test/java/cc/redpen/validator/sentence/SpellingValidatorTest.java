package cc.redpen.validator.sentence;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.distributor.FakeResultDistributor;
import cc.redpen.model.Document;
import cc.redpen.model.DocumentCollection;
import cc.redpen.validator.ValidationError;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class SpellingValidatorTest {

    @Test
    public void testValidate() throws Exception {
        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument(new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence("this iz a pen", 1)
                        .build()).build();

        SpellingValidator validator = new SpellingValidator();
        validator.addWord("this");
        validator.addWord("a");
        validator.addWord("pen");
        List<ValidationError> errors = validator.validate(
                documents.getDocument(0).getLastSection().getParagraph(0).getSentence(0));
        assertEquals(1, errors.size());
    }

    @Test
    public void testLoadDefaultDictionary() throws RedPenException {
        Configuration config = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("Spelling"))
                .setLanguage("en").build();

        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument(new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence("this iz goody", 1)
                        .build()).build();

        RedPen redPen = new RedPen.Builder()
                .setConfiguration(config)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(1, errors.get(documents.getDocument(0)).size());
    }

    @Test
    public void testUpperCase() throws RedPenException {
        Configuration config = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("Spelling"))
                .setLanguage("en").build();

        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument(new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence("This iz goody", 1)
                        .build()).build();

        RedPen redPen = new RedPen.Builder()
                .setConfiguration(config)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(1, errors.get(documents.getDocument(0)).size());
    }


    @Test
    public void testSkipCharacterCase() throws RedPenException {
        Configuration config = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("Spelling"))
                .setLanguage("en").build();

        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument(new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence("That is true, but there is a condition", 1)
                        .build()).build();

        RedPen redPen = new RedPen.Builder()
                .setConfiguration(config)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.getDocument(0)).size());
    }

    @Test
    public void testUserSkipList() throws RedPenException {
        Configuration config = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("Spelling").addAttribute("list", "abeshi,baz"))
                .setLanguage("en").build();

        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument(new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence("Abeshi is a word used in a comic.", 1)
                        .build()).build();

        RedPen redPen = new RedPen.Builder()
                .setConfiguration(config)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.getDocument(0)).size());
    }

    @Test
    public void testEndPeriod() throws RedPenException {
        Configuration config = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("Spelling"))
                .setLanguage("en").build();

        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument(new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence("That is true.", 1)
                        .build()).build();

        RedPen redPen = new RedPen.Builder()
                .setConfiguration(config)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.getDocument(0)).size());
    }

    @Test
    public void testVoid() throws RedPenException {
        Configuration config = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("Spelling"))
                .setLanguage("en").build();

        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument(new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence("", 1)
                        .build()).build();

        RedPen redPen = new RedPen.Builder()
                .setConfiguration(config)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.getDocument(0)).size());
    }
}
