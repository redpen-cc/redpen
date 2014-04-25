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
package org.unigram.docvalidator.validator.section;


import org.unigram.docvalidator.DocumentValidatorException;
import org.unigram.docvalidator.config.CharacterTable;
import org.unigram.docvalidator.config.ValidatorConfiguration;

/**
 *
 */
public final class SectionValidatorFactory {

  private SectionValidatorFactory() { }

  public static SectionValidator getInstance(ValidatorConfiguration config,
                                             CharacterTable characterTable)
      throws DocumentValidatorException {

    if ("SectionLength".equals(config.getConfigurationName())) {
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
