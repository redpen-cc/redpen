package cc.redpen.validator;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.model.Sentence;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmbeddedJavaScriptValidatorTest {
    @Test
    void loadEmbeddedJS() throws RedPenException {
        ValidatorFactory.getInstance("MyEmbeddedJS");
        ValidatorFactory.getInstance("MyEmbeddedJSSection");
        ValidatorFactory.getInstance("MyEmbeddedJSSentence");
    }

    @Test
    void embeddedJS() throws RedPenException {
        Document document = Document.builder()
                .addSection(1)
                .addParagraph()
                .addSentence(new Sentence("the good item is a good example.", 1))
                .build();

        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("MyEmbeddedJS").addProperty("property1", "value1"))
                .addValidatorConfig(new ValidatorConfiguration("MyEmbeddedJSSentence").addProperty("property2", "value2"))
                .addValidatorConfig(new ValidatorConfiguration("MyEmbeddedJSSection").addProperty("property3", "value3"))
                .build();
        RedPen redPen = new RedPen(config);
        List<ValidationError> errors = redPen.validate(document);
        assertEquals(3, errors.size());
        assertEquals("MyEmbeddedJS value1", errors.get(0).getMessage());
        assertEquals("MyEmbeddedJSSentence value2", errors.get(2).getMessage());
        assertEquals("MyEmbeddedJSSection value3", errors.get(1).getMessage());

    }
}
