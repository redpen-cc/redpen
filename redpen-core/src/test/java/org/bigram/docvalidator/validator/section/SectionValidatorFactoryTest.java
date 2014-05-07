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

package org.bigram.docvalidator.validator.section;

import org.bigram.docvalidator.validator.section.*;
import org.junit.Test;
import org.bigram.docvalidator.DocumentValidatorException;
import org.bigram.docvalidator.config.CharacterTable;
import org.bigram.docvalidator.config.ValidatorConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SectionValidatorFactoryTest {

  @Test
  public void testGetInstance() throws Exception {
    SectionValidator validator;
    CharacterTable characterTable = new CharacterTable();

    ValidatorConfiguration configSectionLength =
        new ValidatorConfiguration("SectionLength");
    validator = SectionValidatorFactory
        .getInstance(configSectionLength, characterTable);
    assertEquals(validator.getClass(), SectionLengthValidator.class);

    ValidatorConfiguration configMaxParagraphNumber =
        new ValidatorConfiguration("MaxParagraphNumber");
    validator = SectionValidatorFactory
        .getInstance(configMaxParagraphNumber, characterTable);
    assertEquals(validator.getClass(), ParagraphNumberValidator.class);

    ValidatorConfiguration configParagraphStartWith =
        new ValidatorConfiguration("ParagraphStartWith");
    validator = SectionValidatorFactory
        .getInstance(configParagraphStartWith, characterTable);
    assertEquals(validator.getClass(), ParagraphStartWithValidator.class);

  }


  @Test
  public void testIllegalArgumentConfiguration() {
    CharacterTable characterTable = new CharacterTable();
    ValidatorConfiguration configError = new ValidatorConfiguration("notExist");
    try {
      SectionValidatorFactory.getInstance(configError, characterTable);
      fail("Not occur Exception");
    } catch (Exception e) {
      assertEquals(e.getClass(), DocumentValidatorException.class);
    }
  }
}
