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
package cc.redpen.validator.sentence;

import cc.redpen.RedPenException;
import cc.redpen.model.Sentence;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static cc.redpen.config.SymbolType.*;

/**
 * This validator check if the style end of sentence is American style.
 * @see <a href="http://grammar.ccc.commnet.edu/grammar/marks/quotation.htm">Description of quotation marks</a>
 */
final public class EndOfSentenceValidator extends Validator {

    private char rightSingleQuotation = '\'';
    private char rightDoubleQuotation = '"';
    private char period = '.';
    private char questionMark = '?';
    private char exclamationMark = '!';

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList(Locale.ENGLISH.getLanguage());
    }

    @Override
    public void validate(List<ValidationError> errors, Sentence sentence) {
        String content = sentence.getContent();
        if (content.length() < 2) {
            return;
        }
        char lastCharacter = content.charAt(content.length() - 1);
        char secondCharacter = content.charAt(content.length() - 2);
        if (lastCharacter == period
                || lastCharacter == questionMark
                || lastCharacter == exclamationMark) {
            if (secondCharacter == rightSingleQuotation
                    || secondCharacter == rightDoubleQuotation) {
                StringBuilder builder = new StringBuilder();
                builder.append(secondCharacter).append(lastCharacter);
                errors.add(createValidationErrorWithPosition(sentence,
                        sentence.getOffset(content.length()-2),
                        sentence.getOffset(content.length()-1), builder.toString()));
            }
        }
    }

    @Override
    protected void init() throws RedPenException {
        period = getSymbolTable().getSymbol(FULL_STOP).getValue();
        rightSingleQuotation = getSymbolTable().getSymbol(RIGHT_SINGLE_QUOTATION_MARK).getValue();
        rightDoubleQuotation = getSymbolTable().getSymbol(RIGHT_DOUBLE_QUOTATION_MARK).getValue();
        questionMark = getSymbolTable().getSymbol(QUESTION_MARK).getValue();
        exclamationMark = getSymbolTable().getSymbol(EXCLAMATION_MARK).getValue();
    }

    @Override
    public String toString() {
        return "EndOfSentenceValidator{" +
                "rightSingleQuotation=" + rightSingleQuotation +
                ", rightDoubleQuotation=" + rightDoubleQuotation +
                ", period=" + period +
                ", questionMark=" + questionMark +
                ", exclamationMark=" + exclamationMark +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EndOfSentenceValidator that = (EndOfSentenceValidator) o;

        if (exclamationMark != that.exclamationMark) return false;
        if (period != that.period) return false;
        if (questionMark != that.questionMark) return false;
        if (rightDoubleQuotation != that.rightDoubleQuotation) return false;
        if (rightSingleQuotation != that.rightSingleQuotation) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) rightSingleQuotation;
        result = 31 * result + (int) rightDoubleQuotation;
        result = 31 * result + (int) period;
        result = 31 * result + (int) questionMark;
        result = 31 * result + (int) exclamationMark;
        return result;
    }
}
