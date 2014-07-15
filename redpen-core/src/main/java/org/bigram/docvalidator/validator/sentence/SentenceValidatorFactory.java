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
package org.bigram.docvalidator.validator.sentence;

import org.bigram.docvalidator.DocumentValidatorException;
import org.bigram.docvalidator.config.CharacterTable;
import org.bigram.docvalidator.config.ValidatorConfiguration;
import org.bigram.docvalidator.model.Sentence;
import org.bigram.docvalidator.validator.Validator;

/**
 *
 */
public final class SentenceValidatorFactory {

  private SentenceValidatorFactory() { }

  public static Validator<Sentence> getInstance(ValidatorConfiguration config,
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
    } else {
      throw new DocumentValidatorException(
          "There is no Validator like " + config.getConfigurationName());
    }
  }

}
