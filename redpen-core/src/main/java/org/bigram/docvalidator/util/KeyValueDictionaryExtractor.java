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
package org.bigram.docvalidator.util;

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
