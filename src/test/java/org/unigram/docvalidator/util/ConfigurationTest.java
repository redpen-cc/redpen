package org.unigram.docvalidator.util;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unigram.docvalidator.ConfigurationLoader;

public class ConfigurationTest {

  @Before
  public void setUp () {
    String sampleConfiguraiton = new String(
        "<?xml version=\"1.0\"?>"+ 
        "<configuration name=\"Validator\">" +
          "<property name=\"unit\" value=\"character\" />" +
          "<property name=\"period\" value=\".\" />" +
          "<property name=\"comma\" value=\", \" />" +
          "<property name=\"comment\" value=\"#\" />" +
          "<component name=\"SentenceLength\">" +
            "<property name=\"max_length\" value=\"30\" />" +
          "</component>" +
          "<component name=\"CommaMaxNum\">" +
            "<property name=\"max_comma_num\" value=\"3\" />" +
          "</component>" +
          "<component name=\"SentenceComma\" />" +
          "<component name=\"InvalidSuffix\" />" +
        "</configuration>");

  ConfigurationLoader loader = new ConfigurationLoader();
    InputStream stream = IOUtils.toInputStream(sampleConfiguraiton);
    this.conf = loader.loadConfiguraiton(stream);
    if (this.conf == null) {
      fail();
    }
  }

  @After
  public void tearDown () {
    this.conf = null;
  }

  @Test
  public void testChildrenSize() {
    assertEquals(4, conf.getChildrenNumber());
    
  }

  @Test
  public void testProperties() {
      assertTrue(".".equals(conf.getAttribute("period")));
      assertNull(conf.getAttribute("dummy"));
  }

  @Test
  public void testChildProperties() {
    Iterator<Configuration> iterator = conf.getChildren();
    while(iterator.hasNext()) {
      Configuration childConfiguration = iterator.next();
      if ("SentenceLength".equals(childConfiguration.getConfigurationName())) {
          assertEquals("30", childConfiguration.getAttribute("max_length"));
      }
    }
  }

  @Test
  public void testPropertyInParent() {
    Iterator<Configuration> iterator = conf.getChildren();
    while(iterator.hasNext()) {
      Configuration childConfiguration = iterator.next();
      if ("SentenceLength".equals(childConfiguration.getConfigurationName())) {
        assertNotNull(childConfiguration.getParent());
        assertEquals(".", childConfiguration.getAttribute("period"));
      }
    }
  }

  @Test
  public void testPropertyNotInParent() {
    Iterator<Configuration> iterator = conf.getChildren();
    while(iterator.hasNext()) {
      Configuration childConfiguration = iterator.next();
      if ("SentenceLength".equals(childConfiguration.getConfigurationName())) {
        assertNotNull(childConfiguration.getParent());
        assertEquals(null, childConfiguration.getAttribute("foobar"));
      }
    }
  }

  @Test
  public void testPropertyInBrother() {
    Iterator<Configuration> iterator = conf.getChildren();
    while(iterator.hasNext()) {
      Configuration childConfiguration = iterator.next();
      if ("SentenceLength".equals(childConfiguration.getConfigurationName())) {
        assertNotNull(childConfiguration.getParent());
        assertEquals(null, childConfiguration.getAttribute("max_comma_num"));
      }
    }
  }

  private Configuration conf=null;
}
