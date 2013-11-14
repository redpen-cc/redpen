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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.unigram.docvalidator.ConfigurationLoader;
import org.unigram.docvalidator.store.FileContent;
import org.unigram.docvalidator.store.Paragraph;
import org.unigram.docvalidator.store.Section;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.Configuration;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.DocumentValidatorException;

public class WikiParserTest {

  @Before
  public void setup() {
  }

  @Test
  public void testBasicDocument() throws UnsupportedEncodingException {
    String sampleText = "";
    sampleText += "h1. About Gekioko.\n";
    sampleText += "Gekioko pun pun maru means very very angry.\n";
    sampleText += "\n";
    sampleText += "The word also have posive meaning.\n";
    sampleText += "h1. About Gunma \n";
    sampleText += "\n";
    sampleText += "Gunma is located at west of Saitama.\n";
    sampleText += "- Features\n";
    sampleText += "-- Main City: Gumma City\n";
    sampleText += "-- Capical: 200 Millon\n";
    sampleText += "- Location\n";
    sampleText += "-- Japan\n";
    sampleText += "\n";
    sampleText += "The word also have posive meaning. Hower it is a bit wired.";

    FileContent doc = createFileContent(sampleText);
    assertEquals(3, doc.getSizeOfSections());
    Section lastSection = doc.getSection(doc.getSizeOfSections()-1);
    assertEquals(1, lastSection.getSizeofLists());
    assertEquals(5, lastSection.getLastListBlock().getNumberOfListElements());
    assertEquals(2,lastSection.getParagraphNumber());
  }

  @Test
  public void testGenerateDocumentWithList() {
    String sampleText =
        "Threre are several railway companies in Japan as follows.\n";
    sampleText += "- Tokyu\n";
    sampleText += "-- Toyoko Line\n";
    sampleText += "-- Denentoshi Line\n";
    sampleText += "- Keio\n";
    sampleText += "- Odakyu\n";
    FileContent doc = createFileContent(sampleText);
    assertEquals(5, doc.getLastSection().getLastListBlock().getNumberOfListElements());
  }

  @Test
  public void testGenerateDocumentWithNumberedList() {
    String sampleText =
        "Threre are several railway companies in Japan as follows.\n";
    sampleText += "# Tokyu\n";
    sampleText += "## Toyoko Line\n";
    sampleText += "## Denentoshi Line\n";
    sampleText += "# Keio\n";
    sampleText += "# Odakyu\n";
    FileContent doc = createFileContent(sampleText);
    assertEquals(5, doc.getLastSection().getLastListBlock().getNumberOfListElements());
  }

  @Test
  public void testGenerateDocumentWithMultipleSentenceInOneSentence() {
    String sampleText =
        "Tokyu is a good railway company. The company is reliable. In addition it is rich.";
    String[] expectedResult = {"Tokyu is a good railway company.",
        " The company is reliable.", " In addition it is rich."};
    FileContent doc = createFileContent(sampleText);
    Section firstSections = doc.getSections().next();
    Paragraph firstParagraph = firstSections.getParagraph().next();
    assertEquals(3, firstParagraph.getNumverOfSentences());
    for (int i=0; i<expectedResult.length; i++) {
      assertEquals(expectedResult[i], firstParagraph.getLine(i).content);
    }
  }

  @Test
  public void testGenerateDocumentWithMultipleSentenceInMultipleSentences() {
    String sampleText = "Tokyu is a good railway company. The company is reliable. In addition it is rich.\n";
    sampleText += "I like the company. Howerver someone does not like it.";
    FileContent doc = createFileContent(sampleText);
    Section firstSections = doc.getSections().next();
    Paragraph firstParagraph = firstSections.getParagraph().next();
    assertEquals(5, firstParagraph.getNumverOfSentences());
  }

  @Test
  public void testGenerateDocumentWitVoidContent() {
    String sampleText = "";
    FileContent doc = createFileContent(sampleText);
    Section firstSections = doc.getSections().next();
    assertEquals(false, firstSections.getParagraph().hasNext());
  }

