package org.unigram.docvalidator.util;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class ValidationConfigurationLoaderTest {

  @Test
  public void testLoadValidatorConfigString() {
    String sampleConfiguraitonStr = new String(
        "<?xml version=\"1.0\"?>" +
        "<component name=\"Validator\">" +
        "  <component name=\"SentenceIterator\">" +
        "    <component name=\"SentenceLength\">"+
        "      <property name=\"max_length\" value=\"10\"/>" +
        "    </component>" +
        "  </component>" +
        "</component>");
    InputStream stream = IOUtils.toInputStream(sampleConfiguraitonStr);
    ValidationConfigurationLoader loader = new ValidationConfigurationLoader();
    ValidatorConfiguration conf = loader.loadConfiguraiton(stream);
    assertNotNull(conf);
    assertEquals(1, conf.getChildrenNumber());
  }

  @Test
  public void testLoadValidatorConfigWithoutAnyValidator() {
    String sampleConfiguraitonStr = new String(
        "<?xml version=\"1.0\"?>" +
        "<component name=\"Validator\">" +
        "</component>");
    InputStream stream = IOUtils.toInputStream(sampleConfiguraitonStr);
    ValidationConfigurationLoader loader = new ValidationConfigurationLoader();
    ValidatorConfiguration conf = loader.loadConfiguraiton(stream);
    assertNotNull(conf);
    assertEquals(0, conf.getChildrenNumber());
  }

  @Test
  public void testLoadValidatorConfigWithInvalidBlock() {
    String sampleConfiguraitonStr = new String(
        "<?xml version=\"1.0\"?>" +
        "<component name=\"Validator\">" +
        "  <component name=\"ParagraphLength\" />"+
        "  <dummy name=\"ParagraphLength\" />"+
        "</component>");
    InputStream stream = IOUtils.toInputStream(sampleConfiguraitonStr);
    ValidationConfigurationLoader loader = new ValidationConfigurationLoader();
    ValidatorConfiguration conf = loader.loadConfiguraiton(stream);
    assertNotNull(conf);
    assertEquals(1, conf.getChildrenNumber());
  }

}
