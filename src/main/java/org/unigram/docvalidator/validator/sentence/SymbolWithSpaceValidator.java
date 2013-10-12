package org.unigram.docvalidator.validator.sentence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.Configuration;
import org.unigram.docvalidator.util.DVCharacter;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.validator.SentenceValidator;

/**
 * Validate symbol has before and after symbols. Needed spaces is depend on
 * the symbol and defined in DVCharacterTable.
 */
public class SymbolWithSpaceValidator  implements SentenceValidator {
  public List<ValidationError> check(Sentence sentence) {
    List<ValidationError> errors = new ArrayList<ValidationError>();
    Set<String> names = characterTable.getNames();
    for (Iterator<String> nameIterator = names.iterator();
        nameIterator.hasNext();) {
      String name = nameIterator.next();
      ValidationError error = validateCharcter(sentence, name);
      if (error != null) {
        errors.add(error);
      }
    }
    return errors;
  }

  public boolean initialize(Configuration validatorConf,
      CharacterTable characterConf)
      throws DocumentValidatorException {
    this.characterTable = characterConf;
    return true;
  }

  private ValidationError validateCharcter(Sentence sentence, String name) {
    String sentenceStr = sentence.content;
    DVCharacter character = characterTable.getCharacter(name);
    if (!character.isNeedAfterSpace() && !character.isNeedBeforeSpace()) {
        return null;
    }

    String target = character.getValue();
    int position = sentenceStr.indexOf(target);
    if (position != -1) {
      if (position > 0 && character.isNeedBeforeSpace()
          && !Character.isWhitespace(sentenceStr.charAt(position - 1))) {
        return new ValidationError(sentence.position,
            "Need white space before symbol (" +  character.getName()
            + "): " + sentenceStr.charAt(position)
            + " in \"" + sentenceStr + "\"");
      } else if (position < sentenceStr.length() - 1
          && character.isNeedAfterSpace()
          && !Character.isWhitespace(sentenceStr.charAt(position + 1))) {
        return new ValidationError(sentence.position,
            "Need white space after symbol (" + character.getName()
            + "): " + sentenceStr.charAt(position)
            + " in \"" + sentenceStr + "\"");
      }
    }
    return null;
  }

  protected CharacterTable characterTable;
}
