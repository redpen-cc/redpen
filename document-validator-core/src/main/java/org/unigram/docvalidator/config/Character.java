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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represent a character settings.
 */
public final class Character {
  /**
   * Constructor.
   *
   * @param charName  name of target character
   * @param charValue character
   */
  public Character(String charName, String charValue) {
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
  public Character(String charName, String charValue,
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
  public Character(String charName, String charValue, String invalidCharsStr,
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
