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
 * FileContent represents a file with many elements
 * such as sentences, lists and headers.
 */
public final class FileContent implements Block {
  /**
   * Constructor.
   */
  public FileContent() {
    super();
    sections = new ArrayList<Section>();
    fileName = "";
  }

  /**
   * Get Iterator for Section in the FileContent.
   *
   * @return Iterator of Section list
   */
  public Iterator<Section> getSections() {
    return sections.iterator();
  }

  /**
   * Add a Section.
   *
   * @param section a section in file content
   */
  public void appendSection(Section section) {
    sections.add(section);
  }

  /**
   * Get last Section.
   *
   * @return last section in the FileContent
   */
  public Section getLastSection() {
    Section section = null;
    if (sections.size() > 0) {
      section = sections.get(sections.size() - 1);
    }
    return section;
  }

  /**
   * Get the size of sections in the file.
   *
   * @return size of sections
   */
  public int getNumberOfSections() {
    return sections.size();
  }

  /**
   * Get the specified section.
   *
   * @param id section id
   * @return a section with specified id
   */
  public Section getSection(int id) {
    return sections.get(id);
  }


  /**
   * Get block id of Section class.
   *
   * @return block id
   */
  public int getBlockID() {
    return BlockTypes.DOCUMENT;
  }

  /**
   * Set file name.
   *
   * @param name file name
   */
  public void setFileName(String name) {
    this.fileName = name;
  }

  /**
   * Get file name.
   *
   * @return file name
   */
  public String getFileName() {
    return fileName;
  }

  private final List<Section> sections;

  private String fileName;
}
