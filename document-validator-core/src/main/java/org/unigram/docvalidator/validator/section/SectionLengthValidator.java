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
package org.unigram.docvalidator.validator.section;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.store.Paragraph;
import org.unigram.docvalidator.store.Section;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.util.ValidatorConfiguration;
import org.unigram.docvalidator.validator.SectionValidator;

/**
 * Validate the length of one section.
 */
public class SectionLengthValidator extends SectionValidator {
  /**
   * Constructor.
   */
  public SectionLengthValidator() {
    super();
  }

  @Override
  protected List<ValidationError> check(Section section) {
    List<ValidationError> validationErrors = new ArrayList<ValidationError>();
    int sectionCharNumber = 0;
    for (Iterator<Paragraph> paraIterator =
        section.getParagraphs(); paraIterator.hasNext();) {
      Paragraph currentPraParagraph = paraIterator.next();
      for (Iterator<Sentence> sentenceIterator =
          currentPraParagraph.getSentences(); sentenceIterator.hasNext();) {
        Sentence sentence = sentenceIterator.next();
        sectionCharNumber += sentence.content.length();
      }
      if (sectionCharNumber > maxSectionCharNumber) {
        ValidationError error = new ValidationError(
            "The number of the character exceeds the maximum \""
                + String.valueOf(sectionCharNumber) + "\".",
                section.getHeaderContent(0));
        validationErrors.add(error);
      }
    }
    return validationErrors;
  }

  @Override
  public boolean loadConfiguration(ValidatorConfiguration conf,
      CharacterTable characterTable) {
    if (conf.getAttribute("max_char_number") == null) {
      this.maxSectionCharNumber = DEFAULT_MAXIMUM_CHAR_NUMBER_IN_A_SECTION;
      LOG.info("max_char_number was not set.");
      LOG.info("Using the default value of max_char_number.");
    } else {
      this.maxSectionCharNumber = Integer.valueOf(
          conf.getAttribute("max_char_number"));
    }
    return true;
  }

  private static final int DEFAULT_MAXIMUM_CHAR_NUMBER_IN_A_SECTION = 1000;

  private static final Logger LOG =
      LoggerFactory.getLogger(SectionLengthValidator.class);

  protected int maxSectionCharNumber;

}
