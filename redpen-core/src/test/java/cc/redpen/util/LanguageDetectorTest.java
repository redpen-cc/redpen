package cc.redpen.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LanguageDetectorTest {
  private LanguageDetector detector = new LanguageDetector();

  @Test
  void englishIsDefault() throws Exception {
    assertEquals("en", detector.detectLanguage("Hello there!"));
  }

  @Test
  void russianUsesCyrillicLetters() throws Exception {
    assertEquals("ru", detector.detectLanguage("Привет, Mary!"));
  }

  @Test
  void KoreanUsesHangul() throws Exception {
    assertEquals("ko", detector.detectLanguage("안녕하세요!"));
  }

  @Test
  void japaneseIsDetectedForKatakana() throws Exception {
    assertEquals("ja", detector.detectLanguage("コンピューター"));
  }

  @Test
  void japaneseIsDetectedForHiragana() throws Exception {
    assertEquals("ja", detector.detectLanguage("はなぢ"));
  }

  @Test
  void japaneseIsDetectedForKanji() throws Exception {
    assertEquals("ja", detector.detectLanguage("日本"));
    assertEquals("ja", detector.detectLanguage("最近"));
  }

  @Test
  void zenkakuIsDefaultJapanese() throws Exception {
    assertEquals("ja", detector.detectLanguage("最近利用されているソフトウェアの中には"));
  }

  @Test
  void zenkaku2IsDetectedUsingPunctuation() throws Exception {
    assertEquals("ja.zenkaku2", detector.detectLanguage("こんにちは世界．"));
  }

  @Test
  void zenkakuIsSelectedEvenIfSomeZenkaku2SymbolsArePresent() throws Exception {
    assertEquals("ja", detector.detectLanguage("こんにちは世界． こんにちは世界。"));
  }

  @Test
  void hankakuIsSelectedIfAsciiPunctuationIsUsedWithJapaneseSymbols() throws Exception {
    assertEquals("ja.hankaku", detector.detectLanguage("こんにちは世界!"));
  }
}