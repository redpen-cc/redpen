package cc.redpen.validator.sentence;

import cc.redpen.model.Sentence;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;

import java.util.List;

public class SamplePluginValidator extends Validator {
    @Override
    public void validate(List<ValidationError> errors, Sentence sentence) {
        if (sentence.getContent().length() < 100) {
            errors.add(createValidationError(sentence, sentence.getContent().length(), 10));
        }
    }
}
