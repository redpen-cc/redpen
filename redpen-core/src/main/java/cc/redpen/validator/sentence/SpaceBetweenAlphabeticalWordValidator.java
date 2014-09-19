package cc.redpen.validator.sentence;

import cc.redpen.ValidationError;
import cc.redpen.model.Sentence;
import cc.redpen.util.StringUtils;
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
                    && character != ' ') {
                results.add(createValidationError(block, "before"));
            } else if (
                    !StringUtils.isBasicLatin(character)
                            && StringUtils.isBasicLatin(prevCharacter)
                            && prevCharacter != ' ') {
                results.add(createValidationError(block, "after"));
            }
            prevCharacter = character;
        }
        return results;
    }

}
