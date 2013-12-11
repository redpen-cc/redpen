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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.DefaultSymbols;

/**
 * Configuration table of characters used in DocumentValidator.
 */
public final class CharacterTable {
  public CharacterTable(String fileName) {
    this();
    InputStream fis = null;
    try {
      fis = new FileInputStream(fileName);
    } catch (FileNotFoundException e) {
      LOG.error(e.getMessage());
    }
    CharacterTableLoader.loadTable(fis, characterDictionary);
  }

  /**
   * constructor.
   * @param stream input configuration
   */
  public CharacterTable(InputStream stream) {
    this();
    CharacterTableLoader.loadTable(stream, characterDictionary);
  }

  /**
   * constructor.
   */
  public CharacterTable() {
    super();
    characterDictionary = new HashMap<String, DVCharacter>();
    loadDefaultCharacterTable(characterDictionary);
  }

  public int getSizeDictionarySize() {
    return this.characterDictionary.size();
  }

  public Set<String> getNames() {
    return this.characterDictionary.keySet();
  }

  public DVCharacter getCharacter(String name) {
    return this.characterDictionary.get(name);
  }

  public boolean isContainCharacter(String name) {
    if (this.characterDictionary.get(name) != null) {
      return true;
    }
    return false;
  }

  private void loadDefaultCharacterTable(
      Map<String, DVCharacter> characterTable) {
    Iterator<String> characterNames =
        DefaultSymbols.getAllCharacterNames();
    while (characterNames.hasNext()) {
      String charName = characterNames.next();
      DVCharacter character = DefaultSymbols.get(charName);
      characterTable.put(charName, character);
    }
  }

  static Logger LOG = LoggerFactory.getLogger(CharacterTable.class);

  private Map<String, DVCharacter> characterDictionary;
}
