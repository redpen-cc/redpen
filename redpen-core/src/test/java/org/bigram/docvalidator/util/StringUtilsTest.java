/**
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bigram.docvalidator.util;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.*;
import java.util.regex.Pattern;

public class StringUtilsTest {
  @Test
  public void testEndPosition() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen.");
    assertEquals(13, EndOfSentenceDetector.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void testEndPositionWithTailingSpace() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen. ");
    assertEquals(13, EndOfSentenceDetector.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void testEndPositionInMultipleSentence() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen. that is not pen.");
    assertEquals(13, EndOfSentenceDetector.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void testEndPositionInJapanese() {
    Pattern pattern = Pattern.compile("。");
    String str = new String ("私はペンではない。私は人間です。");
    assertEquals(8, EndOfSentenceDetector.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void testEndPositionInJapaneseWithSpace() {
    Pattern pattern = Pattern.compile("。");
    String str = new String ("私はペンではない。 私は人間です。");
    assertEquals(8, EndOfSentenceDetector.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void tesEndPositionMultipleDodsWithSpace() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen... ");
    assertEquals(15, EndOfSentenceDetector.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void tesEndPositionDods() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen...");
    assertEquals(15, EndOfSentenceDetector.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void tesEndPositionDodsWithinTwoSencences() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen... But that is a pencil.");
    assertEquals(15, EndOfSentenceDetector.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void tesEndPositionDodsWithinTwoSencencesWithoutSpace() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen...But that is a pencil.");
    assertEquals(36, EndOfSentenceDetector.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void tesEndPositionDodsWithinTwoSencencesWithoutSpace2() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen...But that is a pencil. ");
    assertEquals(36, EndOfSentenceDetector.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void tesEndPositionDodsWithinTwoJapaneseSencences() {
    Pattern pattern = Pattern.compile("。");
    String str = new String ("これは。。。 ペンですか。");
    assertEquals(5, EndOfSentenceDetector.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void tesEndPositionDodsWithinTwoJapaneseSencencesWithoutSpace() {
    Pattern pattern = Pattern.compile("。");
    String str = new String ("これは。。。ペンですか。");
    assertEquals(5, EndOfSentenceDetector.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void testEndPositionContainingMultipleCharacters() {
    Pattern pattern = Pattern.compile("\\?|\\.");
    String str = new String ("is this a pen? yes it is.");
    assertEquals(13, EndOfSentenceDetector.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void testEndPositionContainingMultipleNonAsciiCharacters() {
    Pattern pattern = Pattern.compile("。|？");
    String str = new String ("これは群馬ですか？いいえ埼玉です。");
    assertEquals(8, EndOfSentenceDetector.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void testEndPositionOfSentenceWithQuotationMark() {
    Pattern pattern = Pattern.compile("\\.\"");
    String input = "\"pen.\"";
    String str = new String (input);
    assertEquals(5, EndOfSentenceDetector.getSentenceEndPosition(str, pattern));
  }

  @Test
  public void testEndPositionWithWhiteWord() {
    Pattern pattern = Pattern.compile("\\.");
    String str =  "He is Mr. United States.";
    List<String> whiteList = generateUmList("Mr.");
    assertEquals(23, EndOfSentenceDetector.getSentenceEndPosition(str,
        pattern, whiteList));
  }

  @Test
  public void testEndPositionWithMultipleWhiteWords() {
    Pattern pattern = Pattern.compile("\\.");
    String str =  "This Jun. 10th, he was Mr. United States.";
    List<String> whiteList = generateUmList("Mr.", "Jun.");
    assertEquals(40, EndOfSentenceDetector.getSentenceEndPosition(str,
        pattern, whiteList));
  }

  @Test
  public void testEndPositionWithWhiteWordsContainsPeriodInternally() {
    Pattern pattern = Pattern.compile("\\.");
    String str =  "At 10 a.m. we had a lunch.";
    List<String> whiteList = generateUmList("a.m.");
    assertEquals(25, EndOfSentenceDetector.getSentenceEndPosition(str,
        pattern, whiteList));
  }

  @Test
  public void testEndPositionWithWhiteWordAndWithoutEndPeriod() {
    Pattern pattern = Pattern.compile("\\.");
    String str =  "He is Mr. United States";
    List<String> whiteList = generateUmList("Mr.");
    assertEquals(-1, EndOfSentenceDetector.getSentenceEndPosition(str,
        pattern, whiteList));
  }

  @Test
  public void tesIsKatakanaWithHiraganaA() {
    assertFalse(EndOfSentenceDetector.isKatakana('あ'));
  }

  @Test
  public void tesIsKatakanaWithKatakanaA() {
    assertTrue(EndOfSentenceDetector.isKatakana('ア'));
  }

  @Test
  public void tesIsKatakanaWithHyphen() {
    assertTrue(EndOfSentenceDetector.isKatakana('ー'));
  }

  @Test
  public void tesIsKatakanaWithKatakanaMiddleDot() {
    assertTrue(EndOfSentenceDetector.isKatakana('・'));
  }

  @Test
  public void tesIsBasicLatinWithHiraganaA() {
    assertFalse(EndOfSentenceDetector.isBasicLatin('あ'));
  }

  @Test
  public void tesIsBasicLatinWithKatakanaA() {
    assertFalse(EndOfSentenceDetector.isBasicLatin('ア'));
  }

  @Test
  public void tesIsBasicLatinWithHyphen() {
    assertTrue(EndOfSentenceDetector.isBasicLatin('-'));
  }

  @Test
  public void tesIsBasicLatinWithPeriod() {
    assertTrue(EndOfSentenceDetector.isBasicLatin('.'));
  }

  @Test
  public void tesIsBasicLatinWithKatakanaMiddleDot() {
    assertFalse(EndOfSentenceDetector.isBasicLatin('・'));
  }

  private static <E> List<E> generateUmList(E... args){
    List<E> list = new ArrayList<E>(Arrays.asList(args));
    return list;
  }
}
