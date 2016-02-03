package cc.redpen.util;

import static cc.redpen.util.StringUtils.isProbablyJapanese;
import static java.lang.Math.min;

public class LanguageDetector {
  public String detectLanguage(String text) {
    if (!hasJapaneseCharacters(text)) return "en";

    boolean zenkaku = text.indexOf('。') >= 0 || text.indexOf('、') >= 0 || text.indexOf('！') >= 0 || text.indexOf('？') >= 0;
    boolean zenkaku2 = text.indexOf('．') >= 0 || text.indexOf('，') >= 0;
    boolean hankaku = text.indexOf('.') >= 0 || text.indexOf(',') >= 0 || text.indexOf('!') >= 0 || text.indexOf('?') >= 0;

    return zenkaku ? "ja" :
           zenkaku2 ? "ja.zenkaku2" :
           hankaku ? "ja.hankaku":
           "ja";
  }

  private boolean hasJapaneseCharacters(String text) {
    char[] chars = text.toCharArray();
    for (int i = 0; i < min(chars.length, 100); i++) {
      char c = chars[i];
      if (isProbablyJapanese(c)) return true;
    }
    return false;
  }
}
