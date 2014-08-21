package cc.redpen.validator.sentence;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.ValidationError;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.distributor.FakeResultDistributor;
import cc.redpen.model.DocumentCollection;
import cc.redpen.model.Sentence;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class StartWithCapitalLetterValidatorTest {
    @Test
    public void testDetectStartWithSmallCharacter() {
        StartWithCapitalLetterValidator validator = new StartWithCapitalLetterValidator();
        assertEquals(1, validator.validate(new Sentence("this is it.", 0)).size());
    }

    @Test
    public void testDetectStartWithCapitalCharacter() {
        StartWithCapitalLetterValidator validator = new StartWithCapitalLetterValidator();
        assertEquals(0, validator.validate(new Sentence("This is it.", 0)).size());
    }

    @Test
    public void testStartWithElementOfWhiteList() {
        StartWithCapitalLetterValidator validator = new StartWithCapitalLetterValidator();
        validator.addWhiteList("iPhone");
        assertEquals(0, validator.validate(new Sentence("iPhone is a mobile computer.", 0)).size());
    }

    @Test
    public void testStartWithWhiteListItemInJapaneseSentence() {
        StartWithCapitalLetterValidator validator = new StartWithCapitalLetterValidator();
        validator.addWhiteList("iPhone");
        assertEquals(0, validator.validate(new Sentence("iPhone はカッコイイ．", 0)).size());
    }

    @Test
    public void testStartWithWhiteSpaceAndThenItemOfWhiteList() {
        StartWithCapitalLetterValidator validator = new StartWithCapitalLetterValidator();
        validator.addWhiteList("iPhone");
        assertEquals(0, validator.validate(new Sentence(" iPhone is a mobile computer.", 0)).size());
    }

    @Test
    public void testVoid() {
        StartWithCapitalLetterValidator validator = new StartWithCapitalLetterValidator();
        validator.addWhiteList("iPhone");
        assertEquals(0, validator.validate(new Sentence("", 0)).size());
    }

    @Test
    public void testLoadDefaultDictionary() throws RedPenException {
        Configuration config = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("StartWithCapitalLetter"))
                .setSymbolTable("en").build();

        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument("")
                .addSection(1)
                .addParagraph()
                .addSentence(
                        "mixi is a Japanese company.",
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
