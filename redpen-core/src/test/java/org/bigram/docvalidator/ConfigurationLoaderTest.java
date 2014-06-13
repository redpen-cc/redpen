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
package org.bigram.docvalidator;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.Test;

import org.apache.commons.io.IOUtils;
import org.bigram.docvalidator.config.CharacterTable;
import org.bigram.docvalidator.config.CharacterTableLoader;
import org.bigram.docvalidator.config.Configuration;
import org.bigram.docvalidator.config.ValidatorConfiguration;
import org.w3c.dom.Element;

class ConfigurationLoaderForTest extends ConfigurationLoader {
  @Override
  protected CharacterTable extractCharacterTable(String configPath, String lang) {
    InputStream is;
    String dummy = "<?xml version=\"1.0\"?>" +
        "<character-table></character-table>"; // no config override
    is = new ByteArrayInputStream(dummy.getBytes());
    return CharacterTableLoader.load(is, lang);
  }

  @Override
  protected ValidatorConfiguration extractValidatorConfiguration(
      Element rootElement) {
    return new ValidatorConfiguration("dummy");
  }
}

public class ConfigurationLoaderTest {
  @Test
  public void testLoadConfiguration() {
    String sampleConfigString =
        "<configuration> " +
            "  <validator>sample/conf/validation-conf.xml</validator>" +
            "  <lang char-conf=\"sample/conf/symbol-conf-en.xml\">en</lang>" +
            "</configuration>";
    ConfigurationLoader configurationLoader = new ConfigurationLoaderForTest();
    InputStream stream = IOUtils.toInputStream(sampleConfigString);
    Configuration configuration = configurationLoader.loadConfiguration(stream);
    IOUtils.closeQuietly(stream);
    assertNotNull(configuration);
    assertEquals(".", configuration.getCharacterTable().getCharacter("FULL_STOP").getValue());
  }

  @Test
  public void testLoadJapaneseConfiguration() {
    String sampleConfigString =
        "<configuration> " +
            "  <validator>sample/conf/validation-conf.xml</validator>" +
            "  <lang char-conf=\"sample/conf/symbol-conf-ja.xml\">ja</lang>" +
            "</configuration>";
    ConfigurationLoader configurationLoader = new ConfigurationLoaderForTest();
    InputStream stream = IOUtils.toInputStream(sampleConfigString);
    Configuration configuration = configurationLoader.loadConfiguration(stream);
    IOUtils.closeQuietly(stream);
    assertNotNull(configuration);
    assertEquals("。", configuration.getCharacterTable().getCharacter("FULL_STOP").getValue());
  }

  @Test
  public void testLoadConfigurationWithoutValidatorConfig() {
    String sampleConfigString =
        "<configuration> " +
            "  <lang char-conf=\"sample/conf/symbol-conf-en.xml\">en</lang>" +
        "</configuration>";
    ConfigurationLoader configurationLoader = new ConfigurationLoaderForTest();
    InputStream stream = IOUtils.toInputStream(sampleConfigString);
    Configuration configuration = configurationLoader.loadConfiguration(stream);
    assertNull(configuration);
    IOUtils.closeQuietly(stream);
  }

  @Test
  public void testLoadConfigurationWithoutCharConfig() {
    String sampleConfigString =
        "<configuration> " +
            "  <validator>sample/conf/validation-conf.xml</validator>" +
            "  <lang>en</lang>" +
        "</configuration>";
    ConfigurationLoader configurationLoader = new ConfigurationLoaderForTest();
    InputStream stream = IOUtils.toInputStream(sampleConfigString);
    Configuration configuration = configurationLoader.loadConfiguration(stream);
    IOUtils.closeQuietly(stream);
    assertNull(configuration);
  }

  @Test
  public void testLoadConfigurationWithoutLangConfig() {
    String sampleConfigString =
        "<configuration> " +
        "  <validator>sample/conf/validation-conf.xml</validator>" +
        "</configuration>";
    ConfigurationLoader configurationLoader = new ConfigurationLoaderForTest();
    InputStream stream = IOUtils.toInputStream(sampleConfigString);
    Configuration configuration = configurationLoader.loadConfiguration(stream);
    IOUtils.closeQuietly(stream);
    assertNull(configuration);
  }

