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
package cc.redpen.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import cc.redpen.model.Sentence;
import cc.redpen.symbol.AbstractSymbols;
import cc.redpen.symbol.DefaultSymbols;
import cc.redpen.util.EndOfSentenceDetector;

/**
 * Utility Class to extract a Sentence list from given String.
 */
public final class SentenceExtractor {
  /**
   * Default Constructor.
   */
  public SentenceExtractor() {
    AbstractSymbols symbols = DefaultSymbols.getInstance();

    fullStopList.add(symbols.get("FULL_STOP").getValue());
    fullStopList.add(symbols.get("QUESTION_MARK").getValue());
    fullStopList.add(symbols.get("EXCLAMATION_MARK").getValue());

    rightQuotationList.add(symbols.get("RIGHT_SINGLE_QUOTATION_MARK").getValue());
    rightQuotationList.add(symbols.get("RIGHT_DOUBLE_QUOTATION_MARK").getValue());

    this.fullStopPattern = Pattern.compile(
        this.constructEndSentencePattern());

    this.endOfSentenceDetector = new EndOfSentenceDetector(
        this.fullStopPattern, this.whiteWords);
  }

  /**
   * Constructor.
   *
   * @param fullStopList set of end of sentence characters
   */
  public SentenceExtractor(List<String> fullStopList) {
    this();
    this.fullStopList = fullStopList;
    this.fullStopPattern = Pattern.compile(
        this.constructEndSentencePattern());

    this.endOfSentenceDetector = new EndOfSentenceDetector(
        this.fullStopPattern, this.whiteWords);
  }

  /**
   * Constructor.
   *
   * @param fullStopList set of end of sentence characters
   * @param rightQuotationList set of right quotation characters
   */
  public SentenceExtractor(List<String> fullStopList,
      List<String> rightQuotationList) {
    this.fullStopList =fullStopList;
    this.rightQuotationList = rightQuotationList;
    this.fullStopPattern = Pattern.compile(
        this.constructEndSentencePattern());
    this.endOfSentenceDetector = new EndOfSentenceDetector(
        this.fullStopPattern, this.whiteWords);
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
        endOfSentenceDetector.getSentenceEndPosition(line);
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
            endOfSentenceDetector.getSentenceEndPosition(line);
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
        endOfSentenceDetector.getSentenceEndPosition(line);
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
            endOfSentenceDetector.getSentenceEndPosition(line);
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
  public int getSentenceEndPosition(String str){
    return endOfSentenceDetector.getSentenceEndPosition(str);
  }

  /**
   * Given a set of sentence end characters, construct the
   * regex to detect end sentences.
   * This method is protected permission just for testing.
   *
   * @return regex pattern to detect end sentences
   */
  protected String constructEndSentencePattern() {
    if (this.fullStopList == null || this.fullStopList.size() == 0) {
      throw new IllegalArgumentException("No end character is specified");
    }
    StringBuilder patternString = new StringBuilder();
    for (String rightQuotation : rightQuotationList) {
      generateQutotationPattern(this.fullStopList, patternString, rightQuotation);
    }
    generateSimplePattern(this.fullStopList, patternString);
    return patternString.toString();
  }

  private static void generateQutotationPattern(
      List<String> endCharacters, StringBuilder patternString, String quotation) {
    for (String endChar : endCharacters) {
      String pattern;
      pattern = handleSpecialCharacter(endChar) + quotation;
      appendPattern(patternString, pattern);
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

  private static <E> List<E> generateUmList(E... args){
    List<E> list = new ArrayList<>(Arrays.asList(args));
    return list;
  }

  private Pattern fullStopPattern;

  private List<String> fullStopList = new ArrayList<>();

  private List<String> rightQuotationList = new ArrayList<>();

  // TODO make white words configurable.
  private List<String> whiteWords = generateUmList("Mr.",
      "Mrs.", "Dr.", "genn.ai", "Co., Ltd." , "Miss.", "a.m.",
      "U.S.A.", "Jan.", "Feb.", "Mar.", "Apr.",
      "May.", "Jun.", "Jul.","Aug.", "Sep.", "Oct.",
      "Nov.", "Dec.", "Feb.", "B.C", "A.D.");

  private EndOfSentenceDetector endOfSentenceDetector;
}
