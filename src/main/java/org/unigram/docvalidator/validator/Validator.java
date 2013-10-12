package org.unigram.docvalidator.validator;


import java.util.List;

import org.unigram.docvalidator.store.FileContent;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.ResultDistributor;
import org.unigram.docvalidator.util.Configuration;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.util.ValidationError;

/**
 * Validate input document.
 */
public interface Validator {
  /**
   * check the input document and returns the invalid points.
   * @param document input
   * @param distributor flush output
   * @return List of ValidationError
   */
  List<ValidationError> check(FileContent document,
      ResultDistributor distributor);

  /**
   * load configuration files.
   * @param conf validator configuration
   * @param charTable character configuration
   * @return true succeeded, otherwise false
   * @throws DocumentValidatorException
   */
  boolean loadConfiguration(Configuration conf,
      CharacterTable charTable) throws DocumentValidatorException;
}
