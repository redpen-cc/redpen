package cc.redpen.validator;

import cc.redpen.model.Sentence;

import java.util.ArrayList;
import java.util.List;

class ValidationErrorMessageTest extends Validator<Sentence> {

    @Override
    public List<ValidationError> validate(Sentence sentence) {
        List<ValidationError> errors = new ArrayList<>();
        errors.add(createValidationError(sentence, 1, 2, 3, "sentence"));
        errors.add(createValidationError("withKey", sentence, "sentence"));
        return errors;
    }
}
