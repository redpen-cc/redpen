package org.unigram.docvalidator.util;

public interface Formatter {
  /**
   * Convert ValidationError into a string to flush a error message.
   * @param error object containing file and line number information.
   * @return error message
   */
  public String format(ValidationError error);
}
