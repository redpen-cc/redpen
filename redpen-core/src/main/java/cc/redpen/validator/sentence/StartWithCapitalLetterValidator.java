package cc.redpen.validator.sentence;

import cc.redpen.ValidationError;
import cc.redpen.model.Sentence;
import cc.redpen.validator.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Check if the input sentence start with a captal letter.
 */
public class StartWithCapitalLetterValidator extends Validator<Sentence> {
    @Override
    public List<ValidationError> validate(Sentence block) {
        List<ValidationError> results = new ArrayList<>();
        String content = block.content;
        Character headChar = content.charAt(0);
        if (headChar.isLowerCase(headChar)) {
            results.add(new ValidationError(
                    this.getClass(),
                    "Sentence start with a small character",
                    block
            ));
        }
        return results;
    }
}
