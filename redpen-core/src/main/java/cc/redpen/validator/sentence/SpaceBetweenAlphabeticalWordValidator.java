package cc.redpen.validator.sentence;

import cc.redpen.model.Sentence;
import cc.redpen.util.StringUtils;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;

import java.util.ArrayList;
import java.util.List;

public class SpaceBetweenAlphabeticalWordValidator extends Validator<Sentence> {
    public List<ValidationError> validate(Sentence block) {
        List<ValidationError> results = new ArrayList<>();
        char prevCharacter = ' ';
        for (char character : block.content.toCharArray()) {
            if (!StringUtils.isBasicLatin(prevCharacter)
                    && StringUtils.isBasicLatin(character)
                    && Character.isLetter(character)) {
                results.add(createValidationError("Before", block));
            } else if (
                    !StringUtils.isBasicLatin(character)
                            && StringUtils.isBasicLatin(prevCharacter)
                            && Character.isLetter(prevCharacter)) {
                results.add(createValidationError("After", block));
            }
            prevCharacter = character;
        }
        return results;
    }

}
