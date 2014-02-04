/**
 * DocumentValidator
 * Copyright (c) 2013-, Takahiko Ito, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package org.unigram.docvalidator.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration table of characters used in DocumentValidator.
 */
public final class CharacterTable {
  /**
   * Constructor.
   */
  public CharacterTable() {
    super();
    characterDictionary = new HashMap<String, DVCharacter>();
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
  public DVCharacter getCharacter(String name) {
    return this.characterDictionary.get(name);
  }

  /**
   * Get all elements of character dictionary.
   *
   * @return character dictionary
   */
  public Map<String, DVCharacter> getCharacterDictionary() {
    return characterDictionary;
  }

  /**
   * Detect the specified character is exit in the dictionary.
   *
   * @param name character name
   * @return character when exist, null when the specified character does not exist
   */
  public boolean isContainCharacter(String name) {
    return this.characterDictionary.get(name) != null;
  }

  static Logger LOG = LoggerFactory.getLogger(CharacterTable.class);

  private final Map<String, DVCharacter> characterDictionary;

}
