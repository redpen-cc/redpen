package org.unigram.docvalidator.parser;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.unigram.docvalidator.store.Sentence;

public class ParseUtilsTest {

  @Test
  public void testSimple() {
    List<Sentence> outputSentences = new ArrayList<Sentence>();
    String remain = ParseUtils.extractSentences("this is a pen.", ".",
        outputSentences);
    assertEquals(1, outputSentences.size());
    assertEquals("", remain);
  }

  @Test
  public void testMultipleSentences() {
    List<Sentence> outputSentences = new ArrayList<Sentence>();
    String remain = ParseUtils.extractSentences("this is a pen. that is a paper.", ".",
        outputSentences);
    assertEquals(2, outputSentences.size());
    assertEquals("", remain);
  }

  @Test
  public void testMultipleSentencesWithoutPeriodInTheEnd() {
    List<Sentence> outputSentences = new ArrayList<Sentence>();
    String remain = ParseUtils.extractSentences("this is a pen. that is a paper", ".",
        outputSentences);
    assertEquals(1, outputSentences.size());
    assertEquals(" that is a paper", remain); // NOTE: second sentence start with white space.
  }

  @Test
  public void testVoidLine() {
    List<Sentence> outputSentences = new ArrayList<Sentence>();
    String remain = ParseUtils.extractSentences("", ".",
        outputSentences);
    assertEquals(0, outputSentences.size());
    assertEquals(remain, ""); // NOTE: second sentence start with white space.
  }

  @Test
  public void testJustPeriodLine() {
    List<Sentence> outputSentences = new ArrayList<Sentence>();
    String remain = ParseUtils.extractSentences(".", ".",
        outputSentences);
    assertEquals(1, outputSentences.size());
    assertEquals("", remain);
  }

}
