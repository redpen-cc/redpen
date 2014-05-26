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

import org.bigram.docvalidator.DocumentValidatorException;
import org.bigram.docvalidator.config.Configuration;
import org.bigram.docvalidator.model.DocumentCollection;

/**
 * Factory class of DocumentParser.
 */
public final class DocumentParserFactory {
  /**
   * Create DocumentParser object following specified input file type.
   *
   *
   *
   * @param parserType type of parser (plain or wiki etc.)
   * @param configuration   configuration settings
   * @param documentBuilder Builder object of DocumentCollection
   * @return Parser implementation object
   * @throws org.bigram.docvalidator.DocumentValidatorException when failed to generate Parser instance
   *                                    or no specified parser implementation.
   */
  public static Parser generate(Parser.Type parserType,
      Configuration configuration,
      DocumentCollection.Builder documentBuilder)
      throws DocumentValidatorException {
    Parser docparser;
    switch (parserType) {
      case PLAIN:
        docparser = new PlainTextParser();
        break;
      case WIKI:
        docparser = new WikiParser();
        break;
      case MARKDOWN:
        docparser = new MarkdownParser();
        break;
      default:
        throw new DocumentValidatorException("Specified parser type not exist: "
            + parserType);
    }
    docparser.initialize(configuration, documentBuilder);
    return docparser;
  }

  private DocumentParserFactory() {
    super();
  }
}
