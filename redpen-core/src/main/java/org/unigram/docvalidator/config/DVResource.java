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
package org.unigram.docvalidator.config;

/**
 * Contains Settings used throughout DocumentValidator.
 */
public final class DVResource {
  /**
   * Constructor.
   *
   * @param validatorConf settings of Validators
   */
  public DVResource(ValidatorConfiguration validatorConf) {
    super();
    this.configuration = validatorConf;
    this.characterTable = new CharacterTable();
  }

  /**
   * Constructor.
   *
   * @param validatorConf settings of Validators.
   * @param characterConf settings of characters and symbols
   */
  public DVResource(ValidatorConfiguration validatorConf,
                    CharacterTable characterConf) {
    super();
    this.configuration = validatorConf;
    this.characterTable = characterConf;
  }

  /**
   * Get Configuration.
   *
   * @return Configuration
   */
  public ValidatorConfiguration getConfiguration() {
    return configuration;
  }

  /**
   * Get CharacterTable.
   *
   * @return CharacterTable
   */
  public CharacterTable getCharacterTable() {
    return characterTable;
  }

  private final ValidatorConfiguration configuration;

  private final CharacterTable characterTable;
}
