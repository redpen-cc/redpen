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
package org.unigram.docvalidator.parser;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;

import org.unigram.docvalidator.util.ValidationConfigurationLoader;
import org.unigram.docvalidator.store.FileContent;
import org.unigram.docvalidator.store.Paragraph;
import org.unigram.docvalidator.store.Section;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.DocumentValidatorException;

public class PlainTextParserTest {

  private Parser parser = null;

    private List<Paragraph> extractParagraphs(Section section) {
    Iterator<Paragraph> paragraph = section.getParagraphs();
    List<Paragraph> paragraphs = new ArrayList<Paragraph>();
    while(paragraph.hasNext()) {
      Paragraph p = paragraph.next();
      paragraphs.add(p);
    }
    return paragraphs;
  }

  private int calcLineNum(Section section) {
    Iterator<Paragraph> paragraph = section.getParagraphs();
    int lineNum = 0;
    while(paragraph.hasNext()) {
      Paragraph p = paragraph.next();
      lineNum += p.getNumberOfSentences();
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
      "<component name=\"Validator\">" +
      "  <component name=\"SentenceIterator\">" +
      "    <component name=\"LineLength\">"+
      "      <property name=\"max_length\" value=\"10\"/>" +
      "    </component>" +
      "  </component>" +
      "</component>");

  @Before
  public void setup() {
    InputStream stream = IOUtils.toInputStream(this.sampleConfiguraitonStr);
      DVResource resource = new DVResource(ValidationConfigurationLoader.loadConfiguration(stream));
    if (resource == null) {
      fail();
    }
    try {
      parser = DocumentParserFactory.generate("plain", resource);
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
    List<Paragraph> paragraphs = extractParagraphs(section);
    assertEquals(1, paragraphs.size());
    assertEquals(5 ,calcLineNum(section));
    Paragraph paragraph = paragraphs.get(paragraphs.size()-1);
    for (int i=0; i<expectedResult.length; i++) {
      assertEquals(expectedResult[i], paragraph.getSentence(i).content);
    }
    assertEquals(0, section.getHeaderContent(0).position);
    assertEquals("", section.getHeaderContent(0).content);
  }

  @Test
  public void testGenerateDocumentWithNoContent() {
    String sampleText = "";
    FileContent doc = generateDocument(sampleText);
    Section section = doc.getLastSection();
    List<Paragraph> paragraphs = extractParagraphs(section);
    assertEquals(1, paragraphs.size());
    assertEquals(0 ,calcLineNum(section));
  }
}
