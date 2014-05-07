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
package org.bigram.docvalidator.util;

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
      if ((isBasicLatin(str.charAt(position))
          && ' ' == str.charAt(position + 1))) {
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

    if (matchPosition > -1 && (!isBasicLatin(str.charAt(matchPosition)))
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
    return java.lang.Character.UnicodeBlock.of(c) == java.lang.Character.UnicodeBlock.KATAKANA;
  }

  public static boolean isBasicLatin(char c) {
    return java.lang.Character.UnicodeBlock.of(c) == java.lang.Character.UnicodeBlock.BASIC_LATIN;
  }

  private StringUtils() {
  }
}
