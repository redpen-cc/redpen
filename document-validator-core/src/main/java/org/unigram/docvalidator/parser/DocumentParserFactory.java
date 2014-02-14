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

import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.DocumentValidatorException;

/**
 * Factory class of DocumentParser.
 */
public final class DocumentParserFactory {
  /**
   * Create DocumentParser object following specified input file type.
   *
   * @param parserType type of parser (plain or wiki etc.)
   * @param resource   configuration settings
   * @return Parser implementation object
   * @throws DocumentValidatorException when failed to generate Parser instance
   *                                    or no specified parser implementation.
   */
  public static Parser generate(String parserType, DVResource resource)
      throws DocumentValidatorException {
    Parser docparser;
    if (parserType.equals("wiki")) {
      docparser = new WikiParser();
    } else if (parserType.equals("plain")) {
      docparser = new PlainTextParser();
    } else if (parserType.equals("markdown")) {
      docparser = new MarkdownParser();
    } else {
      throw new DocumentValidatorException("Specified parser type not exist: "
          + parserType);
    }
    docparser.initialize(resource);
    return docparser;
  }

  private DocumentParserFactory() {
    super();
  }
}
