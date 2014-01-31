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
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen.");
    assertEquals(13, StringUtils.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void testEndPositionWithTailingSpace() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen. ");
    assertEquals(13, StringUtils.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void testEndPositionInMultipleSentence() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen. that is not pen.");
    assertEquals(13, StringUtils.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void testEndPositionInJapanese() {
    Pattern pattern = Pattern.compile("。");
    String str = new String ("私はペンではない。私は人間です。");
    assertEquals(8, StringUtils.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void testEndPositionInJapaneseWithSpace() {
    Pattern pattern = Pattern.compile("。");
    String str = new String ("私はペンではない。 私は人間です。");
    assertEquals(8, StringUtils.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void tesEndPositionMultipleDodsWithSpace() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen... ");
    assertEquals(15, StringUtils.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void tesEndPositionDods() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen...");
    assertEquals(15, StringUtils.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void tesEndPositionDodsWithinTwoSencences() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen... But that is a pencil.");
    assertEquals(15, StringUtils.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void tesEndPositionDodsWithinTwoSencencesWithoutSpace() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen...But that is a pencil.");
    assertEquals(36, StringUtils.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void tesEndPositionDodsWithinTwoSencencesWithoutSpace2() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen...But that is a pencil. ");
    assertEquals(36, StringUtils.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void tesEndPositionDodsWithinTwoJapaneseSencences() {
    Pattern pattern = Pattern.compile("。");
    String str = new String ("これは。。。 ペンですか。");
    assertEquals(5, StringUtils.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void tesEndPositionDodsWithinTwoJapaneseSencencesWithoutSpace() {
    Pattern pattern = Pattern.compile("。");
    String str = new String ("これは。。。ペンですか。");
    assertEquals(5, StringUtils.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void testEndPositionContainingMultipleCharacters() {
    Pattern pattern = Pattern.compile("\\?|\\.");
    String str = new String ("is this a pen? yes it is.");
    assertEquals(13, StringUtils.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void testEndPositionContainingMultipleNonAsciCharacters() {
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
