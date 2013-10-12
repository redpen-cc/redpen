package org.unigram.docvalidator.validator.sentence;

import java.util.ArrayList;
import java.util.List;

import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.util.Configuration;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.validator.SentenceValidator;

public class WhiteSpaceBetweenAlphabetWordsValidator implements SentenceValidator {

  public List<ValidationError> check(Sentence sentence) {
    String content = sentence.content;
    return new ArrayList<ValidationError>();
  }

  public boolean initialize(Configuration conf, CharacterTable characterTable)
      throws DocumentValidatorException {
    return false;
  }
  
}
