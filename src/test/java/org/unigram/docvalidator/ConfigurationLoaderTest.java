package org.unigram.docvalidator;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.ValidatorConfiguration;
import org.w3c.dom.Element;

class ConfigurationLoaderForTest extends ConfigurationLoader {
  @Override
  protected CharacterTable extractCharacterTable(Element rootElement) {
    return new CharacterTable();
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
            "  <validator-config>sample/conf/validation-conf.xml</validator-config>" +
            "  <symbol-table>sample/conf/symbol-conf-en.xml</symbol-table>" +
        "</configuration>";
    ConfigurationLoader configurationLoader = new ConfigurationLoaderForTest();
    InputStream stream = IOUtils.toInputStream(sampleConfigString);
    DVResource resorce = configurationLoader.loadConfiguraiton(stream);
    IOUtils.closeQuietly(stream);
    assertNotNull(resorce);
  }

  @Test
  public void testLoadConfigurationWithoutValidatorConfig() {
    String sampleConfigString =
        "<configuration> " +
        "  <symbol-table>sample/conf/symbol-conf-en.xml</symbol-table>" +
        "</configuration>";
    ConfigurationLoader configurationLoader = new ConfigurationLoaderForTest();
    InputStream stream = IOUtils.toInputStream(sampleConfigString);
    DVResource resorce = configurationLoader.loadConfiguraiton(stream);
    IOUtils.closeQuietly(stream);
    assertNull(resorce);
  }

  @Test
  public void testLoadConfigurationWithoutCharacterConfig() {
    String sampleConfigString =
        "<configuration> " +
        "  <validator-config>sample/conf/validation-conf.xml</validator-config>" +
        "</configuration>";
    ConfigurationLoader configurationLoader = new ConfigurationLoaderForTest();
    InputStream stream = IOUtils.toInputStream(sampleConfigString);
    DVResource resorce = configurationLoader.loadConfiguraiton(stream);
    IOUtils.closeQuietly(stream);
    assertNull(resorce);
  }

  @Test
  public void testLoadConfigurationWithoutRootConfigBlock() {
    String sampleConfigString =
        "<dummy> " +
            "  <validator-config>sample/conf/validation-conf.xml</validator-config>" +
            "  <symbol-table>sample/conf/symbol-conf-en.xml</symbol-table>" +
        "</dummy>";
    ConfigurationLoader configurationLoader = new ConfigurationLoaderForTest();
    InputStream stream = IOUtils.toInputStream(sampleConfigString);
    DVResource resorce = configurationLoader.loadConfiguraiton(stream);
    IOUtils.closeQuietly(stream);
    assertNull(resorce);
  }
}
