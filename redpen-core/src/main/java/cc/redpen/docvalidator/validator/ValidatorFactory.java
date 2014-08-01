package cc.redpen.docvalidator.validator;

import cc.redpen.docvalidator.DocumentValidatorException;
import cc.redpen.docvalidator.config.CharacterTable;
import cc.redpen.docvalidator.config.ValidatorConfiguration;
import cc.redpen.docvalidator.validator.section.ParagraphNumberValidator;
import cc.redpen.docvalidator.validator.section.ParagraphStartWithValidator;
import cc.redpen.docvalidator.validator.section.SectionLengthValidator;
import cc.redpen.docvalidator.validator.sentence.*;

/**
 * Factory class of validators.
 */
public class ValidatorFactory {

  public static Validator<?> getInstance(ValidatorConfiguration config,
                                         CharacterTable characterTable)
      throws DocumentValidatorException {
    switch (config.getConfigurationName()) {
      case "SentenceLength":
        return new SentenceLengthValidator(config, characterTable);
      case "InvalidExpression":
        return new InvalidExpressionValidator(config, characterTable);
      case "InvalidWord":
        return new InvalidWordValidator(config, characterTable);
      case "SpaceAfterPeriod":
        return new SpaceBeginningOfSentenceValidator(config, characterTable);
      case "CommaNumber":
        return new CommaNumberValidator(config, characterTable);
      case "WordNumber":
        return new WordNumberValidator(config, characterTable);
      case "SuggestExpression":
        return new SuggestExpressionValidator(config, characterTable);
      case "InvalidCharacter":
        return new InvalidCharacterValidator(config, characterTable);
      case "SpaceWithSymbol":
        return new SymbolWithSpaceValidator(config, characterTable);
      case "KatakanaEndHyphen":
        return new KatakanaEndHyphenValidator(config, characterTable);
      case "KatakanaSpellCheck":
        return new KatakanaSpellCheckValidator(config, characterTable);
      case "Spelling":
        return new SpellingValidator(config, characterTable);
      case "SectionLength":
        return new SectionLengthValidator(config, characterTable);
      case "MaxParagraphNumber":
        return new ParagraphNumberValidator(config, characterTable);
      case "ParagraphStartWith":
        return new ParagraphStartWithValidator(config, characterTable);
      default:
        throw new DocumentValidatorException(
            "There is no Validator like " + config.getConfigurationName());
    }
  }
}
