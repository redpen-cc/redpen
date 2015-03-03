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
package cc.redpen;

import cc.redpen.config.Configuration;
import org.junit.Test;

import static cc.redpen.config.SymbolType.EXCLAMATION_MARK;
import static cc.redpen.config.SymbolType.LEFT_SINGLE_QUOTATION_MARK;
import static cc.redpen.config.SymbolType.COMMA;

import static org.junit.Assert.*;

public class ConfigurationLoaderTest {
    @Test
    public void testLoadConfiguration() throws RedPenException{
        String sampleConfigString =
                "<redpen-conf lang=\"en\">" +
                        "<validators>" +
                        "<validator name=\"SentenceLength\">" +
                        "<property name=\"max_length\" value=\"200\" />" +
                        "</validator>" +
                        "<validator name=\"MaxParagraphNumber\" />" +
                        "</validators>" +
                        "<symbols>" +
                        "<symbol name=\"EXCLAMATION_MARK\" value=\"!\" invalid-chars=\"！\" after-space=\"true\" />" +
                        "</symbols>" +
                        "</redpen-conf>";

        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration configuration = configurationLoader.loadFromString(sampleConfigString);

        assertNotNull(configuration);
        assertEquals(2, configuration.getValidatorConfigs().size());
        assertEquals("SentenceLength",
                configuration.getValidatorConfigs().get(0).getConfigurationName());
        assertEquals("200",
                configuration.getValidatorConfigs().get(0).getAttribute("max_length"));
        assertEquals("MaxParagraphNumber",
                configuration.getValidatorConfigs().get(1).getConfigurationName());
        assertNotNull(configuration.getSymbolTable());
        assertEquals('!', configuration.getSymbolTable()
                .getSymbol(EXCLAMATION_MARK).getValue());
        assertEquals(1, configuration.getSymbolTable()
                .getSymbol(EXCLAMATION_MARK).getInvalidChars().length);
        assertEquals('！', configuration.getSymbolTable()
                .getSymbol(EXCLAMATION_MARK).getInvalidChars()[0]);
    }

    @Test
    public void testLoadJapaneseConfiguration() throws RedPenException{
        String sampleConfigString =
                "<redpen-conf lang=\"ja\">" +
                        "<validators>" +
                        "<validator name=\"SentenceLength\">" +
                        "<property name=\"max_length\" value=\"200\" />" +
                        "</validator>" +
                        "<validator name=\"MaxParagraphNumber\" />" +
                        "</validators>" +
                        "<symbols>" +
                        "<symbol name=\"EXCLAMATION_MARK\" value=\"！\" invalid-chars=\"!\" after-space=\"true\" />" +
                        "</symbols>" +
                        "</redpen-conf>";

        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration configuration = configurationLoader.loadFromString(sampleConfigString);

        assertNotNull(configuration);
        assertEquals(2, configuration.getValidatorConfigs().size());
        assertEquals("SentenceLength",
                configuration.getValidatorConfigs().get(0).getConfigurationName());
        assertEquals("200",
                configuration.getValidatorConfigs().get(0).getAttribute("max_length"));
        assertEquals("MaxParagraphNumber",
                configuration.getValidatorConfigs().get(1).getConfigurationName());
        assertNotNull(configuration.getSymbolTable());
        assertEquals('！', configuration.getSymbolTable()
                .getSymbol(EXCLAMATION_MARK).getValue());
        assertEquals(1, configuration.getSymbolTable()
                .getSymbol(EXCLAMATION_MARK).getInvalidChars().length);
        assertEquals('!', configuration.getSymbolTable()
                .getSymbol(EXCLAMATION_MARK).getInvalidChars()[0]);
    }

    @Test
    public void testLoadJapaneseConfigurationWithHankakuType() throws RedPenException{
        String sampleConfigString =
                "<redpen-conf lang=\"ja\" type=\"hankaku\">" +
                        "<validators>" +
                        "<validator name=\"SentenceLength\">" +
                        "<property name=\"max_length\" value=\"200\" />" +
                        "</validator>" +
                        "<validator name=\"MaxParagraphNumber\" />" +
                        "</validators>" +
                        "</redpen-conf>";

        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration configuration = configurationLoader.loadFromString(sampleConfigString);

        assertNotNull(configuration);
        assertEquals(2, configuration.getValidatorConfigs().size());
        assertEquals("SentenceLength",
                configuration.getValidatorConfigs().get(0).getConfigurationName());
        assertEquals("200",
                configuration.getValidatorConfigs().get(0).getAttribute("max_length"));
        assertEquals("MaxParagraphNumber",
                configuration.getValidatorConfigs().get(1).getConfigurationName());
        assertNotNull(configuration.getSymbolTable());
        assertEquals('!', configuration.getSymbolTable()
                .getSymbol(EXCLAMATION_MARK).getValue());
        assertEquals(1, configuration.getSymbolTable()
                .getSymbol(EXCLAMATION_MARK).getInvalidChars().length);
        assertEquals('！', configuration.getSymbolTable()
                .getSymbol(EXCLAMATION_MARK).getInvalidChars()[0]);
    }