  @Test(expected = IllegalStateException.class)
  public void testLoadConfigurationWithoutRootConfigBlock() {
    String sampleConfigString =
        "<dummy> " +
            "  <validator>sample/conf/validation-conf.xml</validator>" +
            "  <lang char-conf=\"sample/conf/symbol-conf-en.xml\">en</lang>" +
        "</dummy>";
    ConfigurationLoader configurationLoader = new ConfigurationLoaderForTest();
    InputStream stream = IOUtils.toInputStream(sampleConfigString);
    configurationLoader.loadConfiguration(stream);
  }

  @Test
  public void testLoadConfigurationWithoutContent() {
    String sampleConfigString = "";
    ConfigurationLoader configurationLoader = new ConfigurationLoaderForTest();
    InputStream stream = IOUtils.toInputStream(sampleConfigString);
    Configuration configuration = configurationLoader.loadConfiguration(stream);
    IOUtils.closeQuietly(stream);
    assertNull(configuration);
  }

  @Test
  public void testLoadNullConfiguration() {
    ConfigurationLoader configurationLoader = new ConfigurationLoaderForTest();
    InputStream stream = null;
    Configuration configuration = configurationLoader.loadConfiguration(stream);
    assertNull(configuration);
  }

  @Test
  public void testLoadInvalidConfiguration() {
    String sampleConfigString =
        "<configuration> " +
            "  <validator>sample/conf/validation-conf.xml</validator>" +
            "  <lang char-conf=\"sample/conf/symbol-conf-en.xml\">en</lang>" +
        "<configuration>"; // NOTE: no slash
    ConfigurationLoader configurationLoader = new ConfigurationLoaderForTest();
    InputStream stream = IOUtils.toInputStream(sampleConfigString);
    Configuration configuration = configurationLoader.loadConfiguration(stream);
    IOUtils.closeQuietly(stream);
    assertNull(configuration);
  }


  @Test
  public void testNewLoadConfiguration() {
    String sampleConfigString =
        "<redpen-conf>" +
            "<validator-list>" +
            "<validator name=\"SentenceLength\">" +
            "<property name=\"max_length\" value=\"200\" />" +
            "</validator>" +
            "<validator name=\"MaxParagraphNumber\" />" +
            "</validator-list>" +
            "<character-table lang=\"en\">" +
            "<character name=\"EXCLAMATION_MARK\" value=\"!\" invalid-chars=\"！\" after-space=\"true\" />" +
            "</character-table>" +
            "</redpen-conf>";

    ConfigurationLoader configurationLoader = new ConfigurationLoader();
    InputStream stream = IOUtils.toInputStream(sampleConfigString);
    Configuration configuration = configurationLoader.loadNewConfiguration(stream);
    IOUtils.closeQuietly(stream);

    assertNotNull(configuration);
    assertEquals(1, configuration.getSentenceValidatorConfigs().size());
    assertEquals("SentenceLength",
        configuration.getSentenceValidatorConfigs().get(0).getConfigurationName());
    assertEquals(1, configuration.getSectionValidatorConfigs().size());
    assertEquals("MaxParagraphNumber",
        configuration.getSectionValidatorConfigs().get(0).getConfigurationName());
    assertNotNull(configuration.getCharacterTable());
    assertEquals("!", configuration.getCharacterTable()
        .getCharacter("EXCLAMATION_MARK").getValue());
    assertEquals(1, configuration.getCharacterTable()
        .getCharacter("EXCLAMATION_MARK").getInvalidChars().size());
    assertEquals("！", configuration.getCharacterTable()
        .getCharacter("EXCLAMATION_MARK").getInvalidChars().get(0));
  }

  @Test
  public void testNewLoadConfigurationWithoutCharacterTableConfig() {
    String sampleConfigString =
        "<redpen-conf>" +
            "<validator-list>" +
            "<validator name=\"SentenceLength\">" +
            "<property name=\"max_length\" value=\"200\" />" +
            "</validator>" +
            "<validator name=\"MaxParagraphNumber\" />" +
            "</validator-list>" +
            "</redpen-conf>";

    ConfigurationLoader configurationLoader = new ConfigurationLoader();
    InputStream stream = IOUtils.toInputStream(sampleConfigString);
    Configuration configuration = configurationLoader.loadNewConfiguration(stream);
    IOUtils.closeQuietly(stream);

    assertNotNull(configuration);
    assertEquals(1, configuration.getSentenceValidatorConfigs().size());
    assertEquals("SentenceLength",
        configuration.getSentenceValidatorConfigs().get(0).getConfigurationName());
    assertEquals(1, configuration.getSectionValidatorConfigs().size());
    assertEquals("MaxParagraphNumber",
        configuration.getSectionValidatorConfigs().get(0).getConfigurationName());
    assertNotNull(configuration.getCharacterTable());
    assertEquals("!", configuration.getCharacterTable()
        .getCharacter("EXCLAMATION_MARK").getValue());
  }

