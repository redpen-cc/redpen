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

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.model.Sentence;
import cc.redpen.validator.ValidationError;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cc.redpen.config.SymbolType.*;
import static org.junit.jupiter.api.Assertions.*;

class ConfigurationLoaderTest {
    @Test
    void emptyConfiguration() throws RedPenException {
        Configuration configuration = new ConfigurationLoader().loadFromString("<redpen-conf/>");

        assertNotNull(configuration);
        assertEquals("en", configuration.getLang());
    }

    @Test
    void testLoadJapaneseConfiguration() throws RedPenException{
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

        Configuration configuration = new ConfigurationLoader().loadFromString(sampleConfigString);

        assertNotNull(configuration);
        assertEquals("zenkaku", configuration.getVariant());
        assertEquals(2, configuration.getValidatorConfigs().size());
        assertEquals("SentenceLength",
                configuration.getValidatorConfigs().get(0).getConfigurationName());
        assertEquals("200",
                configuration.getValidatorConfigs().get(0).getProperty("max_length"));
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
    void testLoadJapaneseConfigurationWithHankakuVariant() throws RedPenException{
        String sampleConfigString =
                "<redpen-conf lang=\"ja\" variant=\"hankaku\">" +
                        "<validators>" +
                        "<validator name=\"SentenceLength\">" +
                        "<property name=\"max_length\" value=\"200\" />" +
                        "</validator>" +
                        "<validator name=\"MaxParagraphNumber\" />" +
                        "</validators>" +
                        "</redpen-conf>";

        Configuration configuration = new ConfigurationLoader().loadFromString(sampleConfigString);

        assertNotNull(configuration);
        assertEquals("hankaku", configuration.getVariant());
        assertEquals(2, configuration.getValidatorConfigs().size());
        assertEquals("SentenceLength",
                configuration.getValidatorConfigs().get(0).getConfigurationName());
        assertEquals("200",
                configuration.getValidatorConfigs().get(0).getProperty("max_length"));
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
    void testVariantCanBeSpecifiedAsTypeForBackwardsCompatibility() throws RedPenException{
        String sampleConfigString = "<redpen-conf lang=\"ja\" type=\"hankaku\"><validators/></redpen-conf>";

        Configuration configuration = new ConfigurationLoader().loadFromString(sampleConfigString);

        assertEquals("hankaku", configuration.getVariant());
    }

    @Test
    void testLoadJapaneseConfigurationWithZenkaku2Variant() throws RedPenException{
        String sampleConfigString =
                "<redpen-conf lang=\"ja\" variant=\"zenkaku2\">" +
                        "<validators>" +
                        "<validator name=\"SentenceLength\">" +
                        "<property name=\"max_length\" value=\"200\" />" +
                        "</validator>" +
                        "<validator name=\"MaxParagraphNumber\" />" +
                        "</validators>" +
                        "</redpen-conf>";

        Configuration configuration = new ConfigurationLoader().loadFromString(sampleConfigString);

        assertNotNull(configuration);
        assertEquals("zenkaku2", configuration.getVariant());
        assertEquals(2, configuration.getValidatorConfigs().size());
        assertEquals("SentenceLength",
                configuration.getValidatorConfigs().get(0).getConfigurationName());
        assertEquals("200",
                configuration.getValidatorConfigs().get(0).getProperty("max_length"));
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
    void testNewLoadConfigurationWithoutSymbolTableConfig() throws RedPenException{
        String sampleConfigString =
                "<redpen-conf>" +
                        "<validators>" +
                        "<validator name=\"SentenceLength\">" +
                        "<property name=\"max_length\" value=\"200\" />" +
                        "</validator>" +
                        "<validator name=\"MaxParagraphNumber\" />" +
                        "</validators>" +
                        "</redpen-conf>";

        Configuration configuration = new ConfigurationLoader().loadFromString(sampleConfigString);

        assertNotNull(configuration);
        assertEquals("en", configuration.getLang());
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
    void testNewLoadConfigurationWithoutSymbolTableConfigContent() throws RedPenException{
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

        Configuration configuration = new ConfigurationLoader().loadFromString(sampleConfigString);

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
    void testNewLoadConfigurationWithoutValidatorConfigContent() throws RedPenException{
        String sampleConfigString =
                "<redpen-conf lang=\"en\">" +
                        "<validators>" +
                        "</validators>" +
                        "<symbols>" +
                        "<symbol name=\"EXCLAMATION_MARK\" value=\"!\" invalid-chars=\"！\" after-space=\"true\" />" +
                        "</symbols>" +
                        "</redpen-conf>";

        Configuration configuration = new ConfigurationLoader().loadFromString(sampleConfigString);
        assertNotNull(configuration);
    }

    @Test
    void testNewLoadInvalidConfiguration() throws RedPenException{
        assertThrows(RedPenException.class, () -> {
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

            // expecting RedPenException
            new ConfigurationLoader().loadFromString(sampleConfigString);
        });
    }

    @Test
    void testSpaceConfiguration() throws RedPenException{
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

        Configuration configuration = new ConfigurationLoader().loadFromString(sampleConfigString);

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
    void testConfigurationWithoutSpaceSetting() throws RedPenException{
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

        Configuration configuration = new ConfigurationLoader().loadFromString(sampleConfigString);

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
    void testConfigurationMultipleInvalids() throws RedPenException{
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

        Configuration configuration = new ConfigurationLoader().loadFromString(sampleConfigString);

        assertNotNull(configuration);

        assertEquals('\'', configuration.getSymbolTable()
                .getSymbol(LEFT_SINGLE_QUOTATION_MARK).getValue());
        assertEquals(2, configuration.getSymbolTable()
                .getSymbol(LEFT_SINGLE_QUOTATION_MARK).getInvalidChars().length);
        assertEquals('‘', configuration.getSymbolTable().getSymbol(LEFT_SINGLE_QUOTATION_MARK).getInvalidChars()[0]);
        assertEquals('’', configuration.getSymbolTable().getSymbol(LEFT_SINGLE_QUOTATION_MARK).getInvalidChars()[1]);
    }

    @Test
    void testSymbolConfigurationWithoutInvalid() throws RedPenException{
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

        Configuration configuration = new ConfigurationLoader().loadFromString(sampleConfigString);

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
    void testAccessNotRegisteredSymbol() throws RedPenException{
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

        Configuration configuration = new ConfigurationLoader().loadFromString(sampleConfigString);

        assertNotNull(configuration);
//        // NOTE: HADOOP_CHARACTER does not exist even in default settings
//        Symbol ch = configuration.getSymbolTable().getSymbol(HADOOP_CHARACTER);
//        assertNull(ch);
    }

    @Test
    void testConfigurationWithMisspelledBlock() throws RedPenException{
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

        Configuration configuration = new ConfigurationLoader().loadFromString(sampleConfigString);
        assertNotNull(configuration); //FIXME: should be null or throw a exception. This will be fixed with issue #133.
        assertNotNull(configuration.getSymbolTable());
    }

    @Test
    void testSymbolConfigurationWithoutName() throws RedPenException{
        assertThrows(IllegalStateException.class, () -> {
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

            new ConfigurationLoader().loadFromString(sampleConfigString);
        });
    }

    @Test
    void loadFromFileSetsBaseDir() throws Exception {
        File file = File.createTempFile("redpen-conf", ".xml");
        file.deleteOnExit();
        try (OutputStream out = new FileOutputStream(file)) {
            out.write("<redpen-conf/>".getBytes());
        }
        Configuration config = new ConfigurationLoader().load(file);
        assertEquals(file.getParentFile(), config.getBase());
    }

    @Test
    void testDuplicatedValidatorConfiguration() throws RedPenException{
        String sampleConfigString =
                "<redpen-conf lang=\"ja\">" +
                        "<validators>" +
                        "<validator name=\"SentenceLength\">" +
                        "<property name=\"max_length\" value=\"200\" />" +
                        "</validator>" +
                        "<validator name=\"SentenceLength\">" +
                        "<property name=\"max_length\" value=\"300\" />" +
                        "</validator>" +
                        "</validators>" +
                        "<symbols>" +
                        "<symbol name=\"EXCLAMATION_MARK\" value=\"！\" invalid-chars=\"!\" after-space=\"true\" />" +
                        "</symbols>" +
                        "</redpen-conf>";

        Configuration configuration = new ConfigurationLoader().loadFromString(sampleConfigString);

        assertNotNull(configuration);
        assertEquals(1, configuration.getValidatorConfigs().size());
        assertEquals("SentenceLength",
                configuration.getValidatorConfigs().get(0).getConfigurationName());
        assertEquals("300",
                configuration.getValidatorConfigs().get(0).getProperty("max_length"));
    }

    @Test
    void testValidatorConfigurationWithErrorLevel() throws RedPenException {
        String sampleConfigString =
                "<redpen-conf lang=\"ja\">" +
                        "<validators>" +
                        "<validator name=\"SentenceLength\" level=\"INFO\" />" +
                        "</validators>" +
                        "</redpen-conf>";

        Configuration configuration = new ConfigurationLoader().loadFromString(sampleConfigString);

        assertNotNull(configuration);
        assertEquals(1, configuration.getValidatorConfigs().size());
        assertEquals("SentenceLength",
                configuration.getValidatorConfigs().get(0).getConfigurationName());
        assertEquals(ValidatorConfiguration.LEVEL.INFO,
                configuration.getValidatorConfigs().get(0).getLevel());
    }

    @Test()
    void testValidatorConfigurationWithInvalidErrorLevel() throws RedPenException {
        assertThrows(RuntimeException.class, () -> {
            String sampleConfigString =
                    "<redpen-conf lang=\"ja\">" +
                            "<validators>" +
                            "<validator name=\"SentenceLength\" level=\"FOOBAR\" />" +
                            "</validators>" +
                            "</redpen-conf>";

            Configuration configuration = new ConfigurationLoader().loadFromString(sampleConfigString);
        });
    }

    @Test
    void testSpecifiedErrorLevelComeInErrors() throws RedPenException {
        String sampleConfigString =
                "<redpen-conf lang=\"ja\">" +
                        "<validators>" +
                        "<validator name=\"SentenceLength\" level=\"INFO\" />" +
                        "</validators>" +
                        "</redpen-conf>";

        Configuration config = new ConfigurationLoader().loadFromString(sampleConfigString);
        List<Document> documents = new ArrayList<>();documents.add(
                Document.builder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("This is a long long long long long long long long long long long long long long long long long long long long long long long long sentence", 1))
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents, "info");
        assertEquals(1, errors.get(documents.get(0)).size());
        assertEquals(ValidatorConfiguration.LEVEL.INFO, errors.get(documents.get(0)).get(0).getLevel());
    }
}
