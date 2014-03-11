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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.parser.Parser;
import org.unigram.docvalidator.parser.DocumentParserFactory;
import org.unigram.docvalidator.store.Document;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.DocumentValidatorException;

/**
 * Generate Document object loading input file.
 */
public final class DocumentGenerator {
  /**
   * Generate Document from input file.
   *
   * @param inputFileNames input file name
   * @param resource       configuration resource
   * @param format         input file format
   * @return a generated Document object
   */
  static Document generate(String[] inputFileNames,
                           DVResource resource,
                           Parser.Type format) {
    Parser docparser;
    try {
      docparser = DocumentParserFactory.generate(format, resource);
    } catch (DocumentValidatorException e) {
      LOG.error("Failed to create document parser: " + e.getMessage());
      return null;
    }

    Document document = new Document();
    for (String inputFileName : inputFileNames) {
      try {
        document.appendFile(docparser.generateDocument(inputFileName));
      } catch (DocumentValidatorException e) {
        e.printStackTrace();
        return null;
      }
    }
    // @TODO extract summary information to validate document effectively
    return document;
  }

  private static final Logger LOG =
      LoggerFactory.getLogger(DocumentGenerator.class);

  private DocumentGenerator() {
    super();
  }
}
