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
package org.unigram.docvalidator;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.CharacterTableLoader;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.ValidatorConfiguration;
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
    DVResource resource = configurationLoader.loadConfiguration(stream);
    IOUtils.closeQuietly(stream);
    assertNotNull(resource);
    assertEquals(".", resource.getCharacterTable().getCharacter("FULL_STOP").getValue());
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
    DVResource resource = configurationLoader.loadConfiguration(stream);
    IOUtils.closeQuietly(stream);
    assertNotNull(resource);
    assertEquals("。", resource.getCharacterTable().getCharacter("FULL_STOP").getValue());
  }

  @Test
  public void testLoadConfigurationWithoutValidatorConfig() {
    String sampleConfigString =
        "<configuration> " +
            "  <lang char-conf=\"sample/conf/symbol-conf-en.xml\">en</lang>" +
        "</configuration>";
    ConfigurationLoader configurationLoader = new ConfigurationLoaderForTest();
    InputStream stream = IOUtils.toInputStream(sampleConfigString);
    DVResource resource = configurationLoader.loadConfiguration(stream);
    IOUtils.closeQuietly(stream);
    assertNull(resource);
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
    DVResource resource = configurationLoader.loadConfiguration(stream);
    IOUtils.closeQuietly(stream);
    assertNull(resource);
  }

  @Test
  public void testLoadConfigurationWithoutLangConfig() {
    String sampleConfigString =
        "<configuration> " +
        "  <validator>sample/conf/validation-conf.xml</validator>" +
        "</configuration>";
    ConfigurationLoader configurationLoader = new ConfigurationLoaderForTest();
    InputStream stream = IOUtils.toInputStream(sampleConfigString);
    DVResource resource = configurationLoader.loadConfiguration(stream);
    IOUtils.closeQuietly(stream);
    assertNull(resource);
  }

  @Test
  public void testLoadConfigurationWithoutRootConfigBlock() {
    String sampleConfigString =
        "<dummy> " +
            "  <validator>sample/conf/validation-conf.xml</validator>" +
            "  <lang char-conf=\"sample/conf/symbol-conf-en.xml\">en</lang>" +
        "</dummy>";
    ConfigurationLoader configurationLoader = new ConfigurationLoaderForTest();
    InputStream stream = IOUtils.toInputStream(sampleConfigString);
    DVResource resource = configurationLoader.loadConfiguration(stream);
    IOUtils.closeQuietly(stream);
    assertNull(resource);
  }

  @Test
  public void testLoadConfigurationWithoutContent() {
    String sampleConfigString = "";
    ConfigurationLoader configurationLoader = new ConfigurationLoaderForTest();
    InputStream stream = IOUtils.toInputStream(sampleConfigString);
    DVResource resource = configurationLoader.loadConfiguration(stream);
    IOUtils.closeQuietly(stream);
    assertNull(resource);
  }

  @Test
  public void testLoadNullConfiguration() {
    ConfigurationLoader configurationLoader = new ConfigurationLoaderForTest();
    InputStream stream = null;
    DVResource resource = configurationLoader.loadConfiguration(stream);
    assertNull(resource);
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
    DVResource resource = configurationLoader.loadConfiguration(stream);
    IOUtils.closeQuietly(stream);
    assertNull(resource);
  }
}
