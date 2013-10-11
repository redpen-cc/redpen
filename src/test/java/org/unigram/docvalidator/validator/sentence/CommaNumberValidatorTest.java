package org.unigram.docvalidator.validator.sentence;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.validator.sentence.CommaNumberValidator;

public class CommaNumberValidatorTest {

  @Test
  public void test() {
    CommaNumberValidator commaNumberValidator = new CommaNumberValidator();
    Sentence str = new Sentence(
        "is it true, not true, but it should be ture, right, or not right.",0);
    List<ValidationError> error = commaNumberValidator.process(str);
    assertNotNull(error);
  }
}
