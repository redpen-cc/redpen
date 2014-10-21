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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InvalidWordValidatorTest {

    @Test
    public void testSimpleRun() {
        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument("")
                .addSection(1)
                .addParagraph()
                .addSentence(
                        "He is a foolish guy.",
                        1)
                .build();

        InvalidWordValidator validator = new InvalidWordValidator();
        validator.addInvalid("foolish");
        List<ValidationError> errors = validator.validate(documents.getDocument(0).getLastSection().getParagraph(0).getSentence(0));
        assertEquals(1, errors.size());
    }

    @Test
    public void testVoid() {
        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument("")
                .addSection(1)
                .addParagraph()
                .addSentence(
                        "",
                        1)
                .build();

        InvalidWordValidator validator = new InvalidWordValidator();
        validator.addInvalid("foolish");
        List<ValidationError> errors = validator.validate(documents.getDocument(0).getLastSection().getParagraph(0).getSentence(0));
        assertEquals(0, errors.size());
    }

    @Test
    public void testLoadDefaultDictionary() throws RedPenException {
        Configuration config = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("InvalidWord"))
                .setLanguage("en").build();

        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument("")
                .addSection(1)
                .addParagraph()
                .addSentence(
                        "he is a foolish man.",
                        1)
                .build();

        RedPen validator = new RedPen.Builder()
                .setConfiguration(config)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        List<ValidationError> errors = validator.check(documents);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).getMessage().contains("foolish"));
    }

    /**
     * Assert not throw a exception even when there is no default dictionary.
     *
     */
    @Test(expected = RedPenException.class)
    public void testLoadNotExistDefaultDictionary() throws RedPenException {
        Configuration config = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("InvalidWord"))
                .setLanguage("ja").build(); // NOTE: no dictionary for japanese or other languages whose words are not split by white space.

        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument("")
                .addSection(1)
                .addParagraph()
                .addSentence(
                        "こんにちは、群馬にきました。",
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
