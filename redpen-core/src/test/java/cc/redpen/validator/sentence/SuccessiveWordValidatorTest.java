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

public class SuccessiveWordValidatorTest {
    @Test
    public void testDetectSuccessiveWord() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("SuccessiveWord"))
                .setLanguage("en").build();

        List<Document> documents = new ArrayList<>();
                documents.add(new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence(
                                "the item is is a good.",
                                1)
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(1, errors.get(documents.get(0)).size());
    }

    @Test
    public void testDetectJapaneseSuccessiveWord() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("SuccessiveWord"))
                .setLanguage("ja").build();

        List<Document> documents = new ArrayList<>(); // TODO: fix
                documents.add(new Document.DocumentBuilder(new JapaneseTokenizer())
                .addSection(1)
                .addParagraph()
                .addSentence("私はは嬉しい.", 1)
                .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(1, errors.get(documents.get(0)).size());
    }

    @Test
    public void testNonSuccessiveDoubledWord() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("SuccessiveWord"))
                .setLanguage("en").build();

        List<Document> documents = new ArrayList<>();
                documents.add(new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence("the item is a item good.", 1)
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(0, errors.get(documents.get(0)).size());
    }
}
