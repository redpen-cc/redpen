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
package cc.redpen.docvalidator.validator.section;

import cc.redpen.docvalidator.ValidationError;
import cc.redpen.docvalidator.config.CharacterTable;
import cc.redpen.docvalidator.config.ValidatorConfiguration;
import cc.redpen.docvalidator.model.Paragraph;
import cc.redpen.docvalidator.model.Section;
import cc.redpen.docvalidator.model.Sentence;
import cc.redpen.docvalidator.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Validate whether paragraph start as specified.
 */
public class ParagraphStartWithValidator implements Validator<Section> {
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
    List<ValidationError> validationErrors = new ArrayList<>();
    for (Paragraph currentParagraph : section.getParagraphs()) {
      if (currentParagraph.getNumberOfSentences() == 0) {
        continue;
      }
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
