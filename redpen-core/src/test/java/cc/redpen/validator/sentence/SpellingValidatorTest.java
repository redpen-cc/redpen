package cc.redpen.validator.sentence;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.distributor.FakeResultDistributor;
import cc.redpen.model.DocumentCollection;
import cc.redpen.validator.ValidationError;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class SpellingValidatorTest {

    @Test
    public void testValidate() throws Exception {
        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument("")
                .addSection(1)
                .addParagraph()
                .addSentence(
                        "this iz a pen",
                        1)
                .build();

        SpellingValidator validator = new SpellingValidator();
        validator.addWord("this");
        validator.addWord("a");
        validator.addWord("pen");
        List<ValidationError> errors = validator.validate(
                documents.getFile(0).getLastSection().getParagraph(0).getSentence(0));
        assertEquals(1, errors.size());
    }

    @Test
    public void testLoadDefaultDictionary() throws RedPenException {
        Configuration config = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("Spelling"))
                .setSymbolTable("en").build();

        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument("")
                .addSection(1)
                .addParagraph()
                .addSentence(
                        "this iz goody",
                        1)
                .build();

        RedPen validator = new RedPen.Builder()
                .setConfiguration(config)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        List<ValidationError> errors = validator.check(documents);
        assertEquals(1, errors.size());
    }

    @Test
    public void testUpperCase() throws RedPenException {
        Configuration config = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("Spelling"))
                .setSymbolTable("en").build();

        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument("")
                .addSection(1)
                .addParagraph()
                .addSentence(
                        "This iz goody",
                        1)
                .build();

        RedPen validator = new RedPen.Builder()
                .setConfiguration(config)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        List<ValidationError> errors = validator.check(documents);
        assertEquals(1, errors.size());
    }


    @Test
    public void testSkipCharacterCase() throws RedPenException {
        Configuration config = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("Spelling"))
                .setSymbolTable("en").build();

        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument("")
                .addSection(1)
                .addParagraph()
                .addSentence(
                        "That is true, but there is a condition",
                        1)
                .build();

        RedPen validator = new RedPen.Builder()
                .setConfiguration(config)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        List<ValidationError> errors = validator.check(documents);
        assertEquals(0, errors.size());
    }

    @Test
    public void testEndPeriod() throws RedPenException {
        Configuration config = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("Spelling"))
                .setSymbolTable("en").build();

        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument("")
                .addSection(1)
                .addParagraph()
                .addSentence(
                        "That is true.",
                        1)
                .build();

        RedPen validator = new RedPen.Builder()
                .setConfiguration(config)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        List<ValidationError> errors = validator.check(documents);
        assertEquals(0, errors.size());
    }

    @Test
    public void testVoid() throws RedPenException {
        Configuration config = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("Spelling"))
                .setSymbolTable("en").build();

        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument("")
                .addSection(1)
                .addParagraph()
                .addSentence(
                        "",
                        1)
                .build();

        RedPen validator = new RedPen.Builder()
                .setConfiguration(config)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        List<ValidationError> errors = validator.check(documents);
        assertEquals(0, errors.size());
    }
}
