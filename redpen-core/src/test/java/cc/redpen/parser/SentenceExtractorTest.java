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
package cc.redpen.parser;

import cc.redpen.model.Sentence;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SentenceExtractorTest {

  @Test
  public void testSimple() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("this is a pen.",
        outputSentences);
    assertEquals(1, outputSentences.size());
    assertEquals("this is a pen.", outputSentences.get(0).content);
    assertEquals("", remain);
  }

  @Test
  public void testMultipleSentences() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
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
    List<Sentence> outputSentences = new ArrayList<>();
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
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("this is a pen. that is a paper",
        outputSentences);
    assertEquals(1, outputSentences.size());
    assertEquals("this is a pen.",outputSentences.get(0).content);
    assertEquals(" that is a paper", remain); // NOTE: second sentence start with white space.
  }

  @Test
  public void testEndWithDoubleQuotation() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("this is a \"pen.\"",
        outputSentences);
    assertEquals(1, outputSentences.size());
    assertEquals("this is a \"pen.\"", outputSentences.get(0).content);
    assertEquals("", remain);
  }

  @Test
  public void testEndWithSingleQuotation() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("this is a \'pen.\'",
        outputSentences);
    assertEquals(1, outputSentences.size());
    assertEquals("this is a \'pen.\'", outputSentences.get(0).content);
    assertEquals("", remain);
  }

  @Test
  public void testEndWithDoubleQuotationEnglishVersion() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("this is a \"pen\".",
        outputSentences);
    assertEquals(1, outputSentences.size());
    assertEquals("this is a \"pen\".", outputSentences.get(0).content);
    assertEquals("", remain);
  }

  @Test
  public void testEndWithSingleQuotationEnglishVersion() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("this is a \'pen\'.",
        outputSentences);
    assertEquals(1, outputSentences.size());
    assertEquals("this is a \'pen\'.", outputSentences.get(0).content);
    assertEquals("", remain);
  }

  @Test
  public void testMultipleSentencesOneOfThemIsEndWithDoubleQuotation() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("this is a \"pen.\" Another one is not a pen.",
        outputSentences);
    assertEquals(2, outputSentences.size());
    assertEquals("this is a \"pen.\"", outputSentences.get(0).content);
    assertEquals(" Another one is not a pen.", outputSentences.get(1).content);
    assertEquals("", remain);
  }

  @Test
  public void testMultipleSentencesWithPartialSplit() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("this is a pen. Another\n" +
            "one is not a pen.",
        outputSentences);
    assertEquals(2, outputSentences.size());
    assertEquals("this is a pen.", outputSentences.get(0).content);
    assertEquals(" Another\none is not a pen.", outputSentences.get(1).content);
    assertEquals("", remain);
  }

  @Test
  public void testMultipleSentencesWithPartialSentence() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("this is a pen. Another\n",
        outputSentences);
    assertEquals(1, outputSentences.size());
    assertEquals("this is a pen.", outputSentences.get(0).content);
    assertEquals(" Another\n", remain);
  }

  @Test
  public void testJapaneseSimple() {
    List<String> stopChars = new ArrayList<>();
    stopChars.add("。");
    stopChars.add("？");
    SentenceExtractor extractor = new SentenceExtractor(stopChars);
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("これは埼玉ですか？いいえ群馬です。",
        outputSentences);
    assertEquals(2, outputSentences.size());
    assertEquals("これは埼玉ですか？", outputSentences.get(0).content);
    assertEquals("いいえ群馬です。", outputSentences.get(1).content);
    assertEquals("", remain);
  }

  @Test
  public void testJapaneseSimpleWithSpace() {
    List<String> stopChars = new ArrayList<>();
    stopChars.add("。");
    stopChars.add("？");
    SentenceExtractor extractor = new SentenceExtractor(stopChars);
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("これは埼玉ですか？ いいえ群馬です。",
        outputSentences);
    assertEquals(2, outputSentences.size());
    assertEquals("これは埼玉ですか？", outputSentences.get(0).content);
    assertEquals(" いいえ群馬です。", outputSentences.get(1).content);
    assertEquals("", remain);
  }

  @Test
  public void testJapaneseSimpleWithEndQuotations() {
    List<String> stopChars = new ArrayList<>();
    stopChars.add("。");
    stopChars.add("？");
    List<String> rightQuotations = new ArrayList<>();
    stopChars.add("’");
    stopChars.add("”");
    SentenceExtractor extractor = new SentenceExtractor(stopChars, rightQuotations);
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("これは“群馬。”",
        outputSentences);
    assertEquals(1, outputSentences.size());
    assertEquals("これは“群馬。”", outputSentences.get(0).content);
    assertEquals("", remain);
  }

  @Test
  public void testJapaneseMultipleSentencesWithEndQuotations() {
    List<String> stopChars = new ArrayList<>();
    stopChars.add("。");
    stopChars.add("？");
    List<String> rightQuotations = new ArrayList<>();
    stopChars.add("’");
    stopChars.add("”");
    SentenceExtractor extractor = new SentenceExtractor(stopChars, rightQuotations);
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("これは“群馬。”あれは群馬ではない。",
        outputSentences);
    assertEquals(2, outputSentences.size());
    assertEquals("これは“群馬。”", outputSentences.get(0).content);
    assertEquals("あれは群馬ではない。", outputSentences.get(1).content);
    assertEquals("", remain);
  }

  @Test
  public void testJapaneseMultipleSentencesWithPartialSplit() {
    List<String> stopChars = new ArrayList<>();
    stopChars.add("．");
    stopChars.add("？");
    List<String> rightQuotations = new ArrayList<>();
    SentenceExtractor extractor = new SentenceExtractor(stopChars, rightQuotations);
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("規約には一文の長さ，利用する句読点の種類（半角全角など），文書中で利用する技術単語の選択などがあり，文書を作成する組織ごとに異なる．たとえば，\n" +
            "アルゴリズムをアルファベットで記述する組織もあれば，カタカナに変換して記述する組織も存在する．",
        outputSentences);
    assertEquals("規約には一文の長さ，利用する句読点の種類（半角全角など），文書中で利用する技術単語の選択などがあり，文書を作成する組織ごとに異なる．", outputSentences.get(0).content);
    assertEquals("たとえば，\nアルゴリズムをアルファベットで記述する組織もあれば，カタカナに変換して記述する組織も存在する．", outputSentences.get(1).content);
    assertEquals(2, outputSentences.size());
  }


  @Test
  public void testExtractWithoutLastSentenceJapaneseWithPartialSplit() {
    List<String> stopChars = new ArrayList<>();
    stopChars.add("．");
    stopChars.add("？");
    List<String> rightQuotations = new ArrayList<>();
    SentenceExtractor extractor = new SentenceExtractor(stopChars, rightQuotations);
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extractWithoutLastSentence("規約には一文の長さ，利用する句読点の種類（半角全角など），文書中で利用する技術単語の選択などがあり，文書を作成する組織ごとに異なる．たとえば，\n" +
        "アルゴリズムをアルファベットで記述する組織もあれば，カタカナに変換して記述する組織も存在する．",  outputSentences, 0);
    assertEquals(1, outputSentences.size());
    assertEquals("規約には一文の長さ，利用する句読点の種類（半角全角など），文書中で利用する技術単語の選択などがあり，文書を作成する組織ごとに異なる．", outputSentences.get(0).content);
    assertEquals("たとえば，\nアルゴリズムをアルファベットで記述する組織もあれば，カタカナに変換して記述する組織も存在する．", remain);
  }

  @Test
  public void testJapanesSentenceWithEndWithNonFullStop() {
    List<String> stopChars = new ArrayList<>();
    stopChars.add("．");
    List<String> rightQuotations = new ArrayList<>();
    SentenceExtractor extractor = new SentenceExtractor(stopChars, rightQuotations);
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("規約には一文の長さ，利用する句読点の種類（半角全角など），文書中で利用する技術単語の選択などがあり，文書を作成する組織ごとに異なる．たとえば，",
        outputSentences);
    assertEquals(1, outputSentences.size());
    assertEquals("規約には一文の長さ，利用する句読点の種類（半角全角など），文書中で利用する技術単語の選択などがあり，文書を作成する組織ごとに異なる．", outputSentences.get(0).content);
    assertEquals("たとえば，", remain);

  }

  @Test
  public void testExtractWithoutLastSentenceJapaneseSentencesEndWithNonFullStop() {
    List<String> stopChars = new ArrayList<>();
    stopChars.add("．");
    stopChars.add("？");
    List<String> rightQuotations = new ArrayList<>();
    SentenceExtractor extractor = new SentenceExtractor(stopChars, rightQuotations);
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extractWithoutLastSentence("規約には一文の長さ，利用する句読点の種類（半角全角など），文書中で利用する技術単語の選択などがあり，文書を作成する組織ごとに異なる．たとえば，\n",
        outputSentences,0);
    assertEquals(1, outputSentences.size());
    assertEquals("たとえば，\n", remain);
  }

  @Test
  public void testSentenceWithWhiteWord() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("He is a Dr. candidate.",  // NOTE: white word list contains "Dr."
        outputSentences);
    assertEquals(1, outputSentences.size());
    assertEquals("He is a Dr. candidate.", outputSentences.get(0).content);
    assertEquals("", remain);
  }

  @Test
  public void testMultipleSentencesWithWhiteWord() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("Is he a Dr. candidate? Yes, he is.",  // NOTE: white word list contains "Dr."
        outputSentences);
    assertEquals(2, outputSentences.size());
    assertEquals("Is he a Dr. candidate?", outputSentences.get(0).content);
    assertEquals(" Yes, he is.", outputSentences.get(1).content);
    assertEquals("", remain);
  }

  @Test
  public void testVoidLine() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("",
        outputSentences);
    assertEquals(0, outputSentences.size());
    assertEquals(remain, ""); // NOTE: second sentence start with white space.
  }

  @Test
  public void testJustPeriodLine() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract(".",
        outputSentences);
    assertEquals(1, outputSentences.size());
    assertEquals("", remain);
  }

  @Test
  public void testConstructPatternString() {
    List<String> endCharacters = new ArrayList<>();
    endCharacters.add("\\.");
    endCharacters.add("?");
    endCharacters.add("!");
    SentenceExtractor extractor = new SentenceExtractor(endCharacters);
    assertEquals("\\.'|\\?'|\\!'|\\.\"|\\?\"|\\!\"|\\.|\\?|\\!", extractor.constructEndSentencePattern());
  }

  @Test
  public void testConstructPatternStringWithoutEscape() {
    List<String> endCharacters = new ArrayList<>();
    endCharacters.add(".");
    endCharacters.add("?");
    endCharacters.add("!");
    SentenceExtractor extractor = new SentenceExtractor(endCharacters);
    assertEquals("\\.'|\\?'|\\!'|\\.\"|\\?\"|\\!\"|\\.|\\?|\\!", extractor.constructEndSentencePattern());
  }

  @Test
     public void testConstructPatternStringForSingleCharacter() {
    List<String> endCharacters = new ArrayList<>();
    endCharacters.add("\\.");
    SentenceExtractor extractor = new SentenceExtractor(endCharacters);
    assertEquals("\\.\'|\\.\"|\\.", extractor.constructEndSentencePattern());
  }

  @Test (expected=IllegalArgumentException.class)
  public void testThrowExceptionGivenVoidList() {
    List<String> endCharacters = new ArrayList<>();
    SentenceExtractor extractor = new SentenceExtractor(endCharacters);
    extractor.constructEndSentencePattern();
  }

  @Test
  public void testThrowExceptionGivenNull() {
    SentenceExtractor extractor = new SentenceExtractor();
    extractor.constructEndSentencePattern(); // not a throw exception
  }
}
