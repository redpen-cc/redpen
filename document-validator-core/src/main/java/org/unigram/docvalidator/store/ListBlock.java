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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represent List in semi-structured format such as wiki.
 */
public final class ListBlock implements Block {
  /**
   * Constructor.
   */
  public ListBlock() {
    super();
    this.listElements = new ArrayList<ListElement>();
  }

  /**
   * Get iterator of list elements.
   *
   * @return Iterator of ListElement
   */
  public Iterator<ListElement> getListElements() {
    return listElements.iterator();
  }

  /**
   * Get the number of list elements.
   *
   * @return number of list elements
   */
  public int getNumberOfListElements() {
    return listElements.size();
  }

  /**
   * Get iterator of list elements.
   *
   * @return Iterator of ListElement
   */
  public ListElement getListElement(int id) {
    return listElements.get(id);
  }

  public int getBlockID() {
    return BlockTypes.LIST;
  }

  /**
   * Append ListElement.
   *
   * @param level    indentation level
   * @param contents contents of list element
   */
  public void appendElement(int level, List<Sentence> contents) {
    listElements.add(new ListElement(level, contents));
  }

  private final List<ListElement> listElements;
}
