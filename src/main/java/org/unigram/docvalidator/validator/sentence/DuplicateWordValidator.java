package org.unigram.docvalidator.validator.sentence;

import java.util.ArrayList;
import java.util.List;

import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.util.Configuration;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.validator.SentenceValidator;

// @note this checker validates a word is used in more than N times
// in one sentence.  
public class DuplicateWordValidator implements SentenceValidator {

  public List<ValidationError> check(Sentence sentence) {
    return new ArrayList<ValidationError>();
  }

  public boolean initialize(Configuration conf, CharacterTable characterTable)
      throws DocumentValidatorException {
    return false;
  }

}
