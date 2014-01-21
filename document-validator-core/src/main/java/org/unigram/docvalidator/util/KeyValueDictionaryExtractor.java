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

import java.util.HashMap;
import java.util.Map;

/**
 * An ResourceExtractor implementation for KeyValue input data.
 */
public class KeyValueDictionaryExtractor implements ResourceExtractor {
  /**
   * Constructor.
   */
  public KeyValueDictionaryExtractor() {
    super();
    map = new HashMap<String, String>();
  }

  /**
   * Load input file. The input file TSV with two columns.
   *
   * @param line line in a file
   * @return 0 when succeeded to load, 1 otherwise
   */
  public int load(String line) {
    String[] result = line.split("\t");
    if (result.length != 2) {
      return 1;
    }
    map.put(result[0], result[1]);
    return 0;
  }

  public Map<String, String> get() {
    return map;
  }

  private final Map<String, String> map;
}
