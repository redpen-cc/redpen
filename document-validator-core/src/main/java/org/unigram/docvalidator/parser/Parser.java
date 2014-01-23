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
package org.unigram.docvalidator.parser;

import java.io.InputStream;

import org.unigram.docvalidator.store.FileContent;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.DocumentValidatorException;

/**
 * Parser generates FileContent from input.
 */
public interface Parser {
  /**
   * Given input stream, return FileContent instance from a stream.
   *
   * @param io input stream containing input content
   * @return a generated file content
   * @throws DocumentValidatorException if Parser failed to parse input.
   */
  FileContent generateDocument(InputStream io)
      throws DocumentValidatorException;

  /**
   * Given input file name, return FileContent instance for the specified file.
   *
   * @param fileName input file name
   * @return a generated file content
   * @throws DocumentValidatorException if Parser failed to parse input.
   */
  FileContent generateDocument(String fileName)
      throws DocumentValidatorException;

  /**
   * Initialize parser.
   *
   * @param resource configuration resources
   * @return true if the configurations are loaded, false otherwise
   */
  boolean initialize(DVResource resource);
}
