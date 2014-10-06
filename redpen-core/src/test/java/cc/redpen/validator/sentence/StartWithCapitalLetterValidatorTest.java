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

public class StartWithCapitalLetterValidatorTest {
    @Test
    public void testDetectStartWithSmallCharacter() {
        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument("")
                .addSection(1)
                .addParagraph()
                .addSentence(
                        "this is it.",
                        1)
                .build();
        StartWithCapitalLetterValidator validator = new StartWithCapitalLetterValidator();
        assertEquals(1, validator.validate(documents.getFile(0).getLastSection().getParagraph(0).getSentence(0)).size());
    }

    @Test
    public void testDetectStartWithCapitalCharacter() {
        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument("")
                .addSection(1)
                .addParagraph()
                .addSentence(
                        "This is it.",
                        1)
                .build();
        StartWithCapitalLetterValidator validator = new StartWithCapitalLetterValidator();
        assertEquals(0, validator.validate(documents.getFile(0).getLastSection().getParagraph(0).getSentence(0)).size());
    }

    @Test
    public void testStartWithElementOfWhiteList() {
        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument("")
                .addSection(1)
                .addParagraph()
                .addSentence(
                        "iPhone is a mobile computer.",
                        1)
                .build();
        StartWithCapitalLetterValidator validator = new StartWithCapitalLetterValidator();
        validator.addWhiteList("iPhone");
        assertEquals(0, validator.validate(documents.getFile(0).getLastSection().getParagraph(0).getSentence(0)).size());
    }

    @Test
    public void testStartWithWhiteListItemInJapaneseSentence() {
        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument("")
                .addSection(1)
                .addParagraph()
                .addSentence(
                        "iPhone はカッコイイ．",
                        1)
                .build();
        StartWithCapitalLetterValidator validator = new StartWithCapitalLetterValidator();
        validator.addWhiteList("iPhone");
        assertEquals(0, validator.validate(documents.getFile(0).getLastSection().getParagraph(0).getSentence(0)).size());
    }

    @Test
    public void testStartWithWhiteSpaceAndThenItemOfWhiteList() {
        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument("")
                .addSection(1)
                .addParagraph()
                .addSentence(
                        " iPhone is a mobile computer.",
                        1)
                .build();
        StartWithCapitalLetterValidator validator = new StartWithCapitalLetterValidator();
        validator.addWhiteList("iPhone");
        assertEquals(0, validator.validate(documents.getFile(0).getLastSection().getParagraph(0).getSentence(0)).size());
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
        StartWithCapitalLetterValidator validator = new StartWithCapitalLetterValidator();
        validator.addWhiteList("iPhone");
        assertEquals(0, validator.validate(documents.getFile(0).getLastSection().getParagraph(0).getSentence(0)).size());
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
