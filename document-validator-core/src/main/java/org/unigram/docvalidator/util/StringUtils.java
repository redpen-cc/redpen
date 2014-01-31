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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to handle a string.
 */
public final class StringUtils {
  /**
   * Get sentence end position.
   *
   * @param str    input string
   * @param period full stop character
   * @return position of full stop when there is a full stop, -1 otherwise
   */
  public static int getSentenceEndPosition(String str, String period) {
    return getEndPosition(str, period, 0);
  }

  private static int getEndPosition(String str, String period, int offset) {
    int position = str.indexOf(period, offset);

    if (checkPosition(position, str)) {
      if (period.equals(".") && str.charAt(position + 1) == ' ') {
        return position;
      }
      return handleSuccessivePeriods(str, period, position);
    }

    if (position == str.length() - 1) {
      // NOTE: period in end of sentence should be the end of the sentence
      // even if there is NO tailing whitespace.
      return position;
    }
    return -1;
  }

  private static int handleSuccessivePeriods(String str, String period,
                                             int position) {
    int nextPosition = position + 1;

    if (!period.equals(".")
        && str.indexOf(period, nextPosition) != nextPosition) {
      // NOTE: Non Latin languages (especially Asian languages, periods do not
      // have tailing spaces in the end of sentences)
      return position;
    }

    if (str.indexOf(period, nextPosition) == nextPosition) {
      // NOTE: handling of period in succession
      if ((position + 1) == str.length() - 1) {
        return nextPosition;
      } else {
        return getEndPosition(str, period, nextPosition);
      }
    } else {
      return getEndPosition(str, period, nextPosition);
    }
  }

  /**
   * Get sentence end position.
   *
   * @param str    input string
   * @param pattern pattern of end of sentence
   * @return position of full stop when there is a full stop, -1 otherwise
   */
  public static int getSentenceEndPosition(String str, Pattern pattern) {
    return getEndPosition(str, pattern, 0);
  }

  private static int getEndPosition(String str, Pattern pattern, int offset) {
    int position = -1;
    Matcher matcher = pattern.matcher(str);
    if (matcher.find(offset)) {
      position = matcher.start();
    }

    if (checkPosition(position, str)) {
      if (('.' == str.charAt(position) && ' ' == str.charAt(position + 1))) {
        return position;
      }
      return handleSuccessivePeriods(str, pattern, position);
    }

    if (position == str.length() - 1) {
      // NOTE: period in end of sentence should be the end of the sentence
      // even if there is NO tailing whitespace.
      return position;
    }
    return -1;
  }

  private static int handleSuccessivePeriods(String str,
                                             Pattern pattern, int position) {
    int nextPosition = position + 1;
    Matcher matcher = pattern.matcher(str);
    int matchPosition = -1;
    if (matcher.find(nextPosition)) {
      matchPosition = matcher.start();
    }

    if (matchPosition > -1 && ('.' != str.charAt(matchPosition))
        && matchPosition != nextPosition) {
      // NOTE: Non Latin languages (especially Asian languages, periods do not
      // have tailing spaces in the end of sentences)
      return position;
    }

    if (matchPosition == nextPosition) {
      // NOTE: handling of period in succession
      if ((position + 1) == str.length() - 1) {
        return nextPosition;
      } else {
        return getEndPosition(str, pattern, nextPosition);
      }
    } else {
      return getEndPosition(str, pattern, nextPosition);
    }
  }

  private static boolean checkPosition(int position, String str) {
    return -1 < position && position < str.length() - 1;
  }

  public static boolean isKatakana(char c) {
    return Character.UnicodeBlock.of(c) == Character.UnicodeBlock.KATAKANA;
  }

  private StringUtils() {
  }
}
