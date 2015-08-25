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

    private String decimalDelimiters = DOT_DELIMITERS;

    @Override
    protected void init() throws RedPenException {
        super.init();
        boolean decimalDelimiterComma = getConfigAttributeAsBoolean("decimal_delimiter_comma", false);
        if (decimalDelimiterComma) {
            decimalDelimiters = COMMA_DELIMITERS;
        } else {
            decimalDelimiters = DOT_DELIMITERS;
        }
    }

    @Override
    public void validate(Sentence sentence) {
        // locate a number (regex or plain?)
        String text = sentence.getContent();
        String number = "";
        boolean haveNumber = false;
        int startPosition = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
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

    private void validateNumber(Sentence sentence, int position, String number) {
        if (!number.isEmpty()) {
            // ensure there is one decimal delimiter
            String integerPortion = number;
            for (char delimiter : decimalDelimiters.toCharArray()) {
                int decimalPosition = number.indexOf(delimiter);
                if (decimalPosition != -1) {
                    integerPortion = number.substring(0, decimalPosition);
                    // test for another decimal
                    if (number.indexOf(delimiter, decimalPosition + 1) != -1) {
                        addValidationErrorWithPosition(
                                "TooManyDecimals",
                                sentence,
                                sentence.getOffset(position),
                                sentence.getOffset(position + number.length()),
                                number);
                    }
                    break;
                }
            }
            // ensure that within the integer portion there are no sequences of digits longer than 3 characters
            int sequenceLength = 0;
            int sequenceStart = 0;
            for (int i = 0; i < integerPortion.length(); i++) {
                char ch = integerPortion.charAt(i);
                if (Character.isDigit(ch)) {
                    sequenceLength++;
                } else {
                    sequenceStart = i;
                    sequenceLength = 0;
                }
                if (sequenceLength > 3) {
                    addValidationErrorWithPosition(
                            "UndelimitedSequenceTooLong",
                            sentence,
                            sentence.getOffset(position),
                            sentence.getOffset(position + number.length()),
                            number);
                    break;
                } else if (sequenceStart != 0) {
                    addValidationErrorWithPosition(
                            "UndelimitedSequenceTooShort",
                            sentence,
                            sentence.getOffset(position),
                            sentence.getOffset(position + number.length()),
                            number);
                    break;
                }
            }
        }
    }

}
