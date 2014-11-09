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

public class SuccessiveWordValidatorTest {
    @Test
    public void testDetectSuccessiveWord() throws RedPenException {
        Configuration config = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("SuccessiveWord"))
                .setLanguage("en").build();

        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument("")
                .addSection(1)
                .addParagraph()
                .addSentence(
                        "the item is is a good.",
                        1)
                .build();

        RedPen redPen = new RedPen.Builder()
                .setConfiguration(config)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        List<ValidationError> errors = redPen.validate(documents);
        assertEquals(1, errors.size());
    }

    @Test
    public void testDetectJapaneseSuccessiveWord() throws RedPenException {
        Configuration config = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("SuccessiveWord"))
                .setLanguage("ja").build();

        DocumentCollection documents = new DocumentCollection.Builder("ja") // TODO: fix
                .addDocument("")
                .addSection(1)
                .addParagraph()
                .addSentence(
                        "私はは嬉しい.",
                        1)
                .build();

        RedPen redPen = new RedPen.Builder()
                .setConfiguration(config)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        List<ValidationError> errors = redPen.validate(documents);
        assertEquals(1, errors.size());
    }

    @Test
    public void testNonSuccessiveDoubledWord() throws RedPenException {
        Configuration config = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("SuccessiveWord"))
                .setLanguage("en").build();

        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument("")
                .addSection(1)
                .addParagraph()
                .addSentence(
                        "the item is a item good.",
                        1)
                .build();

        RedPen redPen = new RedPen.Builder()
                .setConfiguration(config)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        List<ValidationError> errors = redPen.validate(documents);
        assertEquals(0, errors.size());
    }
}
