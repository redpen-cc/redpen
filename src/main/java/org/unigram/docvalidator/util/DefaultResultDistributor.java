/**
 * DocumentValidator
 * Copyright (c) 2013-, Takahiko Ito, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
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
    formatter = new PlainFormatter();
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
    writer.println(formatter.convertError(err));
    writer.flush();
    return 0;
  }

  @Override
  public void flushHeader() {
    writer.println(formatter.header());
  }

  @Override
  public void flushFooter() {
    writer.println(formatter.footer());
  }

  @Override
  public void setFormatter(Formatter formatter) {
    this.formatter = formatter;
  }

  private Formatter formatter;

  private PrintWriter writer;

}