    @Test
    public void testLoadJapaneseConfigurationWithZenkaku2Type() throws RedPenException{
        String sampleConfigString =
                "<redpen-conf lang=\"ja\" type=\"zenkaku2\">" +
                        "<validators>" +
                        "<validator name=\"SentenceLength\">" +
                        "<property name=\"max_length\" value=\"200\" />" +
                        "</validator>" +
                        "<validator name=\"MaxParagraphNumber\" />" +
                        "</validators>" +
                        "</redpen-conf>";

        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration configuration = configurationLoader.loadFromString(sampleConfigString);

        assertNotNull(configuration);
        assertEquals(2, configuration.getValidatorConfigs().size());
        assertEquals("SentenceLength",
                configuration.getValidatorConfigs().get(0).getConfigurationName());
        assertEquals("200",
                configuration.getValidatorConfigs().get(0).getAttribute("max_length"));
        assertEquals("MaxParagraphNumber",
                configuration.getValidatorConfigs().get(1).getConfigurationName());
        assertNotNull(configuration.getSymbolTable());
        assertEquals('，', configuration.getSymbolTable()
                .getSymbol(COMMA).getValue());
        assertEquals(2, configuration.getSymbolTable()
                .getSymbol(COMMA).getInvalidChars().length);
        assertEquals('、', configuration.getSymbolTable()
                .getSymbol(COMMA).getInvalidChars()[0]);
        assertEquals(',', configuration.getSymbolTable()
                .getSymbol(COMMA).getInvalidChars()[1]);
    }

    @Test
    public void testNewLoadConfigurationWithoutSymbolTableConfig() throws RedPenException{
        String sampleConfigString =
                "<redpen-conf>" +
                        "<validators>" +
                        "<validator name=\"SentenceLength\">" +
                        "<property name=\"max_length\" value=\"200\" />" +
                        "</validator>" +
                        "<validator name=\"MaxParagraphNumber\" />" +
                        "</validators>" +
                        "</redpen-conf>";

        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration configuration = configurationLoader.loadFromString(sampleConfigString);

        assertNotNull(configuration);
        assertEquals(2, configuration.getValidatorConfigs().size());
        assertEquals("SentenceLength",
                configuration.getValidatorConfigs().get(0).getConfigurationName());
        assertEquals("MaxParagraphNumber",
                configuration.getValidatorConfigs().get(1).getConfigurationName());
        assertNotNull(configuration.getSymbolTable());
        assertEquals('!', configuration.getSymbolTable()
                .getSymbol(EXCLAMATION_MARK).getValue());
    }

    @Test
    public void testNewLoadConfigurationWithoutSymbolTableConfigContent() throws RedPenException{
        String sampleConfigString =
                "<redpen-conf lang=\"en\">" +
                        "<validators>" +
                        "<validator name=\"SentenceLength\">" +
                        "<property name=\"max_length\" value=\"200\" />" +
                        "</validator>" +
                        "<validator name=\"MaxParagraphNumber\" />" +
                        "</validators>" +
                        "<symbols/>" +
                        "</redpen-conf>";

        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration configuration = configurationLoader.loadFromString(sampleConfigString);

        assertNotNull(configuration);
        assertEquals(2, configuration.getValidatorConfigs().size());
        assertEquals("SentenceLength",
                configuration.getValidatorConfigs().get(0).getConfigurationName());
        assertEquals("MaxParagraphNumber",
                configuration.getValidatorConfigs().get(1).getConfigurationName());
        assertNotNull(configuration.getSymbolTable());
        assertEquals('!', configuration.getSymbolTable()
                .getSymbol(EXCLAMATION_MARK).getValue());
    }

    @Test
    public void testNewLoadConfigurationWithoutValidatorConfig() throws RedPenException {
        String sampleConfigString =
                "<redpen-conf lang=\"en\">" +
                        "<symbols>" +
                        "<symbol name=\"EXCLAMATION_MARK\" value=\"!\" invalid-chars=\"！\" after-space=\"true\" />" +
                        "</symbols>" +
                        "</redpen-conf>";

        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration configuration = configurationLoader.loadFromString(sampleConfigString);
        assertNull(configuration);
    }

