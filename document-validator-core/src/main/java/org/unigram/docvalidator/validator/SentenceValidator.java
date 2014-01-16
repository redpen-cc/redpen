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
package org.unigram.docvalidator.validator;

import java.util.List;

import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.util.ValidatorConfiguration;
import org.unigram.docvalidator.util.DocumentValidatorException;

/**
 * Validate input sentences.
 */
public interface SentenceValidator {
  /**
   * Check input sentences and returns the invalid points.
   * @param sentence input
   * @return list of invalid points
   */
  List<ValidationError> check(Sentence sentence);

  /**
   * initialize SentenceValidator.
   * @param conf validator configuration
   * @param charTable character configuration
   * @return true when initialization succeeded, otherwise false
   * @throws DocumentValidatorException
   */
  boolean initialize(ValidatorConfiguration conf, CharacterTable charTable)
      throws DocumentValidatorException;
}
