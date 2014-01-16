/**
 * DocumentValidator
 * Copyright (c) 2013-, Takahiko Ito, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package org.unigram.docvalidator.util;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationTest {

  @Before
  public void setUp () {
    String sampleConfiguraiton = new String(
        "<?xml version=\"1.0\"?>"+ 
        "<component name=\"Validator\">" +
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
        "</component>");

    InputStream stream = IOUtils.toInputStream(sampleConfiguraiton);
    this.conf = ValidationConfigurationLoader.loadConfiguration(stream);
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
    Iterator<ValidatorConfiguration> iterator = conf.getChildren();
    while(iterator.hasNext()) {
      ValidatorConfiguration childConfiguration = iterator.next();
      if ("SentenceLength".equals(childConfiguration.getConfigurationName())) {
          assertEquals("30", childConfiguration.getAttribute("max_length"));
      }
    }
  }

  @Test
  public void testPropertyInParent() {
    Iterator<ValidatorConfiguration> iterator = conf.getChildren();
    while(iterator.hasNext()) {
      ValidatorConfiguration childConfiguration = iterator.next();
      if ("SentenceLength".equals(childConfiguration.getConfigurationName())) {
        assertNotNull(childConfiguration.getParent());
        assertEquals(".", childConfiguration.getAttribute("period"));
      }
    }
  }

  @Test
  public void testPropertyNotInParent() {
    Iterator<ValidatorConfiguration> iterator = conf.getChildren();
    while(iterator.hasNext()) {
      ValidatorConfiguration childConfiguration = iterator.next();
      if ("SentenceLength".equals(childConfiguration.getConfigurationName())) {
        assertNotNull(childConfiguration.getParent());
        assertEquals(null, childConfiguration.getAttribute("foobar"));
      }
    }
  }

  @Test
  public void testPropertyInBrother() {
    Iterator<ValidatorConfiguration> iterator = conf.getChildren();
    while(iterator.hasNext()) {
      ValidatorConfiguration childConfiguration = iterator.next();
      if ("SentenceLength".equals(childConfiguration.getConfigurationName())) {
        assertNotNull(childConfiguration.getParent());
        assertEquals(null, childConfiguration.getAttribute("max_comma_num"));
      }
    }
  }

  private ValidatorConfiguration conf=null;
}
