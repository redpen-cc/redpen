package org.unigram.docvalidator.util;

/**
 * Utility class to handle a string.
 */
public final class StringUtils {
  /**
   * get sentence end position.
   * @param str input string
   * @param period full stop character
   * @return position of full stop when there is a full stop, otherwise -1
   */
  public static int getSentenceEndPosition(String str, String period) {
    return str.indexOf(period);
  }

  private StringUtils() { }

}
