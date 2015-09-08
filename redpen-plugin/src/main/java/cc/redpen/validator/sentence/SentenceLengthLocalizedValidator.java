package cc.redpen.validator.sentence;

import cc.redpen.model.Sentence;
import cc.redpen.validator.Validator;

public class SentenceLengthLocalizedValidator extends Validator {
    private final int MIN_LENGTH = 100;
    private final int MAX_LENGTH = 1000;
    @Override
    public void validate(Sentence sentence) {
        if (sentence.getContent().length() < MIN_LENGTH) {
            // actual error message is in SentenceLengthLocalizedValidator.properties
            addLocalizedError(sentence, MIN_LENGTH);
        }
        if (sentence.getContent().length() > MAX_LENGTH) {
            // You can specify a message key when you have multiple error message variations
            addLocalizedError("toolong", sentence, MAX_LENGTH);
        }
    }
}
