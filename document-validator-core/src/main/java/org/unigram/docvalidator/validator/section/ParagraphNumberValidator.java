/**
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

  protected void setMaxParagraphNumber(int max) {
    this.maxParagraphs = max;
  }

  private int maxParagraphs;
}
