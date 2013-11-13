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
   * get the size of sections.
   * @return size of sections
   */
  public int getSizeOfSections() {
    return sections.size();
  }

  /**
   * get the specified section.
   * @param id section id
   * @return Section
   */
  public Section getSection(int id) {
    return sections.get(id);
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
   * @param name file name
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
