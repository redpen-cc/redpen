package cc.redpen.util;

import java.util.function.Predicate;

import static java.lang.Math.min;

public class LanguageDetector {
  public String detectLanguage(String text) {
    if (has(text, StringUtils::isProbablyJapanese)) {
      boolean zenkaku = text.indexOf('。') >= 0 || text.indexOf('、') >= 0 || text.indexOf('！') >= 0 || text.indexOf('？') >= 0;
      boolean zenkaku2 = text.indexOf('．') >= 0 || text.indexOf('，') >= 0;
      boolean hankaku = text.indexOf('.') >= 0 || text.indexOf(',') >= 0 || text.indexOf('!') >= 0 || text.indexOf('?') >= 0;

      return zenkaku ? "ja" :
        zenkaku2 ? "ja.zenkaku2" :
        hankaku ? "ja.hankaku":
        "ja";
    }
    else if (has(text, StringUtils::isCyrillic)) {
      return "ru";
    } else if (has(text, StringUtils::isKorean)) {
      return "ko";
    }

    return "en";
  }

  private boolean has(String text, Predicate<Character> func) {
    char[] chars = text.toCharArray();
    for (int i = 0; i < min(chars.length, 100); i++) {
      char c = chars[i];
      if (func.test(c)) return true;
    }
    return false;
  }
}
