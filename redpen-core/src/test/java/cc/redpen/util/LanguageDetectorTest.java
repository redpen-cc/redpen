package cc.redpen.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LanguageDetectorTest {
  LanguageDetector detector = new LanguageDetector();

  @Test
  public void englishIsDefault() throws Exception {
    assertEquals("en", detector.detectLanguage("Hello there!"));
  }

  @Test
  public void japaneseIsDetectedForKatakana() throws Exception {
    assertEquals("ja", detector.detectLanguage("コンピューター"));
  }

  @Test
  public void japaneseIsDetectedForHiragana() throws Exception {
    assertEquals("ja", detector.detectLanguage("はなぢ"));
  }

  @Test
  public void japaneseIsDetectedForKanji() throws Exception {
    assertEquals("ja", detector.detectLanguage("日本"));
    assertEquals("ja", detector.detectLanguage("最近"));
  }

  @Test
  public void zenkakuIsDefaultJapanese() throws Exception {
    assertEquals("ja", detector.detectLanguage("最近利用されているソフトウェアの中には"));
  }

  @Test
  public void zenkaku2IsDetectedUsingPunctuation() throws Exception {
    assertEquals("ja.zenkaku2", detector.detectLanguage("こんにちは世界．"));
  }

  @Test
  public void zenkakuIsSelectedEvenIfSomeZenkaku2SymbolsArePresent() throws Exception {
    assertEquals("ja", detector.detectLanguage("こんにちは世界． こんにちは世界。"));
  }

  @Test
  public void hankakuIsSelectedIfAsciiPunctuationIsUsedWithJapaneseSymbols() throws Exception {
    assertEquals("ja.hankaku", detector.detectLanguage("こんにちは世界!"));
  }
}