package org.unigram.docvalidator.util;

public class XMLFormatter implements Formatter {

  @Override
  public String convertError(ValidationError error) {
    return "<error>" + error.toString() + "</error>";
  }

  @Override
  public String header() {
    return "<validation-result>";
  }

  @Override
  public String footer() {
    return "</validation-result>";
  }

}
