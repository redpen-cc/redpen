package org.unigram.docvalidator.util;
/**
 * FakeResultDistributor does nothing. this class is just for testing.
 */
public class FakeResultDistributor implements ResultDistributor {
  public int flushResult(ValidationError err) {
    return 0;
  }

  public FakeResultDistributor() {
    super();
  }
}
