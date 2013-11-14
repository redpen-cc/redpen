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
 * Represent a section in semi-structured text format such as wiki.
 */
public final class Section implements Block {
  /**
   * constructor.
   * @param sectioLevel section level
   * @param header header content string
   */
  public Section(int sectioLevel, String header) {
    super();
    this.level = sectioLevel;
    this.headerContent = header;
    this.subsections = new Vector<Section>();
    this.paragraphs = new Vector<Paragraph>();
    this.lists = new Vector<List>();
  }

  /**
   * get the iterator of subsections.
   * @return Iterator of Section
   */
  public Iterator<Section> getSeubsections() {
    return subsections.iterator();
  }

  /**
   * add a subsection.
   * @param section section
   */
  public void appendSection(Section section) {
    subsections.add(section);
  }

  /**
   * get super section.
   * @return Section contains this object as a subsection
   */
  public Section getParent() {
    return parent;
  }

  /**
   * set level of section.
   * @param sectionLevel section level
   */
  public void setHeaderLevel(int sectionLevel) {
    this.level = sectionLevel;
  }

  /**
   * Set super section.
   * @param parentSection super section
   */
  public void setParent(Section parentSection) {
    this.parent = parentSection;
  }

  /**
   * get the size of subsections.
   * @return size of subsection
   */
  public int getNumberOfSubsections() {
    return subsections.size();
  }

  /**
   * get level of section.
   * @return section level
   */
  public int getLevel() {
    return level;
  }

  /**
   * get header.
   * @return content string of header.
   */
  public String getHeaderContent() {
    return headerContent;
  }

  /**
   * get last subsection.
   * @return last subsection in this section
   */
  public Section getLastSubsection() {
    return subsections.lastElement();
  }

  /**
   * get paragraphs of section.
   * @return Iterator of Paragraph
   */
  public Iterator<Paragraph> getParagraph() {
    return paragraphs.iterator();
  }

  /**
   * add a paragraph.
   * @param pragraph paragraph
   */
  public void appendParagraph(Paragraph pragraph) {
    paragraphs.add(pragraph);
  }

  /**
   * Append sentence.
   * @param line sentence
   * @param lineNum sentence number
   */
  public void appendSentence(String line, int lineNum) {
    if (paragraphs.size() == 0) {
      appendParagraph(new Paragraph());
    }
    Paragraph currentBlock = paragraphs.lastElement();
    currentBlock.appendSentence(line, lineNum);
    if (currentBlock.getNumverOfSentences() == 1) {
      currentBlock.getLine(0).isStartaragraph = true;
    }
  }

  /**
   * Append sentence.
   * @param sentence sentence to append
   */
  public void appendSentence(Sentence sentence) {
    if (paragraphs.size() == 0) {
      appendParagraph(new Paragraph());
    }
    Paragraph currentBlock = paragraphs.lastElement();
    currentBlock.appendSentence(sentence);
    if (currentBlock.getNumverOfSentences() == 1) {
      currentBlock.getLine(0).isStartaragraph = true;
    }
  }

  /**
   * get block id.
   * @return block id of section
   */
  public int getBlockID() {
    return BlockTypes.SECTION;
  }

  /**
   * Append List.
   */
  public void appendListBlock() {
    this.lists.add(new List());
  }

  /**
   * Append List element.
   * @param listLevel list level
   * @param content list content
   */
  public void appendListElement(int listLevel, String content) {
    this.lists.lastElement().appendElement(listLevel, content);
  }

  /**
   * get size of list.
   * @return number of list block
   */
  public int getSizeofLists() {
    return lists.size();
  }

  /**
   *  get last list block.
   *  @return last list block in the section
   */
  public List getLastListBlock() {
    return lists.lastElement();
  }

  /**
   * get specified list block.
   * @param id id of list block
   * @return number of list block
   */
  public List getLastListBlock(int id) {
    return lists.get(id);
  }

  /**
   * get the number of paragraphs in the section.
   * @return number of paragraphs
   */
  public int getParagraphNumber() {
    return paragraphs.size();
  }

  /* Seciton Level */
  private int level;

  /* Header*/
  private String headerContent;

  /* parent Section */
  private Section parent;

  /* subsecitons */
  private Vector<Section> subsections;

  /* paragrahs in this section. */
  private Vector<Paragraph> paragraphs;

  /* lists */
  private Vector<List> lists;

}
