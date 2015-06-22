package cc.redpen.validator.sentence;

import cc.redpen.model.Sentence;
import cc.redpen.validator.Validator;

public class SamplePluginValidator extends Validator {
    @Override
    public void validate(Sentence sentence) {
        if (sentence.getContent().length() < 100) {
            addValidationError(sentence, sentence.getContent().length(), 10);
        }
    }
}
