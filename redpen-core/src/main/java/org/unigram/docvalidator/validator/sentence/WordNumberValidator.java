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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.config.CharacterTable;
import org.unigram.docvalidator.model.Sentence;
import org.unigram.docvalidator.DocumentValidatorException;
import org.unigram.docvalidator.ValidationError;
import org.unigram.docvalidator.config.ValidatorConfiguration;

import java.util.ArrayList;
import java.util.List;

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

  public WordNumberValidator(ValidatorConfiguration config,
                             CharacterTable characterTable)
      throws DocumentValidatorException {
    initialize(config);
  }

  public List<ValidationError> validate(Sentence sentence) {
    List<ValidationError> result = new ArrayList<ValidationError>();
    String content = sentence.content;
    String[] wordList = content.split(" ");
    int wordNum = wordList.length;
    if (wordNum > maxWordNumber) {
      result.add(new ValidationError(
          this.getClass(),
          "The number of the words exceeds the maximum "
              + String.valueOf(wordNum), sentence
      ));
    }
    return result;
  }

  private boolean initialize(
    ValidatorConfiguration conf)
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
