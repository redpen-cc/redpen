package cc.redpen.validator;

import cc.redpen.RedPenException;
import cc.redpen.model.Sentence;
import cc.redpen.validator.sentence.SentenceLengthLocalizedValidator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class SentenceLengthLocalizedValidatorTest extends Validator {
    @Test
    public void testValidationErrorCreation() throws RedPenException {
        Validator samplePluginValidator = new SentenceLengthLocalizedValidator();
        samplePluginValidator.setLocale(Locale.ENGLISH);
        List<ValidationError> validationErrors = new ArrayList<>();
        samplePluginValidator.setErrorList(validationErrors);
        Sentence sentence = new Sentence("sentence", 1);
        StringBuilder longString = new StringBuilder("long sentence.");
        for (int i = 0; i < 7; i++) {
            longString.append(longString.toString());
        }
        // longString is 1792 characters long.
        Sentence longSentence = new Sentence(longString.toString() , 1);

        samplePluginValidator.validate(sentence);
        samplePluginValidator.validate(longSentence);
        assertEquals(2, validationErrors.size());
        assertEquals("Sentence is shorter than 100 characters long.", validationErrors.get(0).getMessage());
        assertEquals("Sentence is longer than 1,000 characters long.", validationErrors.get(1).getMessage());


        samplePluginValidator.setLocale(Locale.JAPANESE);
        validationErrors = new ArrayList<>();
        samplePluginValidator.setErrorList(validationErrors);
        samplePluginValidator.validate(sentence);
        samplePluginValidator.validate(longSentence);
        assertEquals(2, validationErrors.size());
        assertEquals("文が100文字より短いです。", validationErrors.get(0).getMessage());
        assertEquals("文が1,000文字より長いです。", validationErrors.get(1).getMessage());

    }
}