  @Test
  public void testNewLoadConfigurationWithoutCharacterTableConfigContent() {
    String sampleConfigString =
        "<redpen-conf>" +
            "<validator-list>" +
            "<validator name=\"SentenceLength\">" +
            "<property name=\"max_length\" value=\"200\" />" +
            "</validator>" +
            "<validator name=\"MaxParagraphNumber\" />" +
            "</validator-list>" +
            "<character-table lang=\"en\">" +
            "</character-table>" +
            "</redpen-conf>";

    ConfigurationLoader configurationLoader = new ConfigurationLoader();
    InputStream stream = IOUtils.toInputStream(sampleConfigString);
    Configuration configuration = configurationLoader.loadNewConfiguration(stream);
    IOUtils.closeQuietly(stream);

    assertNotNull(configuration);
    assertEquals(1, configuration.getSentenceValidatorConfigs().size());
    assertEquals("SentenceLength",
        configuration.getSentenceValidatorConfigs().get(0).getConfigurationName());
    assertEquals(1, configuration.getSectionValidatorConfigs().size());
    assertEquals("MaxParagraphNumber",
        configuration.getSectionValidatorConfigs().get(0).getConfigurationName());
    assertNotNull(configuration.getCharacterTable());
    assertEquals("!", configuration.getCharacterTable()
        .getCharacter("EXCLAMATION_MARK").getValue());
  }

  @Test
  public void testNewLoadConfigurationWithoutValidatorConfig() {
    String sampleConfigString =
        "<redpen-conf>" +
            "<character-table lang=\"en\">" +
            "<character name=\"EXCLAMATION_MARK\" value=\"!\" invalid-chars=\"！\" after-space=\"true\" />" +
            "</character-table>" +
            "</redpen-conf>";

    ConfigurationLoader configurationLoader = new ConfigurationLoader();
    InputStream stream = IOUtils.toInputStream(sampleConfigString);
    Configuration configuration = configurationLoader.loadNewConfiguration(stream);
    assertNull(configuration);
    IOUtils.closeQuietly(stream);
  }

  @Test
  public void testNewLoadConfigurationWithoutValidatorConfigContent() {
    String sampleConfigString =
        "<redpen-conf>" +
            "<validator-list>" +
            "</validator-list>" +
            "<character-table lang=\"en\">" +
            "<character name=\"EXCLAMATION_MARK\" value=\"!\" invalid-chars=\"！\" after-space=\"true\" />" +
            "</character-table>" +
            "</redpen-conf>";

    ConfigurationLoader configurationLoader = new ConfigurationLoader();
    InputStream stream = IOUtils.toInputStream(sampleConfigString);
    Configuration configuration = configurationLoader.loadNewConfiguration(stream);
    assertNull(configuration);
    IOUtils.closeQuietly(stream);
  }

  @Test
  public void testVoidConfig() {
    String sampleConfigString =
        "<redpen-conf></redpen-conf>";
    ConfigurationLoader configurationLoader = new ConfigurationLoader();
    InputStream stream = IOUtils.toInputStream(sampleConfigString);
    Configuration configuration = configurationLoader.loadNewConfiguration(stream);
    assertNull(configuration);
    IOUtils.closeQuietly(stream);
  }

  @Test
  public void testNewLoadInvalidConfiguration() {
    String sampleConfigString =
        "<redpen-conf>" +
            "<validator-list>" +
            "<validator name=\"SentenceLength\">" +
            "<property name=\"max_length\" value=\"200\" />" +
            "</validator>" +
            "<validator name=\"MaxParagraphNumber\" />" +
            "</validator-list>" +
            "<character-table lang=\"en\">" +
            "<character name=\"EXCLAMATION_MARK\" value=\"!\" invalid-chars=\"！\" after-space=\"true\" />" +
            "</character-table>" +
            "<redpen-conf>";  // NOTE: Invalid xml since slash should be exist.

    ConfigurationLoader configurationLoader = new ConfigurationLoader();
    InputStream stream = IOUtils.toInputStream(sampleConfigString);
    Configuration configuration = configurationLoader.loadNewConfiguration(stream);
    IOUtils.closeQuietly(stream);

    assertNull(configuration);
  }
}
