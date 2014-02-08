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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.ValidationError;

public class KatakanaSpellCheckValidatorTest {
  @Test
  public void testSingleSentence() {
    KatakanaSpellCheckValidator validator
      = new KatakanaSpellCheckValidator();
    Sentence st = new Sentence("ハロー、ハロ。"
      + "あのインデクスとこのインデックス"
      , 0); 
    List<ValidationError> errors = validator.check(st);
    // We do not detect "ハロー" and "ハロ" as a similar pair,
    // but "インデクス" and "インデックス".
    assertEquals(st.toString(), 1, errors.size());
  }

  @Test
  public void testMultiSentence() {
    KatakanaSpellCheckValidator validator
      = new KatakanaSpellCheckValidator();
    List<ValidationError> errors = new ArrayList<ValidationError>();
    Sentence st;
    st = new Sentence("フレーズ・アナライズにバグがある", 0);
    errors.addAll(validator.check(st));
    assertEquals(st.toString(), 0, errors.size());
    st = new Sentence("バグのあるフェーズ・アナライシス", 1);
    errors.addAll(validator.check(st));
    // We detect a similar pair of "フレーズ・アナライズ"
    // and "フェーズ・アナライシス".   
    assertEquals(st.toString(), 1, errors.size());
  }
}
