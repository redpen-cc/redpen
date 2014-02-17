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
package org.unigram.docvalidator.validator.sentence;

import static org.junit.Assert.*;

import java.util.List;
import org.junit.Test;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.ValidationError;

class SentenceLengthValidatorForTest extends SentenceLengthValidator {
  protected void setLengthLimit(int maxLength) {
    this.setMaxLength(maxLength);
  }
}

public class SentenceLengthValidatorTest {
  @Test
  public void testWithLongSentence(){
    SentenceLengthValidatorForTest validator = new SentenceLengthValidatorForTest();
    validator.setLengthLimit(30);
    Sentence str = new Sentence("this is a very long long long long long long"
        + "long long long long long long sentence.",0);
    List<ValidationError> error = validator.check(str);
    assertNotNull(error);
    assertEquals(1, error.size());
  }

  @Test
  public void testWithShortSentence(){
    SentenceLengthValidatorForTest validator = new SentenceLengthValidatorForTest();
    validator.setLengthLimit(30);
    Sentence str = new Sentence("this is a sentence.",0);
    List<ValidationError> error = validator.check(str);
    assertNotNull(error);
    assertEquals(0, error.size());
  }

  @Test
  public void testWithZeroLengthSentence(){
    SentenceLengthValidatorForTest validator = new SentenceLengthValidatorForTest();
    validator.setLengthLimit(30);
    Sentence str = new Sentence("",0);
    List<ValidationError> error = validator.check(str);
    assertNotNull(error);
    assertEquals(0, error.size());
  }
}
