/**
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
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
  public static Validator createValidator(
      String validatorType,
      ValidatorConfiguration conf,
      CharacterTable charTable) throws DocumentValidatorException {
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

    // FIXME: Should be removed as refactoring progresses
    ((ConfigurationLoader) validator).loadConfiguration(conf, charTable);
    return validator;
  }

  private ValidatorFactory() {
    super();
  }
}
