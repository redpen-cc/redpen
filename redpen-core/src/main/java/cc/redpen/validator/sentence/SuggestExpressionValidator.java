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
package cc.redpen.validator.sentence;

import cc.redpen.DocumentValidatorException;
import cc.redpen.ValidationError;
import cc.redpen.config.CharacterTable;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Sentence;
import cc.redpen.util.FileLoader;
import cc.redpen.util.KeyValueDictionaryExtractor;
import cc.redpen.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * If input sentences contain invalid expressions, this validator
 * returns the errors with corrected expressions.
 */
public class SuggestExpressionValidator implements Validator<Sentence> {

  public SuggestExpressionValidator() {
    super();
    synonyms = new HashMap<>();
  }

  public SuggestExpressionValidator(ValidatorConfiguration config,
                                    CharacterTable characterTable)
      throws DocumentValidatorException {
    initialize(config);
  }

  public List<ValidationError> validate(Sentence line) {
    List<ValidationError> result = new ArrayList<>();
    String str = line.content;
    Set<String> invalidWords = synonyms.keySet();
    for (String w : invalidWords) {
      if (str.contains(w)) {
        result.add(new ValidationError(
            this.getClass(),
            "Found invalid word, \"" + w + "\". "
                + "Use the synonym of the word \""
                + synonyms.get(w) + "\" instead.", line
        ));
      }
    }
    return result;
  }

  private boolean initialize(
    ValidatorConfiguration conf)
      throws DocumentValidatorException {
    String confFile = conf.getAttribute("invalid_word_file");
    LOG.info("dictionary file is " + confFile);
    if (confFile == null || confFile.equals("")) {
      LOG.error("dictionary file is not specified");
      return false;
    }
    KeyValueDictionaryExtractor extractor = new KeyValueDictionaryExtractor();
    FileLoader loader = new FileLoader(extractor);
    if (loader.loadFile(confFile) != 0) {
      return false;
    }
    synonyms = extractor.get();
    return true;
  }

  protected void setSynonyms(Map<String, String> synonymMap) {
    this.synonyms = synonymMap;
  }

  private static final Logger LOG =
      LoggerFactory.getLogger(SuggestExpressionValidator.class);

  private Map<String, String> synonyms;
}
