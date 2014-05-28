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
package org.bigram.docvalidator.config;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains Settings used throughout DocumentValidator.
 */
public final class Configuration {
  /**
   * Constructor.
   *
   */
  public Configuration(ValidatorConfiguration validatorConfig) {
    this(validatorConfig, new CharacterTable());
  }

  /**
   * Constructor.
   *
   * @param validatorConfig settings of validators
   * @param characterConf settings of characters and symbols
   */
  public Configuration(ValidatorConfiguration validatorConfig, CharacterTable characterConf) {
    super();
    this.characterTable = characterConf;

    // TODO tricky implementation. this code is need to refactor with ConfigurationLoader.
    for (ValidatorConfiguration config : validatorConfig.getChildren()) {
      if ("SentenceIterator".equals(config.getConfigurationName())) {
        this.sentenceValidatorConfigs.addAll(config.getChildren());
      } else if ("SectionLength".equals(config.getConfigurationName())) {
        this.sectionValidatorConfigs.add(config);
      } else if ("MaxParagraphNumber".equals(config.getConfigurationName())) {
        this.sectionValidatorConfigs.add(config);
      } else if ("ParagraphStartWith".equals(config.getConfigurationName())) {
        this.sectionValidatorConfigs.add(config);
      } else {
        LOG.warn("No validator such as '" +config.getConfigurationName() + "'");
      }
    }
  }

  /**
   * Get CharacterTable.
   *
   * @return CharacterTable
   */
  public CharacterTable getCharacterTable() {
    return characterTable;
  }

  /**
   * Get document validator configurations.
   * @return list of configurations
   */
  public List<ValidatorConfiguration> getDocumentValidatorConfigs() {
    return documentValidatorConfigs;
  }

  /**
   * Get section validator configurations.
   * @return list of configurations
   */
  public List<ValidatorConfiguration> getSectionValidatorConfigs() {
    return sectionValidatorConfigs;
  }

  /**
   * Get paragraph validator configurations.
   * @return list of configurations
   */
  public List<ValidatorConfiguration> getParagraphValidatorConfigs() {
    return paragraphValidatorConfigs;
  }
  /**
   * Get sentence validator configurations.
   * @return list of configurations
   */
  public List<ValidatorConfiguration> getSentenceValidatorConfigs() {
    return sentenceValidatorConfigs;
  }

  private final CharacterTable characterTable;

  private final List<ValidatorConfiguration> documentValidatorConfigs =
      new ArrayList<ValidatorConfiguration>();
  private final List<ValidatorConfiguration> sectionValidatorConfigs =
      new ArrayList<ValidatorConfiguration>();
  private final List<ValidatorConfiguration> paragraphValidatorConfigs =
      new ArrayList<ValidatorConfiguration>();
  private final List<ValidatorConfiguration> sentenceValidatorConfigs =
      new ArrayList<ValidatorConfiguration>();

  private static final Logger LOG =
      LoggerFactory.getLogger(Configuration.class);
}
