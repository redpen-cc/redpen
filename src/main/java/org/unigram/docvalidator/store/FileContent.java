package org.unigram.docvalidator.store;

import java.util.Iterator;
import java.util.Vector;

/**
 * FileContent represents a file with many elements
 * such as sentences, lists and headers.
 */
public final class FileContent implements Block {
  /**
   * constructor.
   */
  public FileContent() {
    super();
    sections = new Vector<Section>();
    fileName = "";
  }

  /**
   * get Iterator for Section in the FileContent.
   * @return Iterator of Section list
   */
  public Iterator<Section> getSections() {
    return sections.iterator();
  }

  /**
   * add Section.
   * @param section
   */
  public void appendSection(Section section) {
     sections.add(section);
  }

  /**
   * get last Section.
   * @return last section in the FileContent
   */
  public Section getLastSection() {
    return sections.lastElement();
  }

  /**
   * get the size of Sections.
   * @return size of Sections
   */
  public int getSizeOfSections() {
    return sections.size();
  }

  /**
   * get block id of Section class.
   * @return block id
   */
  public int getBlockID() {
    return BlockTypes.DOCUMENT;
  }

  /**
   * extract Summary of FileContent.
   * @return 0 succeeded, otherwise 1
   */
  public int extractSummary() {
    return 0;
  }

  /**
   * set file name.
   * @param fileName file name
   */
  public void setFileName(String name) {
    this.fileName = name;
  }

  /**
   * get file name.
   * @return file name
   */
  public String getFileName() {
    return fileName;
  }

  private Vector<Section> sections;

  private String fileName;
}
