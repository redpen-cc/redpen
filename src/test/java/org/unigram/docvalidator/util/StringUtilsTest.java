package org.unigram.docvalidator.util;

import static org.junit.Assert.*;

import org.junit.Test;

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

}
