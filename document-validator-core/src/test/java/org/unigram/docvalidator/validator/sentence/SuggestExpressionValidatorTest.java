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
package org.unigram.docvalidator.validator.sentence;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.ValidationError;


class SuggestExpressionValidatorForTest extends SuggestExpressionValidator {
  void loadSynonyms () {
    synonyms = new HashMap<String, String>();
    synonyms.put("like","such as");
    synonyms.put("info","infomation");
  }
}

public class SuggestExpressionValidatorTest {
  @Test
  public void testSynonym() {
    SuggestExpressionValidatorForTest synonymValidator = new SuggestExpressionValidatorForTest();
    synonymValidator.loadSynonyms();
    Sentence str = new Sentence("it like a piece of a cake.",0);
    List<ValidationError> error = synonymValidator.check(str);
    assertNotNull(error);
    assertEquals(1, error.size());
  }

  @Test
  public void testWitoutSynonym() {
    SuggestExpressionValidatorForTest synonymValidator = new SuggestExpressionValidatorForTest();
    synonymValidator.loadSynonyms();
    Sentence str = new Sentence("it love a piece of a cake.",0);
    List<ValidationError> error = synonymValidator.check(str);
    assertNotNull(error);
    assertEquals(0, error.size());
  }

  @Test
  public void testWithMultipleSynonyms() {
    SuggestExpressionValidatorForTest synonymValidator = new SuggestExpressionValidatorForTest();
    synonymValidator.loadSynonyms();
    Sentence str = new Sentence("it like a the info.",0);
    List<ValidationError> error = synonymValidator.check(str);
    assertNotNull(error);
    assertEquals(2, error.size());
  }

  @Test
  public void testWitoutZeroLengthSentence() {
    SuggestExpressionValidatorForTest synonymValidator = new SuggestExpressionValidatorForTest();
    synonymValidator.loadSynonyms();
    Sentence str = new Sentence("",0);
    List<ValidationError> error = synonymValidator.check(str);
    assertNotNull(error);
    assertEquals(0, error.size());
  }
}
