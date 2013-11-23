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
 * Represent a section in semi-structured text format such as wiki.
 */
public final class Section implements Block {
  /**
   * constructor.
   * @param sectioLevel section level
   */
  public Section(int sectioLevel) {
    super();
    this.level = sectioLevel;
    this.headerContent = new ArrayList<Sentence>();
    this.subsections = new ArrayList<Section>();
    this.paragraphs = new ArrayList<Paragraph>();
    this.lists = new ArrayList<ListBlock>();
  }

  /**
   * constructor.
   * @param sectioLevel section level
   * @param header header content string
   */
  public Section(int sectioLevel, List<Sentence> header) {
    super();
    this.level = sectioLevel;
    this.headerContent = header;
    this.subsections = new ArrayList<Section>();
    this.paragraphs = new ArrayList<Paragraph>();
    this.lists = new ArrayList<ListBlock>();
  }

  /**
   * get the iterator of subsections.
   * @return Iterator of Section
   */
  public Iterator<Section> getSubSections() {
    return subsections.iterator();
  }

  /**
   * add a subsection.
   * @param section section
   */
  public void appendSubSection(Section section) {
    subsections.add(section);
  }

  /**
   * get the iterator of subsections.
   * @param section id
   * @return specified section
   */
  public Section getSubSection(int id) {
    return subsections.get(id);
  }

  /**
   * get super section.
   * @return Section contains this object as a subsection
   */
  public Section getParentSection() {
    return parent;
  }

  /**
   * set level of section.
   * @param sectionLevel section level
   */
  public void setLevel(int sectionLevel) {
    this.level = sectionLevel;
  }

  /**
   * Set super section.
   * @param parentSection super section
   */
  public void setParentSection(Section parentSection) {
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
   * get iterator of header sentences.
   * @return contents of header.
   * NOTE: header can contain more than one header sentences.
   */
  public Iterator<Sentence> getHeaderContents() {
    return headerContent.iterator();
  }

  /**
   * get iterator of header sentences.
   * @param id id of sentence in header
   * @return contents of header.
   */
  public Sentence getHeaderContent(int id) {
    return headerContent.get(id);
  }

  /**
   * Get the number of sentences in header
   * @return
   */
  public int getHeaderContentsListSize() {
    return headerContent.size();
  }


  /**
   * get last subsection.
   * @return last subsection in this section
   */
  public Section getLastSubsection() {
    return subsections.get(subsections.size()-1);
  }

  /**
   * get the iterator of paragraphs of section.
   * @return Iterator of Paragraph
   */
  public Iterator<Paragraph> getParagraphs() {
    return paragraphs.iterator();
  }

  /**
   * get the specified paragraph.
   * @param id paragraph id
   * @return paragraph
   */
  public Paragraph getParagraph(int id) {
    return paragraphs.get(id);
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
    Paragraph currentBlock = paragraphs.get(paragraphs.size()-1);
    currentBlock.appendSentence(line, lineNum);
    if (currentBlock.getNumberOfSentences() == 1) {
      currentBlock.getSentence(0).isStartaragraph = true;
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
    Paragraph currentBlock = paragraphs.get(paragraphs.size()-1);
    currentBlock.appendSentence(sentence);
    if (currentBlock.getNumberOfSentences() == 1) {
      currentBlock.getSentence(0).isStartaragraph = true;
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
    this.lists.add(new ListBlock());
  }

  /**
   * Append List element.
   * @param listLevel list level
   * @param contents list content
   */
  public void appendListElement(int listLevel, List<Sentence> contents) {
    this.lists.get(lists.size() - 1).appendElement(listLevel, contents);
  }

  /**
   * get size of list.
   * @return number of list block
   */
  public int getNumberOfLists() {
    return lists.size();
  }

  /**
   *  get last list block.
   *  @return last list block in the section
   */
  public ListBlock getLastListBlock() {
    return lists.get(lists.size() -1);
  }

  /**
   * get specified list block.
   * @param id id of list block
   * @return number of list block
   */
  public ListBlock getListBlock(int id) {
    return lists.get(id);
  }

  /**
   * get specified list block.
   * @return number of list block
   */
  public Iterator<ListBlock> getListBlocks() {
    return lists.iterator();
  }

  /**
   * get the number of paragraphs in the section.
   * @return number of paragraphs
   */
  public int getNumberOfParagraphs() {
    return paragraphs.size();
  }

  /* Seciton Level */
  private int level;

  /* Header*/
  private List<Sentence> headerContent;

  /* parent Section */
  private Section parent;

  /* subsecitons */
  private List<Section> subsections;

  /* paragrahs in this section. */
  private List<Paragraph> paragraphs;

  /* lists */
  private List<ListBlock> lists;

}
