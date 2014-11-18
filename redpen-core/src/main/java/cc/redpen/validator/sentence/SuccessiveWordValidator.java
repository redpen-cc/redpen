package cc.redpen.validator.sentence;

import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.TokenElement;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;

import java.util.ArrayList;
import java.util.List;

public class SuccessiveWordValidator extends Validator {
    @Override
    public List<ValidationError> validate(Sentence sentence) {
        List<ValidationError> errors = new ArrayList<>();
        String prevSurface = "";
        for (TokenElement token : sentence.tokens) {
            String currentSurface = token.getSurface();
            if (prevSurface.equals(currentSurface) && currentSurface.length() > 0) {
                errors.add(createValidationError(sentence, currentSurface));
            }
            prevSurface = currentSurface;
        }
        return errors;
    }
}
