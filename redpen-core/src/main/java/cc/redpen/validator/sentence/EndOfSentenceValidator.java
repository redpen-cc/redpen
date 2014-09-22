package cc.redpen.validator.sentence;

import cc.redpen.RedPenException;
import cc.redpen.model.Sentence;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * This validator check if the style end of sentence is American style.
 * @see <a herf="http://grammar.ccc.commnet.edu/grammar/marks/quotation.htm">Description of quotation marks</a>
 */
final public class EndOfSentenceValidator extends Validator<Sentence> {

    private char rightSingleQuotation = '\'';
    private char rightDoubleQuotation = '"';
    private char period = '.';
    private char questionMark = '?';
    private char exclamationMark = '!';

    @Override
    public List<ValidationError> validate(Sentence block) {
        List<ValidationError> validationErrors = new ArrayList<>();
        String content = block.content;
        if (content.length() < 2) {
            return validationErrors;
        }
        Character lastCharacter = content.charAt(content.length() - 1);
        Character secondCharacter = content.charAt(content.length() - 2);
        if (lastCharacter == period
                || lastCharacter == questionMark
                || lastCharacter == exclamationMark) {
            if (secondCharacter == rightSingleQuotation
                    || secondCharacter == rightDoubleQuotation) {
                validationErrors.add(createValidationError(block, secondCharacter+lastCharacter));
            }
        }
        return validationErrors;
    }

    @Override
    protected void init() throws RedPenException {
        period = getSymbolTable().getSymbol("FULL_STOP").getValue().charAt(0);
        rightSingleQuotation = getSymbolTable().getSymbol("RIGHT_SINGLE_QUOTATION_MARK").getValue().charAt(0);
        rightSingleQuotation = getSymbolTable().getSymbol("RIGHT_DOUBLE_QUOTATION_MARK").getValue().charAt(0);
        questionMark = getSymbolTable().getSymbol("QUESTION_MARK").getValue().charAt(0);
        exclamationMark = getSymbolTable().getSymbol("EXCLAMATION_MARK").getValue().charAt(0);
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
