package cc.redpen.validator.sentence;

import cc.redpen.model.Sentence;
import cc.redpen.validator.Validator;
import cc.redpen.validator.ValidatorFactory;

public class NumberOfCharactersValidator extends Validator {
    private final int MIN_LENGTH = 100;
    private final int MAX_LENGTH = 1000;

    static {
        ValidatorFactory.registerValidator(NumberOfCharactersValidator.class);
    }

    @Override
    public void validate(Sentence sentence) {
        if (sentence.getContent().length() < MIN_LENGTH) {
            addError("Sentence is shorter than " + MIN_LENGTH + " characters long.", sentence);
        }
        if (sentence.getContent().length() > MAX_LENGTH) {
            addError("Sentence is longer than " + MAX_LENGTH + " characters long.", sentence);
        }
    }
}
