package cc.redpen.validator.sentence;

import cc.redpen.ValidationError;
import cc.redpen.model.Sentence;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class SpaceBetweenAlphabeticalWordTest {
    @Test
    public void testNeedBeforeSpace() {
        SpaceBetweenAlphabeticalWord validator = new SpaceBetweenAlphabeticalWord();
        List<ValidationError> errors =
                validator.validate(new Sentence("きょうはCoke を飲みたい。", 0));
        assertEquals(1, errors.size());
    }

    @Test
    public void testNeedAfterSpace() {
        SpaceBetweenAlphabeticalWord validator = new SpaceBetweenAlphabeticalWord();
        List<ValidationError> errors =
                validator.validate(new Sentence("きょうは Cokeを飲みたい。", 0));
        assertEquals(1, errors.size());
    }

    @Test
    public void testNeedBeforeAndAfterSpace() {
        SpaceBetweenAlphabeticalWord validator = new SpaceBetweenAlphabeticalWord();
        List<ValidationError> errors =
                validator.validate(new Sentence("きょうはCokeを飲みたい。", 0));
        assertEquals(2, errors.size());
    }

    @Test
    public void testNotNeedSpaces() {
        SpaceBetweenAlphabeticalWord validator = new SpaceBetweenAlphabeticalWord();
        List<ValidationError> errors =
                validator.validate(new Sentence("This Coke is cold", 0));
        assertEquals(0, errors.size());
    }
}
