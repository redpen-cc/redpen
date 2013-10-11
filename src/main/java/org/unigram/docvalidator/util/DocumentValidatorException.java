package org.unigram.docvalidator.util;

/**
 * Error used to store the failure of Validators.
 */
@SuppressWarnings("serial")
public class DocumentValidatorException extends Exception {

  public DocumentValidatorException() {
    super();
  }

  public DocumentValidatorException(String message) {
    super(message);
  }

  public DocumentValidatorException(String message, Throwable cause) {
    super(message, cause);
  }

}
