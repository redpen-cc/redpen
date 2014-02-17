/**
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unigram.docvalidator.util;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * An implementation of ResultDistributor which flush the result into
 * given output stream.
 */
public class DefaultResultDistributor implements ResultDistributor {
  /**
   * Constructor.
   *
   * @param os output stream
   */
  public DefaultResultDistributor(OutputStream os) {
    super();
    if (os == null) {
      throw new IllegalArgumentException("argument OutputStream is null");
    }
    try {
      writer = new PrintStream(os, true, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException("Specified output stream is illegal: "
          + e.getMessage());
    }
    myFormatter = new PlainFormatter();
  }

  /**
   * Constructor.
   *
   * @param ps output stream
   */
  public DefaultResultDistributor(PrintStream ps) {
    if (ps == null) {
      throw new IllegalArgumentException("argument PrintStream is null");
    }
    try {
      writer = new PrintStream(ps, true, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException("Specified output stream is illegal: "
          + e.getMessage());
    }
  }

  /**
   * Output given validation error.
   *
   * @param err validation error
   */
  public int flushResult(ValidationError err) {
    if (err == null) {
      throw new IllegalArgumentException("argument ValidationError is null");
    }
    writer.println(myFormatter.convertError(err));
    writer.flush();
    return 0;
  }

  @Override
  public void flushHeader() {
    String header = myFormatter.header();
    if (header != null) {
      writer.println(header);
    }
  }

  @Override
  public void flushFooter() {
    String footer = myFormatter.footer();
    if (footer != null) {
      writer.println(footer);
      writer.flush();
    }
  }

  @Override
  public void setFormatter(Formatter formatter) {
    if (formatter == null) {
      throw new IllegalArgumentException("argument formatter is null");
    }
    this.myFormatter = formatter;
  }

  private Formatter myFormatter;

  private PrintStream writer;

}
