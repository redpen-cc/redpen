package org.unigram.docvalidator.parser;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.unigram.docvalidator.ConfigurationLoader;
import org.unigram.docvalidator.store.FileContent;
import org.unigram.docvalidator.store.Paragraph;
import org.unigram.docvalidator.store.Section;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.DocumentValidatorException;

public class PlainTextParserTest {

  private Parser parser = null;
  private DVResource resource;

  private Vector<Paragraph> extractParagraphs(Section section) {
    Iterator<Paragraph> paragraph = section.getParagraph();
    Vector<Paragraph> paragraphs = new Vector<Paragraph>();
    while(paragraph.hasNext()) {
      Paragraph p = paragraph.next();
      paragraphs.add(p);
    }
    return paragraphs;
  }

  private int calcLineNum(Section section) {
    Iterator<Paragraph> paragraph = section.getParagraph();
    int lineNum = 0;
    while(paragraph.hasNext()) {
      Paragraph p = paragraph.next();
      lineNum += p.getNumverOfSentences();
    }
    return lineNum;
  }

  private FileContent generateDocument(String sampleText) {
    InputStream is = null;
    try {
      is = new ByteArrayInputStream(sampleText.getBytes("utf-8"));
    } catch (UnsupportedEncodingException e1) {
      fail();
    }
    FileContent doc = null;
    try {
      doc = parser.generateDocument(is);
    } catch (DocumentValidatorException e) {
      fail();
    }
    return doc;
  }

  private String sampleConfiguraitonStr = new String(
      "<?xml version=\"1.0\"?>" +
      "<configuration name=\"Validator\">" +
      "  <component name=\"SentenceIterator\">" +
      "    <component name=\"LineLength\">"+
      "      <property name=\"max_length\" value=\"10\"/>" +
      "    </component>" +
      "  </component>" +
      "</configuration>");

  @Before
  public void setup() {
    ConfigurationLoader loader = new ConfigurationLoader();
    InputStream stream = IOUtils.toInputStream(this.sampleConfiguraitonStr);
    this.resource = new DVResource(loader.loadConfiguraiton(stream));
    if (this.resource == null) {
      fail();
    }
    try {
      parser = DocumentParserFactory.generate("t", resource);
    } catch (DocumentValidatorException e1) {
      fail();
      e1.printStackTrace();
    }
  }

  @Test
  public void testGenerateDocument() {
    String sampleText = "";
    sampleText += "This is a pen.\n";
    sampleText += "That is a orange.\n";
    sampleText += "\n";
    sampleText += "However, pen is not oranges.\n";
    sampleText += "We need to be peisient.\n";
    sampleText += "\n";
    sampleText += "Happ life.\n";
    sampleText += "Happy home.\n";
    sampleText += "Tama Home.\n";
    FileContent doc = generateDocument(sampleText);
    Section section = doc.getLastSection();
    assertEquals(7 ,calcLineNum(section));
    assertEquals(3, extractParagraphs(section).size());
  }

  @Test
  public void testGenerateDocumentWithMultipleSentenceInOneLine() {
    String sampleText = "Tokyu is a good railway company. ";
    sampleText += "The company is reliable. In addition it is rich. ";
    sampleText += "I like the company. Howerver someone does not like it.";
    String[] expectedResult = {"Tokyu is a good railway company.",
          " The company is reliable.", " In addition it is rich.",
          " I like the company.", " Howerver someone does not like it."};
    FileContent doc = generateDocument(sampleText);
    Section section = doc.getLastSection();
    Vector<Paragraph> paragraphs = extractParagraphs(section);
    assertEquals(1, paragraphs.size());
    assertEquals(5 ,calcLineNum(section));
    Paragraph paragraph = paragraphs.lastElement();
    for (int i=0; i<expectedResult.length; i++) {
      assertEquals(expectedResult[i], paragraph.getLine(i).content);
    }
  }

  @Test
  public void testGenerateDocumentWithNoContent() {
    String sampleText = "";
    FileContent doc = generateDocument(sampleText);
    Section section = doc.getLastSection();
    Vector<Paragraph> paragraphs = extractParagraphs(section);
    assertEquals(1, paragraphs.size());
    assertEquals(0 ,calcLineNum(section));
  }
}
