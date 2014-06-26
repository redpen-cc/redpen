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
package org.bigram.docvalidator.validator.sentence;

import org.bigram.docvalidator.DocumentValidatorException;
import org.bigram.docvalidator.config.CharacterTable;
import org.bigram.docvalidator.model.Sentence;
import org.bigram.docvalidator.util.FileLoader;
import org.bigram.docvalidator.util.WordListExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.bigram.docvalidator.ValidationError;
import org.bigram.docvalidator.config.ValidatorConfiguration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validate input sentences contain invalid expression.
 */
public class InvalidExpressionValidator implements SentenceValidator {
  /**
   * Constructor.
   */
  public InvalidExpressionValidator() {
    invalidExpressions = new HashSet<String>();
  }

  /**
   * Constructor
   * @param config Configuration object
   * @param characterTable  Character settings
   * @throws DocumentValidatorException
   */
  public InvalidExpressionValidator(ValidatorConfiguration config,
                                    CharacterTable characterTable)
      throws DocumentValidatorException {
    initialize(config, characterTable);
  }

  public List<ValidationError> validate(Sentence line) {
    List<ValidationError> result = new ArrayList<ValidationError>();
    String str = line.content;
    for (String w : invalidExpressions) {
      if (str.contains(w)) {
        result.add(new ValidationError(
            this.getClass(),
            "Found invalid expression: \"" + w + "\"", line));
      }
    }
    return result;
  }

  /**
   * Add invalid element. This method is used for testing
   *
   * @param invalid invalid expression to be added the list
   */
  public void addInvalid(String invalid) {
    invalidExpressions.add(invalid);
  }

  private boolean initialize(ValidatorConfiguration conf,
      CharacterTable characterTable)
      throws DocumentValidatorException {
    String lang = characterTable.getLang();

    WordListExtractor extractor = new WordListExtractor();
    FileLoader loader = new FileLoader(extractor);

    LOG.info("Loading default invalid expression dictionary for " +
        "\"" + lang + "\".");
    String defaultDictionaryFile = DEFAULT_RESOURCE_PATH
        + "/invalid-" + lang + ".dat";
    InputStream inputStream = getClass()
        .getClassLoader()
        .getResourceAsStream(defaultDictionaryFile);
    if (loader.loadFile(inputStream) == 0) {
      LOG.info("Succeeded to load default dictionary.");
    } else {
      LOG.info("Failed to load default dictionary.");
    }

    String confFile = conf.getAttribute("dictionary");
    if (confFile == null || confFile.equals("")) {
      LOG.error("Dictionary file is not specified.");
    } else {
      LOG.info("user dictionary file is " + confFile);
      if (loader.loadFile(confFile) != 0) {
        LOG.info("Succeeded to load specified user dictionary.");
      } else {
        LOG.error("Failed to load user dictionary.");
      }
    }
    invalidExpressions = extractor.get();
    return true;
  }

  private static final String DEFAULT_RESOURCE_PATH = "dicts/invalid";

  private Set<String> invalidExpressions;

  private static final Logger LOG =
      LoggerFactory.getLogger(InvalidExpressionValidator.class);
}
