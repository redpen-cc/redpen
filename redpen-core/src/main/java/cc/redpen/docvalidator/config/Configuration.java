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
package cc.redpen.docvalidator.config;

import cc.redpen.docvalidator.symbol.AbstractSymbols;
import cc.redpen.docvalidator.symbol.DefaultSymbols;
import cc.redpen.docvalidator.symbol.JapaneseSymbols;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    this.validatorConfigs.addAll(builder.validatorConfigs);
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
   * Get validator configurations.
   *
   * @return list of configurations
   */
  public List<ValidatorConfiguration> getValidatorConfigs() {
    return validatorConfigs;
  }


  /**
   * Builder class of Configuration.
   */
  public static class Builder {
    private CharacterTable characterTable;

    private final List<ValidatorConfiguration> validatorConfigs =
        new ArrayList<>();

    public Builder setCharacterTable(String lang) {
      this.characterTable = loadLanguageDefaultCharacterTable(lang);
      return this;
    }

    public Builder setCharacter(Character character) {
      this.characterTable.override(character);
      return this;
    }

    public Builder setCharacter(String name, String value) {
      this.characterTable.override(new Character(name, value));
      return this;
    }

    public Builder addInvalidPattern(String name, String invalid) {
      Character character = this.characterTable.getCharacter(name);
      character.addInvalid(invalid);
      return this;
    }

    public Builder addValidatorConfig(ValidatorConfiguration config) {
        validatorConfigs.add(config);
      return this;
    }

    private static CharacterTable loadLanguageDefaultCharacterTable(
        String lang) {
      CharacterTable characterTable = new CharacterTable();
      Map<String, Character> dictionary
          = characterTable.getCharacterDictionary();
      AbstractSymbols symbolSettings;
      if (lang.equals("ja")) {
        symbolSettings = JapaneseSymbols.getInstance();
        characterTable.setLang("ja");
      } else {
        symbolSettings = DefaultSymbols.getInstance();
        characterTable.setLang("en");
      }

      Iterator<String> characterNames =
          symbolSettings.getAllCharacterNames();
      while (characterNames.hasNext()) {
        String charName = characterNames.next();
        Character character = symbolSettings.get(charName);
        dictionary.put(charName, character);
      }
      return characterTable;
    }

    public Configuration build() {
      return new Configuration(this);
    }
  }

  private final CharacterTable characterTable;

  private final List<ValidatorConfiguration> validatorConfigs =
      new ArrayList<>();
}
