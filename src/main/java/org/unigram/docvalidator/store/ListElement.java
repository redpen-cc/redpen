package org.unigram.docvalidator.store;

/**
 * Element of List in semi-strcutured text format such as wiki.
 */
public final class ListElement {
  /**
   * constructor.
   * @param level indentation level
   * @param content content of list element
   */
  public ListElement(int l, String c) {
    super();
    this.content = c;
    this.level = l;
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