    @Test
    public void testNewLoadConfigurationWithoutValidatorConfigContent() throws RedPenException{
        String sampleConfigString =
                "<redpen-conf lang=\"en\">" +
                        "<validators>" +
                        "</validators>" +
                        "<symbols>" +
                        "<symbol name=\"EXCLAMATION_MARK\" value=\"!\" invalid-chars=\"！\" after-space=\"true\" />" +
                        "</symbols>" +
                        "</redpen-conf>";

        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration configuration = configurationLoader.loadFromString(sampleConfigString);
        assertNotNull(configuration);
    }

    @Test
    public void testVoidConfig() throws RedPenException{
        String sampleConfigString =
                "<redpen-conf></redpen-conf>";
        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration configuration = configurationLoader.loadFromString(sampleConfigString);
        assertNull(configuration);
    }

    @Test(expected = RedPenException.class)
    public void testNewLoadInvalidConfiguration() throws RedPenException{
        String sampleConfigString =
                "<redpen-conf lang=\"en\">" +
                        "<validators>" +
                        "<validator name=\"SentenceLength\">" +
                        "<property name=\"max_length\" value=\"200\" />" +
                        "</validator>" +
                        "<validator name=\"MaxParagraphNumber\" />" +
                        "</validators>" +
                        "<symbols>" +
                        "<symbol name=\"EXCLAMATION_MARK\" value=\"!\" invalid-chars=\"！\" after-space=\"true\" />" +
                        "</symbols>" +
                        "<redpen-conf>";  // NOTE: Invalid xml since slash should be exist.

        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        // expecting RedPenException
        configurationLoader.loadFromString(sampleConfigString);
    }

    @Test
    public void testSpaceConfiguration() throws RedPenException{
        String sampleConfigString =
                "<redpen-conf lang=\"en\">" +
                        "<validators>" +
                        "<validator name=\"SentenceLength\" />" +
                        "<validator name=\"MaxParagraphNumber\" />" +
                        "</validators>" +
                        "<symbols>" +
                        "<symbol name=\"EXCLAMATION_MARK\" value=\"!\" invalid-chars=\"！\" after-space=\"true\" />" +
                        "</symbols>" +
                        "</redpen-conf>";

        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration configuration = configurationLoader.loadFromString(sampleConfigString);

        assertNotNull(configuration);

        assertEquals('!', configuration.getSymbolTable()
                .getSymbol(EXCLAMATION_MARK).getValue());
        assertEquals(1, configuration.getSymbolTable()
                .getSymbol(EXCLAMATION_MARK).getInvalidChars().length);
        assertEquals('！', configuration.getSymbolTable()
                .getSymbol(EXCLAMATION_MARK).getInvalidChars()[0]);
        assertEquals(false, configuration.getSymbolTable()
                .getSymbol(EXCLAMATION_MARK).isNeedBeforeSpace());
        assertEquals(true, configuration.getSymbolTable()
                .getSymbol(EXCLAMATION_MARK).isNeedAfterSpace());
    }

    @Test
    public void testConfigurationWithoutSpaceSetting() throws RedPenException{
        String sampleConfigString =
                "<redpen-conf lang=\"en\">" +
                        "<validators>" +
                        "<validator name=\"SentenceLength\" />" +
                        "<validator name=\"MaxParagraphNumber\" />" +
                        "</validators>" +
                        "<symbols>" +
                        "<symbol name=\"EXCLAMATION_MARK\" value=\"!\" invalid-chars=\"！\" />" +
                        "</symbols>" +
                        "</redpen-conf>";

        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration configuration = configurationLoader.loadFromString(sampleConfigString);

        assertNotNull(configuration);

        assertEquals('!', configuration.getSymbolTable()
                .getSymbol(EXCLAMATION_MARK).getValue());
        assertEquals(1, configuration.getSymbolTable()
                .getSymbol(EXCLAMATION_MARK).getInvalidChars().length);
        assertEquals('！', configuration.getSymbolTable()
                .getSymbol(EXCLAMATION_MARK).getInvalidChars()[0]);
        assertEquals(false, configuration.getSymbolTable()
                .getSymbol(EXCLAMATION_MARK).isNeedBeforeSpace());
        assertEquals(false, configuration.getSymbolTable()
                .getSymbol(EXCLAMATION_MARK).isNeedAfterSpace());
    }


