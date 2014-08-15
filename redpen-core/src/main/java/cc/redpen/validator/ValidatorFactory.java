package cc.redpen.validator;

import cc.redpen.DocumentValidatorException;
import cc.redpen.config.SymbolTable;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.validator.section.ParagraphNumberValidator;
import cc.redpen.validator.section.ParagraphStartWithValidator;
import cc.redpen.validator.section.SectionLengthValidator;
import cc.redpen.validator.sentence.*;

/**
 * Factory class of validators.
 */
public class ValidatorFactory {

  public static Validator<?> getInstance(ValidatorConfiguration config,
                                         SymbolTable symbolTable)
      throws DocumentValidatorException {
    switch (config.getConfigurationName()) {
      case "SentenceLength":
        return new SentenceLengthValidator(config, symbolTable);
      case "InvalidExpression":
        return new InvalidExpressionValidator(config, symbolTable);
      case "InvalidWord":
        return new InvalidWordValidator(config, symbolTable);
      case "SpaceAfterPeriod":
        return new SpaceBeginningOfSentenceValidator(config, symbolTable);
      case "CommaNumber":
        return new CommaNumberValidator(config, symbolTable);
      case "WordNumber":
        return new WordNumberValidator(config, symbolTable);
      case "SuggestExpression":
        return new SuggestExpressionValidator(config, symbolTable);
      case "InvalidCharacter":
        return new InvalidSymbolValidator(config, symbolTable);
      case "SpaceWithSymbol":
        return new SymbolWithSpaceValidator(config, symbolTable);
      case "KatakanaEndHyphen":
        return new KatakanaEndHyphenValidator(config, symbolTable);
      case "KatakanaSpellCheck":
        return new KatakanaSpellCheckValidator(config, symbolTable);
      case "Spelling":
        return new SpellingValidator(config, symbolTable);
      case "SpaceBetweenAlphabeticalWord":
        return new SpaceBetweenAlphabeticalWord();
      case "SectionLength":
        return new SectionLengthValidator(config, symbolTable);
      case "MaxParagraphNumber":
        return new ParagraphNumberValidator(config, symbolTable);
      case "ParagraphStartWith":
        return new ParagraphStartWithValidator(config, symbolTable);
      default:
        throw new DocumentValidatorException(
            "There is no Validator like " + config.getConfigurationName());
    }
  }
}
