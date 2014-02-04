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
                           String format) {
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

  private static final Logger LOG = LoggerFactory.getLogger(DocumentGenerator.class);

  private DocumentGenerator() {
    super();
  }
}
