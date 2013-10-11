package org.unigram.docvalidator.validator;

import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.Configuration;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.validator.section.ParagraphNumberValidator;
import org.unigram.docvalidator.validator.section.ParagraphStartWithValidator;
import org.unigram.docvalidator.validator.section.SectionLengthValidator;

/**
 * Create Validator objects.
 */
public final class ValidatorFactory {
  public static Validator createValidator(String validatorType,
      Configuration conf, CharacterTable charTable)
        throws DocumentValidatorException {
  Validator validator = null;
  // @todo accept plug-in validators.
  if (validatorType.equals("SentenceIterator")) {
    validator =  new SentenceIterator();
  } else if (validatorType.equals("SectionLength")) {
    validator =  new SectionLengthValidator();
  } else if (validatorType.equals("MaxParagraphNumber")) {
    validator =  new ParagraphNumberValidator();
  } else if (validatorType.equals("ParagraphStartWith")) {
    validator =  new ParagraphStartWithValidator();
  } else {
    throw new DocumentValidatorException(
        "There is no Validator like " + validatorType);
  }
  validator.loadConfiguration(conf, charTable);
  return validator;
  }

  private ValidatorFactory() {
    super();
  }
}
