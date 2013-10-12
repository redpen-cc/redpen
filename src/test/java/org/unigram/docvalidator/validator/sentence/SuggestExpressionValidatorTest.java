package org.unigram.docvalidator.validator.sentence;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.validator.sentence.SuggestExpressionValidator;


class SuggestExpressionValidatorForTest extends SuggestExpressionValidator {
  void loadSynonyms () {
    synonms = new HashMap<String, String>();
    synonms.put("like","such as");
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
  }
}
