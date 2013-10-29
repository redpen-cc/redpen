package org.unigram.docvalidator.validator.sentence;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.ValidationError;

public class QuotationValidatorTest {
  @Test
  public void testDoubleQuotationMakrs() {
    QuotationValidator validator =
        new QuotationValidator();
    Sentence str = new Sentence("I said “That is true”.",0);
    List<ValidationError> errors = validator.check(str);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }

  @Test
  public void testSingleQuotationMakrs() {
    QuotationValidator validator =
        new QuotationValidator();
    Sentence str = new Sentence("I said ‘that is true’.",0);
    List<ValidationError> errors = validator.check(str);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }

  @Test
  public void testDoubleQuotationMakrWithoutRight() {
    QuotationValidator validator =
        new QuotationValidator();
    Sentence str = new Sentence("I said “That is true.",0);
    List<ValidationError> errors = validator.check(str);
    assertNotNull(errors);
    assertEquals(1, errors.size());
  }

  @Test
  public void testSingleQuotationMakrWithoutRight() {
    QuotationValidator validator =
        new QuotationValidator();
    Sentence str = new Sentence("I said ‘that is true.",0);
    List<ValidationError> errors = validator.check(str);
    assertNotNull(errors);
    assertEquals(1, errors.size());
  }

  @Test
  public void testDoubleQuotationMakrWithoutLeft() {
    QuotationValidator validator =
        new QuotationValidator();
    Sentence str = new Sentence("I said That is true”.",0);
    List<ValidationError> errors = validator.check(str);
    assertNotNull(errors);
    assertEquals(1, errors.size());
  }

  @Test
  public void testSingleQuotationMakrkWithoutLeft() {
    QuotationValidator validator =
        new QuotationValidator();
    Sentence str = new Sentence("I said that is true’.",0);
    List<ValidationError> errors = validator.check(str);
    assertNotNull(errors);
    assertEquals(1, errors.size());
  }

  @Test
  public void testExceptionCase() {
    QuotationValidator validator =
        new QuotationValidator();
    Sentence str = new Sentence("I’m a jedi knight.",0);
    List<ValidationError> errors = validator.check(str);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }

  @Test
  public void testQuotedExceptionCase() {
    QuotationValidator validator =
        new QuotationValidator();
    Sentence str = new Sentence("he said ‘I’m a jedi knight’.",0);
    List<ValidationError> errors = validator.check(str);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }

  @Test
  public void testDoubleLeftSingleQuotationMakrk() {
    QuotationValidator validator =
        new QuotationValidator();
    Sentence str = new Sentence("I said ‘that is true‘.",0);
    List<ValidationError> errors = validator.check(str);
    assertNotNull(errors);
    assertEquals(1, errors.size());
  }

  @Test
  public void testDoubleLeftDoubleQuotationMakrk() {
    QuotationValidator validator =
        new QuotationValidator();
    Sentence str = new Sentence("I said “that is true.“",0);
    List<ValidationError> errors = validator.check(str);
    assertNotNull(errors);
    assertEquals(1, errors.size());
  }

  @Test
  public void testDoubleRightSingleQuotationMakrk() {
    QuotationValidator validator =
        new QuotationValidator();
    Sentence str = new Sentence("I said ’that is true’.",0);
    List<ValidationError> errors = validator.check(str);
    assertNotNull(errors);
    assertEquals(1, errors.size());
  }

  @Test
  public void testDoubleRightDoubleQuotationMakrk() {
    QuotationValidator validator =
        new QuotationValidator();
    Sentence str = new Sentence("I said ”that is true”.",0);
    List<ValidationError> errors = validator.check(str);
    assertNotNull(errors);
    assertEquals(1, errors.size());
  }

  @Test
  public void testAsciiExceptionCase() {
    QuotationValidator validator =
        new QuotationValidator(true);
    Sentence str = new Sentence("I'm a jedi knight.",0);
    List<ValidationError> errors = validator.check(str);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }

  @Test
  public void testAsciiDoubleQuotationMakrk() {
    QuotationValidator validator =
        new QuotationValidator(true);
    Sentence str = new Sentence("I said \"that is true\".",0);
    List<ValidationError> errors = validator.check(str);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }

  @Test
  public void testNoQuotationMakrk() {
    QuotationValidator validator =
        new QuotationValidator(true);
    Sentence str = new Sentence("I said that is true.",0);
    List<ValidationError> errors = validator.check(str);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }

  @Test
  public void testNoInput() {
    QuotationValidator validator =
        new QuotationValidator(true);
    Sentence str = new Sentence("",0);
    List<ValidationError> errors = validator.check(str);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }

  @Test
  public void testTwiceQuotations() {
    QuotationValidator validator =
        new QuotationValidator();
    Sentence str = new Sentence("I said ‘that is true’ and not said ‘that is false’",0);
    List<ValidationError> errors = validator.check(str);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }

  @Test
  public void testOneOfFailureInTwiceQuotations() {
    QuotationValidator validator =
        new QuotationValidator();
    Sentence str = new Sentence("I said ‘that is true and not said ‘that is false’",0);
    List<ValidationError> errors = validator.check(str);
    assertNotNull(errors);
    assertEquals(1, errors.size());
  }

  @Test
  public void testLeftDoubleQuotationsWihtoutSpace() {
    QuotationValidator validator =
        new QuotationValidator();
    Sentence str = new Sentence("I said“that is true”.",0);
    List<ValidationError> errors = validator.check(str);
    assertNotNull(errors);
    assertEquals(1, errors.size());
  }

  @Test
  public void testLeftAsciiDoubleQuotationsWihtoutSpace() {
    QuotationValidator validator =
        new QuotationValidator(true);
    Sentence str = new Sentence("I said\"that is true\".",0);
    List<ValidationError> errors = validator.check(str);
    assertNotNull(errors);
    assertEquals(1, errors.size());
  }

  @Test
  public void testRightDoubleQuotationsWihtoutSpace() {
    QuotationValidator validator =
        new QuotationValidator();
    Sentence str = new Sentence("I said “that is true”is true.",0);
    List<ValidationError> errors = validator.check(str);
    assertNotNull(errors);
    assertEquals(1, errors.size());
  }

  @Test
  public void testRightAsciiDoubleQuotationsWihtoutSpace() {
    QuotationValidator validator =
        new QuotationValidator(true);
    Sentence str = new Sentence("I said \"that is true\"is true.",0);
    List<ValidationError> errors = validator.check(str);
    assertNotNull(errors);
    assertEquals(1, errors.size());
  }
}
