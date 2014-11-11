package cc.redpen.validator.sentence;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.distributor.FakeResultDistributor;
import cc.redpen.model.Document;
import cc.redpen.model.DocumentCollection;
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.JapaneseTokenizer;
import cc.redpen.validator.ValidationError;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class EndOfSentenceValidatorTest {
    @Test
    public void testInvalidEndOfSentence() {
        EndOfSentenceValidator validator = new EndOfSentenceValidator();
        List<ValidationError> errors = validator.validate(
                new Sentence("He said \"that is right\".", 0));
        assertEquals(1, errors.size());
    }

    @Test
    public void testValidEndOfSentence() {
        EndOfSentenceValidator validator = new EndOfSentenceValidator();
        List<ValidationError> errors = validator.validate(
                new Sentence("He said \"that is right.\"", 0));
        assertEquals(0, errors.size());
    }

    @Test
    public void testInValidEndOfSentenceWithQuestionMark() {
        EndOfSentenceValidator validator = new EndOfSentenceValidator();
        List<ValidationError> errors = validator.validate(
                new Sentence("He said \"Is it right\"?", 0));
        assertEquals(1, errors.size());
    }

    @Test
    public void testVoid() {
        EndOfSentenceValidator validator = new EndOfSentenceValidator();
        List<ValidationError> errors = validator.validate(
                new Sentence("", 0));
        assertEquals(0, errors.size());
    }

    @Test
    public void testJapaneseInvalidEndOfSentence() throws RedPenException {
        Configuration config = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("EndOfSentence"))
                .setLanguage("ja").build();

        DocumentCollection documents = new DocumentCollection.Builder().addDocument(
                new Document.DocumentBuilder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence("彼は言った，“今日は誕生日”。", 1)
                        .build()).build();

        RedPen redPen = new RedPen.RedPenBuilder()
                .setConfiguration(config)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(1, errors.get(documents.getDocument(0)).size());
    }
}
