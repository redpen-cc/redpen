package cc.redpen.validator.sentence;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.tokenizer.TokenElement;
import cc.redpen.tokenizer.JapaneseTokenizer;
import cc.redpen.validator.ValidationError;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductVersionValidatorTest {
    @Test
    public void testProductVersion() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("ProductVersion"))
                .setLanguage("ja").build();

        List<Document> documents = new ArrayList<>();
        documents.add(
                new Document.DocumentBuilder(new JapaneseTokenizer())
                .addSection(1)
                .addParagraph()
                .addSentence("RedPen 1.3.0", 0)
                .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        List<TokenElement> tokens = documents.get(0).getSection(0).getParagraph(0).getSentence(0).getTokens();
        Assert.assertEquals(3, tokens.size());
    }

    @Test
    public void testProductVersionTwice() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("ProductVersion"))
                .setLanguage("ja").build();

        List<Document> documents = new ArrayList<>();
        documents.add(
                new Document.DocumentBuilder(new JapaneseTokenizer())
                .addSection(1)
                .addParagraph()
                .addSentence("RedPen 0.6 RedPen 1.2", 0)
                .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        List<TokenElement> tokens = documents.get(0).getSection(0).getParagraph(0).getSentence(0).getTokens();
        Assert.assertEquals(7, tokens.size());
    }
}
