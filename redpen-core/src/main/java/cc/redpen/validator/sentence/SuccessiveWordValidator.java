package cc.redpen.validator.sentence;

import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.TokenElement;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;

import java.util.ArrayList;
import java.util.List;

public class SuccessiveWordValidator extends Validator<Sentence> {
    @Override
    public List<ValidationError> validate(Sentence block) {
        List<ValidationError> errors = new ArrayList<>();
        String prevSurface = "";
        for (TokenElement token : block.tokens) {
            String currentSurface = token.getSurface();
            if (prevSurface.equals(currentSurface) && currentSurface.length() > 0) {
                errors.add(createValidationError(block, currentSurface));
            }
            prevSurface = currentSurface;
        }
        return errors;
    }
}
