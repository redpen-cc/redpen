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
package org.unigram.docvalidator.validator.sentence;

import org.unigram.docvalidator.DocumentValidatorException;
import org.unigram.docvalidator.config.CharacterTable;
import org.unigram.docvalidator.config.ValidatorConfiguration;

/**
 *
 */
public final class SentenceValidatorFactory {

  private SentenceValidatorFactory() { }

  public static SentenceValidator getInstance(ValidatorConfiguration config,
                                              CharacterTable characterTable)
      throws DocumentValidatorException {

    if ("SentenceLength".equals(config.getConfigurationName())) {
      return new SentenceLengthValidator(config, characterTable);
    } else if (config.getConfigurationName().equals("InvalidExpression")) {
      return new InvalidExpressionValidator(config, characterTable);
    } else if (config.getConfigurationName().equals("SpaceAfterPeriod")) {
      return new SpaceBeginningOfSentenceValidator(config, characterTable);
    } else if (config.getConfigurationName().equals("CommaNumber")) {
      return new CommaNumberValidator(config, characterTable);
    } else if (config.getConfigurationName().equals("WordNumber")) {
      return new WordNumberValidator(config, characterTable);
    } else if (config.getConfigurationName().equals("SuggestExpression")) {
      return new SuggestExpressionValidator(config, characterTable);
    } else if (config.getConfigurationName().equals("InvalidCharacter")) {
      return new InvalidCharacterValidator(config, characterTable);
    } else if (config.getConfigurationName().equals("SpaceWithSymbol")) {
      return new SymbolWithSpaceValidator(config, characterTable);
    } else if (config.getConfigurationName().equals("KatakanaEndHyphen")) {
      return new KatakanaEndHyphenValidator(config, characterTable);
    } else if (config.getConfigurationName().equals("KatakanaSpellCheckValidator")) {
      return new KatakanaSpellCheckValidator(config, characterTable);
    } else {
      throw new DocumentValidatorException(
          "There is no Validator like " + config.getConfigurationName());
    }
  }

}
