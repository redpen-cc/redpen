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
package cc.redpen.docvalidator.parser;

import java.io.InputStream;

import cc.redpen.docvalidator.DocumentValidatorException;
import cc.redpen.docvalidator.model.Document;
import cc.redpen.docvalidator.config.Configuration;
import cc.redpen.docvalidator.model.DocumentCollection;

/**
 * Parser generates Document from input.
 */
public interface Parser {
  /**
   * Given input stream, return Document instance from a stream.
   *
   * @param io input stream containing input content
   * @return a generated file content
   * @throws cc.redpen.docvalidator.DocumentValidatorException if Parser failed to parse input.
   */
  Document generateDocument(InputStream io)
      throws DocumentValidatorException;

  /**
   * Given input file name, return Document instance for the specified file.
   *
   * @param fileName input file name
   * @return a generated file content
   * @throws DocumentValidatorException if Parser failed to parse input.
   */
  Document generateDocument(String fileName)
      throws DocumentValidatorException;

  /**
   * Initialize parser.
   *
   *
   * @param configuration configuration
   * @param documentBuilder
   * @throws DocumentValidatorException if the configurations loading failed
   */
  void initialize(Configuration configuration,
      DocumentCollection.Builder documentBuilder) throws DocumentValidatorException;

  /**
   * the type of parser using DocumentParserFactory.
   */
  enum Type {

    /** plain text parser. */
    PLAIN,

    /** wiki parser. */
    WIKI,

    /** markdown parser. */
    MARKDOWN

  }
}
