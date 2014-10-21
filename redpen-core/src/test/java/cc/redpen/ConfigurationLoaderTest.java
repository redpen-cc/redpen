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
package cc.redpen;

import cc.redpen.config.Configuration;
import cc.redpen.config.Symbol;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

public class ConfigurationLoaderTest {
    @Test
    public void testLoadConfiguration() {
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
        InputStream stream = IOUtils.toInputStream(sampleConfigString);
        Configuration configuration = configurationLoader.loadConfiguration(stream);
        IOUtils.closeQuietly(stream);

        assertNotNull(configuration);
        assertEquals(2, configuration.getValidatorConfigs().size());
        assertEquals("SentenceLength",
                configuration.getValidatorConfigs().get(0).getConfigurationName());
        assertEquals("200",
                configuration.getValidatorConfigs().get(0).getAttribute("max_length"));
        assertEquals("MaxParagraphNumber",
                configuration.getValidatorConfigs().get(1).getConfigurationName());
        assertNotNull(configuration.getSymbolTable());
        assertEquals("!", configuration.getSymbolTable()
                .getSymbol("EXCLAMATION_MARK").getValue());
        assertEquals(1, configuration.getSymbolTable()
                .getSymbol("EXCLAMATION_MARK").getInvalidSymbols().size());
        assertEquals("！", configuration.getSymbolTable()
                .getSymbol("EXCLAMATION_MARK").getInvalidSymbols().get(0));
    }

    @Test
    public void testLoadJapaneseConfiguration() {
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
        InputStream stream = IOUtils.toInputStream(sampleConfigString);
        Configuration configuration = configurationLoader.loadConfiguration(stream);
        IOUtils.closeQuietly(stream);

        assertNotNull(configuration);
        assertEquals(2, configuration.getValidatorConfigs().size());
        assertEquals("SentenceLength",
                configuration.getValidatorConfigs().get(0).getConfigurationName());
        assertEquals("200",
                configuration.getValidatorConfigs().get(0).getAttribute("max_length"));
        assertEquals("MaxParagraphNumber",
                configuration.getValidatorConfigs().get(1).getConfigurationName());
        assertNotNull(configuration.getSymbolTable());
        assertEquals("！", configuration.getSymbolTable()
                .getSymbol("EXCLAMATION_MARK").getValue());
        assertEquals(1, configuration.getSymbolTable()
                .getSymbol("EXCLAMATION_MARK").getInvalidSymbols().size());
        assertEquals("!", configuration.getSymbolTable()
                .getSymbol("EXCLAMATION_MARK").getInvalidSymbols().get(0));
    }

    @Test
    public void testNewLoadConfigurationWithoutSymbolTableConfig() {
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
        InputStream stream = IOUtils.toInputStream(sampleConfigString);
        Configuration configuration = configurationLoader.loadConfiguration(stream);
        IOUtils.closeQuietly(stream);

        assertNotNull(configuration);
        assertEquals(2, configuration.getValidatorConfigs().size());
        assertEquals("SentenceLength",
                configuration.getValidatorConfigs().get(0).getConfigurationName());
        assertEquals("MaxParagraphNumber",
                configuration.getValidatorConfigs().get(1).getConfigurationName());
        assertNotNull(configuration.getSymbolTable());
        assertEquals("!", configuration.getSymbolTable()
                .getSymbol("EXCLAMATION_MARK").getValue());
    }

    @Test
    public void testNewLoadConfigurationWithoutSymbolTableConfigContent() {
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
        InputStream stream = IOUtils.toInputStream(sampleConfigString);
        Configuration configuration = configurationLoader.loadConfiguration(stream);
        IOUtils.closeQuietly(stream);

        assertNotNull(configuration);
        assertEquals(2, configuration.getValidatorConfigs().size());
        assertEquals("SentenceLength",
                configuration.getValidatorConfigs().get(0).getConfigurationName());
        assertEquals("MaxParagraphNumber",
                configuration.getValidatorConfigs().get(1).getConfigurationName());
        assertNotNull(configuration.getSymbolTable());
        assertEquals("!", configuration.getSymbolTable()
                .getSymbol("EXCLAMATION_MARK").getValue());
    }

    @Test
    public void testNewLoadConfigurationWithoutValidatorConfig() {
        String sampleConfigString =
                "<redpen-conf lang=\"en\">" +
                        "<symbols>" +
                        "<symbol name=\"EXCLAMATION_MARK\" value=\"!\" invalid-chars=\"！\" after-space=\"true\" />" +
                        "</symbols>" +
                        "</redpen-conf>";

        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        InputStream stream = IOUtils.toInputStream(sampleConfigString);
        Configuration configuration = configurationLoader.loadConfiguration(stream);
        assertNull(configuration);
        IOUtils.closeQuietly(stream);
    }

    @Test
    public void testNewLoadConfigurationWithoutValidatorConfigContent() {
        String sampleConfigString =
                "<redpen-conf lang=\"en\">" +
                        "<validators>" +
                        "</validators>" +
                        "<symbols>" +
                        "<symbol name=\"EXCLAMATION_MARK\" value=\"!\" invalid-chars=\"！\" after-space=\"true\" />" +
                        "</symbols>" +
                        "</redpen-conf>";

        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        InputStream stream = IOUtils.toInputStream(sampleConfigString);
        Configuration configuration = configurationLoader.loadConfiguration(stream);
        assertNotNull(configuration);
        IOUtils.closeQuietly(stream);
    }

    @Test
    public void testVoidConfig() {
        String sampleConfigString =
                "<redpen-conf></redpen-conf>";
        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        InputStream stream = IOUtils.toInputStream(sampleConfigString);
        Configuration configuration = configurationLoader.loadConfiguration(stream);
        assertNull(configuration);
        IOUtils.closeQuietly(stream);
    }

