package cc.redpen.validator.sentence;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.distributor.FakeResultDistributor;
import cc.redpen.model.Document;
import cc.redpen.model.DocumentCollection;
import cc.redpen.model.Sentence;
import cc.redpen.validator.ValidationError;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SpaceBetweenAlphabeticalWordValidatorTest {
    @Test
    public void testNeedBeforeSpace() {
        SpaceBetweenAlphabeticalWordValidator validator = new SpaceBetweenAlphabeticalWordValidator();
        List<ValidationError> errors =
                validator.validate(new Sentence("きょうはCoke を飲みたい。", 0));
        assertEquals(1, errors.size());
    }

    @Test
    public void testNeedAfterSpace() {
        SpaceBetweenAlphabeticalWordValidator validator = new SpaceBetweenAlphabeticalWordValidator();
        List<ValidationError> errors =
                validator.validate(new Sentence("きょうは Cokeを飲みたい。", 0));
        assertEquals(1, errors.size());
    }

    @Test
    public void testNeedBeforeAndAfterSpace() {
        SpaceBetweenAlphabeticalWordValidator validator = new SpaceBetweenAlphabeticalWordValidator();
        List<ValidationError> errors =
                validator.validate(new Sentence("きょうはCokeを飲みたい。", 0));
        assertEquals(2, errors.size());
    }

    @Test
    public void testNotNeedSpaces() {
        SpaceBetweenAlphabeticalWordValidator validator = new SpaceBetweenAlphabeticalWordValidator();
        List<ValidationError> errors =
                validator.validate(new Sentence("This Coke is cold", 0));
        assertEquals(0, errors.size());
    }

    @Test
    public void testLatinSymbolWithoutSpace() {
        SpaceBetweenAlphabeticalWordValidator validator = new SpaceBetweenAlphabeticalWordValidator();
        List<ValidationError> errors =
                validator.validate(new Sentence("きょうは,コーラを飲みたい。", 0));
        assertEquals(0, errors.size());
    }

    @Test
    public void testWithParenthesis() throws RedPenException {
        Configuration config = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("SpaceBetweenAlphabeticalWord"))
                .setLanguage("ja").build();

        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument("")
                .addSection(1)
                .addParagraph()
                .addSentence(
                        "きょうは（Coke）を飲みたい。",
                        1)
                .build();

        RedPen redPen = new RedPen.Builder()
                .setConfiguration(config)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(0, errors.get(documents.getDocument(0)).size());
    }

    @Test
    public void testWithComma() throws RedPenException {
        Configuration config = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("SpaceBetweenAlphabeticalWord"))
                .setLanguage("ja").build();

        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument("")
                .addSection(1)
                .addParagraph()
                .addSentence(
                        "きょうは、Coke を飲みたい。",
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
