package org.unigram.docvalidator.store;

import java.util.Iterator;
import java.util.Vector;

/**
 * Represent List in semi-structured format such as wiki.
 */
public final class List implements Block {
  /**
   * Constructor.
   */
  public List() {
    super();
    this.listElements = new Vector<ListElement>();
  }

  /**
   * get iterator of list elements.
   * @return Iterator of ListElement
   */
  public Iterator<ListElement> getListElements() {
    return listElements.iterator();
  }

  /**
   * get the number of list elements.
   * @return number of list elements
   */
  public int getNumberOfListElements() {
    return listElements.size();
  }

  public int getBlockID() {
    return BlockTypes.LIST;
  }

  /**
   * Append ListElement.
   * @param level indentation level
   * @param content content of list element
   */
  public void appendElement(int level, String content) {
    listElements.add(new ListElement(level, content));
  }

  public int extractSummary() {
    return 0;
  }

  private Vector<ListElement> listElements;
}
