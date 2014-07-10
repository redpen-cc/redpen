package org.bigram.docvalidator.validator.sentence;

import org.bigram.docvalidator.DocumentValidator;
import org.bigram.docvalidator.DocumentValidatorException;
import org.bigram.docvalidator.ValidationError;
import org.bigram.docvalidator.config.Configuration;
import org.bigram.docvalidator.config.ValidatorConfiguration;
import org.bigram.docvalidator.distributor.FakeResultDistributor;
import org.bigram.docvalidator.model.DocumentCollection;
import org.bigram.docvalidator.model.Sentence;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class InvalidWordValidatorTest {

  @Test
  public void testSimpleRun() {
    InvalidWordValidator validator = new InvalidWordValidator();
    validator.addInvalid("foolish");
    List<ValidationError> errors = validator.validate(new Sentence("He is a foolish guy.", 0));
    assertEquals(1, errors.size());
  }

  @Test
  public void testVoid() {
    InvalidWordValidator validator = new InvalidWordValidator();
    validator.addInvalid("foolish");
    List<ValidationError> errors = validator.validate(new Sentence("", 0));
    assertEquals(0, errors.size());
  }

  @Test
  public void testLoadDefaultDictionary() throws DocumentValidatorException {
    Configuration config = new Configuration.Builder()
        .addSentenceValidatorConfig(new ValidatorConfiguration("InvalidWord"))
        .setCharacterTable("en").build();

    DocumentCollection documents = new DocumentCollection.Builder()
        .addDocument("")
        .addSection(1, new ArrayList<Sentence>())
        .addParagraph()
        .addSentence(
            "he is a foolish man.",
            1)
        .build();

    DocumentValidator validator = new DocumentValidator.Builder()
        .setConfiguration(config)
        .setResultDistributor(new FakeResultDistributor())
        .build();

    List<ValidationError> errors = validator.check(documents);
    assertEquals(1, errors.size());
    assertTrue(errors.get(0).getMessage().contains("foolish"));
  }

  /**
   * Assert not throw a exception even when there is no default dictionary.
   *
   * @throws DocumentValidatorException
   */
  @Test
  public void testLoadNotExistDefaultDictionary() throws DocumentValidatorException {
    Configuration config = new Configuration.Builder()
        .addSentenceValidatorConfig(new ValidatorConfiguration("InvalidWord"))
        .setCharacterTable("ja").build(); // NOTE: no dictionary for japanese or other languages whose words are not split by white space.

    DocumentCollection documents = new DocumentCollection.Builder()
        .addDocument("")
        .addSection(1, new ArrayList<Sentence>())
        .addParagraph()
        .addSentence(
            "こんにちは、群馬にきました。",
            1)
        .build();

    DocumentValidator validator = new DocumentValidator.Builder()
        .setConfiguration(config)
        .setResultDistributor(new FakeResultDistributor())
        .build();

    List<ValidationError> errors = validator.check(documents);
    assertEquals(0, errors.size());
  }
}
