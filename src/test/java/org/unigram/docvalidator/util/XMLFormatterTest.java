package org.unigram.docvalidator.util;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.unigram.docvalidator.store.Sentence;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XMLFormatterTest {

  @Test
  public void testConvertValidationError() {
    ValidationError error = new ValidationError("Fatal Error",
        new Sentence("This is a sentence", 0), "foobar.md");
    XMLFormatter formatter = createXMLFormatter();
    String resultString = formatter.convertError(error);
    Document document = extractDocument(resultString);
    assertEquals(1, document.getElementsByTagName("error").getLength());
    assertEquals(1, document.getElementsByTagName("message").getLength());
    assertEquals("Fatal Error",
        document.getElementsByTagName("message").item(0).getTextContent());
    assertEquals(1, document.getElementsByTagName("file").getLength());
    assertEquals("foobar.md",
        document.getElementsByTagName("file").item(0).getTextContent());
    assertEquals(1, document.getElementsByTagName("lineNum").getLength());
    assertEquals("0",
        document.getElementsByTagName("lineNum").item(0).getTextContent());
    assertEquals(1, document.getElementsByTagName("sentence").getLength());
    assertEquals("This is a sentence",
        document.getElementsByTagName("sentence").item(0).getTextContent());
  }

  @Test
  public void testConvertValidationErrorWithoutFileName() {
    ValidationError error = new ValidationError(0, "Fatal Error");
    XMLFormatter formatter = createXMLFormatter();
    String resultString = formatter.convertError(error);
    Document document = extractDocument(resultString);
    assertEquals(1, document.getElementsByTagName("error").getLength());
    assertEquals(1, document.getElementsByTagName("message").getLength());
    assertEquals("Fatal Error",
        document.getElementsByTagName("message").item(0).getTextContent());
    assertEquals(0, document.getElementsByTagName("file").getLength());
    assertEquals(1, document.getElementsByTagName("lineNum").getLength());
    assertEquals("0",
        document.getElementsByTagName("lineNum").item(0).getTextContent());
  }

  @Test
  public void testConvertValidationErrorWithoutLineNumAndFileName() {
    ValidationError error = new ValidationError("Fatal Error");
    XMLFormatter formatter = createXMLFormatter();
    String resultString = formatter.convertError(error);
    Document document = extractDocument(resultString);
    assertEquals(1, document.getElementsByTagName("error").getLength());
    assertEquals(1, document.getElementsByTagName("message").getLength());
    assertEquals("Fatal Error",
        document.getElementsByTagName("message").item(0).getTextContent());
    assertEquals(0, document.getElementsByTagName("file").getLength());
    assertEquals(1, document.getElementsByTagName("lineNum").getLength());
    assertEquals("-1",
        document.getElementsByTagName("lineNum").item(0).getTextContent());
  }

  private Document extractDocument(String resultString) {
    DocumentBuilder docBuilder = null;
    try {
      docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch (ParserConfigurationException e) {
     fail();
      e.printStackTrace();
    }

    Document document = null;
    try {
      document = docBuilder.parse(new ByteArrayInputStream(resultString.getBytes()));
    } catch (SAXException e) {
      e.printStackTrace();
     fail();
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
    return document;
  }

  private XMLFormatter createXMLFormatter() {
    XMLFormatter formatter = null;
    try {
      formatter = new XMLFormatter();
    } catch (DocumentValidatorException e) {
      fail();
      e.printStackTrace();
    }
    return formatter;
  }

}
