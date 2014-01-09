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
package org.unigram.docvalidator.validator.sentence.lang.ja;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.ValidationError;

public class KatakanaEndHyphenValidatorTest {
  @Test
  public void testEmptyString() {
    KatakanaEndHyphenValidator validator
      = new KatakanaEndHyphenValidator();
    Sentence str = new Sentence("", 0);
    List<ValidationError> errors = validator.check(str);
    assertEquals(str.toString(), 0, errors.size());
  }

  @Test
  public void testSingleHiragana() {
    KatakanaEndHyphenValidator validator
      = new KatakanaEndHyphenValidator();
    Sentence str = new Sentence("あ", 0);
    List<ValidationError> errors = validator.check(str);
    assertEquals(str.toString(), 0, errors.size());
  }

  @Test
  public void testSingleKatakana() {
    KatakanaEndHyphenValidator validator
      = new KatakanaEndHyphenValidator();
    Sentence str = new Sentence("ア", 0);
    List<ValidationError> errors = validator.check(str);
    assertEquals(str.toString(), 0, errors.size());
  }

  @Test
  public void testKatakanaOfLength2() {
    KatakanaEndHyphenValidator validator
      = new KatakanaEndHyphenValidator();
    Sentence str = new Sentence("ドア", 0);
    List<ValidationError> errors = validator.check(str);
    assertEquals(str.toString(), 0, errors.size());
  }

  @Test
  public void testKatakanaOfLength3andHyphen() {
    KatakanaEndHyphenValidator validator
      = new KatakanaEndHyphenValidator();
    Sentence str = new Sentence("ミラー", 0);
    List<ValidationError> errors = validator.check(str);
    assertEquals(str.toString(), 0, errors.size());
  }

  @Test
  public void testKatakanaOfLength4andHyphen() {
    KatakanaEndHyphenValidator validator
      = new KatakanaEndHyphenValidator();
    Sentence str = new Sentence("コーヒー", 0); // This is an error.
    List<ValidationError> errors = validator.check(str);
    assertEquals(str.toString(), 1, errors.size());
  }

  @Test
  public void testSentenceBeginningWithKatakanaWithHypen() {
    KatakanaEndHyphenValidator validator
      = new KatakanaEndHyphenValidator();
    Sentence str = new Sentence("コンピューターが壊れた。", 0);
    List<ValidationError> errors = validator.check(str);
    assertEquals(str.toString(), 1, errors.size());
  }

  @Test
  public void testSentenceBeginningWithKatakanaWithoutHypen() {
    KatakanaEndHyphenValidator validator
      = new KatakanaEndHyphenValidator();
    Sentence str = new Sentence("コンピュータが壊れた。", 0);
    List<ValidationError> errors = validator.check(str);
    assertEquals(str.toString(), 0, errors.size());
  }

  @Test
  public void testSentenceContainKatakanaWithHypen() {
    KatakanaEndHyphenValidator validator
      = new KatakanaEndHyphenValidator();
    Sentence str = new Sentence("僕のコンピューターが壊れた。", 0);
    List<ValidationError> errors = validator.check(str);
    assertEquals(str.toString(), 1, errors.size());
  }

  @Test
  public void testSentenceContainKatakanaWitouthHypen() {
    KatakanaEndHyphenValidator validator
      = new KatakanaEndHyphenValidator();
    Sentence str = new Sentence("僕のコンピュータが壊れた。", 0);
    List<ValidationError> errors = validator.check(str);
    assertEquals(str.toString(), 0, errors.size());
  }

  @Test
  public void testSentenceEndingWithKatakanaWithHypen() {
    KatakanaEndHyphenValidator validator
      = new KatakanaEndHyphenValidator();
    Sentence str = new Sentence("僕のコンピューター", 0);
    List<ValidationError> errors = validator.check(str);
    assertEquals(str.toString(), 1, errors.size());
  }

  @Test
  public void testSentenceEndingWithKatakanaWithoutHypen() {
    KatakanaEndHyphenValidator validator
      = new KatakanaEndHyphenValidator();
    Sentence str = new Sentence("僕のコンピュータ", 0);
    List<ValidationError> errors = validator.check(str);
    assertEquals(str.toString(), 0, errors.size());
  }

  @Test
  public void testSentenceContainWithKatakanaMiddleDot() {
    KatakanaEndHyphenValidator validator
      = new KatakanaEndHyphenValidator();
    Sentence str = new Sentence("コーヒー・コンピューター", 0);
    List<ValidationError> errors = validator.check(str);
    assertEquals(str.toString(), 2, errors.size());
  }
}
