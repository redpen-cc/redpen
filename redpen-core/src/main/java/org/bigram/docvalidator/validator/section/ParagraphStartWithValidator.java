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

import java.util.List;
import java.util.ArrayList;

import org.bigram.docvalidator.config.CharacterTable;
import org.bigram.docvalidator.model.Section;
import org.bigram.docvalidator.model.Sentence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.bigram.docvalidator.model.Paragraph;
import org.bigram.docvalidator.config.ValidatorConfiguration;
import org.bigram.docvalidator.ValidationError;

/**
 * Validate whether paragraph start as specified.
 */
public class ParagraphStartWithValidator extends AbstractSectionValidator {
  /**
   * Default matter paragraph start with.
   */
  @SuppressWarnings("WeakerAccess")
  public static final String DEFAULT_PARAGRAPH_START_WITH = " ";

  /**
   * Constructor.
   */
  public ParagraphStartWithValidator() {
    super();
    this.beginningOfParagraph = DEFAULT_PARAGRAPH_START_WITH;
  }

  public ParagraphStartWithValidator(ValidatorConfiguration conf,
                                     CharacterTable charTable) {
    this();
    loadConfiguration(conf);
  }

  @Override
  public List<ValidationError> validate(Section section) {
    List<ValidationError> validationErrors = new ArrayList<ValidationError>();

    for (Paragraph currentParagraph : section.getParagraphs()) {
      Sentence firstSentence = currentParagraph.getSentence(0);
      if (firstSentence.content.indexOf(this.beginningOfParagraph) != 0) {
        validationErrors.add(new ValidationError(
            this.getClass(),
            "Found invalid beginning of paragraph: \"",
            firstSentence));
      }
    }

    return validationErrors;
  }

  private boolean loadConfiguration(ValidatorConfiguration conf) {
    if (conf.getAttribute("paragraph_start_with") == null) {
      this.beginningOfParagraph = DEFAULT_PARAGRAPH_START_WITH;
      LOG.info("Using the default value of paragraph_start_with.");
    } else {
      this.beginningOfParagraph = conf.getAttribute("paragraph_start_with");
    }
    return true;
  }

  private String beginningOfParagraph;

  private static final Logger LOG =
      LoggerFactory.getLogger(ParagraphStartWithValidator.class);

}
