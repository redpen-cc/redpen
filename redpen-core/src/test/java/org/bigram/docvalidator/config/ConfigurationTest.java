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
  public void testSentencValidatorConfiguration() throws Exception {
    ValidatorConfiguration rootConfig = new ValidatorConfiguration("top");

    ValidatorConfiguration configSentenceLength = new ValidatorConfiguration("SentenceLength");
    ValidatorConfiguration configInvalidExpression = new ValidatorConfiguration("InvalidExpression");
    ValidatorConfiguration configSpaceAfterPeriod = new ValidatorConfiguration("SpaceAfterPeriod");
    ValidatorConfiguration configCommaNumber = new ValidatorConfiguration("CommaNumber");
    ValidatorConfiguration configWordNumber = new ValidatorConfiguration("WordNumber");
    ValidatorConfiguration configSuggestExpression = new ValidatorConfiguration("SuggestExpression");
    ValidatorConfiguration configInvalidCharacter = new ValidatorConfiguration("InvalidCharacter");
    ValidatorConfiguration configSpaceWithSymbol = new ValidatorConfiguration("SpaceWithSymbol");
    ValidatorConfiguration configKatakanaEndHyphen = new ValidatorConfiguration("KatakanaEndHyphen");
    ValidatorConfiguration configKatakanaSpellCheckValidator = new ValidatorConfiguration("KatakanaSpellCheck");

    rootConfig.addChild(configSentenceLength);
    rootConfig.addChild(configInvalidExpression);
    rootConfig.addChild(configSpaceAfterPeriod);
    rootConfig.addChild(configCommaNumber);
    rootConfig.addChild(configWordNumber);
    rootConfig.addChild(configSuggestExpression);
    rootConfig.addChild(configInvalidCharacter);
    rootConfig.addChild(configSpaceWithSymbol);
    rootConfig.addChild(configKatakanaEndHyphen);
    rootConfig.addChild(configKatakanaSpellCheckValidator);

    Configuration configuration = new Configuration(rootConfig);
    assertEquals(10, configuration.getSentenceValidatorConfigs().size());
  }

  @Test(expected = IllegalStateException.class)
  public void testInvalidValidatorConfiguration() {
    ValidatorConfiguration rootConfig = new ValidatorConfiguration("top");
    ValidatorConfiguration configInvalid = new ValidatorConfiguration("ThereIsNo");
    rootConfig.addChild(configInvalid);
    Configuration configuration = new Configuration(rootConfig);
  }

  @Test
  public void testSectionValidatorConfiguration() throws Exception{
    ValidatorConfiguration rootConfig = new ValidatorConfiguration("top");

    ValidatorConfiguration configSectionLength = new ValidatorConfiguration("SectionLength");
    ValidatorConfiguration configMaxParagraphNumber = new ValidatorConfiguration("MaxParagraphNumber");
    ValidatorConfiguration configParagraphStartWith = new ValidatorConfiguration("ParagraphStartWith");

    rootConfig.addChild(configSectionLength);
    rootConfig.addChild(configMaxParagraphNumber);
    rootConfig.addChild(configParagraphStartWith);

    Configuration configuration = new Configuration(rootConfig);
    assertEquals(3, configuration.getSectionValidatorConfigs().size());
  }
}
