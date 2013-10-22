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