    @Test
    public void testNewLoadInvalidConfiguration() {
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
        InputStream stream = IOUtils.toInputStream(sampleConfigString);
        Configuration configuration = configurationLoader.loadConfiguration(stream);
        IOUtils.closeQuietly(stream);

        assertNull(configuration);
    }

    @Test
    public void testSpaceConfiguration() {
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
        InputStream stream = IOUtils.toInputStream(sampleConfigString);
        Configuration configuration = configurationLoader.loadConfiguration(stream);
        IOUtils.closeQuietly(stream);

        assertNotNull(configuration);

        assertEquals("!", configuration.getSymbolTable()
                .getSymbol("EXCLAMATION_MARK").getValue());
        assertEquals(1, configuration.getSymbolTable()
                .getSymbol("EXCLAMATION_MARK").getInvalidSymbols().size());
        assertEquals("！", configuration.getSymbolTable()
                .getSymbol("EXCLAMATION_MARK").getInvalidSymbols().get(0));
        assertEquals(false, configuration.getSymbolTable()
                .getSymbol("EXCLAMATION_MARK").isNeedBeforeSpace());
        assertEquals(true, configuration.getSymbolTable()
                .getSymbol("EXCLAMATION_MARK").isNeedAfterSpace());
    }

    @Test
    public void testConfigurationWithoutSpaceSetting() {
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
        InputStream stream = IOUtils.toInputStream(sampleConfigString);
        Configuration configuration = configurationLoader.loadConfiguration(stream);
        IOUtils.closeQuietly(stream);

        assertNotNull(configuration);

        assertEquals("!", configuration.getSymbolTable()
                .getSymbol("EXCLAMATION_MARK").getValue());
        assertEquals(1, configuration.getSymbolTable()
                .getSymbol("EXCLAMATION_MARK").getInvalidSymbols().size());
        assertEquals("！", configuration.getSymbolTable()
                .getSymbol("EXCLAMATION_MARK").getInvalidSymbols().get(0));
        assertEquals(false, configuration.getSymbolTable()
                .getSymbol("EXCLAMATION_MARK").isNeedBeforeSpace());
        assertEquals(false, configuration.getSymbolTable()
                .getSymbol("EXCLAMATION_MARK").isNeedAfterSpace());
    }


    @Test
    public void testConfigurationMultipleInvalids() {
        String sampleConfigString =
                "<redpen-conf lang=\"en\">" +
                        "<validators>" +
                        "<validator name=\"SentenceLength\" />" +
                        "<validator name=\"MaxParagraphNumber\" />" +
                        "</validators>" +
                        "<symbols>" +
                        "<symbol name=\"LEFT_QUOTATION_MARK\" value=\"\'\" invalid-chars=\"‘’\"/>" +
                        "</symbols>" +
                        "</redpen-conf>";

        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        InputStream stream = IOUtils.toInputStream(sampleConfigString);
        Configuration configuration = configurationLoader.loadConfiguration(stream);
        IOUtils.closeQuietly(stream);

        assertNotNull(configuration);

        assertEquals("\'", configuration.getSymbolTable()
                .getSymbol("LEFT_QUOTATION_MARK").getValue());
        assertEquals(2, configuration.getSymbolTable()
                .getSymbol("LEFT_QUOTATION_MARK").getInvalidSymbols().size());
        assertEquals("‘", configuration.getSymbolTable().getSymbol("LEFT_QUOTATION_MARK").getInvalidSymbols().get(0));
        assertEquals("’", configuration.getSymbolTable().getSymbol("LEFT_QUOTATION_MARK").getInvalidSymbols().get(1));
    }

    @Test
    public void testSymbolConfigurationWithoutInvalid() {
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
        InputStream stream = IOUtils.toInputStream(sampleConfigString);
        Configuration configuration = configurationLoader.loadConfiguration(stream);
        IOUtils.closeQuietly(stream);

        assertNotNull(configuration);

        assertEquals("!", configuration.getSymbolTable()
                .getSymbol("EXCLAMATION_MARK").getValue());
        assertEquals(0, configuration.getSymbolTable()
                .getSymbol("EXCLAMATION_MARK").getInvalidSymbols().size());
        assertEquals(false, configuration.getSymbolTable()
                .getSymbol("EXCLAMATION_MARK").isNeedBeforeSpace());
        assertEquals(false, configuration.getSymbolTable()
                .getSymbol("EXCLAMATION_MARK").isNeedAfterSpace());
    }

    @Test
    public void testAccessNotRegisteredSymbol() {
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
        InputStream stream = IOUtils.toInputStream(sampleConfigString);
        Configuration configuration = configurationLoader.loadConfiguration(stream);
        IOUtils.closeQuietly(stream);

        assertNotNull(configuration);
        // NOTE: HADOOP_CHARACTER does not exist even in default settings
        Symbol ch = configuration.getSymbolTable().getSymbol("HADOOP_CHARACTER");
        assertNull(ch);
    }


    @Test
    public void testConfigurationWithMisspelledBlock() {
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
        InputStream stream = IOUtils.toInputStream(sampleConfigString);
        Configuration configuration = configurationLoader.loadConfiguration(stream);
        IOUtils.closeQuietly(stream);
        assertNotNull(configuration); //FIXME: should be null or throw a exception. This will be fixed with issue #133.
        assertNotNull(configuration.getSymbolTable());
    }

    @Test(expected = IllegalStateException.class)
    public void testSymbolConfigurationWithoutName() {
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
        InputStream stream = IOUtils.toInputStream(sampleConfigString);
        configurationLoader.loadConfiguration(stream);
        IOUtils.closeQuietly(stream);
    }

}
