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
package org.unigram.docvalidator.validator.sentence.lang.ja;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.util.ValidatorConfiguration;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.validator.SentenceValidator;

public class KatakanaEndHyphenValidatorTest {
  @Test
  public void testIsKatakana() {
    assertFalse(KatakanaEndHyphenValidator.isKatakana('あ'));
    assertFalse(KatakanaEndHyphenValidator.isKatakana('ー'));
    assertTrue(KatakanaEndHyphenValidator.isKatakana('ア'));
  }

  @Test
  public void testKatakanaEndHyphenValidator() {
    KatakanaEndHyphenValidator validator = new KatakanaEndHyphenValidator();
    Sentence str;
    List<ValidationError> errors;
    str = new Sentence("カラオケ", 0);
    errors = validator.check(str);
    assertEquals("カラオケ", 0, errors.size());
    str = new Sentence("ヒーター", 0);
    errors = validator.check(str);
    assertEquals("ヒーター", 0, errors.size());
    str = new Sentence("ビター", 0);
    errors = validator.check(str);
    assertEquals("ビター", 0, errors.size());
    str = new Sentence("コンピューターが壊れた。", 0);
    errors = validator.check(str);
    assertEquals("コンピューターが壊れた。", 1, errors.size());
    str = new Sentence("壊れたコンピューター", 0);
    errors = validator.check(str);
    assertEquals("壊れたコンピューター", 1, errors.size());
    str = new Sentence("僕のコンピューターが壊れた。", 0);
    errors = validator.check(str);
    assertEquals("僕のコンピューターが壊れた。", 1, errors.size());
    str = new Sentence("僕のコンピュータが壊れた。", 0);
    errors = validator.check(str);
    assertEquals("僕のコンピュータが壊れた。", 0, errors.size());
  }
}
