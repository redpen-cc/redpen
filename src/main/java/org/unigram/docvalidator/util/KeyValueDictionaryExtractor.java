package org.unigram.docvalidator.util;

import java.util.HashMap;
import java.util.Map;

/**
 * An ResourceExtractor implementation for KeyValue input data.
 */
public class KeyValueDictionaryExtractor implements ResourceExtractor {

  public KeyValueDictionaryExtractor() {
    super();
    map = new HashMap<String, String>();
  }

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

  private Map<String, String> map;
}
