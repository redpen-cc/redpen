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
package org.unigram.docvalidator.parser;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.unigram.docvalidator.store.Sentence;

public class SentenceExtractorTest {

  @Test
  public void testSimple() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<Sentence>();
    String remain = extractor.extract("this is a pen.",
        outputSentences);
    assertEquals(1, outputSentences.size());
    assertEquals("this is a pen.", outputSentences.get(0).content);
    assertEquals("", remain);
  }

  @Test
  public void testMultipleSentences() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<Sentence>();
    String remain = extractor.extract("this is a pen. that is a paper.",
        outputSentences);
    assertEquals(2, outputSentences.size());
    assertEquals("this is a pen.", outputSentences.get(0).content);
    assertEquals(" that is a paper.", outputSentences.get(1).content);
    assertEquals("", remain);
  }

  @Test
  public void testTwoSentencesWithDifferentStopCharacters() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<Sentence>();
    String remain = extractor.extract("is this a pen? that is a paper.",
        outputSentences);
    assertEquals(2, outputSentences.size());
    assertEquals("is this a pen?", outputSentences.get(0).content);
    assertEquals(" that is a paper.", outputSentences.get(1).content);
    assertEquals("", remain);
  }

  @Test
  public void testMultipleSentencesWithoutPeriodInTheEnd() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<Sentence>();
    String remain = extractor.extract("this is a pen. that is a paper",
        outputSentences);
    assertEquals(1, outputSentences.size());
    assertEquals("this is a pen.",outputSentences.get(0).content);
    assertEquals(" that is a paper", remain); // NOTE: second sentence start with white space.
  }

  @Test
  public void testJapaneseSimple() {
    List<String> stopChars = new ArrayList<String>();
    stopChars.add("。");
    stopChars.add("？");
    SentenceExtractor extractor = new SentenceExtractor(stopChars);
    List<Sentence> outputSentences = new ArrayList<Sentence>();
    String remain = extractor.extract("これは埼玉ですか？いいえ群馬です。",
        outputSentences);
    assertEquals(2, outputSentences.size());
    assertEquals("これは埼玉ですか？", outputSentences.get(0).content);
    assertEquals("いいえ群馬です。", outputSentences.get(1).content);
    assertEquals("", remain);
  }

  @Test
  public void testJapaneseSimpleWithSpace() {
    List<String> stopChars = new ArrayList<String>();
    stopChars.add("。");
    stopChars.add("？");
    SentenceExtractor extractor = new SentenceExtractor(stopChars);
    List<Sentence> outputSentences = new ArrayList<Sentence>();
    String remain = extractor.extract("これは埼玉ですか？ いいえ群馬です。",
        outputSentences);
    assertEquals(2, outputSentences.size());
    assertEquals("これは埼玉ですか？", outputSentences.get(0).content);
    assertEquals(" いいえ群馬です。", outputSentences.get(1).content);
    assertEquals("", remain);
  }

  @Test
  public void testVoidLine() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<Sentence>();
    String remain = extractor.extract("",
        outputSentences);
    assertEquals(0, outputSentences.size());
    assertEquals(remain, ""); // NOTE: second sentence start with white space.
  }

  @Test
  public void testJustPeriodLine() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<Sentence>();
    String remain = extractor.extract(".",
        outputSentences);
    assertEquals(1, outputSentences.size());
    assertEquals("", remain);
  }

  @Test
  public void testConstructPatternString() {
    List<String> endCharacters = new ArrayList<String>();
    endCharacters.add("\\.");
    endCharacters.add("?");
    endCharacters.add("!");
    assertEquals("\\.|\\?|\\!", SentenceExtractor.constructEndSentencePattern(
        endCharacters));
  }

  @Test
  public void testConstructPatternStringWithoutEscape() {
    List<String> endCharacters = new ArrayList<String>();
    endCharacters.add(".");
    endCharacters.add("?");
    endCharacters.add("!");
    assertEquals("\\.|\\?|\\!", SentenceExtractor.constructEndSentencePattern(
        endCharacters));
  }

  @Test
     public void testConstructPatternStringForSingleCharacter() {
    List<String> endCharacters = new ArrayList<String>();
    endCharacters.add("\\.");
    assertEquals("\\.", SentenceExtractor.constructEndSentencePattern(
        endCharacters));
  }

  @Test (expected=IllegalArgumentException.class)
  public void testThrowExceptionGivenVoidList() {
    List<String> endCharacters = new ArrayList<String>();
    SentenceExtractor.constructEndSentencePattern(endCharacters);
  }

  @Test (expected=IllegalArgumentException.class)
  public void testThrowExceptionGivenNull() {
    SentenceExtractor.constructEndSentencePattern(null);
  }
}
