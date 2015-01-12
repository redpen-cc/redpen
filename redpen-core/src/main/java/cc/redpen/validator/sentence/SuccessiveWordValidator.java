package cc.redpen.validator.sentence;

import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.TokenElement;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;

import java.util.List;

public class SuccessiveWordValidator extends Validator {
    @Override
    public void validate(List<ValidationError> errors, Sentence sentence) {
        String prevSurface = "";
        for (TokenElement token : sentence.getTokens()) {
            String currentSurface = token.getSurface();
            if (prevSurface.equals(currentSurface) && currentSurface.length() > 0) {
                errors.add(createValidationError(sentence, currentSurface));
            }
            prevSurface = currentSurface;
        }
    }
}