  @Test
  public void testGenerateDocumentWithPeriodInSuccession() {
    String sampleText = "...";
    FileContent doc = createFileContent(sampleText);
    Section firstSections = doc.getSections().next();
    Paragraph firstParagraph = firstSections.getParagraph().next();
    assertEquals(1, firstParagraph.getNumverOfSentences());
  }

  @Test
  public void testGenerateDocumentWitoutPeriodInLastSentence() {
    String sampleText = "Hongo is located at the west of Tokyo. Saitama is located at the north";
    FileContent doc = createFileContent(sampleText);
    Section firstSections = doc.getSections().next();
    Paragraph firstParagraph = firstSections.getParagraph().next();
    assertEquals(2, firstParagraph.getNumverOfSentences());
  }

  @Test
  public void testGenerateDocumentWithSentenceLongerThanOneLine() {
    String sampleText = "This is a good day.\n";
    sampleText += "Hongo is located at the west of Tokyo ";
    sampleText += "which is the capital of Japan ";
    sampleText += "which is not located in the south of the earth.";
    FileContent doc = createFileContent(sampleText);
    Section firstSections = doc.getSections().next();
    Paragraph firstParagraph = firstSections.getParagraph().next();
    assertEquals(2, firstParagraph.getNumverOfSentences());
  }

  @Test
  public void testPlainLink() {
    String sampleText = "this is not a [[pen]], but also this is not [[Google|http://google.com]] either.";
    FileContent doc = createFileContent(sampleText);
    Section firstSections = doc.getSections().next();
    Paragraph firstParagraph = firstSections.getParagraph().next();
    assertEquals(1, firstParagraph.getNumverOfSentences());
    assertEquals(2, firstParagraph.getLine(0).links.size());
    assertEquals("pen", firstParagraph.getLine(0).links.get(0));
    assertEquals("Google", firstParagraph.getLine(0).links.get(1));
    assertEquals("this is not a pen, but also this is not Google either.",
        firstParagraph.getLine(0).content);
  }

  @Test
  public void testPlainLinkWithSpaces() {
    String sampleText = "the url is not [[Google | http://google.com ]].";
    FileContent doc = createFileContent(sampleText);
    Section firstSections = doc.getSections().next();
    Paragraph firstParagraph = firstSections.getParagraph().next();
    assertEquals(1, firstParagraph.getNumverOfSentences());
    assertEquals(1, firstParagraph.getLine(0).links.size());
    assertEquals("Google", firstParagraph.getLine(0).links.get(0));
    assertEquals("the url is not Google.",
        firstParagraph.getLine(0).content);
  }

  @Test
  public void testLinkWithoutTag() {
    String sampleText = "url of google is [[http://google.com]].";
    FileContent doc = createFileContent(sampleText);
    Section firstSections = doc.getSections().next();
    Paragraph firstParagraph = firstSections.getParagraph().next();
    assertEquals(1, firstParagraph.getNumverOfSentences());
    assertEquals(1, firstParagraph.getLine(0).links.size());
    assertEquals("http://google.com", firstParagraph.getLine(0).links.get(0));
    assertEquals("url of google is http://google.com.",
        firstParagraph.getLine(0).content);
  }

  @Test
  public void testIncompleteLink() {
    String sampleText = "url of google is [[http://google.com.";
    FileContent doc = createFileContent(sampleText);
    Section firstSections = doc.getSections().next();
    Paragraph firstParagraph = firstSections.getParagraph().next();
    assertEquals(1, firstParagraph.getNumverOfSentences());
    assertEquals(0, firstParagraph.getLine(0).links.size());
    assertEquals("url of google is [[http://google.com.",
        firstParagraph.getLine(0).content);
  }

