package org.unigram.docvalidator.util;

public class PlainFormatter implements Formatter {

  @Override
  public String convertError(ValidationError error) {
    return error.toString();
  }

  @Override
  public String header() {
    return null;
  }

  @Override
  public String footer() {
    return null;
  }

}
