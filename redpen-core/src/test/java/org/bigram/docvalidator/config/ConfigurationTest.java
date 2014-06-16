/*
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

package org.bigram.docvalidator.config;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for Configuration Structure
 */
public class ConfigurationTest {

  @Test
  public void testSentenceValidatorConfiguration() throws Exception {

    Configuration configuration = new Configuration.Builder()
        .addValidationConfig(new ValidatorConfiguration("SentenceLength"))
        .addValidationConfig(new ValidatorConfiguration("InvalidExpression"))
        .addValidationConfig(new ValidatorConfiguration("SpaceAfterPeriod"))
        .addValidationConfig(new ValidatorConfiguration("CommaNumber"))
        .addValidationConfig(new ValidatorConfiguration("WordNumber"))
        .addValidationConfig(new ValidatorConfiguration("SuggestExpression"))
        .addValidationConfig(new ValidatorConfiguration("InvalidCharacter"))
        .addValidationConfig(new ValidatorConfiguration("SpaceWithSymbol"))
        .addValidationConfig(new ValidatorConfiguration("KatakanaEndHyphen"))
        .addValidationConfig(new ValidatorConfiguration("KatakanaSpellCheck"))
        .build();
    assertEquals(10, configuration.getSentenceValidatorConfigs().size());
  }

  @Test(expected = IllegalStateException.class)
  public void testInvalidValidatorConfiguration() {
    Configuration configuration = new Configuration.Builder()
        .addValidationConfig(new ValidatorConfiguration("ThereIsNoSuchValidator")).build();
  }

  @Test
  public void testSectionValidatorConfiguration() throws Exception{
    Configuration configuration = new Configuration.Builder().addValidationConfig(new ValidatorConfiguration("SectionLength"))
        .addValidationConfig(new ValidatorConfiguration("MaxParagraphNumber"))
        .addValidationConfig(new ValidatorConfiguration("ParagraphStartWith")).build();
    assertEquals(3, configuration.getSectionValidatorConfigs().size());
  }
}
