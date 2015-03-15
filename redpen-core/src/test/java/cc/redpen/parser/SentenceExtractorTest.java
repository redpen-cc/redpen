/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
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

import cc.redpen.config.Configuration;
import cc.redpen.model.Sentence;
import cc.redpen.util.Pair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SentenceExtractorTest {

    private List<Sentence> createSentences(List<Pair<Integer, Integer>> outputPositions,
                                           int lastPosition, String line) {
        List<Sentence> output = new ArrayList<>();
        for (Pair<Integer, Integer> outputPosition : outputPositions) {
            output.add(new Sentence(line.substring(outputPosition.first, outputPosition.second), 0));
        }
        return output;
    }

    @Test
    public void testSimple() {
        SentenceExtractor extractor = new SentenceExtractor(
                new Configuration.ConfigurationBuilder().build().getSymbolTable());
        List<Pair<Integer, Integer>> outputPositions = new ArrayList<>();
        final String input = "this is a pen.";
        int lastPosition = extractor.extract(input, outputPositions);
        List<Sentence> outputSentences = createSentences(outputPositions, lastPosition, input);
        assertEquals(1, outputSentences.size());
        assertEquals(input, outputSentences.get(0).getContent());
        assertEquals(14, lastPosition);
    }

    @Test
    public void testMultipleSentences() {
        SentenceExtractor extractor = new SentenceExtractor(
                new Configuration.ConfigurationBuilder().build().getSymbolTable());
        final String input = "this is a pen. that is a paper.";
        List<Pair<Integer, Integer>> outputPositions = new ArrayList<>();
        int lastPosition = extractor.extract(input, outputPositions);
        List<Sentence> outputSentences = createSentences(outputPositions, lastPosition, input);
        assertEquals(2, outputSentences.size());
        assertEquals("this is a pen.", outputSentences.get(0).getContent());
        assertEquals(" that is a paper.", outputSentences.get(1).getContent());
        assertEquals(31, lastPosition);
    }

    @Test
    public void testTwoSentencesWithDifferentStopCharacters() {
        SentenceExtractor extractor = new SentenceExtractor(
                new Configuration.ConfigurationBuilder().build().getSymbolTable());
        final String input = "is this a pen? that is a paper.";
        List<Pair<Integer, Integer>> outputPositions = new ArrayList<>();
        int lastPosition = extractor.extract(input, outputPositions);
        List<Sentence> outputSentences = createSentences(outputPositions, lastPosition, input);
        assertEquals(2, outputSentences.size());
        assertEquals("is this a pen?", outputSentences.get(0).getContent());
        assertEquals(" that is a paper.", outputSentences.get(1).getContent());
        assertEquals(31, lastPosition);
    }

    @Test
    public void testMultipleSentencesWithoutPeriodInTheEnd() {
        SentenceExtractor extractor = new SentenceExtractor(new Configuration.ConfigurationBuilder().build().getSymbolTable());
        final String input = "this is a pen. that is a paper";
        List<Pair<Integer, Integer>> outputPositions = new ArrayList<>();
        int lastPosition = extractor.extract(input, outputPositions);
        List<Sentence> outputSentences = createSentences(outputPositions, lastPosition, input);
        assertEquals(1, outputSentences.size());
        assertEquals("this is a pen.", outputSentences.get(0).getContent());
        assertEquals(14, lastPosition); // NOTE: second sentence start with white space.
    }

    @Test
    public void testEndWithDoubleQuotation() {
        SentenceExtractor extractor = new SentenceExtractor(
                new Configuration.ConfigurationBuilder().build().getSymbolTable());
        List<Pair<Integer, Integer>> outputPositions = new ArrayList<>();
        final String input = "this is a \"pen.\"";
        int lastPosition = extractor.extract(input, outputPositions);
        List<Sentence> outputSentences = createSentences(outputPositions, lastPosition, input);
        assertEquals(1, outputSentences.size());
        assertEquals("this is a \"pen.\"", outputSentences.get(0).getContent());
        assertEquals(input.length(), lastPosition);
    }

    @Test
    public void testEndWithSingleQuotation() {
        SentenceExtractor extractor = new SentenceExtractor(
                new Configuration.ConfigurationBuilder().build().getSymbolTable());
        final String input = "this is a \'pen.\'";
        List<Pair<Integer, Integer>> outputPositions = new ArrayList<>();
        int lastPosition = extractor.extract(input, outputPositions);
        List<Sentence> outputSentences = createSentences(outputPositions, lastPosition, input);
        assertEquals(1, outputSentences.size());
        assertEquals("this is a \'pen.\'", outputSentences.get(0).getContent());
        assertEquals(input.length(), lastPosition);
    }

    @Test
    public void testEndWithDoubleQuotationEnglishVersion() {
        SentenceExtractor extractor = new SentenceExtractor(
                new Configuration.ConfigurationBuilder().build().getSymbolTable());
        final String input = "this is a \"pen\".";
        List<Pair<Integer, Integer>> outputPositions = new ArrayList<>();
        int lastPosition = extractor.extract(input, outputPositions);
        List<Sentence> outputSentences = createSentences(outputPositions, lastPosition, input);
        assertEquals(1, outputSentences.size());
        assertEquals("this is a \"pen\".", outputSentences.get(0).getContent());
        assertEquals(input.length(), lastPosition);
    }

    @Test
    public void testEndWithSingleQuotationEnglishVersion() {
        SentenceExtractor extractor = new SentenceExtractor(
                new Configuration.ConfigurationBuilder().build().getSymbolTable());
        final String input = "this is a \'pen\'.";
        List<Pair<Integer, Integer>> outputPositions = new ArrayList<>();
        int lastPosition = extractor.extract(input, outputPositions);
        List<Sentence> outputSentences = createSentences(outputPositions, lastPosition, input);
        assertEquals(1, outputSentences.size());
        assertEquals("this is a \'pen\'.", outputSentences.get(0).getContent());
        assertEquals(input.length(), lastPosition);
    }

    @Test
    public void testMultipleSentencesOneOfThemIsEndWithDoubleQuotation() {
        SentenceExtractor extractor = new SentenceExtractor(
                new Configuration.ConfigurationBuilder().build().getSymbolTable());
        final String input = "this is a \"pen.\" Another one is not a pen.";
        List<Pair<Integer, Integer>> outputPositions = new ArrayList<>();
        int lastPosition = extractor.extract(input, outputPositions);
        List<Sentence> outputSentences = createSentences(outputPositions, lastPosition, input);
        assertEquals(2, outputSentences.size());
        assertEquals("this is a \"pen.\"", outputSentences.get(0).getContent());
        assertEquals(" Another one is not a pen.", outputSentences.get(1).getContent());
        assertEquals(input.length(), lastPosition);
    }

    @Test
    public void testMultipleSentencesWithPartialSplit() {
        SentenceExtractor extractor = new SentenceExtractor(
                new Configuration.ConfigurationBuilder().build().getSymbolTable());
        final String input = "this is a pen. Another\n" + "one is not a pen.";
        List<Pair<Integer, Integer>> outputPositions = new ArrayList<>();
        int lastPosition = extractor.extract(input, outputPositions);
        List<Sentence> outputSentences = createSentences(outputPositions, lastPosition, input);
        assertEquals(2, outputSentences.size());
        assertEquals("this is a pen.", outputSentences.get(0).getContent());
        assertEquals(" Another\none is not a pen.", outputSentences.get(1).getContent());
        assertEquals(input.length(), lastPosition);
    }

    @Test
    public void testMultipleSentencesWithSplitInEndOfSentence() {
        SentenceExtractor extractor = new SentenceExtractor(
                new Configuration.ConfigurationBuilder().build().getSymbolTable());
        final String input = "this is a pen.\nAnother one is not a pen.";
        List<Pair<Integer, Integer>> outputPositions = new ArrayList<>();
        int lastPosition = extractor.extract(input, outputPositions);
        List<Sentence> outputSentences = createSentences(outputPositions, lastPosition, input);
        assertEquals(2, outputSentences.size());
        assertEquals("this is a pen.", outputSentences.get(0).getContent());
        assertEquals("\nAnother one is not a pen.", outputSentences.get(1).getContent());
        assertEquals(input.length(), lastPosition);
    }

    @Test
    public void testMultipleSentencesWithPartialSentence() {
        SentenceExtractor extractor = new SentenceExtractor(
                new Configuration.ConfigurationBuilder().build().getSymbolTable());
        final String input = "this is a pen. Another\n";
        List<Pair<Integer, Integer>> outputPositions = new ArrayList<>();
        int lastPosition = extractor.extract(input, outputPositions);
        List<Sentence> outputSentences = createSentences(outputPositions, lastPosition, input);
        assertEquals(1, outputSentences.size());
        assertEquals("this is a pen.", outputSentences.get(0).getContent());
        assertEquals(14, lastPosition);
    }

    @Test
    public void testJapaneseSimple() {
        char[] stopChars = {'。', '？'};
        SentenceExtractor extractor = new SentenceExtractor(stopChars);
        final String input = "これは埼玉ですか？いいえ群馬です。";
        List<Pair<Integer, Integer>> outputPositions = new ArrayList<>();
        int lastPosition = extractor.extract(input, outputPositions);
        List<Sentence> outputSentences = createSentences(outputPositions, lastPosition, input);
        assertEquals(2, outputSentences.size());
        assertEquals("これは埼玉ですか？", outputSentences.get(0).getContent());
        assertEquals("いいえ群馬です。", outputSentences.get(1).getContent());
        assertEquals(input.length(), lastPosition);
    }

    @Test
    public void testJapaneseSimpleWithSpace() {
        char[] stopChars = {'。', '？'};
        SentenceExtractor extractor = new SentenceExtractor(stopChars);
        final String input = "これは埼玉ですか？ いいえ群馬です。";
        List<Pair<Integer, Integer>> outputPositions = new ArrayList<>();
        int lastPosition = extractor.extract(input, outputPositions);
        List<Sentence> outputSentences = createSentences(outputPositions, lastPosition, input);
        assertEquals(2, outputSentences.size());
        assertEquals("これは埼玉ですか？", outputSentences.get(0).getContent());
        assertEquals(" いいえ群馬です。", outputSentences.get(1).getContent());
        assertEquals(input.length(), lastPosition);
    }

    @Test
    public void testJapaneseSimpleWithEndQuotations() {
        char[] stopChars = {'。', '？'};
        char[] rightQuotations = {'’', '”'};
        SentenceExtractor extractor = new SentenceExtractor(stopChars, rightQuotations);
        final String input = "これは“群馬。”";
        List<Pair<Integer, Integer>> outputPositions = new ArrayList<>();
        int lastPosition = extractor.extract(input, outputPositions);
        List<Sentence> outputSentences = createSentences(outputPositions, lastPosition, input);
        assertEquals(1, outputSentences.size());
        assertEquals("これは“群馬。”", outputSentences.get(0).getContent());
        assertEquals(input.length(), lastPosition);
    }

    @Test
    public void testJapaneseMultipleSentencesWithEndQuotations() {
        char[] stopChars = {'。', '？'};
        char[] rightQuotations = {'’', '”'};
        SentenceExtractor extractor = new SentenceExtractor(stopChars, rightQuotations);
        final String input = "これは“群馬。”あれは群馬ではない。";
        List<Pair<Integer, Integer>> outputPositions = new ArrayList<>();
        int lastPosition = extractor.extract(input, outputPositions);
        List<Sentence> outputSentences = createSentences(outputPositions, lastPosition, input);
        assertEquals(2, outputSentences.size());
        assertEquals("これは“群馬。”", outputSentences.get(0).getContent());
        assertEquals("あれは群馬ではない。", outputSentences.get(1).getContent());
        assertEquals(input.length(), lastPosition);
    }

    @Test
    public void testJapaneseMultipleSentencesWithPartialSplit() {
        char[] stopChars = {'．', '？'};
        char[] rightQuotations = {};
        SentenceExtractor extractor = new SentenceExtractor(stopChars, rightQuotations);
        final String input = "それは異なる．たとえば，\n" +
                "以下のとおりである．";
        List<Pair<Integer, Integer>> outputPositions = new ArrayList<>();
        int lastPosition = extractor.extract(input, outputPositions);
        List<Sentence> outputSentences = createSentences(outputPositions, lastPosition, input);
        assertEquals(2, outputSentences.size());
        assertEquals("それは異なる．", outputSentences.get(0).getContent());
        assertEquals("たとえば，\n以下のとおりである．", outputSentences.get(1).getContent());
        assertEquals(input.length(), lastPosition);
    }

    @Test
    public void testJapanesSentenceWithEndWithNonFullStop() {
        char[] stopChars = {'．'};
        char[] rightQuotations = {};
        SentenceExtractor extractor = new SentenceExtractor(stopChars, rightQuotations);
        final String input = "それは異なる．たとえば，";
        List<Pair<Integer, Integer>> outputPositions = new ArrayList<>();
        int lastPosition = extractor.extract(input, outputPositions);
        List<Sentence> outputSentences = createSentences(outputPositions, lastPosition, input);
        assertEquals(1, outputSentences.size());
        assertEquals("それは異なる．", outputSentences.get(0).getContent());
        assertEquals(7, lastPosition);
    }

    @Test
    public void testSentenceWithWhiteWordPosition() {
        SentenceExtractor extractor = new SentenceExtractor(
                new Configuration.ConfigurationBuilder().build().getSymbolTable());
        final String input = "He is a Dr. candidate.";
        List<Pair<Integer, Integer>> outputPositions = new ArrayList<>();
        int lastPosition = extractor.extract(input, outputPositions);
        List<Sentence> outputSentences = createSentences(outputPositions, lastPosition, input);
        assertEquals(1, outputSentences.size());
        assertEquals("He is a Dr. candidate.", outputSentences.get(0).getContent());
        assertEquals(input.length(), lastPosition);
    }

    @Test
    public void testMultipleSentencesWithWhiteWordPosition() {
        SentenceExtractor extractor = new SentenceExtractor
                (new Configuration.ConfigurationBuilder().build().getSymbolTable());
        final String input = "Is he a Dr. candidate? Yes, he is.";  // NOTE: white word list contains "Dr."
        List<Pair<Integer, Integer>> outputPositions = new ArrayList<>();
        int lastPosition = extractor.extract(input, outputPositions);
        List<Sentence> outputSentences = createSentences(outputPositions, lastPosition, input);
        assertEquals(2, outputSentences.size());
        assertEquals("Is he a Dr. candidate?", outputSentences.get(0).getContent());
        assertEquals(" Yes, he is.", outputSentences.get(1).getContent());
        assertEquals(input.length(), lastPosition);
    }

    @Test
    public void testVoidLine() {
        SentenceExtractor extractor = new SentenceExtractor(
                new Configuration.ConfigurationBuilder().build().getSymbolTable());
        final String input = "";
        List<Pair<Integer, Integer>> outputPositions = new ArrayList<>();
        int lastPosition = extractor.extract(input, outputPositions);
        List<Sentence> outputSentences = createSentences(outputPositions, lastPosition, input);
        assertEquals(0, outputSentences.size());
        assertEquals(0, lastPosition); // NOTE: second sentence start with white space.
    }

    @Test
    public void testJustPeriodLine() {
        SentenceExtractor extractor = new SentenceExtractor(
                new Configuration.ConfigurationBuilder().build().getSymbolTable());
        final String input = ".";
        List<Pair<Integer, Integer>> outputPositions = new ArrayList<>();
        int lastPosition = extractor.extract(input, outputPositions);
        List<Sentence> outputSentences = createSentences(outputPositions, lastPosition, input);
        assertEquals(1, outputSentences.size());
        assertEquals(input.length(), lastPosition);
    }

    //    @Test
//    public void testConstructPatternString() {
//        List<Character> endCharacters = new ArrayList<>();
//        endCharacters.add('\\.');
//        endCharacters.add('?');
//        endCharacters.add('!');
//        SentenceExtractor extractor = new SentenceExtractor(endCharacters);
//        assertEquals("\\.'|\\?'|\\!'|\\.\"|\\?\"|\\!\"|\\.|\\?|\\!", extractor.constructEndSentencePattern().pattern());
//    }
//
    @Test
    public void testConstructPatternStringWithoutEscape() {
        char[] endCharacters = {'.', '?', '!'};
        SentenceExtractor extractor = new SentenceExtractor(endCharacters);
        assertEquals("\\.'|\\?'|\\!'|\\.\"|\\?\"|\\!\"|\\.|\\?|\\!", extractor.constructEndSentencePattern().pattern());
    }

//    @Test
//    public void testConstructPatternStringForSingleCharacter() {
//        List<String> endCharacters = new ArrayList<>();
//        endCharacters.add("\\.");
//        SentenceExtractor extractor = new SentenceExtractor(endCharacters);
//        assertEquals("\\.\'|\\.\"|\\.", extractor.constructEndSentencePattern().pattern());
//    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowExceptionGivenVoidList() {
        char[] endCharacters = {};
        SentenceExtractor extractor = new SentenceExtractor(endCharacters);
        extractor.constructEndSentencePattern();
    }

    @Test
    public void testThrowExceptionGivenNull() {
        SentenceExtractor extractor = new SentenceExtractor(new Configuration.ConfigurationBuilder().build().getSymbolTable());
        extractor.constructEndSentencePattern(); // not a throw exception
    }
}
