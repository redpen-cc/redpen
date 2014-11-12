package cc.redpen.validator.sentence;

import cc.redpen.RedPenException;
import cc.redpen.model.Sentence;
import cc.redpen.util.StringUtils;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;

import java.util.ArrayList;
import java.util.List;

import static cc.redpen.config.SymbolType.*;

public class SpaceBetweenAlphabeticalWordValidator extends Validator<Sentence> {
    private char leftParenthesis = '(';
    private char rightParenthesis = ')';
    private char comma = ',';

    public List<ValidationError> validate(Sentence block) {
        List<ValidationError> results = new ArrayList<>();
        char prevCharacter = ' ';
        for (char character : block.content.toCharArray()) {
            if (!StringUtils.isBasicLatin(prevCharacter)
                    && prevCharacter != leftParenthesis && prevCharacter != comma
                    && StringUtils.isBasicLatin(character)
                    && Character.isLetter(character)) {
                results.add(createValidationError("Before", block));
            } else if (
                    !StringUtils.isBasicLatin(character) && character != rightParenthesis
                            && StringUtils.isBasicLatin(prevCharacter)
                            && Character.isLetter(prevCharacter)) {
                results.add(createValidationError("After", block));
            }
            prevCharacter = character;
        }
        return results;
    }

    @Override
    protected void init() throws RedPenException {
        leftParenthesis = getSymbolTable().getSymbol(LEFT_PARENTHESIS).getValue().charAt(0);
        rightParenthesis = getSymbolTable().getSymbol(RIGHT_PARENTHESIS).getValue().charAt(0);
        comma = getSymbolTable().getSymbol(COMMA).getValue().charAt(0);
    }
}
