package cc.redpen.validator.sentence;

import cc.redpen.ValidationError;
import cc.redpen.model.Sentence;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class StartWithCapitalLetterValidatorTest {
    @Test
    public void testDetectStartWithSmallCharacter() {
        StartWithCapitalLetterValidator validator = new StartWithCapitalLetterValidator();
        List<ValidationError> errors = validator.validate(new Sentence("this is it.", 0));
        assertEquals(1, errors.size());
    }

    @Test
    public void testDetectStartWithChaptalCharacter() {
        StartWithCapitalLetterValidator validator = new StartWithCapitalLetterValidator();
        List<ValidationError> errors = validator.validate(new Sentence("This is it.", 0));
        assertEquals(0, errors.size());
    }
}
