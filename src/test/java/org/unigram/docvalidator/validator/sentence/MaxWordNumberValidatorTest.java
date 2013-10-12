package org.unigram.docvalidator.validator.sentence;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.validator.sentence.MaxWordNumberValidator;

public class MaxWordNumberValidatorTest {

  @Test
  public void testWithShortSentence() {
    MaxWordNumberValidator maxWordNumberValidator = new MaxWordNumberValidator();
    Sentence str = new Sentence(
        "this sentence is short.",0);
    List<ValidationError> errors = maxWordNumberValidator.check(str);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }

  @Test
  public void testWithLongSentence() {
    MaxWordNumberValidator maxWordNumberValidator = new MaxWordNumberValidator();
    Sentence str = new Sentence(
        "this sentence is very very very very very very very very very very" +
        " very very very very very very very very very very very very very very long",0);
    List<ValidationError> errors = maxWordNumberValidator.check(str);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }

}
