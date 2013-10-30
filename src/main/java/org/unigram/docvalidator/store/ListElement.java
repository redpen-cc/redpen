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
package org.unigram.docvalidator.store;

/**
 * Element of List in semi-strcutured text format such as wiki.
 */
public final class ListElement {
  /**
   * constructor.
   * @param listLevel indentation level
   * @param listContent content of list element
   */
  public ListElement(int listLevel, String listContent) {
    super();
    this.level = listLevel;
    this.content = listContent;
  }

  /**
   * get content of list element.
   * @return content of list element
   */
  public String getContent() {
    return content;
  }

  /**
   * get indentation level.
   * @return indentation level
   */
  public int getLevel() {
    return level;
  }

  private String content;

  private int level;
}
