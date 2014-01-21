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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represent a character settings.
 */
public final class DVCharacter {
  /**
   * Constructor.
   *
   * @param charName  name of target character
   * @param charValue character
   */
  public DVCharacter(String charName, String charValue) {
    this.name = charName;
    this.value = charValue;
    this.invalidChars = new ArrayList<String>();
    this.needBeforeSpace = false;
    this.needAfterSpace = false;
  }

  /**
   * Constructor.
   *
   * @param charName        name of target character
   * @param charValue       character
   * @param invalidCharsStr list of invalid characters
   */
  public DVCharacter(String charName, String charValue,
                     String invalidCharsStr) {
    this(charName, charValue);
    if (invalidCharsStr.length() > 0) {
      this.invalidChars = Arrays.asList(invalidCharsStr.split("(?!^)"));
    }
  }

  /**
   * Constructor.
   *
   * @param charName        name of target character
   * @param charValue       character
   * @param invalidCharsStr list of invalid characters
   * @param haveBeforeSpace flag to have a space before the character
   * @param haveAfterSpace  flag to have a pace after the character
   */
  public DVCharacter(String charName, String charValue, String invalidCharsStr,
                     boolean haveBeforeSpace, boolean haveAfterSpace) {
    this(charName, charValue, invalidCharsStr);
    this.needBeforeSpace = haveBeforeSpace;
    this.needAfterSpace = haveAfterSpace;
  }

  /**
   * Get name of character.
   *
   * @return character name
   */
  public String getName() {
    return name;
  }

  /**
   * Set name of character.
   *
   * @param charName name of character
   */
  public void setName(String charName) {
    this.name = charName;
  }

  /**
   * Get value of character.
   *
   * @return character
   */
  public String getValue() {
    return value;
  }

  /**
   * Set value of character.
   *
   * @param charValue
   */
  public void setValue(String charValue) {
    this.value = charValue;
  }

  /**
   * Get invalid characters.
   *
   * @return a list of invalid characters
   */
  public List<String> getInvalidChars() {
    return invalidChars;
  }

  /**
   * Set invalid characters.
   *
   * @param invalidCharList list of invalid characters
   */
  public void setInvalidChars(List<String> invalidCharList) {
    this.invalidChars = invalidCharList;
  }

  /**
   * Get the flag to know the character should have a space.
   *
   * @return flag to determine the character should have a space before it
   */
  public boolean isNeedBeforeSpace() {
    return needBeforeSpace;
  }

  /**
   * Set the flag to know the character should have a space.
   *
   * @param beforeSpace the character should have a space before it
   */
  public void setNeedBeforeSpace(boolean beforeSpace) {
    this.needBeforeSpace = beforeSpace;
  }

  /**
   * Get the flag to know the character should have a space.
   *
   * @return flag to determine the character should have a space after it
   */
  public boolean isNeedAfterSpace() {
    return needAfterSpace;
  }

  /**
   * Set the flag to know the character should have a space.
   *
   * @param afterSpace the character should have a space after it
   */
  public void setNeedAfterSpace(boolean afterSpace) {
    this.needAfterSpace = afterSpace;
  }

  private String name;
  private String value;
  private List<String> invalidChars;
  private boolean needBeforeSpace;
  private boolean needAfterSpace;
}
