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

import java.util.List;

import org.junit.Test;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.validator.sentence.WordNumberValidator;

public class WordNumberValidatorTest {

  @Test
  public void testWithShortSentence() {
    WordNumberValidator maxWordNumberValidator = new WordNumberValidator();
    Sentence str = new Sentence(
        "this sentence is short.",0);
    List<ValidationError> errors = maxWordNumberValidator.check(str);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }

  @Test
  public void testWithLongSentence() {
    WordNumberValidator maxWordNumberValidator = new WordNumberValidator();
    Sentence str = new Sentence(
        "this sentence is very very very very very very very very very very" +
        " very very very very very very very very very very very very very very long",0);
    List<ValidationError> errors = maxWordNumberValidator.check(str);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }

  @Test
  public void testWithZeroLengthSentence() {
    WordNumberValidator maxWordNumberValidator = new WordNumberValidator();
    Sentence str = new Sentence("", 0);
    List<ValidationError> errors = maxWordNumberValidator.check(str);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }
}
