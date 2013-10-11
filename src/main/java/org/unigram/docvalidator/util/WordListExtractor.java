package org.unigram.docvalidator.util;

import java.util.HashSet;
import java.util.Set;

/**
 * WordListExtractor extracts word from a given line. This class is called from
 * FileLoader.
 */
public class WordListExtractor implements ResourceExtractor {

  public WordListExtractor() {
    super();
    wordList = new HashSet<String>();
  }

  public int load(String line) {
    wordList.add(line);
    return 0;
  }

  public Set<String> get() {
    return wordList;
  }

  private Set<String> wordList;
}
