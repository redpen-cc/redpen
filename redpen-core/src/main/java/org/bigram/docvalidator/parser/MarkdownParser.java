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
package org.bigram.docvalidator.parser;

import org.apache.commons.io.IOUtils;
import org.bigram.docvalidator.DocumentValidatorException;
import org.bigram.docvalidator.model.Document;
import org.bigram.docvalidator.model.Section;
import org.bigram.docvalidator.model.Sentence;
import org.pegdown.Extensions;
import org.pegdown.ParsingTimeoutException;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.RootNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.bigram.docvalidator.parser.markdown.ToFileContentSerializer;

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

  MarkdownParser() {
    super();
  }

  @Override
  public Document generateDocument(String fileName)
      throws DocumentValidatorException {
    InputStream inputStream = this.loadStream(fileName);
    Document document = this.generateDocument(inputStream);
    if (document != null) {
      document.setFileName(fileName);
    }
    return document;
  }

  @Override
  public Document generateDocument(InputStream inputStream)
      throws DocumentValidatorException {
    builder.addDocument("");
    Document document = null;

    StringBuilder sb = new StringBuilder();
    String line;
    int charCount = 0;
    List<Integer> lineList = new ArrayList<Integer>();
    BufferedReader br = null;

    try {
      br = createReader(inputStream);
      while ((line = br.readLine()) != null) {
        sb.append(line);
        sb.append("\n");
        // TODO surrogate pair ?
        charCount += line.length() + 1;
        lineList.add(charCount);
      }

      document = builder.getLastDocument();
      List<Sentence> headers = new ArrayList<Sentence>();
      headers.add(new Sentence("", 0));
      Section currentSection = new Section(0, headers);
      document.appendSection(currentSection);

      // TODO create document after parsing... overhead...
      RootNode rootNode =
          pegDownProcessor.parseMarkdown(sb.toString().toCharArray());
      ToFileContentSerializer serializer =
          new ToFileContentSerializer(document,
              lineList, this.getSentenceExtractor());
      document = serializer.toFileContent(rootNode);
    } catch (ParsingTimeoutException e) {
      throw new DocumentValidatorException("Failed to parse timeout");
    } catch (IOException e) {
      throw new DocumentValidatorException("Failed to read lines");
    } finally {
      IOUtils.closeQuietly(br);
    }
    return document;
  }


  private static final Logger LOG =
      LoggerFactory.getLogger(MarkdownParser.class);
}
