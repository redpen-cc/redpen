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
  public Character getCharacter(String name) {
    return this.characterDictionary.get(name);
  }

  /**
   * Get all elements of character dictionary.
   *
   * @return character dictionary
   */
  public Map<String, Character> getCharacterDictionary() {
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
   * Replace the current character setting.
   * @param character symbol configuration
   */
  public void override(Character character) {
    characterDictionary.put(character.getName(), character);
  }

  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  private final Map<String, Character> characterDictionary;

  private String lang;

}
