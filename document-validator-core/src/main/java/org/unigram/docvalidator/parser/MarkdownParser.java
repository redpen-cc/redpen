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

import org.apache.commons.io.IOUtils;
import org.pegdown.Extensions;
import org.pegdown.ParsingTimeoutException;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.RootNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.parser.markdown.ToFileContentSerializer;
import org.unigram.docvalidator.store.FileContent;
import org.unigram.docvalidator.store.Section;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.DocumentValidatorException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser for Markdown format.<br/>
 * <p/>
 * Markdown Syntax @see http://daringfireball.net/projects/markdown/
 */
public class MarkdownParser extends BasicDocumentParser {

  private PegDownProcessor pegDownProcessor = new PegDownProcessor(
      Extensions.HARDWRAPS
          + Extensions.AUTOLINKS
          + Extensions.FENCED_CODE_BLOCKS);


  @Override
  public FileContent generateDocument(String fileName)
      throws DocumentValidatorException {
    InputStream inputStream = this.loadStream(fileName);
    FileContent fileContent = this.generateDocument(inputStream);
    if (fileContent != null) {
      fileContent.setFileName(fileName);
    }
    return fileContent;
  }

  @Override
  public FileContent generateDocument(InputStream inputStream)
      throws DocumentValidatorException {
    assert inputStream != null;
    BufferedReader br = createReader(inputStream);
    //TODO similar process in WikiParser, not exist PlainTextParser
    if (br == null) {
      LOG.error("Failed to create reader");
      return null;
    }
    FileContent fileContent = null;

    StringBuilder sb = new StringBuilder();
    String line = null;
    int charCount = 0;
    List<Integer> lineList = new ArrayList<Integer>();

    try {
      while ((line = br.readLine()) != null) {
        sb.append(line);
        sb.append("\n");
        // TODO surrogate pair ?
        charCount += line.length() + 1;
        lineList.add(charCount);
      }

      fileContent = new FileContent();
      List<Sentence> headers = new ArrayList<Sentence>();
      headers.add(new Sentence("", 0));
      Section currentSection = new Section(0, headers);
      fileContent.appendSection(currentSection);

      // TODO create fileContent after parsing... overhead...
      RootNode rootNode =
          pegDownProcessor.parseMarkdown(sb.toString().toCharArray());
      ToFileContentSerializer serializer =
          new ToFileContentSerializer(fileContent, lineList, this.period);
      fileContent = serializer.toFileContent(rootNode);

    } catch (ParsingTimeoutException e) {
      LOG.error("Failed to parse timeout");
      return null;
    } catch (IOException e) {
      LOG.error("Failed to read lines");
      return null;
    } finally {
      IOUtils.closeQuietly(br);
    }


    return fileContent;
  }


  private static final Logger LOG =
      LoggerFactory.getLogger(MarkdownParser.class);

}
