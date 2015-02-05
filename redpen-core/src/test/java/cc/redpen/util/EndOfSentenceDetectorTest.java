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
package cc.redpen.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

public class EndOfSentenceDetectorTest {
    private static <E> List<E> generateUmList(E... args) {
        return new ArrayList<>(Arrays.asList(args));
    }

    @Test
    public void testEndPosition() {
        Pattern pattern = Pattern.compile("\\.");
        String str = "this is a pen.";
        EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
        assertEquals(13, detector.getSentenceEndPosition(str));
    }

    @Test
    public void testEndPositionSpecifyingStartPositon() {
        Pattern pattern = Pattern.compile("\\.");
        String str = "Right. That is not a pen.";
        EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
        assertEquals(5, detector.getSentenceEndPosition(str, 0));
        assertEquals(24, detector.getSentenceEndPosition(str, 6));
    }

    @Test
    public void testEndPositionWithTailingSpace() {
        Pattern pattern = Pattern.compile("\\.");
        String str = "this is a pen. ";
        EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
        assertEquals(13, detector.getSentenceEndPosition(str));
    }

    @Test
    public void testEndPositionInMultipleSentence() {
        Pattern pattern = Pattern.compile("\\.");
        String str = "this is a pen. that is not pen.";
        EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
        assertEquals(13, detector.getSentenceEndPosition(str));
    }


    @Test
    public void testEndPositionInMultipleSentencesInMultipleLines() {
        Pattern pattern = Pattern.compile("\\.");
        String str = "this is a pen.\nthat is not pen.";
        EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
        assertEquals(13, detector.getSentenceEndPosition(str));
    }


    @Test
    public void tesEndPositionForPartialSentence() {
        Pattern pattern = Pattern.compile("\\.");
        String str = "this is a pen. that is not";
        EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
        assertEquals(13, detector.getSentenceEndPosition(str));
    }

    @Test
    public void testEndPositionInJapanese() {
        Pattern pattern = Pattern.compile("。");
        String str = "私はペンではない。私は人間です。";
        EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
        assertEquals(8, detector.getSentenceEndPosition(str));
    }

    @Test
    public void testEndPositionInJapaneseWithSpace() {
        Pattern pattern = Pattern.compile("。");
        String str = "私はペンではない。 私は人間です。";
        EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
        assertEquals(8, detector.getSentenceEndPosition(str));
    }

    @Test
    public void tesEndPositionMultipleDodsWithSpace() {
        Pattern pattern = Pattern.compile("\\.");
        String str = "this is a pen... ";
        EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
        assertEquals(15, detector.getSentenceEndPosition(str));
    }

    @Test
    public void tesEndPositionDods() {
        Pattern pattern = Pattern.compile("\\.");
        String str = "this is a pen...";
        EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
        assertEquals(15, detector.getSentenceEndPosition(str));
    }

    @Test
    public void tesEndPositionDodsWithinTwoSencences() {
        Pattern pattern = Pattern.compile("\\.");
        String str = "this is a pen... But that is a pencil.";
        EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
        assertEquals(15, detector.getSentenceEndPosition(str));
    }

    @Test
    public void tesEndPositionDodsWithinTwoSencencesWithoutSpace() {
        Pattern pattern = Pattern.compile("\\.");
        String str = "this is a pen...But that is a pencil.";
        EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
        assertEquals(36, detector.getSentenceEndPosition(str));
    }

    @Test
    public void tesEndPositionDodsWithinTwoSencencesWithoutSpace2() {
        Pattern pattern = Pattern.compile("\\.");
        String str = "this is a pen...But that is a pencil. ";
        EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
        assertEquals(36, detector.getSentenceEndPosition(str));
    }

    @Test
    public void tesEndPositionDodsWithinTwoJapaneseSencences() {
        Pattern pattern = Pattern.compile("。");
        String str = "これは。。。 ペンですか。";
        EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
        assertEquals(5, detector.getSentenceEndPosition(str));
    }

    @Test
    public void tesEndPositionDodsWithinTwoJapaneseSencencesWithoutSpace() {
        Pattern pattern = Pattern.compile("。");
        String str = "これは。。。ペンですか。";
        EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
        assertEquals(5, detector.getSentenceEndPosition(str));
    }

    @Test
    public void tesEndPositionForPartialJapaneseSentence() {
        Pattern pattern = Pattern.compile("。");
        String str = "異なる。たとえば，";
        EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
        assertEquals(3, detector.getSentenceEndPosition(str));
    }

    @Test
    public void tesEndPositionForMultipleJapaneseSentencesSplitWithEndOfPosition() {
        Pattern pattern = Pattern.compile("。");
        String str = "異なる。\nたとえば，";
        EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
        assertEquals(3, detector.getSentenceEndPosition(str));
    }

    @Test
    public void testEndPositionContainingMultipleSymbold() {
        Pattern pattern = Pattern.compile("\\?|\\.");
        String str = "is this a pen? yes it is.";
        EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
        assertEquals(13, detector.getSentenceEndPosition(str));
    }

    @Test
    public void testEndPositionContainingMultipleNonAsciiSymbols() {
        Pattern pattern = Pattern.compile("。|？");
        String str = "これは群馬ですか？いいえ埼玉です。";
        EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
        assertEquals(8, detector.getSentenceEndPosition(str));
    }

    @Test
    public void testEndPositionOfSentenceWithQuotationMark() {
        Pattern pattern = Pattern.compile("\\.\"");
        String str = "\"pen.\"";
        EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern);
        assertEquals(5, detector.getSentenceEndPosition(str));
    }

    @Test
    public void testEndPositionWithWhiteWord() {
        Pattern pattern = Pattern.compile("\\.");
        String str = "He is Mr. United States.";
        List<String> whiteList = generateUmList("Mr.");
        EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern, whiteList);
        assertEquals(23, detector.getSentenceEndPosition(str));
    }

    @Test
    public void testEndPositionWithMultipleWhiteWords() {
        Pattern pattern = Pattern.compile("\\.");
        String str = "This Jun. 10th, he was Mr. United States.";
        List<String> whiteList = generateUmList("Mr.", "Jun.");
        EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern, whiteList);
        assertEquals(40, detector.getSentenceEndPosition(str));
    }

    @Test
    public void testEndPositionWithWhiteWordsContainsPeriodInternally() {
        Pattern pattern = Pattern.compile("\\.");
        String str = "At 10 a.m. we had a lunch.";
        List<String> whiteList = generateUmList("a.m.");
        EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern, whiteList);
        assertEquals(25, detector.getSentenceEndPosition(str));
    }

    @Test
    public void testEndPositionWithWhiteWordAndWithoutEndPeriod() {
        Pattern pattern = Pattern.compile("\\.");
        String str = "He is Mr. United States";
        List<String> whiteList = generateUmList("Mr.");
        EndOfSentenceDetector detector = new EndOfSentenceDetector(pattern, whiteList);
        assertEquals(-1, detector.getSentenceEndPosition(str));
    }
}
