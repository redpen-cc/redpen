package org.bigram.docvalidator.validator;

import org.bigram.docvalidator.DocumentValidatorException;
import org.bigram.docvalidator.config.CharacterTable;
import org.bigram.docvalidator.config.ValidatorConfiguration;
import org.bigram.docvalidator.validator.section.ParagraphNumberValidator;
import org.bigram.docvalidator.validator.section.ParagraphStartWithValidator;
import org.bigram.docvalidator.validator.section.SectionLengthValidator;
import org.bigram.docvalidator.validator.sentence.*;

/**
 * Factory class of validators.
 */
public class ValidatorFactory {

  public static Validator<?> getInstance(ValidatorConfiguration config,
      CharacterTable characterTable)
      throws DocumentValidatorException {

    if ("SentenceLength".equals(config.getConfigurationName())) {
      return new SentenceLengthValidator(config, characterTable);
    } else if ("InvalidExpression".equals(config.getConfigurationName())) {
      return new InvalidExpressionValidator(config, characterTable);
    } else if ("InvalidWord".equals(config.getConfigurationName())) {
      return new InvalidWordValidator(config, characterTable);
    } else if ("SpaceAfterPeriod".equals(config.getConfigurationName())) {
      return new SpaceBeginningOfSentenceValidator(config, characterTable);
    } else if ("CommaNumber".equals(config.getConfigurationName())) {
      return new CommaNumberValidator(config, characterTable);
    } else if ("WordNumber".equals(config.getConfigurationName())) {
      return new WordNumberValidator(config, characterTable);
    } else if ("SuggestExpression".equals(config.getConfigurationName())) {
      return new SuggestExpressionValidator(config, characterTable);
    } else if ("InvalidCharacter".equals(config.getConfigurationName())) {
      return new InvalidCharacterValidator(config, characterTable);
    } else if ("SpaceWithSymbol".equals(config.getConfigurationName())) {
      return new SymbolWithSpaceValidator(config, characterTable);
    } else if ("KatakanaEndHyphen".equals(config.getConfigurationName())) {
      return new KatakanaEndHyphenValidator(config, characterTable);
    } else if ("KatakanaSpellCheck"
        .equals(config.getConfigurationName())) {
      return new KatakanaSpellCheckValidator(config, characterTable);
    } if ("SectionLength".equals(config.getConfigurationName())) {
      return new SectionLengthValidator(config, characterTable);
    } else if ("MaxParagraphNumber".equals(config.getConfigurationName())) {
      return new ParagraphNumberValidator(config, characterTable);
    } else if ("ParagraphStartWith".equals(config.getConfigurationName())) {
      return new ParagraphStartWithValidator(config, characterTable);
    } else {
      throw new DocumentValidatorException(
          "There is no Validator like " + config.getConfigurationName());
    }
  }
}