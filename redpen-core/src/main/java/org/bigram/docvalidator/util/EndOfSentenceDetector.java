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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to handle a string.
 */
public final class EndOfSentenceDetector {

  public EndOfSentenceDetector(Pattern pattern) {
    this.pattern = pattern;
    this.whiteList = new ArrayList<String>();
  }

  public EndOfSentenceDetector(Pattern pattern, List<String> whiteList) {
    this.pattern = pattern;
    this.whiteList = whiteList;
  }

  /**
   * Get sentence end position.
   *
   * @param str    input string
   * @return position of full stop when there is a full stop, -1 otherwise
   */
  public int getSentenceEndPosition(String str) {
    Set<Integer> nonEndOfSentencePositions =
        extractNonEndOfSentencePositions(str, whiteList);
    return getEndPosition(str, pattern, 0, nonEndOfSentencePositions);
  }

  private static int getEndPosition(String str, Pattern pattern,
      int offset, Set<Integer> whitePositions) {
    int startPosition = -1;
    int endPosition = -1;
    Matcher matcher = pattern.matcher(str);
    boolean matchResult = getEndPositionSkippingWhiteList(offset,
        matcher, whitePositions);
    if (matchResult) {
      startPosition = matcher.start();
      endPosition = matcher.end();
    }

    if (checkPosition(endPosition - 1, str)) {
      if ((StringUtils.isBasicLatin(str.charAt(startPosition))
          && ' ' == str.charAt(endPosition))) {
        return endPosition - 1;
      }
      return handleSuccessivePeriods(str, pattern, startPosition, whitePositions);
    }

    if (endPosition == str.length()) {
      // NOTE: period in end of sentence should be the end of the sentence
      // even if there is NO tailing whitespace.
      return endPosition - 1;
    }
    return -1;
  }

  private static boolean getEndPositionSkippingWhiteList(int offset,
      Matcher matcher, Set<Integer> whitePositions) {
    boolean result = matcher.find(offset);
    while(result) {
      int startPosition = matcher.start();
      int endPosition = matcher.end();
      boolean containsWhite = false;
      for (int i = startPosition; i < endPosition; i++) {
        if (whitePositions.contains(i)) {
          containsWhite = true;
        }
      }
      if (containsWhite) {
        result = getEndPositionSkippingWhiteList(endPosition,
            matcher, whitePositions);
      } else {
        break;
      }
    }
    return result;
  }

  private static Set<Integer> extractNonEndOfSentencePositions(
      String inputString,
      List<String> whiteList) {
    Set<Integer> nonEndOfSentencePositions = new HashSet<Integer>();
    for (String whiteWord : whiteList) {
      int offset = 0;
      while(true) {
        int matchStartPosition = inputString.indexOf(whiteWord, offset);
        int matchEndPosition = matchStartPosition + whiteWord.length();
        if (matchStartPosition == -1) {
          break;
        }
        for (int i = matchStartPosition;
             i < matchEndPosition; i++) {
          nonEndOfSentencePositions.add(i);
        }
        offset = matchEndPosition;
      }
    }
    return nonEndOfSentencePositions;
  }


  private static int handleSuccessivePeriods(String str,
      Pattern pattern, int position, Set<Integer> whitePositions) {
    int nextPosition = position + 1;
    Matcher matcher = pattern.matcher(str);
    int matchPosition = -1;
    if (matcher.find(nextPosition)) {
      matchPosition = matcher.start();
    }

    if (matchPosition > -1 && (!StringUtils.isBasicLatin(str.charAt(matchPosition)))
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
        return getEndPosition(str, pattern, nextPosition, whitePositions);
      }
    } else {
      return getEndPosition(str, pattern, nextPosition, whitePositions);
    }
  }

  private static boolean checkPosition(int position, String str) {
    return -1 < position && position < str.length() - 1;
  }

  private List<String> whiteList;

  private Pattern pattern;
}
