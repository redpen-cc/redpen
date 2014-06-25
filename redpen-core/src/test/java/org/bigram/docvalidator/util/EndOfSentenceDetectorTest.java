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

public class EndOfSentenceDetectorTest {
  @Test
  public void testEndPosition() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen.");
    EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
    assertEquals(13, detector.getSentenceEndPosition(str));
  }

  @Test
  public void testEndPositionWithTailingSpace() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen. ");
    EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
    assertEquals(13, detector.getSentenceEndPosition(str));
  }

  @Test
  public void testEndPositionInMultipleSentence() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen. that is not pen.");
    EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
    assertEquals(13, detector.getSentenceEndPosition(str));
  }

  @Test
  public void testEndPositionInJapanese() {
    Pattern pattern = Pattern.compile("。");
    String str = new String ("私はペンではない。私は人間です。");
    EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
    assertEquals(8, detector.getSentenceEndPosition(str));
  }

  @Test
  public void testEndPositionInJapaneseWithSpace() {
    Pattern pattern = Pattern.compile("。");
    String str = new String ("私はペンではない。 私は人間です。");
    EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
    assertEquals(8, detector.getSentenceEndPosition(str));
  }

  @Test
  public void tesEndPositionMultipleDodsWithSpace() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen... ");
    EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
    assertEquals(15, detector.getSentenceEndPosition(str));
  }

  @Test
  public void tesEndPositionDods() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen...");
    EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
    assertEquals(15, detector.getSentenceEndPosition(str));
  }

  @Test
  public void tesEndPositionDodsWithinTwoSencences() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen... But that is a pencil.");
    EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
    assertEquals(15, detector.getSentenceEndPosition(str));
  }

  @Test
  public void tesEndPositionDodsWithinTwoSencencesWithoutSpace() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen...But that is a pencil.");
    EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
    assertEquals(36, detector.getSentenceEndPosition(str));
  }

  @Test
  public void tesEndPositionDodsWithinTwoSencencesWithoutSpace2() {
    Pattern pattern = Pattern.compile("\\.");
    String str = new String ("this is a pen...But that is a pencil. ");
    EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
    assertEquals(36, detector.getSentenceEndPosition(str));
  }

  @Test
  public void tesEndPositionDodsWithinTwoJapaneseSencences() {
    Pattern pattern = Pattern.compile("。");
    String str = new String ("これは。。。 ペンですか。");
    EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
    assertEquals(5, detector.getSentenceEndPosition(str));
  }

  @Test
  public void tesEndPositionDodsWithinTwoJapaneseSencencesWithoutSpace() {
    Pattern pattern = Pattern.compile("。");
    String str = new String ("これは。。。ペンですか。");
    EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
    assertEquals(5, detector.getSentenceEndPosition(str));
  }

  @Test
  public void testEndPositionContainingMultipleCharacters() {
    Pattern pattern = Pattern.compile("\\?|\\.");
    String str = new String ("is this a pen? yes it is.");
    EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
    assertEquals(13, detector.getSentenceEndPosition(str));
  }

  @Test
  public void testEndPositionContainingMultipleNonAsciiCharacters() {
    Pattern pattern = Pattern.compile("。|？");
    String str = new String ("これは群馬ですか？いいえ埼玉です。");
    EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
    assertEquals(8, detector.getSentenceEndPosition(str));
  }

  @Test
  public void testEndPositionOfSentenceWithQuotationMark() {
    Pattern pattern = Pattern.compile("\\.\"");
    String input = "\"pen.\"";
    String str = new String (input);
    EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
    assertEquals(5, detector.getSentenceEndPosition(str));
  }

  @Test
  public void testEndPositionWithWhiteWord() {
    Pattern pattern = Pattern.compile("\\.");
    String str =  "He is Mr. United States.";
    List<String> whiteList = generateUmList("Mr.");
    EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern, whiteList);
    assertEquals(23, detector.getSentenceEndPosition(str));
  }

  @Test
  public void testEndPositionWithMultipleWhiteWords() {
    Pattern pattern = Pattern.compile("\\.");
    String str =  "This Jun. 10th, he was Mr. United States.";
    List<String> whiteList = generateUmList("Mr.", "Jun.");
    EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern, whiteList);
    assertEquals(40, detector.getSentenceEndPosition(str));
  }

  @Test
  public void testEndPositionWithWhiteWordsContainsPeriodInternally() {
    Pattern pattern = Pattern.compile("\\.");
    String str =  "At 10 a.m. we had a lunch.";
    List<String> whiteList = generateUmList("a.m.");
    EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern, whiteList);
    assertEquals(25, detector.getSentenceEndPosition(str));
  }

  @Test
  public void testEndPositionWithWhiteWordAndWithoutEndPeriod() {
    Pattern pattern = Pattern.compile("\\.");
    String str =  "He is Mr. United States";
    List<String> whiteList = generateUmList("Mr.");
    EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern, whiteList);
    assertEquals(-1, detector.getSentenceEndPosition(str));
  }

  private static <E> List<E> generateUmList(E... args){
    List<E> list = new ArrayList<E>(Arrays.asList(args));
    return list;
  }
}
