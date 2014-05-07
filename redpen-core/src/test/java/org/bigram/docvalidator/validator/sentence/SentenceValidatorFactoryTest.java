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

import org.bigram.docvalidator.validator.sentence.*;
import org.junit.Test;
import org.bigram.docvalidator.DocumentValidatorException;
import org.bigram.docvalidator.config.CharacterTable;
import org.bigram.docvalidator.config.ValidatorConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SentenceValidatorFactoryTest {

  @Test
  public void testGetInstance() throws Exception {
    SentenceValidator validator;
    CharacterTable characterTable = new CharacterTable();
    ValidatorConfiguration configSentenceLength =
        new ValidatorConfiguration("SentenceLength");
    validator = SentenceValidatorFactory
        .getInstance(configSentenceLength, characterTable);
    assertEquals(validator.getClass(), SentenceLengthValidator.class);

    ValidatorConfiguration configInvalidExpression =
        new ValidatorConfiguration("InvalidExpression");
    validator = SentenceValidatorFactory
        .getInstance(configInvalidExpression, characterTable);
    assertEquals(validator.getClass(), InvalidExpressionValidator.class);

    ValidatorConfiguration configSpaceAfterPeriod =
        new ValidatorConfiguration("SpaceAfterPeriod");
    validator = SentenceValidatorFactory
        .getInstance(configSpaceAfterPeriod, characterTable);
    assertEquals(validator.getClass(), SpaceBeginningOfSentenceValidator.class);

    ValidatorConfiguration configCommaNumber =
        new ValidatorConfiguration("CommaNumber");
    configCommaNumber.addAttribute("max_length", "3");
    validator =
        SentenceValidatorFactory.getInstance(configCommaNumber, characterTable);
    assertEquals(validator.getClass(), CommaNumberValidator.class);

    ValidatorConfiguration configWordNumber =
        new ValidatorConfiguration("WordNumber");
    validator =
        SentenceValidatorFactory.getInstance(configWordNumber, characterTable);
    assertEquals(validator.getClass(), WordNumberValidator.class);

    ValidatorConfiguration configSuggestExpression =
        new ValidatorConfiguration("SuggestExpression");
    validator = SentenceValidatorFactory
        .getInstance(configSuggestExpression, characterTable);
    assertEquals(validator.getClass(), SuggestExpressionValidator.class);

    ValidatorConfiguration configInvalidCharacter =
        new ValidatorConfiguration("InvalidCharacter");
    validator = SentenceValidatorFactory
        .getInstance(configInvalidCharacter, characterTable);
    assertEquals(validator.getClass(), InvalidCharacterValidator.class);

    ValidatorConfiguration configSpaceWithSymbol =
        new ValidatorConfiguration("SpaceWithSymbol");
    validator = SentenceValidatorFactory
        .getInstance(configSpaceWithSymbol, characterTable);
    assertEquals(validator.getClass(), SymbolWithSpaceValidator.class);

    ValidatorConfiguration configKatakanaEndHyphen =
        new ValidatorConfiguration("KatakanaEndHyphen");
    validator = SentenceValidatorFactory
        .getInstance(configKatakanaEndHyphen, characterTable);
    assertEquals(validator.getClass(), KatakanaEndHyphenValidator.class);

    ValidatorConfiguration configKatakanaSpellCheckValidator =
        new ValidatorConfiguration("KatakanaSpellCheckValidator");
    validator = SentenceValidatorFactory
        .getInstance(configKatakanaSpellCheckValidator, characterTable);
    assertEquals(validator.getClass(), KatakanaSpellCheckValidator.class);

  }

  @Test
  public void testIllegalArgumentConfiguration() {
    CharacterTable characterTable = new CharacterTable();

    ValidatorConfiguration configError = new ValidatorConfiguration("notExist");
    try {
      SentenceValidatorFactory.getInstance(configError, characterTable);
      fail("Not occur Exception");
    } catch (Exception e) {
      assertEquals(e.getClass(), DocumentValidatorException.class);
    }

  }
}
