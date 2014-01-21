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
 * Validate the number of commas in one sentence.
 */
public class CommaNumberValidator implements SentenceValidator {
  /**
   * Default maximum number of comma.
   */
  public static final int DEFAULT_MAX_COMMA_NUMBER = 3;

  /**
   * Default comma character.
   */
  public static final String DEFAULT_COMMA = ",";

  /**
   * Constructor.
   */
  public CommaNumberValidator() {
    super();
    maxCommaNum = DEFAULT_MAX_COMMA_NUMBER;
    comma = DEFAULT_COMMA;
  }

  public List<ValidationError> check(Sentence line) {
    List<ValidationError> result = new ArrayList<ValidationError>();
    String content = line.content;
    int commaCount =  0;
    int position = 0;
    while (position != -1) {
      position = content.indexOf(this.comma);
      commaCount++;
      content = content.substring(position + 1, content.length());
    }
    if (maxCommaNum < commaCount) {
      result.add(new ValidationError(
          "The number of comma is exceeds the maximum \""
          + String.valueOf(commaCount) + "\".", line));
    }
    return result;
  }

  public boolean initialize(ValidatorConfiguration conf, CharacterTable characterTable)
      throws DocumentValidatorException {
    //TODO search parent configurations to get comma settings...
    this.maxCommaNum = DEFAULT_MAX_COMMA_NUMBER;
    if (conf.getAttribute("max_comma_num") != null) {
      this.maxCommaNum = Integer.valueOf(conf.getAttribute("max_length"));
      LOG.info("Maximum number of comma in one sentence is set to "
          + this.maxCommaNum);
    } else {
      LOG.info("Maximum number of comma in one sentence is not set.");
      LOG.info("Using the default value.");
    }
    this.comma = DEFAULT_COMMA;
    if (characterTable.isContainCharacter("COMMA")) {
      this.comma = characterTable.getCharacter("COMMA").getValue();
      LOG.info("comma is set to \"" + this.comma + "\"");
    } else {
      this.maxCommaNum = Integer.valueOf(conf.getAttribute("max_length"));
    }
    return true;
  }

  private static final Logger LOG =
      LoggerFactory.getLogger(CommaNumberValidator.class);

  private int maxCommaNum;

  private String comma;
}
