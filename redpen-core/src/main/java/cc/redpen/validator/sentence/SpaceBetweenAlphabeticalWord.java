package cc.redpen.validator.sentence;

import cc.redpen.ValidationError;
import cc.redpen.model.Sentence;
import cc.redpen.util.StringUtils;
import cc.redpen.validator.Validator;

import java.util.ArrayList;
import java.util.List;

public class SpaceBetweenAlphabeticalWord extends Validator<Sentence> {
    @Override
    public List<ValidationError> validate(Sentence block) {
        List<ValidationError> results = new ArrayList<>();
        char prevCharacter = ' ';
        for (char character : block.content.toCharArray()) {
            if (!StringUtils.isBasicLatin(prevCharacter)
                    && StringUtils.isBasicLatin(character)
                    && character != ' ') {
                results.add(new ValidationError(
                        this.getClass(),
                        "Space does not exit before alphabetical word.",
                        block));
            } else if (
                    !StringUtils.isBasicLatin(character)
                            && StringUtils.isBasicLatin(prevCharacter)
                            && prevCharacter != ' ') {
                results.add(new ValidationError(
                        this.getClass(),
                        "Space does not exit after alphabetical word.",
                        block));
            }
            prevCharacter = character;
        }
        return results;
    }

}
