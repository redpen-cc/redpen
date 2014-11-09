package cc.redpen.validator.sentence;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.distributor.FakeResultDistributor;
import cc.redpen.model.Document;
import cc.redpen.model.DocumentCollection;
import cc.redpen.validator.ValidationError;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

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
        assertEquals(1, validator.validate(documents.getDocument(0).getLastSection().getParagraph(0).getSentence(0)).size());
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
        assertEquals(0, validator.validate(documents.getDocument(0).getLastSection().getParagraph(0).getSentence(0)).size());
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
        assertEquals(0, validator.validate(documents.getDocument(0).getLastSection().getParagraph(0).getSentence(0)).size());
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
        assertEquals(0, validator.validate(documents.getDocument(0).getLastSection().getParagraph(0).getSentence(0)).size());
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
        assertEquals(0, validator.validate(documents.getDocument(0).getLastSection().getParagraph(0).getSentence(0)).size());
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
        assertEquals(0, validator.validate(documents.getDocument(0).getLastSection().getParagraph(0).getSentence(0)).size());
    }

    @Test
    public void testLoadDefaultDictionary() throws RedPenException {
        Configuration config = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("StartWithCapitalLetter"))
                .setLanguage("en").build();

        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument("")
                .addSection(1)
                .addParagraph()
                .addSentence(
                        "mixi is a Japanese company.",
                        1)
                .build();

        RedPen redPen = new RedPen.Builder()
                .setConfiguration(config)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(0, errors.get(documents.getDocument(0)).size());
    }
}
