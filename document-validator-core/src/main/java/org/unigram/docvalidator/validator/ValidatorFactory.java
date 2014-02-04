/**
 * DocumentValidator
 * Copyright (c) 2013-, Takahiko Ito, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package org.unigram.docvalidator.validator;

import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.ValidatorConfiguration;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.validator.section.ParagraphNumberValidator;
import org.unigram.docvalidator.validator.section.ParagraphStartWithValidator;
import org.unigram.docvalidator.validator.section.SectionLengthValidator;

/**
 * Create Validator objects.
 */
public final class ValidatorFactory {
  /**
   * Create specified Validator instance.
   *
   * @param validatorType validator type
   * @param conf          configuration needed to create the validator
   * @param charTable     character settings
   * @return
   * @throws DocumentValidatorException
   */
  public static Validator createValidator(String validatorType,
                                          ValidatorConfiguration conf, CharacterTable charTable)
      throws DocumentValidatorException {
    Validator validator;
    // @todo accept plug-in validators.
    if (validatorType.equals("SentenceIterator")) {
      validator = new SentenceIterator();
    } else if (validatorType.equals("SectionLength")) {
      validator = new SectionLengthValidator();
    } else if (validatorType.equals("MaxParagraphNumber")) {
      validator = new ParagraphNumberValidator();
    } else if (validatorType.equals("ParagraphStartWith")) {
      validator = new ParagraphStartWithValidator();
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
