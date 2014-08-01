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
package cc.redpen.util;

import java.util.HashSet;
import java.util.Set;

/**
 * WordListExtractor extracts word from a given line. This class is called from
 * FileLoader.
 */
public class WordListExtractor implements ResourceExtractor<Set<String>> {

  /**
   * Constructor.
   */
  public WordListExtractor() {
    super();
    wordList = new HashSet<>();
  }

  /**
   * Load word list file.
   *
   * @param line line in a file
   * @return 0 when succeeded.
   */
  @Override
  public int load(String line) {
    if (this.toLowerCase) {
      line = line.toLowerCase();
    }
    wordList.add(line);
    return 0;
  }

  /**
   * Get word list.
   *
   * @return word list
   */
  @Override
  public Set<String> get() {
    return wordList;
  }

  /**
   * Add inputs after convert the character to lowercase.
   */
  public void setToLowerCase() {
    this.toLowerCase = true;
  }

  private final Set<String> wordList;

  private boolean toLowerCase = false;

}
