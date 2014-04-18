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
package org.unigram.docvalidator;

import org.junit.Test;
import org.unigram.docvalidator.model.DocumentCollection;

import static org.junit.Assert.*;
import static org.unigram.docvalidator.parser.Parser.Type.*;

public class SampleDocumentGeneratorTest {
  @Test
  public void testGenerateSimplePlainDocument() throws DocumentValidatorException {
    String sampleText = "";
    sampleText += "Gekioko pun pun maru means very very angry.\n";
    DocumentCollection doc = SampleDocumentGenerator.generateOneFileDocument(sampleText,
        WIKI);
    assertNotNull(doc);
    assertEquals(1, doc.size());
    assertEquals(1, doc.getFile(0).getNumberOfSections());
    assertEquals(1, doc.getFile(0).getSection(0).getNumberOfParagraphs());
    assertEquals(1, doc.getFile(0).getSection(0).getParagraph(0)
        .getNumberOfSentences());
    assertEquals("Gekioko pun pun maru means very very angry.", doc.getFile(0).getSection(0)
        .getParagraph(0).getSentence(0).content);
  }

  @Test
  public void testGenerateSimpleWikiDocument() throws DocumentValidatorException {
    String sampleText = "";
    sampleText += "h1. About Gekioko.\n";
    sampleText += "Gekioko pun pun maru means very very angry.\n";
    DocumentCollection doc = SampleDocumentGenerator.generateOneFileDocument(sampleText,
        WIKI);
    assertNotNull(doc);
    assertEquals(1, doc.size());
    assertEquals(2, doc.getFile(0).getNumberOfSections());
    assertEquals("About Gekioko.", doc.getFile(0).getSection(1).getHeaderContent(0).content);
    assertEquals(1, doc.getFile(0).getSection(1).getNumberOfParagraphs());
    assertEquals(1, doc.getFile(0).getSection(1).getParagraph(0)
        .getNumberOfSentences());
    assertEquals("Gekioko pun pun maru means very very angry.", doc.getFile(0).getSection(1)
        .getParagraph(0).getSentence(0).content);
  }

  @Test
  public void testGenerateSimpleMarkdownDocument() throws DocumentValidatorException {
    String sampleText = "";
    sampleText += "# About Gekioko.\n";
    sampleText += "Gekioko pun pun maru means very very angry.\n";
    DocumentCollection doc = SampleDocumentGenerator.generateOneFileDocument(sampleText,
        MARKDOWN);
    assertNotNull(doc);
    assertEquals(1, doc.size());
    assertEquals(2, doc.getFile(0).getNumberOfSections());
    assertEquals("About Gekioko.", doc.getFile(0).getSection(1).getHeaderContent(0).content);
    assertEquals(1, doc.getFile(0).getSection(1).getNumberOfParagraphs());
    assertEquals(1, doc.getFile(0).getSection(1).getParagraph(0)
        .getNumberOfSentences());
    assertEquals("Gekioko pun pun maru means very very angry.", doc.getFile(0).getSection(1)
        .getParagraph(0).getSentence(0).content);
  }

  @Test(expected=NullPointerException.class)
  public void testInputNullDocument() throws DocumentValidatorException {
    SampleDocumentGenerator.generateOneFileDocument(null, MARKDOWN);
  }
}
