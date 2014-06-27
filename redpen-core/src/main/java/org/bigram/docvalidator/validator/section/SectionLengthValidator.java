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
package org.bigram.docvalidator.validator.section;

import org.bigram.docvalidator.config.CharacterTable;
import org.bigram.docvalidator.config.ValidatorConfiguration;
import org.bigram.docvalidator.model.Section;
import org.bigram.docvalidator.model.Sentence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.bigram.docvalidator.model.Paragraph;
import org.bigram.docvalidator.ValidationError;

import java.util.ArrayList;
import java.util.List;

/**
 * Validate the length of one section.
 */
public class SectionLengthValidator extends AbstractSectionValidator {
  /**
   * Constructor.
   */
  public SectionLengthValidator() {
    super();
  }

  public SectionLengthValidator(ValidatorConfiguration conf, CharacterTable
    charTable) {
    this();
    loadConfiguration(conf);
  }

  @Override
  public List<ValidationError> validate(Section section) {
    List<ValidationError> validationErrors = new ArrayList<ValidationError>();
    int sectionCharNumber = 0;

    for (Paragraph currentParagraph : section.getParagraphs()) {
      for (Sentence sentence : currentParagraph.getSentences()) {
        sectionCharNumber += sentence.content.length();
      }
      if (sectionCharNumber > maxSectionCharNumber) {
        Sentence header = new Sentence("",0);
        if (section.getHeaderContentsListSize() > 0) {
          header = section.getHeaderContent(0);
        }
        ValidationError error = new ValidationError(
          this.getClass(),
          "The number of the character exceeds the maximum \""
            + String.valueOf(sectionCharNumber) + "\".",
            header
        );
        validationErrors.add(error);
      }
    }
    return validationErrors;
  }

  private boolean loadConfiguration(ValidatorConfiguration conf) {
    if (conf.getAttribute("max_char_num") == null) {
      this.maxSectionCharNumber = DEFAULT_MAXIMUM_CHAR_NUMBER_IN_A_SECTION;
      LOG.info("max_char_number was not set.");
      LOG.info("Using the default value of max_char_num.");
    } else {
      this.maxSectionCharNumber = Integer.valueOf(
        conf.getAttribute("max_char_num"));
    }
    return true;
  }

  protected void setMaxSectionLength(int max) {
    this.maxSectionCharNumber = max;
  }

  private static final int DEFAULT_MAXIMUM_CHAR_NUMBER_IN_A_SECTION = 1000;

  private static final Logger LOG =
    LoggerFactory.getLogger(SectionLengthValidator.class);

  private int maxSectionCharNumber;

}
