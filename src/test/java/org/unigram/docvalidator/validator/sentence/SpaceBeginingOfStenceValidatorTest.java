package org.unigram.docvalidator.validator.sentence;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.validator.sentence.SpaceBegginingOfSentenceValidator;

public class SpaceBeginingOfStenceValidatorTest {

  @Test
  public void testProcessSetenceWithoutEndSpace() {
    SpaceBegginingOfSentenceValidator spaceValidator =
        new SpaceBegginingOfSentenceValidator();
    Sentence str = new Sentence("That is true.",0);
    List<ValidationError> errors = spaceValidator.check(str);
    assertNotNull(errors);
    assertEquals(1, errors.size());
  }

  @Test
  public void testProcessWithEndSpace() {
    SpaceBegginingOfSentenceValidator spaceValidator =
        new SpaceBegginingOfSentenceValidator();
    Sentence str = new Sentence(" That is true.",0);
    List<ValidationError> errors = spaceValidator.check(str);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }

  @Test
  public void testProcessWithHeadSentenceInAParagraph() {
    SpaceBegginingOfSentenceValidator spaceValidator =
        new SpaceBegginingOfSentenceValidator();
    Sentence str = new Sentence("That is true.",0);
    str.isStartaragraph = true;
    List<ValidationError> errors = spaceValidator.check(str);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }
}
