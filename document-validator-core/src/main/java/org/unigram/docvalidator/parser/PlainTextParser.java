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
package org.unigram.docvalidator.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.store.FileContent;
import org.unigram.docvalidator.store.Paragraph;
import org.unigram.docvalidator.store.Section;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.DocumentValidatorException;

/**
 * Parser for plain text file.
 */
public final class PlainTextParser extends BasicDocumentParser {
  /**
   * Constructor.
   */
  PlainTextParser() {
    super();
  }

  public FileContent generateDocument(String fileName)
      throws DocumentValidatorException {
    InputStream iStream = this.loadStream(fileName);
    FileContent content = this.generateDocument(iStream);
    content.setFileName(fileName);
    return content;
  }

  public FileContent generateDocument(InputStream is) {
    BufferedReader br = createReader(is);
    FileContent fileContent = new FileContent();
    List<Sentence> headers = new ArrayList<Sentence>();
    headers.add(new Sentence("", 0));

    fileContent.appendSection(new Section(0, headers));
    Section currentSection = fileContent.getLastSection();
    currentSection.appendParagraph(new Paragraph());
    try {
      String remain = "";
      String line;
      int lineNum = 0;
      while ((line = br.readLine()) != null) {
        int periodPosition =
            this.getSentenceExtractor().getSentenceEndPosition(line);
        if (line.equals("")) {
          currentSection.appendParagraph(new Paragraph());
        } else if (periodPosition == -1) {
          remain = remain + line;
        } else {
          remain =
              this.extractSentences(lineNum, remain + line, currentSection);
        }
        lineNum++;
      }
      if (remain.length() > 0) {
        currentSection.appendSentence(remain, lineNum);
      }
    } catch (IOException e) {
      LOG.error("Failed to parse: " + e.getMessage());
      return null;
    }
    return fileContent;
  }

  private String extractSentences(int lineNum, String line,
      Section currentSection) {
    int periodPosition = getSentenceExtractor().getSentenceEndPosition(line);
    if (periodPosition == -1) {
      return line;
    } else {
      while (true) {
        currentSection.appendSentence(
            line.substring(0, periodPosition + 1), lineNum);
        line = line.substring(periodPosition + 1, line.length());
        periodPosition = getSentenceExtractor().getSentenceEndPosition(line);
        if (periodPosition == -1) {
          return line;
        }
      }
    }
  }

  private static final Logger LOG =
      LoggerFactory.getLogger(PlainTextParser.class);
}
