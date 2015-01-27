/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
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
package cc.redpen.config;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for Configuration Structure
 */
public class ConfigurationTest {

    @Test
    public void testSentenceValidatorConfiguration() throws Exception {

        Configuration configuration = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("SentenceLength"))
                .addValidatorConfig(new ValidatorConfiguration("InvalidExpression"))
                .addValidatorConfig(new ValidatorConfiguration("SpaceBeginningOfSentence"))
                .addValidatorConfig(new ValidatorConfiguration("CommaNumber"))
                .addValidatorConfig(new ValidatorConfiguration("WordNumber"))
                .addValidatorConfig(new ValidatorConfiguration("SuggestExpression"))
                .addValidatorConfig(new ValidatorConfiguration("InvalidCharacter"))
                .addValidatorConfig(new ValidatorConfiguration("SymbolWithSpace"))
                .addValidatorConfig(new ValidatorConfiguration("KatakanaEndHyphen"))
                .addValidatorConfig(new ValidatorConfiguration("KatakanaSpellCheck"))
                .build();
        assertEquals(10, configuration.getValidatorConfigs().size());
    }

    @Test
    public void testInvalidValidatorConfiguration() {
        // NOTE: not throw a exception even when adding a non exist validator.
        // The errors occurs when creating the added non existing validator instance.
        try {
            new Configuration.ConfigurationBuilder()
                    .addValidatorConfig(new ValidatorConfiguration("ThereIsNoSuchValidator")).build();
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testSectionValidatorConfiguration() throws Exception {
        Configuration configuration = new Configuration.ConfigurationBuilder().addValidatorConfig(new ValidatorConfiguration("SectionLength"))
                .addValidatorConfig(new ValidatorConfiguration("MaxParagraphNumber"))
                .addValidatorConfig(new ValidatorConfiguration("ParagraphStartWith")).build();
        assertEquals(3, configuration.getValidatorConfigs().size());
    }

    @Test
    public void testSymbolTableWithoutLanguageSetting() throws Exception {
        Configuration configuration = new Configuration.ConfigurationBuilder().build(); // NOTE: load "en" setting when lang is not specified
        assertEquals("en", configuration.getLang());
        assertNotNull(configuration.getLang());
    }
}
