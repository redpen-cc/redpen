package cc.redpen.validator.sentence;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.tokenizer.JapaneseTokenizer;
import cc.redpen.validator.ValidationError;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InvalidWordValidatorTest {

    @Test
    public void testSimpleRun() {
        List<Document> documents = new ArrayList<>();documents.add(
                new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence(
                                "He is a foolish guy.",
                                1)
                        .build());

        InvalidWordValidator validator = new InvalidWordValidator();
        validator.addInvalid("foolish");
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, documents.get(0).getLastSection().getParagraph(0).getSentence(0));
        assertEquals(1, errors.size());
    }

    @Test
    public void testVoid() {
        List<Document> documents = new ArrayList<>();documents.add(
                new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence(
                                "",
                                1)
                        .build());

        InvalidWordValidator validator = new InvalidWordValidator();
        validator.addInvalid("foolish");
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, documents.get(0).getLastSection().getParagraph(0).getSentence(0));
        assertEquals(0, errors.size());
    }

    @Test
    public void testLoadDefaultDictionary() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("InvalidWord"))
                .setLanguage("en").build();

        List<Document> documents = new ArrayList<>();documents.add(
                new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence("he is a foolish man.", 1)
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(1, errors.get(documents.get(0)).size());
        assertTrue(errors.get(documents.get(0)).get(0).getMessage().contains("foolish"));
    }


    @Test
    public void testLoadUserDictionary() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("InvalidWord").addAttribute("list", "boom,domo"))
                .setLanguage("en").build();

        List<Document> documents = new ArrayList<>();documents.add(
                new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence("Domo is a greeting word in Japan.", 1)
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(1, errors.get(documents.get(0)).size());
    }

    /**
     * Assert not throw a exception even when there is no default dictionary.
     */
    @Test(expected = RedPenException.class)
    public void testLoadNotExistDefaultDictionary() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("InvalidWord"))
                .setLanguage("ja").build(); // NOTE: no dictionary for japanese or other languages whose words are not split by white space.

        List<Document> documents = new ArrayList<>();documents.add(
                new Document.DocumentBuilder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence("こんにちは、群馬にきました。", 1)
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(0, errors.get(documents.get(0)).size());
    }
}
