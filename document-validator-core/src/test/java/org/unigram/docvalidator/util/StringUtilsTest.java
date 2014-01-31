/**
 * DocumentValidator
 * Copyright (c) 2013-, Takahiko Ito, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package org.unigram.docvalidator.util;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.regex.Pattern;

public class StringUtilsTest {

  @Test
  public void testEndPosition() {
    String str = new String ("this is a pen.");
    assertEquals(13, StringUtils.getSentenceEndPosition(str, "."));
  }

  @Test
  public void testEndPositionWithSpace() {
    String str = new String ("this is a pen. ");
    assertEquals(13, StringUtils.getSentenceEndPosition(str, "."));
  }

  @Test
  public void testEndPositionInMultipleSentence() {
    String str = new String ("this is a pen. that is not pen.");
    assertEquals(13, StringUtils.getSentenceEndPosition(str, "."));
  }

  @Test
  public void testEndPositionInJapanese() {
    String str = new String ("私はペンではない。私は人間です。");
    assertEquals(8, StringUtils.getSentenceEndPosition(str, "。"));
  }

  @Test
  public void testEndPositionInJapaneseWithSpace() {
    String str = new String ("私はペンではない。 私は人間です。");
    assertEquals(8, StringUtils.getSentenceEndPosition(str, "。"));
  }

  @Test
  public void tesEndPositionDodsWithSpace() {
    String str = new String ("this is a pen... ");
    assertEquals(15, StringUtils.getSentenceEndPosition(str, "."));
  }

  @Test
  public void tesEndPositionDods() {
    String str = new String ("this is a pen...");
    assertEquals(15, StringUtils.getSentenceEndPosition(str, "."));
  }

  @Test
  public void tesEndPositionDodsWithinTwoSencences() {
    String str = new String ("this is a pen... But that is a pencil.");
    assertEquals(15, StringUtils.getSentenceEndPosition(str, "."));
  }

  @Test
  public void tesEndPositionDodsWithinTwoSencencesWithoutSpace() {
    String str = new String ("this is a pen...But that is a pencil.");
    assertEquals(36, StringUtils.getSentenceEndPosition(str, "."));
  }

  @Test
  public void tesEndPositionDodsWithinTwoSencencesWithoutSpace2() {
    String str = new String ("this is a pen...But that is a pencil. ");
    assertEquals(36, StringUtils.getSentenceEndPosition(str, "."));
  }

  @Test
  public void tesEndPositionDodsWithinTwoJapaneseSencences() {
    String str = new String ("これは。。。 ペンですか。");
    assertEquals(5, StringUtils.getSentenceEndPosition(str, "。"));
  }

  @Test
  public void tesEndPositionDodsWithinTwoJapaneseSencencesWithoutSpace() {
    String str = new String ("これは。。。ペンですか。");
    assertEquals(5, StringUtils.getSentenceEndPosition(str, "。"));
  }

  @Test
  public void testEndPositionWithPattern() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen.");
    assertEquals(13, StringUtils.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void testEndPositionWithPatternWithTailingSpace() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen. ");
    assertEquals(13, StringUtils.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void testEndPositionWithPatternInMultipleSentence() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen. that is not pen.");
    assertEquals(13, StringUtils.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void testEndPositionWithPatternInJapanese() {
    Pattern pattern = Pattern.compile("。");
    String str = new String ("私はペンではない。私は人間です。");
    assertEquals(8, StringUtils.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void testEndPositionWithPatternInJapaneseWithSpace() {
    Pattern pattern = Pattern.compile("。");
    String str = new String ("私はペンではない。 私は人間です。");
    assertEquals(8, StringUtils.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void tesEndPositionWithPatternMultipleDodsWithSpace() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen... ");
    assertEquals(15, StringUtils.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void tesEndPositionWithPatternDods() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen...");
    assertEquals(15, StringUtils.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void tesEndPositionWithPatternDodsWithinTwoSencences() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen... But that is a pencil.");
    assertEquals(15, StringUtils.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void tesEndPositionWithPatternDodsWithinTwoSencencesWithoutSpace() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen...But that is a pencil.");
    assertEquals(36, StringUtils.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void tesEndPositionWithPatternDodsWithinTwoSencencesWithoutSpace2() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen...But that is a pencil. ");
    assertEquals(36, StringUtils.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void tesEndPositionWithPatternDodsWithinTwoJapaneseSencences() {
    Pattern pattern = Pattern.compile("。");
    String str = new String ("これは。。。 ペンですか。");
    assertEquals(5, StringUtils.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void tesEndPositionWithPatternDodsWithinTwoJapaneseSencencesWithoutSpace() {
    Pattern pattern = Pattern.compile("。");
    String str = new String ("これは。。。ペンですか。");
    assertEquals(5, StringUtils.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void testEndPositionWithPatternContainingMultipleCharacters() {
    Pattern pattern = Pattern.compile("\\?|\\.");
    String str = new String ("is this a pen? yes it is.");
    assertEquals(13, StringUtils.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void testEndPositionWithPatternContainingMultipleNonAsciCharacters() {
    Pattern pattern = Pattern.compile("。|？");
    String str = new String ("これは群馬ですか？いいえ埼玉です。");
    assertEquals(8, StringUtils.getSentenceEndPosition(str, pattern));
  }

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
