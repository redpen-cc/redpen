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
package org.unigram.docvalidator.validator.sentence;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.util.ValidatorConfiguration;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.validator.SentenceValidator;

/**
 * Validate input sentences contain more characters more than specified.
 */
public class SentenceLengthValidator implements SentenceValidator {
  @SuppressWarnings("WeakerAccess")
  public static final int DEFAULT_MAX_LENGTH = 30;

  public List<ValidationError> check(Sentence line) {
    List<ValidationError> result = new ArrayList<ValidationError>();
    if (line.content.length() > maxLength) {
      result.add(new ValidationError(
          "The length of the line exceeds the maximum "
              + String.valueOf(line.content.length()) + ".",
              line));
    }
    return result;
  }

  public SentenceLengthValidator() {
    super();
    this.maxLength = DEFAULT_MAX_LENGTH;
  }

  public boolean initialize(ValidatorConfiguration conf, CharacterTable characterTable)
        throws DocumentValidatorException {
    if (conf.getAttribute("max_length") == null) {
      this.maxLength = DEFAULT_MAX_LENGTH;
      LOG.info("max_length was not set.");
      LOG.info("Using the default value of max_length.");
    } else {
      this.maxLength = Integer.valueOf(conf.getAttribute("max_length"));
    }
    return true;
  }

  private static final Logger LOG =
      LoggerFactory.getLogger(SentenceLengthValidator.class);

  protected int maxLength;
}
