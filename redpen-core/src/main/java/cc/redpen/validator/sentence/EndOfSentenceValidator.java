package cc.redpen.validator.sentence;

import cc.redpen.RedPenException;
import cc.redpen.ValidationError;
import cc.redpen.model.Sentence;
import cc.redpen.validator.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * This validator check if the style end of sentence is American style.
 * @see <a herf="http://grammar.ccc.commnet.edu/grammar/marks/quotation.htm">Description of quotation marks</a>
 */
public class EndOfSentenceValidator extends Validator<Sentence> {

    private char rightSingleQuotation = '\'';
    private char rightDoubleQuotation = '"';
    private char period = '.';
    private char questionMark = '?';
    private char exclamationMark = '!';

    @Override
    public List<ValidationError> validate(Sentence block) {
        List<ValidationError> errors = new ArrayList<>();
        String content = block.content;
        if (content.length() < 2) {
            return errors;
        }
        Character lastCharacter = content.charAt(content.length() - 1);
        Character secondCharacter = content.charAt(content.length() - 2);
        if (lastCharacter == period
                || lastCharacter == questionMark
                || lastCharacter == exclamationMark) {
            if (secondCharacter == rightSingleQuotation
                    || secondCharacter == rightDoubleQuotation) {
                errors.add(new ValidationError(
                        this.getClass(),
                        "Invalid end of sentence",block));
            }
        }
        return errors;
    }

    @Override
    protected void init() throws RedPenException {
        period = getSymbolTable().getSymbol("FULL_STOP").getValue().charAt(0);
        rightSingleQuotation = getSymbolTable().getSymbol("RIGHT_SINGLE_QUOTATION_MARK").getValue().charAt(0);
        rightSingleQuotation = getSymbolTable().getSymbol("RIGHT_DOUBLE_QUOTATION_MARK").getValue().charAt(0);
        questionMark = getSymbolTable().getSymbol("QUESTION_MARK").getValue().charAt(0);
        exclamationMark = getSymbolTable().getSymbol("EXCLAMATION_MARK").getValue().charAt(0);
    }
}
