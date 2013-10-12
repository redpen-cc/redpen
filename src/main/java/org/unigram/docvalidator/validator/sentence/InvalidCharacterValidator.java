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
 * Validate if there is invalid characters in sentences.
 */
public class InvalidCharacterValidator implements SentenceValidator {
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

  public boolean initialize(Configuration conf, CharacterTable characterTable)
      throws DocumentValidatorException {
    this.characterTable = characterTable;
    return true;
  }

  private ValidationError validateCharcter(Sentence sentence, String name) {
    String sentenceStr = sentence.content;
    DVCharacter character = characterTable.getCharacter(name);
    List<String> invalidCharsList = character.getInvalidChars();
    for (Iterator<String> charIterator = invalidCharsList.iterator();
        charIterator.hasNext();) {
       String invalidcChar = charIterator.next();
       if (sentenceStr.indexOf(invalidcChar) != -1) {
         return new ValidationError(sentence.position,
             "Invalid symbol found: " + invalidcChar
             + " in \"" + sentenceStr + "\"");
      }
    }
    return null;
  }

  protected CharacterTable characterTable;
}
