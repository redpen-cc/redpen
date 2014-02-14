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
