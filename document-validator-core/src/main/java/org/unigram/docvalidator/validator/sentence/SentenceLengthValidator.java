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
