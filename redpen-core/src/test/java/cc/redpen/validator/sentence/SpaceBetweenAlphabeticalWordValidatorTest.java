package cc.redpen.validator.sentence;

import cc.redpen.model.Sentence;
import cc.redpen.validator.ValidationError;
import org.junit.Test;

import java.util.List;

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
}
