package cc.redpen.validator;

import cc.redpen.RedPenException;
import cc.redpen.model.Sentence;
import cc.redpen.validator.sentence.NumberOfCharactersValidator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class NumberOfCharactersValidatorTest extends Validator {
    @Test
    public void testValidationErrorCreation() throws RedPenException {
        Validator validator = new NumberOfCharactersValidator();
        List<ValidationError> validationErrors = new ArrayList<>();
        validator.setErrorList(validationErrors);
        Sentence shortSentence = new Sentence("shortSentence", 1);
        StringBuilder longString = new StringBuilder("long sentence.");
        for (int i = 0; i < 7; i++) {
            longString.append(longString.toString());
        }
        // longString is 1792 characters long.
        Sentence longSentence = new Sentence(longString.toString() , 1);
        validator.validate(shortSentence);
        validator.validate(longSentence);
        assertEquals(2, validationErrors.size());
        assertEquals("Sentence is shorter than 100 characters long.", validationErrors.get(0).getMessage());
        assertEquals("Sentence is longer than 1000 characters long.", validationErrors.get(1).getMessage());
    }
}
