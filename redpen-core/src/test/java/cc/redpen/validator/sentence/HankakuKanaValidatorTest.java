package cc.redpen.validator.sentence;

import cc.redpen.model.Sentence;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class HankakuKanaValidatorTest {
    @Test
    public void testDetectHankakuKana() {
        Validator validator = new HankakuKanaValidator();
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(new Sentence("岩の木陰にﾊﾅが咲いている", 0));
        assertEquals(2, errors.size());
    }

    @Test
    public void testRunValidatorWithoutHanakakuKana() {
        Validator validator = new HankakuKanaValidator();
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(new Sentence("岩の木陰にハナが咲いている", 0));
        assertEquals(0, errors.size());
    }
}
