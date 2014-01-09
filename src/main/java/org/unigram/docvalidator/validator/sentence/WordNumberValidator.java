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
import org.unigram.docvalidator.util.ValidatorConfiguration;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.validator.SentenceValidator;

/**
 * Validate input sentences have more words than specified.
 */
public class WordNumberValidator implements SentenceValidator {
  /**
   * Default maximum number of words in one sentence.
   */
  @SuppressWarnings("WeakerAccess")
  public static final int DEFAULT_MAXIMUM_WORDS_IN_A_SENTENCE = 30;

  public WordNumberValidator() {
    super();
    this.maxWordNumber = DEFAULT_MAXIMUM_WORDS_IN_A_SENTENCE;
  }

  public List<ValidationError> check(Sentence sentence) {
    List<ValidationError> result = new ArrayList<ValidationError>();
    String content = sentence.content;
    String[] wordList = content.split(" ");
    int wordNum = wordList.length;
    if (wordNum > maxWordNumber) {
      result.add(new ValidationError(
          "The number of the words exceeds the maximum "
          + String.valueOf(wordNum), sentence));
    }
    return result;
  }

  public boolean initialize(ValidatorConfiguration conf, CharacterTable characterTable)
      throws DocumentValidatorException {
    if (conf.getAttribute("max_word_num") == null) {
      this.maxWordNumber = DEFAULT_MAXIMUM_WORDS_IN_A_SENTENCE;
      LOG.info("max_length was not set.");
      LOG.info("Using the default value of max_length.");
    } else {
      this.maxWordNumber = Integer.valueOf(conf.getAttribute("max_word_num"));
    }
    return true;
  }
  private static final Logger LOG =
      LoggerFactory.getLogger(WordNumberValidator.class);

  private int maxWordNumber;
}
