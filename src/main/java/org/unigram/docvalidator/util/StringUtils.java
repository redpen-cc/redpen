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

/**
 * Utility class to handle a string.
 */
public final class StringUtils {
  /**
   * get sentence end position.
   * @param str input string
   * @param period full stop character
   * @return position of full stop when there is a full stop, otherwise -1
   */
  public static int getSentenceEndPosition(String str, String period) {
     int position = str.indexOf(period);
     if (-1 < position && position < str.length() -1
         && period.equals(".") && str.charAt(position+1) == ' ') {
       return position;
     } else if (-1 < position && position < str.length() -1
         && !period.equals(".")) {
       // NOTE: for non Latin languages (in Asian languages, periods do not
       // have tailing spaces in the end of sentences)
       return position;
     } else if (position == str.length() - 1) {
       return position;
     } else {
       return -1;
     }
  }

  private StringUtils() { }

}
