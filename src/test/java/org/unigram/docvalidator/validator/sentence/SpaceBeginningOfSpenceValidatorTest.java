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

public class SpaceBeginningOfSpenceValidatorTest {

  @Test
  public void testProcessSetenceWithoutEndSpace() {
    SpaceBeginningOfSentenceValidator spaceValidator =
        new SpaceBeginningOfSentenceValidator();
    Sentence str = new Sentence("That is true.",0);
    List<ValidationError> errors = spaceValidator.check(str);
    assertNotNull(errors);
    assertEquals(1, errors.size());
  }

  @Test
  public void testProcessEndSpace() {
    SpaceBeginningOfSentenceValidator spaceValidator =
        new SpaceBeginningOfSentenceValidator();
    Sentence str = new Sentence(" That is true.",0);
    List<ValidationError> errors = spaceValidator.check(str);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }

  @Test
  public void testProcessHeadSentenceInAParagraph() {
    SpaceBeginningOfSentenceValidator spaceValidator =
        new SpaceBeginningOfSentenceValidator();
    Sentence str = new Sentence("That is true.",0);
    str.isStartParagraph = true;
    List<ValidationError> errors = spaceValidator.check(str);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }

  @Test
  public void testProcessZerorLengthSentence() {
    SpaceBeginningOfSentenceValidator spaceValidator =
        new SpaceBeginningOfSentenceValidator();
    Sentence str = new Sentence("",0);
    str.isStartParagraph = true;
    List<ValidationError> errors = spaceValidator.check(str);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }
}
