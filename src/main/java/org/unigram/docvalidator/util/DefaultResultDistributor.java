package org.unigram.docvalidator.util;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * An implementation of ResultDistributor which flush the result into
 * given output stream.
 */
public class DefaultResultDistributor implements ResultDistributor {
  /**
   * constructor.
   * @param os output stream
   */
  public DefaultResultDistributor(OutputStream os) {
    super();
    writer = new PrintWriter(os);
  }

  /**
   * constructor.
   * @param ps output stream
   */
  public DefaultResultDistributor(PrintStream ps) {
    writer = new PrintWriter(ps);
  }

  /**
   * output given validation error.
   * @param err validation error
   */
  public int flushResult(ValidationError err) {
    writer.println(err.toString());
    writer.flush();
    return 0;
  }

  private PrintWriter writer;
}
