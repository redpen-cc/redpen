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

class SentenceLengthValidatorForTest extends SentenceLengthValidator {
  protected void setMaxLength(int maxLength) {
    this.maxLength = maxLength;
  }
}

public class SentenceLengthValidatorTest {
  @Test
  public void testWithLongSentence(){
    SentenceLengthValidatorForTest validator = new SentenceLengthValidatorForTest();
    validator.setMaxLength(30);
    Sentence str = new Sentence("this is a very long long long long long long"
        + "long long long long long long sentence.",0);
    List<ValidationError> error = validator.check(str);
    assertNotNull(error);
    assertEquals(1, error.size());
  }

  @Test
  public void testWithShortSentence(){
    SentenceLengthValidatorForTest validator = new SentenceLengthValidatorForTest();
    validator.setMaxLength(30);
    Sentence str = new Sentence("this is a sentence.",0);
    List<ValidationError> error = validator.check(str);
    assertNotNull(error);
    assertEquals(0, error.size());
  }

  @Test
  public void testWithZeroLengthSentence(){
    SentenceLengthValidatorForTest validator = new SentenceLengthValidatorForTest();
    validator.setMaxLength(30);
    Sentence str = new Sentence("",0);
    List<ValidationError> error = validator.check(str);
    assertNotNull(error);
    assertEquals(0, error.size());
  }
}
