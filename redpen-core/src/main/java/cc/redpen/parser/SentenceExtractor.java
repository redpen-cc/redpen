/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
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
import cc.redpen.util.EndOfSentenceDetector;
import cc.redpen.util.Pair;
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
    private char[] fullStopList;
    private char[] rightQuotationList;
    // TODO make white words configurable.
    private static final List<String> WHITE_WORDS = generateUmList("Mr.",
            "Mrs.", "Dr.", "genn.ai", "Co., Ltd.", "Miss.", "a.m.",
            "U.S.A.", "Jan.", "Feb.", "Mar.", "Apr.",
            "May.", "Jun.", "Jul.", "Aug.", "Sep.", "Oct.",
            "Nov.", "Dec.", "Feb.", "B.C", "A.D.");
    private EndOfSentenceDetector endOfSentenceDetector;
    // reference to the symbol table used to create us
    private SymbolTable symbolTable = null;

    /**
     * Constructor.
     *
     * @param fullStopList set of end of sentence characters
     */
    SentenceExtractor(char[] fullStopList) {
        this(fullStopList, extractRightQuotations(new Configuration.ConfigurationBuilder().build().getSymbolTable()));
    }

    /**
     * Constructor.
     *
     * @param symbolTable symbolTable
     */
    public SentenceExtractor(SymbolTable symbolTable) {
        this(extractPeriods(symbolTable), extractRightQuotations(symbolTable));
        this.symbolTable = symbolTable;
    }

    /**
     * Constructor.
     */
    SentenceExtractor(char[] fullStopList, char[] rightQuotationList) {
        this.fullStopList = fullStopList;
        this.rightQuotationList = rightQuotationList;
        this.fullStopPattern = this.constructEndSentencePattern();
        this.endOfSentenceDetector = new EndOfSentenceDetector(
                this.fullStopPattern, this.WHITE_WORDS);
    }

    private static char[] extractPeriods(SymbolTable symbolTable) {
        char[] periods = new char[]{
                symbolTable.getValueOrFallbackToDefault(FULL_STOP),
                symbolTable.getValueOrFallbackToDefault(QUESTION_MARK),
                symbolTable.getValueOrFallbackToDefault(EXCLAMATION_MARK)
        };
        LOG.info("\"" + Arrays.toString(periods) + "\" are added as a end of sentence characters");
        return periods;
    }

    private static char[] extractRightQuotations(SymbolTable symbolTable) {
        char[] rightQuotations = new char[]{
                symbolTable.getValueOrFallbackToDefault(RIGHT_SINGLE_QUOTATION_MARK),
                symbolTable.getValueOrFallbackToDefault(RIGHT_DOUBLE_QUOTATION_MARK)
        };
        LOG.info("\"" + Arrays.toString(rightQuotations) + "\" are added as a right quotation characters");
        return rightQuotations;
    }

    private void generateQuotationPattern(char[] endCharacters, StringBuilder patternString, char quotation) {
        for (char endChar : endCharacters) {
            String pattern = handleSpecialCharacter(endChar) + quotation;
            appendPattern(patternString, pattern);
        }
    }

    private void generateSimplePattern(char[] endCharacters, StringBuilder patternString) {
        for (char endChar : endCharacters) {
            appendPattern(patternString, handleSpecialCharacter(endChar));
        }
    }

    private void appendPattern(StringBuilder patternString,
                               String newPattern) {
        if (patternString.length() > 0) {
            patternString.append("|");
        }
        patternString.append(newPattern);
    }

    private static String handleSpecialCharacter(char endChar) {
        if (endChar == '.') {
            return "\\.";
        } else if (endChar == '?') {
            return "\\?";
        } else if (endChar == '!') {
            return "\\!";
        } else {
            return String.valueOf(endChar);
        }
    }

    private static <E> List<E> generateUmList(E... args) {
        List<E> list = new ArrayList<>(Arrays.asList(args));
        return list;
    }

    /**
     * Get Sentence lists.
     *
     * @param line              Input line which can contain more than one sentences
     * @param sentencePositions List of extracted sentences
     * @return remaining line
     */
    public int extract(String line, List<Pair<Integer, Integer>> sentencePositions) {
        int startPosition = 0;
        int periodPosition = endOfSentenceDetector.getSentenceEndPosition(line);
        while (periodPosition >= 0) {
            sentencePositions.add(new Pair<>(startPosition, periodPosition + 1));
            startPosition = periodPosition + 1;
            periodPosition = endOfSentenceDetector.getSentenceEndPosition(line, startPosition);
        }
        return startPosition;
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
     * Return the string that should be used to re-join lines broken with \n in
     * <p/>
     * For English, this is a space.
     * For Japanese, it is an empty string.
     *
     * The specification of this string should be moved to part of the configuration
     *
     * @return a string used to join lines that have been 'broken'
     */
    public String getBrokenLineSeparator() {
        return (symbolTable != null) && (symbolTable.getLang().equals("ja")) ? "" : " ";
    }

    /**
     * Given a set of sentence end characters, construct the
     * regex to detect end sentences.
     * This method is protected permission just for testing.
     *
     * @return regex pattern to detect end sentences
     */
    Pattern constructEndSentencePattern() {
        if (this.fullStopList == null || this.fullStopList.length == 0) {
            throw new IllegalArgumentException("No end character is specified");
        }
        StringBuilder patternString = new StringBuilder();
        for (char rightQuotation : rightQuotationList) {
            generateQuotationPattern(this.fullStopList, patternString, rightQuotation);
        }
        generateSimplePattern(this.fullStopList, patternString);
        return Pattern.compile(patternString.toString());
    }

}
