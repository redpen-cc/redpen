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
package org.bigram.docvalidator.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bigram.docvalidator.model.Sentence;
import org.bigram.docvalidator.symbol.AbstractSymbols;
import org.bigram.docvalidator.symbol.DefaultSymbols;
import org.bigram.docvalidator.util.StringUtils;

/**
 * Utility Class to extract a Sentence list from given String.
 */
public final class SentenceExtractor {
  /**
   * Default Constructor.
   */
  public SentenceExtractor() {
    AbstractSymbols symbols = DefaultSymbols.getInstance();
    List<String> fullStopList = new ArrayList<String>();
    fullStopList.add(symbols.get("FULL_STOP").getValue());
    fullStopList.add(symbols.get("QUESTION_MARK").getValue());
    fullStopList.add(symbols.get("EXCLAMATION_MARK").getValue());
    this.fullStopPattern = Pattern.compile(
        this.constructEndSentencePattern(fullStopList));
  }

  /**
   * Constructor.
   *
   * @param fullStopList set of end of sentence characters
   */
  public SentenceExtractor(List<String> fullStopList) {
    this.fullStopPattern = Pattern.compile(
        this.constructEndSentencePattern(fullStopList));
  }

  /**
   * Get Sentence lists.
   *
   * @param line            input line which can contain more than one sentences
   * @param outputSentences List of extracted sentences
   * @return remaining line
   */
  public String extract(String line, List<Sentence> outputSentences) {
    int periodPosition =
        StringUtils.getSentenceEndPosition(line, fullStopPattern);
    if (periodPosition == -1) {
      return line;
    } else {
      while (true) {
        Sentence sentence = new Sentence(line.substring(0,
            periodPosition + 1), 0);
        outputSentences.add(sentence);
        line = line.substring(periodPosition + 1,
            line.length());
        periodPosition =
            StringUtils.getSentenceEndPosition(line, fullStopPattern);
        if (periodPosition == -1) {
          return line;
        }
      }
    }
  }

  /**
   * FIXME temporary implementation! need to refactor
   * Get Sentence lists without creating the last sentence.
   * @param line input line which can contain more than one sentences
   * @param outputSentences List of extracted sentences
   * @param position line number
   * @return remaining line or last sentence
   */
  public String extractWithoutLastSentence(
      String line, List<Sentence> outputSentences,
      int position) {
    int periodPosition =
        StringUtils.getSentenceEndPosition(line, fullStopPattern);
    if (periodPosition == -1) {
      return line;
    } else {
      while (true) {
        if (periodPosition == line.length() - 1) {
          return line;
        }
        Sentence sentence =
            new Sentence(line.substring(0, periodPosition + 1), position);
        outputSentences.add(sentence);
        line = line.substring(periodPosition + 1, line.length());
        periodPosition =
            StringUtils.getSentenceEndPosition(line, fullStopPattern);
        if (periodPosition == -1) {
          return line;
        }
      }
    }
  }

  /**
   * Given string, return sentence end position.
   *
   * @param str    input string
   * @return position of full stop when there is a full stop, -1 otherwise
   */
  public int getSentenceEndPosition(String str) {
    return StringUtils.getSentenceEndPosition(str, fullStopPattern);
  }

  /**
   * Given a set of sentence end characters, construct the
   * regex to detect end sentences.
   * This method is protected permission just for testing.
   *
   * @param endCharacters characters used in the end of
   *                      sentences such as period
   * @return regex pattern to detect end sentences
   */
  protected static String constructEndSentencePattern(
      List<String> endCharacters) {
    if (endCharacters == null || endCharacters.size() == 0) {
      throw new IllegalArgumentException("No end character is specified");
    }
    StringBuilder patternString = new StringBuilder();
    generateQutotationPattern(endCharacters, patternString);
    generateSimplePattern(endCharacters, patternString);
    return patternString.toString();
  }

  private static void generateQutotationPattern(
      List<String> endCharacters, StringBuilder patternString) {
    int index = 0;
    for (String endChar : endCharacters) {
      String pattern;
      pattern = handleSpecialCharacter(endChar) + "\"";
      appendPattern(patternString, pattern);
      index++;
    }
  }

  private static void generateSimplePattern(List<String> endCharacters,
      StringBuilder patternString) {

    for (String endChar : endCharacters) {
      endChar = handleSpecialCharacter(endChar);
      appendPattern(patternString, endChar);
    }
  }

  private static void appendPattern(StringBuilder patternString,
      String newPattern) {
    if (patternString.length() > 0) {
      patternString.append("|");
    }
    patternString.append(newPattern);
  }

  private static String handleSpecialCharacter(String endChar) {
    if (endChar.equals(".")) {
      endChar = "\\.";
    }
    if (endChar.equals("?")) {
      endChar = "\\?";
    }
    if (endChar.equals("!")) {
      endChar = "\\!";
    }
    return endChar;
  }

  private Pattern fullStopPattern;
}
