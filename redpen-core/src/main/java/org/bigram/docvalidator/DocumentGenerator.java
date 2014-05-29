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
package org.bigram.docvalidator;

import org.bigram.docvalidator.config.Configuration;
import org.bigram.docvalidator.parser.Parser;
import org.bigram.docvalidator.parser.DocumentParserFactory;
import org.bigram.docvalidator.model.DocumentCollection;

/**
 * Generate DocumentCollection object loading input file.
 */
public final class DocumentGenerator {
  /**
   * Generate DocumentCollection from input file.
   *
   * @param inputFileNames input file name
   * @param configuration       configuration configuration
   * @param format         input file format
   * @return a generated DocumentCollection object
   */
  static DocumentCollection generate(String[] inputFileNames,
                           Configuration configuration,
                           Parser.Type format) throws DocumentValidatorException {
    DocumentCollection.Builder documentBuilder =
        new DocumentCollection.Builder();
    Parser parser = DocumentParserFactory.generate(format,
          configuration, documentBuilder);

    for (String inputFileName : inputFileNames) {
        parser.generateDocument(inputFileName);
    }
    // @TODO extract summary information to validate documentCollection effectively
    return documentBuilder.build();
  }

  private DocumentGenerator() {
    super();
  }
}
