package cc.redpen.docvalidator.validator.sentence;

import cc.redpen.docvalidator.ValidationError;
import cc.redpen.docvalidator.model.Sentence;
import cc.redpen.docvalidator.util.StringUtils;
import cc.redpen.docvalidator.validator.Validator;

import java.util.ArrayList;
import java.util.List;

public class SpaceBetweenAlphabeticalWord implements Validator<Sentence> {
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