  @Test
  public void testDocumentWithSections() throws UnsupportedEncodingException {
    String sampleText = "h1. Prefectures in Japan.\n";
    sampleText += "There are 47 prefectures in Japan.\n";
    sampleText += "\n";
    sampleText += "Each prefectures has its features.\n";
    sampleText += "h2. Gunma \n";
    sampleText += "Gumma is very beautiful";

    FileContent doc = createFileContent(sampleText);
    assertEquals(3, doc.getSizeOfSections());
    Section rootSection = doc.getSection(0);
    Section h1Section = doc.getSection(1);
    Section h2Section = doc.getSection(2);

    assertEquals(0, rootSection.getLevel());
    assertEquals(1, h1Section.getLevel());
    assertEquals(2, h2Section.getLevel());

    assertEquals(rootSection.getSubSection(0), h1Section);
    assertEquals(h1Section.getParent(), rootSection);
    assertEquals(h2Section.getParent(), h1Section);
    assertEquals(rootSection.getParent(), null);
  }

  @Test
  public void testGenerateJapaneseDocument() {
    String japaneseConfiguraitonStr = new String(
        "<?xml version=\"1.0\"?>" +
        "<component name=\"Validator\">" +
        "</component>");

    String japaneseCharTableStr = new String(
        "<?xml version=\"1.0\"?>" +
        "<character-table>" +
         "<character name=\"FULL_STOP\" value=\"。\" />" +
        "</character-table>");

    String sampleText = "埼玉は東京の北に存在する。";
    sampleText += "大きなベッドタウンであり、多くの人が住んでいる。";
    FileContent doc = null;

    try {
      doc = createFileContent(sampleText, japaneseConfiguraitonStr,
          japaneseCharTableStr);
    } catch (DocumentValidatorException e1) {
      e1.printStackTrace();
      fail();
    }

    Section firstSections = doc.getSections().next();
    Paragraph firstParagraph = firstSections.getParagraph().next();
    assertEquals(2, firstParagraph.getNumverOfSentences());
  }

  private Parser loadParser(DVResource resource) {
    Parser parser = null;
    try {
      parser = DocumentParserFactory.generate("w", resource);
    } catch (DocumentValidatorException e1) {
      fail();
      e1.printStackTrace();
    }
    return parser;
  }

  private FileContent createFileContent(String inputDocumentString,
      String configurationString,
      String characterTableString) throws DocumentValidatorException {
    InputStream configStream = IOUtils.toInputStream(configurationString);
    ConfigurationLoader loader = new ConfigurationLoader();
    Configuration conf = loader.loadConfiguraiton(configStream);

    CharacterTable characterTable = null;
    if (characterTableString.length() > 0) {
      InputStream characterTableStream =
          IOUtils.toInputStream(characterTableString);
      characterTable = new CharacterTable(characterTableStream);
    }
    return createFileContent(inputDocumentString, conf, characterTable);
  }

  private FileContent createFileContent(String inputDocumentString,
      Configuration conf,
      CharacterTable characterTable) {
    InputStream inputDocumentStream = null;
    try {
      inputDocumentStream =
          new ByteArrayInputStream(inputDocumentString.getBytes("utf-8"));
    } catch (UnsupportedEncodingException e1) {
      fail();
    }

    Parser parser = null;
    if (characterTable != null) {
      parser = loadParser(new DVResource(conf, characterTable));
    } else {
      parser = loadParser(new DVResource(conf));
    }

    try {
      return parser.generateDocument(inputDocumentStream);
    } catch (DocumentValidatorException e) {
      e.printStackTrace();
      return null;
    }
  }

  private FileContent createFileContent(
      String inputDocumentString) {
    Configuration conf = new Configuration("dummy");
    Parser parser = loadParser(new DVResource(conf));
    InputStream is;
    try {
      is = new ByteArrayInputStream(inputDocumentString.getBytes("utf-8"));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return null;
    }
    FileContent doc = null;
    try {
      doc = parser.generateDocument(is);
    } catch (DocumentValidatorException e) {
      e.printStackTrace();
    }
    return doc;
  }

}
