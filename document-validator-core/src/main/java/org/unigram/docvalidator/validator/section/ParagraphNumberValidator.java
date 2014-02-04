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

import java.util.List;
import java.util.ArrayList;

import org.unigram.docvalidator.store.Section;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.ValidatorConfiguration;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.validator.SectionValidator;

/**
 * Validate paragraph number. If a section has paragraphs more than specified,
 * This validator reports it.
 */
public class ParagraphNumberValidator extends SectionValidator {
  /**
   * Default maximum number of paragraphs in a section.
   */
  @SuppressWarnings("WeakerAccess")
  public static final int DEFAULT_MAX_PARAGRAPHS_IN_A_SECTION = 100;

  @Override
  protected List<ValidationError> check(Section section) {
    List<ValidationError> validationErrors = new ArrayList<ValidationError>();
    int paragraphNumber = section.getNumberOfParagraphs();
    if (maxParagraphs < paragraphNumber) {
      validationErrors.add(new ValidationError(
          "The number of the paragraphs exceeds the maximum "
              + String.valueOf(paragraphNumber), section.getHeaderContent(0)));
      return validationErrors;
    }
    return validationErrors;
  }

  @Override
  public boolean loadConfiguration(ValidatorConfiguration conf,
                                   CharacterTable characterTable) {
    if (conf.getAttribute("max_char_number") == null) {
      this.maxParagraphs = DEFAULT_MAX_PARAGRAPHS_IN_A_SECTION;
    } else {
      this.maxParagraphs = Integer.valueOf(conf.getAttribute("max_paragraphs"));
    }
    return true;
  }

  protected int maxParagraphs;
}
