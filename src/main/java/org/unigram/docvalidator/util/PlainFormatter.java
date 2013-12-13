package org.unigram.docvalidator.util;

public class PlainFormatter implements Formatter {

  @Override
  public String format(ValidationError error) {
    return error.toString();
  }

}
