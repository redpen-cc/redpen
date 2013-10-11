package org.unigram.docvalidator.util;

/**
 * ResultDistributor flush the errors reported from Validators.
 */
public interface ResultDistributor {
  /**
   * flush given ValidationError.
   * @param err error reproted from a Validator
   * @return 0 succeeded, otherwise 1
   */
  int flushResult(ValidationError err);
}
