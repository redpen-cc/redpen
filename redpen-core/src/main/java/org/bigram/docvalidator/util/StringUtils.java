package org.bigram.docvalidator.util;

public class StringUtils {
  public static boolean isKatakana(char c) {
    return java.lang.Character.UnicodeBlock.of(c) == java.lang.Character.UnicodeBlock.KATAKANA;
  }

  public static boolean isBasicLatin(char c) {
    return java.lang.Character.UnicodeBlock.of(c) == java.lang.Character.UnicodeBlock.BASIC_LATIN;
  }
}
