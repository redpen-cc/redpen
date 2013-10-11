package org.unigram.docvalidator.validator.sentence;

import java.util.ArrayList;
import java.util.List;

import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.Configuration;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.validator.SentenceValidator;

/**
 * Validate input sentences except for first sentence of a paragraph start with
 * a space.
 */
public class SpaceBegginingOfSentenceValidator implements SentenceValidator {

  public List<ValidationError> process(Sentence sentence) {
    List<ValidationError> result = new ArrayList<ValidationError>();
    String content = sentence.content;
    if (!sentence.isStartaragraph
        && !String.valueOf(content.charAt(0)).equals(" ")) {
      result.add(new ValidationError(sentence.position,
          "Space not exist the beggining of sentence "
      + " in line: " + sentence.content));
    }
    return result;
  }

  public boolean initialize(Configuration conf, CharacterTable characterTable)
      throws DocumentValidatorException {
    return true;
  }
}