    @Test
    public void testConfigurationMultipleInvalids() throws RedPenException{
        String sampleConfigString =
                "<redpen-conf lang=\"en\">" +
                        "<validators>" +
                        "<validator name=\"SentenceLength\" />" +
                        "<validator name=\"MaxParagraphNumber\" />" +
                        "</validators>" +
                        "<symbols>" +
                        "<symbol name=\"LEFT_SINGLE_QUOTATION_MARK\" value=\"\'\" invalid-chars=\"‘’\"/>" +
                        "</symbols>" +
                        "</redpen-conf>";

        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration configuration = configurationLoader.loadFromString(sampleConfigString);

        assertNotNull(configuration);

        assertEquals('\'', configuration.getSymbolTable()
                .getSymbol(LEFT_SINGLE_QUOTATION_MARK).getValue());
        assertEquals(2, configuration.getSymbolTable()
                .getSymbol(LEFT_SINGLE_QUOTATION_MARK).getInvalidChars().length);
        assertEquals('‘', configuration.getSymbolTable().getSymbol(LEFT_SINGLE_QUOTATION_MARK).getInvalidChars()[0]);
        assertEquals('’', configuration.getSymbolTable().getSymbol(LEFT_SINGLE_QUOTATION_MARK).getInvalidChars()[1]);
    }

    @Test
    public void testSymbolConfigurationWithoutInvalid() throws RedPenException{
        String sampleConfigString =
                "<redpen-conf lang=\"en\">" +
                        "<validators>" +
                        "<validator name=\"SentenceLength\" />" +
                        "<validator name=\"MaxParagraphNumber\" />" +
                        "</validators>" +
                        "<symbols>" +
                        "<symbol name=\"EXCLAMATION_MARK\" value=\"!\" />" +
                        "</symbols>" +
                        "</redpen-conf>";

        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration configuration = configurationLoader.loadFromString(sampleConfigString);

        assertNotNull(configuration);

        assertEquals('!', configuration.getSymbolTable()
                .getSymbol(EXCLAMATION_MARK).getValue());
        assertEquals(0, configuration.getSymbolTable()
                .getSymbol(EXCLAMATION_MARK).getInvalidChars().length);
        assertEquals(false, configuration.getSymbolTable()
                .getSymbol(EXCLAMATION_MARK).isNeedBeforeSpace());
        assertEquals(false, configuration.getSymbolTable()
                .getSymbol(EXCLAMATION_MARK).isNeedAfterSpace());
    }

    @Test
    public void testAccessNotRegisteredSymbol() throws RedPenException{
        String sampleConfigString =
                "<redpen-conf lang=\"en\">" +
                        "<validators>" +
                        "<validator name=\"SentenceLength\" />" +
                        "<validator name=\"MaxParagraphNumber\" />" +
                        "</validators>" +
                        "<symbols>" +
                        "<symbol name=\"EXCLAMATION_MARK\" value=\"!\" />" +
                        "</symbols>" +
                        "</redpen-conf>";

        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration configuration = configurationLoader.loadFromString(sampleConfigString);

        assertNotNull(configuration);
//        // NOTE: HADOOP_CHARACTER does not exist even in default settings
//        Symbol ch = configuration.getSymbolTable().getSymbol(HADOOP_CHARACTER);
//        assertNull(ch);
    }


    @Test
    public void testConfigurationWithMisspelledBlock() throws RedPenException{
        String sampleConfigString =
                "<redpen-conf lang=\"en\">" +
                        "<validators>" +
                        "<validator name=\"SentenceLength\" />" +
                        "<validator name=\"MaxParagraphNumber\" />" +
                        "</validators>" +
                        "<symbols>" +
                        "<charcc name=\"EXCLAMATION_MARK\" value=\"!\" />" +
                        "</symbols>" +
                        "</redpen-conf>";

        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration configuration = configurationLoader.loadFromString(sampleConfigString);
        assertNotNull(configuration); //FIXME: should be null or throw a exception. This will be fixed with issue #133.
        assertNotNull(configuration.getSymbolTable());
    }

    @Test(expected = IllegalStateException.class)
    public void testSymbolConfigurationWithoutName() throws RedPenException{
        String sampleConfigString =
                "<redpen-conf lang=\"en\">" +
                        "<validators>" +
                        "<validator name=\"SentenceLength\" />" +
                        "<validator name=\"MaxParagraphNumber\" />" +
                        "</validators>" +
                        "<symbols>" +
                        "<symbol value=\"!\" invalid-chars=\"！\"/>" + //NOTE: NO NAME!
                        "</symbols>" +
                        "</redpen-conf>";

        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        configurationLoader.loadFromString(sampleConfigString);
    }

}
