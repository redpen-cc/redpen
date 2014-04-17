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
import org.unigram.docvalidator.model.Sentence;
import org.unigram.docvalidator.config.DVResource;
import org.unigram.docvalidator.DocumentValidatorException;
import org.unigram.docvalidator.util.FileLoader;
import org.unigram.docvalidator.ValidationError;
import org.unigram.docvalidator.config.ValidatorConfiguration;
import org.unigram.docvalidator.util.WordListExtractor;

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

  public InvalidExpressionValidator(DVResource resource) throws DocumentValidatorException {
    ValidatorConfiguration conf = resource.getConfiguration();
    initialize(conf);
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

  private boolean initialize(ValidatorConfiguration conf)
      throws DocumentValidatorException {
    String confFile = conf.getAttribute("dictionary");
    LOG.info("dictionary file is " + confFile);
    if (confFile == null || confFile.equals("")) {
      LOG.error("dictionary file is not specified");
      return false;
    }
    WordListExtractor extractor = new WordListExtractor();
    FileLoader loader = new FileLoader(extractor);
    if (loader.loadFile(confFile) != 0) {
      return false;
    }
    invalidExpressions = extractor.get();
    return true;
  }

  private Set<String> invalidExpressions;

  private static final Logger LOG =
      LoggerFactory.getLogger(InvalidExpressionValidator.class);
}
