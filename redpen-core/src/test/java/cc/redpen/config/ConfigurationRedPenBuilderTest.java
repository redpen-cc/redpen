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

import static cc.redpen.config.SymbolType.FULL_STOP;
import static org.junit.Assert.*;

/**
 * Tests for creating Configuration object with the Builder.
 */
public class ConfigurationRedPenBuilderTest {
    @Test
    public void testBuildSimpleConfiguration() {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("InvalidExpression"))
                .addValidatorConfig(new ValidatorConfiguration("SentenceLength"))
                .setLanguage("en").build();

        assertEquals(2, config.getValidatorConfigs().size());
        assertNotNull(config.getSymbolTable());
        assertEquals("InvalidExpression", config.getValidatorConfigs()
                .get(0).getConfigurationName());
        assertEquals("SentenceLength", config.getValidatorConfigs()
                .get(1).getConfigurationName());
    }

    @Test
    public void testBuildConfigurationWithoutSymbolTable() {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("InvalidExpression"))
                .addValidatorConfig(new ValidatorConfiguration("SentenceLength")).build();

        assertEquals(2, config.getValidatorConfigs().size());
        assertNotNull(config.getSymbolTable());
        assertEquals("InvalidExpression", config.getValidatorConfigs()
                .get(0).getConfigurationName());
        assertEquals("SentenceLength", config.getValidatorConfigs()
                .get(1).getConfigurationName());
    }

    @Test
    public void testBuildConfigurationAddingProperties() {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("InvalidExpression")
                        .addAttribute("dict", "./foobar.dict"))
                .addValidatorConfig(new ValidatorConfiguration("SentenceLength")
                        .addAttribute("max_length", "10")).build();

        assertEquals(2, config.getValidatorConfigs().size());
        assertNotNull(config.getSymbolTable());
        assertEquals("InvalidExpression", config.getValidatorConfigs()
                .get(0).getConfigurationName());
        assertEquals("./foobar.dict",
                config.getValidatorConfigs().get(0).getAttribute("dict"));
        assertEquals("SentenceLength", config.getValidatorConfigs()
                .get(1).getConfigurationName());
        assertEquals("10",
                config.getValidatorConfigs().get(1).getAttribute("max_length"));
    }

    @Test
    public void testBuildConfigurationSpecifyingLanguage() {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("InvalidExpression"))
                .setLanguage("ja")
                .build();

        assertNotNull(config.getSymbolTable());
        assertNotNull(config.getSymbolTable().getSymbol(FULL_STOP));
        assertEquals('。', config.getSymbolTable().getSymbol(FULL_STOP).getValue());
    }

    @Test
    public void testBuildConfigurationSpecifyingLanguageAndType() {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("InvalidExpression"))
                .setLanguage("ja")
                .setType("hankaku")
                .build();

        assertNotNull(config.getSymbolTable());
        assertNotNull(config.getSymbolTable().getSymbol(FULL_STOP));
        assertEquals('.', config.getSymbolTable().getSymbol(FULL_STOP).getValue());
    }

    @Test
    public void testBuildConfigurationOverrideSymbolSetting() {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("InvalidExpression"))
                .setLanguage("ja")
                .addSymbol(new Symbol(FULL_STOP, '.'))
                .build();

        assertNotNull(config.getSymbolTable());
        assertNotNull(config.getSymbolTable().getSymbol(FULL_STOP));
        assertEquals('.', config.getSymbolTable().getSymbol(FULL_STOP).getValue());
    }

    @Test
    public void testBuildConfigurationOverrideAddInvalidSymbolSetting() {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("InvalidExpression"))
                .setLanguage("ja")
                .addSymbol(new Symbol(SymbolType.FULL_STOP, '。', ".．●"))
                .build();

        assertNotNull(config.getSymbolTable());
        assertNotNull(config.getSymbolTable().getSymbol(FULL_STOP));
        assertEquals('。', config.getSymbolTable().getSymbol(FULL_STOP).getValue());
        assertTrue(new String(config.getSymbolTable()
                .getSymbol(FULL_STOP).getInvalidChars()).contains("●"));
    }

    @Test
    public void testBuildConfigurationAccessingSymbolByValue() {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("InvalidExpression"))
                .setLanguage("ja")
                .addSymbol(new Symbol(SymbolType.FULL_STOP, '。', ".．●"))
                .build();

        assertNotNull(config.getSymbolTable());
        assertNotNull(config.getSymbolTable().getSymbol(FULL_STOP));
        assertEquals('。', config.getSymbolTable().getSymbol(FULL_STOP).getValue());
        assertTrue(new String(config.getSymbolTable()
                .getSymbol(FULL_STOP).getInvalidChars()).contains("●"));
        assertTrue(config.getSymbolTable().containsSymbolByValue('。'));
        assertTrue(new String(config.getSymbolTable()
                .getSymbolByValue('。').getInvalidChars()).contains("●"));
    }

    @Test(expected = IllegalStateException.class)
    public void testBuildTwice(){
        Configuration.ConfigurationBuilder builder = new Configuration.ConfigurationBuilder();
        builder.setLanguage("en");
        builder.build();
        // ConfigurationBuilder is not designed to build more than one RedPen instance
        builder.setLanguage("ja");
    }
}
