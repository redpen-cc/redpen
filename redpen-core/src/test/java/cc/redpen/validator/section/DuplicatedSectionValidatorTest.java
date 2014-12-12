package cc.redpen.validator.section;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.validator.ValidationError;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DuplicatedSectionValidatorTest {

    @Test
    public void testDetectDuplicatedSection() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("DuplicateSection"))
                .setLanguage("en").build();

        List<Document> documents = new ArrayList<>();
        documents.add(
                new Document.DocumentBuilder()
                        .addSection(1)
                        .addSectionHeader("this is header")
                        .addParagraph()
                        .addSentence("he is a super man.", 1)
                        .addSection(1)
                        .addSectionHeader("this is header 2")
                        .addParagraph()
                        .addSentence("he is a super man.", 2)
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(2, errors.get(documents.get(0)).size());
    }


    @Test
    public void testDetectNonDuplicatedSection() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("DuplicateSection"))
                .setLanguage("en").build();

        List<Document> documents = new ArrayList<>();
        documents.add(
                new Document.DocumentBuilder()
                        .addSection(1)
                        .addSectionHeader("foobar")
                        .addParagraph()
                        .addSentence("baz baz baz", 1)
                        .addSection(1)
                        .addSectionHeader("aho")
                        .addParagraph()
                        .addSentence("zoo zoo zoo", 2)
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void testDetectDuplicatedSectionWithSameHeader() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("DuplicateSection"))
                .setLanguage("en").build();

        List<Document> documents = new ArrayList<>();
        documents.add(
                new Document.DocumentBuilder()
                        .addSection(1)
                        .addSectionHeader("this is header.")
                        .addParagraph()
                        .addSentence("he is a super man.", 1)
                        .addSection(1)
                        .addSectionHeader("this is header.")
                        .addParagraph()
                        .addSentence("he is a super man.", 2)
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(2, errors.get(documents.get(0)).size());
    }

}
