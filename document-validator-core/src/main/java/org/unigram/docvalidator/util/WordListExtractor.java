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
package org.unigram.docvalidator.util;

import java.util.HashSet;
import java.util.Set;

/**
 * WordListExtractor extracts word from a given line. This class is called from
 * FileLoader.
 */
public class WordListExtractor implements ResourceExtractor {

  /**
   * Constructor.
   */
  public WordListExtractor() {
    super();
    wordList = new HashSet<String>();
  }

  /**
   * Load　word list file.
   *
   * @param line line in a file
   * @return 0 when succeeded.
   */
  public int load(String line) {
    wordList.add(line);
    return 0;
  }

  /**
   * Get word list.
   *
   * @return word list
   */
  public Set<String> get() {
    return wordList;
  }

  private final Set<String> wordList;
}
