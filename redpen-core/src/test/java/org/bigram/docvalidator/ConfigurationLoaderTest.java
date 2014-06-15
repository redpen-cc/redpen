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
import java.io.InputStream;
import org.junit.Test;

import org.apache.commons.io.IOUtils;
import org.bigram.docvalidator.config.Configuration;

public class ConfigurationLoaderTest {
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
    Configuration configuration = configurationLoader.loadConfiguration(stream);
    IOUtils.closeQuietly(stream);

    assertNotNull(configuration);
    assertEquals(1, configuration.getSentenceValidatorConfigs().size());
    assertEquals("SentenceLength",
        configuration.getSentenceValidatorConfigs().get(0).getConfigurationName());
    assertEquals("200",
        configuration.getSentenceValidatorConfigs().get(0).getAttribute("max_length"));
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
    Configuration configuration = configurationLoader.loadConfiguration(stream);
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
    Configuration configuration = configurationLoader.loadConfiguration(stream);
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
    Configuration configuration = configurationLoader.loadConfiguration(stream);
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
    Configuration configuration = configurationLoader.loadConfiguration(stream);
    IOUtils.closeQuietly(stream);

    assertNull(configuration);
  }
}
