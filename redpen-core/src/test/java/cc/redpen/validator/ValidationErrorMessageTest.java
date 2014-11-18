package cc.redpen.validator;

import cc.redpen.model.Sentence;

import java.util.List;

class ValidationErrorMessageTest extends Validator {

    @Override
    public void validate(List<ValidationError> errors, Sentence sentence) {
        errors.add(createValidationError(sentence, 1, 2, 3, "sentence"));
        errors.add(createValidationError("withKey", sentence, "sentence"));
    }
}
