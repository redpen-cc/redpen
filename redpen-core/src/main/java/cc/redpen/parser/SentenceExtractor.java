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

import cc.redpen.config.Configuration;
import cc.redpen.config.SymbolTable;
import cc.redpen.model.Sentence;
import cc.redpen.util.EndOfSentenceDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static cc.redpen.config.SymbolType.*;

/**
 * Utility Class to extract a Sentence list from given String.
 */
public final class SentenceExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(SentenceExtractor.class);
    private Pattern fullStopPattern;
    private List<String> fullStopList = new ArrayList<>();
    private List<String> rightQuotationList = new ArrayList<>();
    // TODO make white words configurable.
    private static final List<String> WHITE_WORDS = generateUmList("Mr.",
            "Mrs.", "Dr.", "genn.ai", "Co., Ltd.", "Miss.", "a.m.",
            "U.S.A.", "Jan.", "Feb.", "Mar.", "Apr.",
            "May.", "Jun.", "Jul.", "Aug.", "Sep.", "Oct.",
            "Nov.", "Dec.", "Feb.", "B.C", "A.D.");
    private EndOfSentenceDetector endOfSentenceDetector;

    /**
     * Constructor.
     *
     * @param fullStopList set of end of sentence characters
     */
    SentenceExtractor(List<String> fullStopList) {
        this(fullStopList, extractRightQuotations(new Configuration.ConfigurationBuilder().build().getSymbolTable()));
    }

    /**
     * Constructor.
     *
     * @param symbolTable symbolTable
     */
    public SentenceExtractor(SymbolTable symbolTable) {
        this(extractPeriods(symbolTable), extractRightQuotations(symbolTable));
    }

    /**
     * Constructor.
     */
    SentenceExtractor(List<String> fullStopList, List<String> rightQuotationList) {
        this.fullStopList = fullStopList;
        this.rightQuotationList = rightQuotationList;
        this.fullStopPattern = this.constructEndSentencePattern();
        this.endOfSentenceDetector = new EndOfSentenceDetector(
                this.fullStopPattern, this.WHITE_WORDS);
    }

    private static List<String> extractPeriods(SymbolTable symbolTable) {
        List<String> periods = new ArrayList<>();
        periods.add(symbolTable.getValueOrFallbackToDefault(FULL_STOP));
        periods.add(symbolTable.getValueOrFallbackToDefault(QUESTION_MARK));
        periods.add(symbolTable.getValueOrFallbackToDefault(EXCLAMATION_MARK));

        for (String period : periods) {
            LOG.info("\"" + period + "\" is added as a end of sentence character");
        }
        return periods;
    }

    private static List<String> extractRightQuotations(SymbolTable symbolTable) {
        List<String> rightQuotations = new ArrayList<>();
        rightQuotations.add(symbolTable.getValueOrFallbackToDefault(RIGHT_SINGLE_QUOTATION_MARK));
        rightQuotations.add(symbolTable.getValueOrFallbackToDefault(RIGHT_DOUBLE_QUOTATION_MARK));
        for (String rightQuotation : rightQuotations) {
            LOG.info("\"" + rightQuotation + "\" is added as a end of right quotation character.");
        }
        return rightQuotations;
    }

    private void generateQutotationPattern(
            List<String> endCharacters, StringBuilder patternString, String quotation) {
        for (String endChar : endCharacters) {
            String pattern;
            pattern = handleSpecialCharacter(endChar) + quotation;
            appendPattern(patternString, pattern);
        }
    }

    private void generateSimplePattern(List<String> endCharacters,
                                       StringBuilder patternString) {

        for (String endChar : endCharacters) {
            endChar = handleSpecialCharacter(endChar);
            appendPattern(patternString, endChar);
        }
    }

    private void appendPattern(StringBuilder patternString,
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

    private static <E> List<E> generateUmList(E... args) {
        List<E> list = new ArrayList<>(Arrays.asList(args));
        return list;
    }

    /**
     * Get Sentence lists.
     *
     * @param line            input line which can contain more than one sentences
     * @param outputSentences List of extracted sentences
     * @param position        line number
     * @return remaining line
     */
    public String extract(String line, List<Sentence> outputSentences, int position) {
        int periodPosition = endOfSentenceDetector.getSentenceEndPosition(line);
        if (periodPosition == -1) {
            return line;
        } else {
            while (true) {
                Sentence sentence = new Sentence(line.substring(0,
                        periodPosition + 1), position);
                outputSentences.add(sentence);
                line = line.substring(periodPosition + 1,
                        line.length());
                periodPosition = endOfSentenceDetector.getSentenceEndPosition(line);
                if (periodPosition == -1) {
                    return line;
                }
            }
        }
    }

    /**
     * Given string, return sentence end position.
     *
     * @param str input string
     * @return position of full stop when there is a full stop, -1 otherwise
     */
    public int getSentenceEndPosition(String str) {
        return endOfSentenceDetector.getSentenceEndPosition(str);
    }

    /**
     * Given a set of sentence end characters, construct the
     * regex to detect end sentences.
     * This method is protected permission just for testing.
     *
     * @return regex pattern to detect end sentences
     */
    Pattern constructEndSentencePattern() {
        if (this.fullStopList == null || this.fullStopList.size() == 0) {
            throw new IllegalArgumentException("No end character is specified");
        }
        StringBuilder patternString = new StringBuilder();
        for (String rightQuotation : rightQuotationList) {
            generateQutotationPattern(this.fullStopList, patternString, rightQuotation);
        }
        generateSimplePattern(this.fullStopList, patternString);
        return Pattern.compile(patternString.toString());
    }
}
