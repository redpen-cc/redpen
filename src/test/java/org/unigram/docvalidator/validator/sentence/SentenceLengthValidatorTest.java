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
package org.unigram.docvalidator.validator.sentence;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.unigram.docvalidator.ConfigurationLoader;
import org.unigram.docvalidator.parser.Parser;
import org.unigram.docvalidator.parser.PlainTextParser;
import org.unigram.docvalidator.store.Document;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.FakeResultDistributor;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.util.Configuration;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.validator.DocumentValidator;

public class SentenceLengthValidatorTest {

  private String sampleText;
  private Document doc;
  private Configuration conf;

  private String sampleConfiguraitonStr = new String(
      "<?xml version=\"1.0\"?>" +
      "<component name=\"Validator\">" +
      "  <component name=\"SentenceIterator\">" +
      "    <component name=\"SentenceLength\">"+
      "      <property name=\"max_length\" value=\"10\"/>" +
      "    </component>" +
      "  </component>" +
      "</component>");

  @Before
  public void setup() {
    this.doc = new Document();
    this.sampleText = "This is a long long long long long long long long long long long long long long long sentence.\n";
    ConfigurationLoader loader = new ConfigurationLoader();
    InputStream stream = IOUtils.toInputStream(this.sampleConfiguraitonStr);
    this.conf = loader.loadConfiguraiton(stream);
    if (this.conf == null) {
      fail();
    }

    Parser parser = new PlainTextParser();
    parser.initialize(new DVResource(conf));
    InputStream is = null;
    try {
      is = new ByteArrayInputStream(sampleText.getBytes("utf-8"));
    } catch (UnsupportedEncodingException e) {
      fail();
    }
    try {
      this.doc.appendFile(parser.generateDocument(is));
    } catch (DocumentValidatorException e) {
      fail();
    }
  }

  @Test
  public void testLength() {
    DocumentValidator validator = null;
    try {
      validator = new DocumentValidator(new DVResource(conf), new FakeResultDistributor());
    } catch (DocumentValidatorException e) {
      fail();
    }
    List<ValidationError> errors = validator.check(doc);
    assertEquals(1, errors.size());
  }
}
