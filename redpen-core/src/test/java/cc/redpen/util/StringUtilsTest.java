package cc.redpen.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringUtilsTest {
  @Test
  public void tesIsKatakanaWithHiraganaA() {
    assertFalse(StringUtils.isKatakana('あ'));
  }

  @Test
  public void tesIsKatakanaWithKatakanaA() {
    assertTrue(StringUtils.isKatakana('ア'));
  }

  @Test
  public void tesIsKatakanaWithHyphen() {
    assertTrue(StringUtils.isKatakana('ー'));
  }

  @Test
  public void tesIsKatakanaWithKatakanaMiddleDot() {
    assertTrue(StringUtils.isKatakana('・'));
  }

  @Test
  public void tesIsBasicLatinWithHiraganaA() {
    assertFalse(StringUtils.isBasicLatin('あ'));
  }

  @Test
  public void tesIsBasicLatinWithKatakanaA() {
    assertFalse(StringUtils.isBasicLatin('ア'));
  }

  @Test
  public void tesIsBasicLatinWithHyphen() {
    assertTrue(StringUtils.isBasicLatin('-'));
  }

  @Test
  public void tesIsBasicLatinWithPeriod() {
    assertTrue(StringUtils.isBasicLatin('.'));
  }

  @Test
  public void tesIsBasicLatinWithKatakanaMiddleDot() {
    assertFalse(StringUtils.isBasicLatin('・'));
  }

}
