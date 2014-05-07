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
package org.bigram.docvalidator.validator.sentence;

import static org.junit.Assert.*;

import java.util.List;

import org.bigram.docvalidator.validator.sentence.SpaceBeginningOfSentenceValidator;
import org.junit.Test;
import org.bigram.docvalidator.model.Sentence;
import org.bigram.docvalidator.ValidationError;

public class SpaceBeginningOfSpenceValidatorTest {

  @Test
  public void testProcessSetenceWithoutEndSpace() {
    SpaceBeginningOfSentenceValidator spaceValidator =
        new SpaceBeginningOfSentenceValidator();
    Sentence str = new Sentence("That is true.",0);
    List<ValidationError> errors = spaceValidator.validate(str);
    assertNotNull(errors);
    assertEquals(1, errors.size());
  }

  @Test
  public void testProcessEndSpace() {
    SpaceBeginningOfSentenceValidator spaceValidator =
        new SpaceBeginningOfSentenceValidator();
    Sentence str = new Sentence(" That is true.",0);
    List<ValidationError> errors = spaceValidator.validate(str);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }

  @Test
  public void testProcessHeadSentenceInAParagraph() {
    SpaceBeginningOfSentenceValidator spaceValidator =
        new SpaceBeginningOfSentenceValidator();
    Sentence str = new Sentence("That is true.",0);
    str.isFirstSentence = true;
    List<ValidationError> errors = spaceValidator.validate(str);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }

  @Test
  public void testProcessZerorLengthSentence() {
    SpaceBeginningOfSentenceValidator spaceValidator =
        new SpaceBeginningOfSentenceValidator();
    Sentence str = new Sentence("",0);
    str.isFirstSentence = true;
    List<ValidationError> errors = spaceValidator.validate(str);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }
}
