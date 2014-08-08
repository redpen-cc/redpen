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
package cc.redpen.config;

import java.lang.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Configuration table of characters used in DocumentValidator.
 */
public final class CharacterTable {
  /**
   * Constructor.
   */
  public CharacterTable() {
    super();
    characterDictionary = new HashMap<>();
    valueDictionary = new HashMap<>();
  }

  /**
   * Return the size of character Dictionary.
   *
   * @return size of registered character
   */
  public int getSizeDictionarySize() {
    return this.characterDictionary.size();
  }

  /**
   * Get the character names in the dictionary.
   *
   * @return names of characters
   */
  public Set<String> getNames() {
    return this.characterDictionary.keySet();
  }

  /**
   * Get the character specified with the name.
   *
   * @param name character name
   * @return character containing the settings
   */
  public cc.redpen.config.Character getCharacter(String name) {
    return this.characterDictionary.get(name);
  }

  /**
   * Get the character specified with the value.
   *
   * @param value character name
   * @return character containing the settings
   */
  public Character getCharacterByValue(String value) {
    return this.valueDictionary.get(value);
  }

  /**
   * Get all elements of character dictionary.
   *
   * @return character dictionary
   */
  public Map<String, cc.redpen.config.Character> getCharacterDictionary() {
    return characterDictionary;
  }

  /**
   * Detect the specified character is exit in the dictionary.
   *
   * @param name character name
   * @return character when exist, null when the specified
   * character does not exist
   */
  public boolean isContainCharacter(String name) {
    return this.characterDictionary.get(name) != null;
  }

  /**
   * Detect the specified character is exit in the dictionary.
   *
   * @param value character value
   * @return character when exist, null when the specified
   * character does not exist
   */
  public boolean isContainCharacterByValue(String value) {
    return this.valueDictionary.get(value) != null;
  }

  /**
   * Replace the current character setting.
   * @param character symbol configuration
   */
  public void override(cc.redpen.config.Character character) {
    characterDictionary.put(character.getName(), character);
    valueDictionary.put(character.getValue(), character);
  }

  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  private final Map<String, cc.redpen.config.Character> characterDictionary;

  private final Map<String, Character> valueDictionary;

  private String lang;

}
