package org.unigram.docvalidator.validator.sentence;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

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
      "<configuration name=\"Validator\">" +
      "  <component name=\"SentenceIterator\">" +
      "    <component name=\"SentenceLength\">"+
      "      <property name=\"max_length\" value=\"10\"/>" +
      "    </component>" +
      "  </component>" +
      "</configuration>");

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
    Vector<ValidationError> errors = validator.process(doc);
    assertEquals(1, errors.size());
  }
}
