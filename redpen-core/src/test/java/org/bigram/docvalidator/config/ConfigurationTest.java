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

import org.bigram.docvalidator.config.Configuration;
import org.bigram.docvalidator.config.ValidatorConfiguration;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for Configuration Structure
 */
public class ConfigurationTest {

  @Test
  public void testSentenceIteratorConfiguration() throws Exception {
    ValidatorConfiguration rootConfig = new ValidatorConfiguration("top");
    ValidatorConfiguration sentenceIteratorConfig = new ValidatorConfiguration("SentenceIterator");

    ValidatorConfiguration configSentenceLength = new ValidatorConfiguration("SentenceLength");
    ValidatorConfiguration configInvalidExpression = new ValidatorConfiguration("InvalidExpression");
    ValidatorConfiguration configSpaceAfterPeriod = new ValidatorConfiguration("SpaceAfterPeriod");
    ValidatorConfiguration configCommaNumber = new ValidatorConfiguration("CommaNumber");
    ValidatorConfiguration configWordNumber = new ValidatorConfiguration("WordNumber");
    ValidatorConfiguration configSuggestExpression = new ValidatorConfiguration("SuggestExpression");
    ValidatorConfiguration configInvalidCharacter = new ValidatorConfiguration("InvalidCharacter");
    ValidatorConfiguration configSpaceWithSymbol = new ValidatorConfiguration("SpaceWithSymbol");
    ValidatorConfiguration configKatakanaEndHyphen = new ValidatorConfiguration("KatakanaEndHyphen");
    ValidatorConfiguration configKatakanaSpellCheckValidator = new ValidatorConfiguration("KatakanaSpellCheckValidator");

    sentenceIteratorConfig.addChild(configSentenceLength);
    sentenceIteratorConfig.addChild(configInvalidExpression);
    sentenceIteratorConfig.addChild(configSpaceAfterPeriod);
    sentenceIteratorConfig.addChild(configCommaNumber);
    sentenceIteratorConfig.addChild(configWordNumber);
    sentenceIteratorConfig.addChild(configSuggestExpression);
    sentenceIteratorConfig.addChild(configInvalidCharacter);
    sentenceIteratorConfig.addChild(configSpaceWithSymbol);
    sentenceIteratorConfig.addChild(configKatakanaEndHyphen);
    sentenceIteratorConfig.addChild(configKatakanaSpellCheckValidator);

    rootConfig.addChild(sentenceIteratorConfig);

    Configuration configuration = new Configuration(rootConfig);
    assertEquals(10, configuration.getSentenceValidatorConfigs().size());

  }

  @Test
  public void testInvalidNestedSentenceValidatorConfiguration() throws Exception{
    ValidatorConfiguration rootConfig = new ValidatorConfiguration("top");

    ValidatorConfiguration sentenceIteratorConfig = new ValidatorConfiguration("NotSentenceIterator");

    ValidatorConfiguration configSentenceLength = new ValidatorConfiguration("SentenceLength");
    ValidatorConfiguration configInvalidExpression = new ValidatorConfiguration("InvalidExpression");
    ValidatorConfiguration configSpaceAfterPeriod = new ValidatorConfiguration("SpaceAfterPeriod");
    ValidatorConfiguration configCommaNumber = new ValidatorConfiguration("CommaNumber");
    ValidatorConfiguration configWordNumber = new ValidatorConfiguration("WordNumber");
    ValidatorConfiguration configSuggestExpression = new ValidatorConfiguration("SuggestExpression");
    ValidatorConfiguration configInvalidCharacter = new ValidatorConfiguration("InvalidCharacter");
    ValidatorConfiguration configSpaceWithSymbol = new ValidatorConfiguration("SpaceWithSymbol");
    ValidatorConfiguration configKatakanaEndHyphen = new ValidatorConfiguration("KatakanaEndHyphen");
    ValidatorConfiguration configKatakanaSpellCheckValidator = new ValidatorConfiguration("KatakanaSpellCheckValidator");


    sentenceIteratorConfig.addChild(configSentenceLength);
    sentenceIteratorConfig.addChild(configInvalidExpression);
    sentenceIteratorConfig.addChild(configSpaceAfterPeriod);
    sentenceIteratorConfig.addChild(configCommaNumber);
    sentenceIteratorConfig.addChild(configWordNumber);
    sentenceIteratorConfig.addChild(configSuggestExpression);
    sentenceIteratorConfig.addChild(configInvalidCharacter);
    sentenceIteratorConfig.addChild(configSpaceWithSymbol);
    sentenceIteratorConfig.addChild(configKatakanaEndHyphen);
    sentenceIteratorConfig.addChild(configKatakanaSpellCheckValidator);
    rootConfig.addChild(sentenceIteratorConfig);

    Configuration configuration = new Configuration(rootConfig);
    assertEquals(0, configuration.getSentenceValidatorConfigs().size());

  }

  @Test
  public void testConfiguration() throws Exception{
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
