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

import org.apache.commons.io.IOUtils;
import org.unigram.docvalidator.parser.DocumentParserFactory;
import org.unigram.docvalidator.parser.Parser;
import org.unigram.docvalidator.store.Document;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.util.ValidatorConfiguration;

import java.io.InputStream;

/**
 * Generate Document objects from String. This class are applied
 * only for testing purpose.
 */
public class SampleDocumentGenerator {
  /**
   * Given a string and the syntax type, build a Document object.
   * This build method is made to write test easily, but this generator
   * class does not supports the configurations if the configurations are
   * needed please use DocumentGenerator class.
   *
   * @param docString input document string
   * @param type document syntax: wiki, markdown or plain
   * @return Document object
   */
  public static Document generateOneFileDocument(String docString,
      String type) throws DocumentValidatorException {
    if (docString == null) {
      throw new DocumentValidatorException("Input string is null");
    }

    Parser parser;
    try {
      DVResource resource = new DVResource(
          new ValidatorConfiguration("dummy"), new CharacterTable());
      parser = DocumentParserFactory.generate(type, resource);
    } catch (DocumentValidatorException e) {
      throw new DocumentValidatorException(
          "Failed to create a parser: " + e.getMessage());
    }

    InputStream stream = IOUtils.toInputStream(docString);
    Document document = new Document();
    try {
      document.appendFile(parser.generateDocument(stream));
    } catch (DocumentValidatorException e) {
      throw new DocumentValidatorException(
          "Failed to parse input document: " + e.getMessage());
    }
    return document;
  }
}
