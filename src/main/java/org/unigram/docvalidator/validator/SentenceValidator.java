package org.unigram.docvalidator.validator;

import java.util.List;

import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.util.Configuration;
import org.unigram.docvalidator.util.DocumentValidatorException;

/**
 * Validate input sentences.
 */
public interface SentenceValidator {
  /**
   * Check input sentences and returns the invalid points.
   * @param sentence input
   * @return list of invalid points
   */
  List<ValidationError> check(Sentence sentence);

  /**
   * initialize SentenceValidator.
   * @param conf validator configuration
   * @param charTable character configuration
   * @return true when initialization succeeded, otherwise false
   * @throws DocumentValidatorException
   */
  boolean initialize(Configuration conf, CharacterTable charTable)
      throws DocumentValidatorException;
}
