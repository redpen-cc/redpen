/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.redpen.validator.sentence;

import cc.redpen.RedPenException;
import cc.redpen.model.Sentence;
import cc.redpen.validator.Validator;

/**
 * Ensure numbers are formatted correctly, with commas (or fullstops) in the correct place
 */
public class NumberFormatValidator extends Validator {
    private static final String DOT_DELIMITERS = ".・";
    private static final String COMMA_DELIMITERS = "、,";

    // specifies which characters delimite the decimal part of a number
    private String decimalDelimiters = DOT_DELIMITERS;

    // should we ignore years (basically four digit integers)
    boolean ignoreYears = false;

    @Override
    protected void init() throws RedPenException {
        super.init();

        // set the following to true to support EU formats such as 1.000,00
        boolean decimalDelimiterComma = getConfigAttributeAsBoolean("decimal_delimiter_is_comma", false);
        ignoreYears = getConfigAttributeAsBoolean("ignore_years", false);
        if (decimalDelimiterComma) {
            decimalDelimiters = COMMA_DELIMITERS;
        } else {
            decimalDelimiters = DOT_DELIMITERS;
        }
    }

    /**
     * Search for numbers in the sentence and ensure they are correctly formatted
     *
     * @param sentence input
     */
    @Override
    public void validate(Sentence sentence) {
        String text = sentence.getContent();
        String number = "";
        boolean haveNumber = false;
        int startPosition = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            // ignore fullstop at end of sentence
            if ((i == text.length() - 1) && (DOT_DELIMITERS.indexOf(ch) != -1)) {
                break;
            }

            // is this character part of a number?
            if (Character.isDigit(ch) ||
                    (haveNumber && ((COMMA_DELIMITERS.indexOf(ch) != -1) || (DOT_DELIMITERS.indexOf(ch) != -1)))) {
                number += text.charAt(i);
                if (!haveNumber) {
                    startPosition = i;
                }
                haveNumber = true;
            } else {
                validateNumber(sentence, startPosition, number);
                number = "";
                haveNumber = false;
            }
        }
        validateNumber(sentence, startPosition, number);
    }

    /**
     * Inspect how a number is formatted and generate an error where appropriate
     *
     * @param sentence
     * @param position
     * @param number
     */
    private void validateNumber(Sentence sentence, int position, String number) {
        if (!number.isEmpty()) {
            // ensure there is one decimal delimiter
            boolean isInteger = true;
            String integerPortion = number;
            for (char delimiter : decimalDelimiters.toCharArray()) {
                int decimalPosition = number.indexOf(delimiter);
                if (decimalPosition != -1) {
                    isInteger = false;
                    integerPortion = number.substring(0, decimalPosition);
                    // test for another decimal
                    if (number.indexOf(delimiter, decimalPosition + 1) != -1) {
                        addLocalizedErrorWithPosition(
                                "TooManyDecimals",
                                sentence,
                                position,
                                position + number.length(),
                                number);
                    }
                    break;
                }
            }

            // if it's a four digit integer and we are ignoring years, ignore this
            if (ignoreYears && isInteger && (integerPortion.length() == 4)) {
                return;
            }

            // ensure that within the integer portion there are no sequences of digits longer than 3 characters
            int sequenceLength = 0;
            int sequenceStart = 0;
            for (int i = 0; i < integerPortion.length(); i++) {
                char ch = integerPortion.charAt(i);

                if (Character.isDigit(ch)) {
                    sequenceLength++;
                    if ((sequenceStart != 0) && (i == integerPortion.length() - 1) && (sequenceLength > 0) && (sequenceLength < 3)) {
                        addLocalizedErrorWithPosition(
                                "UndelimitedSequenceTooShort",
                                sentence,
                                position,
                                position + number.length(),
                                number);
                        break;
                    }
                } else {
                    if ((sequenceStart != 0) && (sequenceLength > 0) && (sequenceLength < 3)) {
                        addLocalizedErrorWithPosition(
                                "UndelimitedSequenceTooShort",
                                sentence,
                                position,
                                position + number.length(),
                                number);
                        break;
                    }

                    sequenceStart = i;
                    sequenceLength = 0;
                }

                if (sequenceLength > 3) {
                    addLocalizedErrorWithPosition(
                            "UndelimitedSequenceTooLong",
                            sentence,
                            position,
                            position + number.length(),
                            number);
                    break;
                }
            }
        }
    }

}
