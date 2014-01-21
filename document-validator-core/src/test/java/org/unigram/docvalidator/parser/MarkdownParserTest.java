/*
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

import org.junit.Before;
import org.junit.Test;
import org.unigram.docvalidator.store.FileContent;
import org.unigram.docvalidator.store.Section;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.util.ValidatorConfiguration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;

public class MarkdownParserTest {

  @Before
  public void setup(){
  }

  @Test
  public void testNullDocument() {
    try{
      FileContent doc = createFileContentFromInputStream(null);
      fail("no error");
    }catch(AssertionError as){

    }
  }

  @Test
  public void testBasicDocument() throws UnsupportedEncodingException {
    String sampleText = "";
    sampleText += "# About Gekioko.\n";
    sampleText += "Gekioko pun pun maru means very very angry.\n";
    sampleText += "\n";
    sampleText += "The word also have posive meaning.\n";
    sampleText += "## About Gunma.\n";
    sampleText += "\n";
    sampleText += "Gunma is located at west of Saitama.\n";
    sampleText += "\n";
    sampleText += "* Features\n";
    sampleText += "  * Main City: Gumma City\n";
    sampleText += "  * Capical: 200 Millon\n";
    sampleText += "* Location\n";
    sampleText += "  * Japan\n";
    sampleText += "\n";
    sampleText += "The word also have posive meaning. Hower it is a bit wired.";

    FileContent doc = createFileContent(sampleText);
    assertNotNull("doc is null", doc);
    assertEquals(3, doc.getNumberOfSections());
    Section lastSection = doc.getSection(doc.getNumberOfSections()-1);
    assertEquals(1, lastSection.getNumberOfLists());
    assertEquals(5, lastSection.getListBlock(0).getNumberOfListElements());
    assertEquals(3,lastSection.getNumberOfParagraphs());
    assertEquals(1, lastSection.getHeaderContentsListSize());
    assertEquals("About Gunma.", lastSection.getHeaderContent(0).content);
  }


  private Parser loadParser(DVResource resource) {
    Parser parser = null;
    try {
      parser = DocumentParserFactory.generate("markdown", resource);
    } catch (DocumentValidatorException e1) {
      fail();
      e1.printStackTrace();
    }
    return parser;
  }

  private FileContent createFileContentFromInputStream(InputStream inputStream) {
    ValidatorConfiguration conf = new ValidatorConfiguration("dummy");
    Parser parser = loadParser(new DVResource(conf));
    FileContent doc = null;
    try {
      parser.generateDocument(inputStream);
    } catch (DocumentValidatorException e) {
      e.printStackTrace();
      fail();
    }
    return doc;
  }

  private FileContent createFileContent(
      String inputDocumentString) {
    ValidatorConfiguration conf = new ValidatorConfiguration("dummy");
    Parser parser = loadParser(new DVResource(conf));
    InputStream is;
    try {
      is = new ByteArrayInputStream(inputDocumentString.getBytes("utf-8"));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      fail();
      return null;
    }
    FileContent doc = null;
    try {
      doc = parser.generateDocument(is);
    } catch (DocumentValidatorException e) {
      e.printStackTrace();
      fail();
    }
    return doc;
  }


}
