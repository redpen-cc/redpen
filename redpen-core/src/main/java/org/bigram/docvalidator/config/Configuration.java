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
   * @param builder Configuration builder
   */
  public Configuration(Builder builder) {
    if (builder.characterTable == null) {
      this.characterTable = new CharacterTable();
    } else {
      this.characterTable = builder.characterTable;
    }
    this.sentenceValidatorConfigs.addAll(builder.sentenceValidatorConfigs);
    this.paragraphValidatorConfigs.addAll(builder.paragraphValidatorConfigs);
    this.sectionValidatorConfigs.addAll(builder.sectionValidatorConfigs);
    this.documentValidatorConfigs.addAll(builder.documentValidatorConfigs);
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
   *
   * @return list of configurations
   */
  public List<ValidatorConfiguration> getDocumentValidatorConfigs() {
    return documentValidatorConfigs;
  }

  /**
   * Get section validator configurations.
   *
   * @return list of configurations
   */
  public List<ValidatorConfiguration> getSectionValidatorConfigs() {
    return sectionValidatorConfigs;
  }

  /**
   * Get paragraph validator configurations.
   *
   * @return list of configurations
   */
  public List<ValidatorConfiguration> getParagraphValidatorConfigs() {
    return paragraphValidatorConfigs;
  }

  /**
   * Get sentence validator configurations.
   *
   * @return list of configurations
   */
  public List<ValidatorConfiguration> getSentenceValidatorConfigs() {
    return sentenceValidatorConfigs;
  }

  /**
   * Builder class of Configuration.
   */
  public static class Builder {
    private CharacterTable characterTable;

    private final List<ValidatorConfiguration> documentValidatorConfigs =
        new ArrayList<ValidatorConfiguration>();
    private final List<ValidatorConfiguration> sectionValidatorConfigs =
        new ArrayList<ValidatorConfiguration>();
    private final List<ValidatorConfiguration> paragraphValidatorConfigs =
        new ArrayList<ValidatorConfiguration>();
    private final List<ValidatorConfiguration> sentenceValidatorConfigs =
        new ArrayList<ValidatorConfiguration>();

    public Builder setCharacterTable(CharacterTable characterTable) {
      this.characterTable = characterTable;
      return this;
    }

    public Builder addSentenceValidatorConfig(ValidatorConfiguration config) {
      sentenceValidatorConfigs.add(config);
      return this;
    }

    public Builder addSectionValidatorConfig(ValidatorConfiguration config) {
      sectionValidatorConfigs.add(config);
      return this;
    }

    public Builder addParagraphValidatorConfig(ValidatorConfiguration config) {
      paragraphValidatorConfigs.add(config);
      return this;
    }

    public Builder addRootValidatorConfig(ValidatorConfiguration config) {
      this.extractChildValidators(config);
      return this;
    }

    private void extractChildValidators(ValidatorConfiguration validatorConfig) {
      if (validatorConfig == null) {
        return;
      }
      // TODO tricky implementation. this code is need to refactor with ConfigurationLoader.
      for (ValidatorConfiguration config : validatorConfig.getChildren()) {
        if ("SentenceLength".equals(config.getConfigurationName())) {
          sentenceValidatorConfigs.add(config);
        } else if ("InvalidExpression".equals(config.getConfigurationName())) {
          sentenceValidatorConfigs.add(config);
        } else if ("SpaceAfterPeriod".equals(config.getConfigurationName())) {
          sentenceValidatorConfigs.add(config);
        } else if ("CommaNumber".equals(config.getConfigurationName())) {
          sentenceValidatorConfigs.add(config);
        } else if ("WordNumber".equals(config.getConfigurationName())) {
          sentenceValidatorConfigs.add(config);
        } else if ("SuggestExpression".equals(config.getConfigurationName())) {
          sentenceValidatorConfigs.add(config);
        } else if ("InvalidCharacter".equals(config.getConfigurationName())) {
          sentenceValidatorConfigs.add(config);
        } else if ("SpaceWithSymbol".equals(config.getConfigurationName())) {
          sentenceValidatorConfigs.add(config);
        } else if ("KatakanaEndHyphen".equals(config.getConfigurationName())) {
          sentenceValidatorConfigs.add(config);
        } else if ("KatakanaSpellCheck".equals(config.getConfigurationName())) {
          sentenceValidatorConfigs.add(config);
        } else if ("SectionLength".equals(config.getConfigurationName())) {
          this.sectionValidatorConfigs.add(config);
        } else if ("MaxParagraphNumber".equals(config.getConfigurationName())) {
          this.sectionValidatorConfigs.add(config);
        } else if ("ParagraphStartWith".equals(config.getConfigurationName())) {
          this.sectionValidatorConfigs.add(config);
        } else {
          throw new IllegalStateException("No Validator such as '"
              + config.getConfigurationName() + "'");
        }
      }
    }

    public Configuration build() {
      return new Configuration(this);
    }
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
