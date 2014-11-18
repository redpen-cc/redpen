package cc.redpen.validator.sentence;

import cc.redpen.RedPenException;
import cc.redpen.model.Sentence;
import cc.redpen.util.StringUtils;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;

import java.util.ArrayList;
import java.util.List;

import static cc.redpen.config.SymbolType.*;

public class SpaceBetweenAlphabeticalWordValidator extends Validator {
    private char leftParenthesis = '(';
    private char rightParenthesis = ')';
    private char comma = ',';

    @Override
    public List<ValidationError> validate(Sentence sentence) {
        List<ValidationError> results = new ArrayList<>();
        char prevCharacter = ' ';
        for (char character : sentence.content.toCharArray()) {
            if (!StringUtils.isBasicLatin(prevCharacter)
                    && prevCharacter != leftParenthesis && prevCharacter != comma
                    && StringUtils.isBasicLatin(character)
                    && Character.isLetter(character)) {
                results.add(createValidationError("Before", sentence));
            } else if (
                    !StringUtils.isBasicLatin(character) && character != rightParenthesis
                            && StringUtils.isBasicLatin(prevCharacter)
                            && Character.isLetter(prevCharacter)) {
                results.add(createValidationError("After", sentence));
            }
            prevCharacter = character;
        }
        return results;
    }

    @Override
    protected void init() throws RedPenException {
        leftParenthesis = getSymbolTable().getSymbol(LEFT_PARENTHESIS).getValue();
        rightParenthesis = getSymbolTable().getSymbol(RIGHT_PARENTHESIS).getValue();
        comma = getSymbolTable().getSymbol(COMMA).getValue();
    }
}
