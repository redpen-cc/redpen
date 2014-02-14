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
    ValidatorConfiguration conf =
        ValidationConfigurationLoader.loadConfiguration(stream);
    assertNotNull(conf);
    assertEquals(1, conf.getChildrenNumber());
    IOUtils.closeQuietly(stream);
  }

  @Test
  public void testLoadValidatorConfigWithoutAnyValidator() {
    String sampleConfiguraitonStr = new String(
        "<?xml version=\"1.0\"?>" +
        "<component name=\"Validator\">" +
        "</component>");
    InputStream stream = IOUtils.toInputStream(sampleConfiguraitonStr);
    ValidatorConfiguration conf =
        ValidationConfigurationLoader.loadConfiguration(stream);
    assertNotNull(conf);
    assertEquals(0, conf.getChildrenNumber());
    IOUtils.closeQuietly(stream);
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
    ValidatorConfiguration conf =
        ValidationConfigurationLoader.loadConfiguration(stream);
    assertNotNull(conf);
    assertEquals(1, conf.getChildrenNumber());
    IOUtils.closeQuietly(stream);
  }

  @Test
  public void testLoadValidatorInvalidXMLConfig() {
    String sampleConfiguraitonStr = new String(
        "<?xml version=\"1.0\"?>" +
        "<component name=\"Validator\">" +
        "  <component name=\"ParagraphLength\" />"+
        "  <dummy name=\"ParagraphLength\" />"+
        "<component>"); // NOTE: no slash
    InputStream stream = IOUtils.toInputStream(sampleConfiguraitonStr);
    ValidatorConfiguration conf =
        ValidationConfigurationLoader.loadConfiguration(stream);
    assertNull(conf);
    IOUtils.closeQuietly(stream);
  }

  @Test
  public void testLoadValidatorConfigWithoutContent() {
    String sampleConfiguraitonStr = new String("");
    InputStream stream = IOUtils.toInputStream(sampleConfiguraitonStr);
    ValidatorConfiguration conf =
        ValidationConfigurationLoader.loadConfiguration(stream);
    assertNull(conf);
    IOUtils.closeQuietly(stream);
  }

  @Test
  public void testNull() {
    InputStream stream = null;
    ValidatorConfiguration conf =
        ValidationConfigurationLoader.loadConfiguration(stream);
    assertNull(conf);
  }
}
