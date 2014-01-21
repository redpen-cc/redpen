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
   * Constructor.
   *
   * @param sectionLevel section level
   */
  public Section(int sectionLevel) {
    super();
    this.level = sectionLevel;
    this.headerContent = new ArrayList<Sentence>();
    this.subsections = new ArrayList<Section>();
    this.paragraphs = new ArrayList<Paragraph>();
    this.lists = new ArrayList<ListBlock>();
  }

  /**
   * Constructor.
   *
   * @param sectionLevel section level
   * @param header       header contents
   */
  public Section(int sectionLevel, List<Sentence> header) {
    super();
    this.level = sectionLevel;
    this.headerContent = header;
    this.subsections = new ArrayList<Section>();
    this.paragraphs = new ArrayList<Paragraph>();
    this.lists = new ArrayList<ListBlock>();
  }

  /**
   * Constructor.
   *
   * @param sectionLevel section level
   * @param headerString header content string
   */
  public Section(int sectionLevel, String headerString) {
    this.level = sectionLevel;
    Sentence headerSentence = new Sentence(headerString, 0);
    List<Sentence> headers = new ArrayList<Sentence>();
    headers.add(headerSentence);
    this.headerContent = headers;
    this.subsections = new ArrayList<Section>();
    this.paragraphs = new ArrayList<Paragraph>();
    this.lists = new ArrayList<ListBlock>();
  }

  /**
   * Get the iterator of subsections.
   *
   * @return Iterator of Section
   */
  public Iterator<Section> getSubSections() {
    return subsections.iterator();
  }

  /**
   * Add a subsection.
   *
   * @param section section
   */
  public void appendSubSection(Section section) {
    subsections.add(section);
  }

  /**
   * Get the iterator of subsections.
   *
   * @param id section id
   * @return specified section
   */
  public Section getSubSection(int id) {
    return subsections.get(id);
  }

  /**
   * Get super section.
   *
   * @return a section contains this object as a subsection
   */
  public Section getParentSection() {
    return parent;
  }

  /**
   * Set super section.
   *
   * @param parentSection super section
   */
  public void setParentSection(Section parentSection) {
    this.parent = parentSection;
  }

  /**
   * Get the size of subsections.
   *
   * @return size of subsection
   */
  public int getNumberOfSubsections() {
    return subsections.size();
  }

  /**
   * Get level of section.
   *
   * @return section level
   */
  public int getLevel() {
    return level;
  }

  /**
   * Set level of section.
   *
   * @param sectionLevel section level
   */
  public void setLevel(int sectionLevel) {
    this.level = sectionLevel;
  }

  /**
   * Get iterator of header sentences.
   *
   * @return contents of header.
   * NOTE: header can contain more than one header sentences.
   */
  public Iterator<Sentence> getHeaderContents() {
    return headerContent.iterator();
  }

  /**
   * Get iterator of header sentences. When there is not specified header in the section,
   * return null otherwise return specified id.
   *
   * @param id id of sentence in header
   * @return contents of header.
   */
  public Sentence getHeaderContent(int id) {
    if (headerContent.size() > id) {
      return headerContent.get(id);
    } else {
      return null;
    }
  }

  /**
   * Get the number of sentences in header.
   *
   * @return the size of sentences in header
   */
  public int getHeaderContentsListSize() {
    return headerContent.size();
  }

  /**
   * Get last subsection.
   *
   * @return last subsection in this section
   */
  public Section getLastSubsection() {
    return subsections.get(subsections.size() - 1);
  }

  /**
   * Get the iterator of paragraphs of section.
   *
   * @return Iterator of Paragraph
   */
  public Iterator<Paragraph> getParagraphs() {
    return paragraphs.iterator();
  }

  /**
   * Get the specified paragraph.
   *
   * @param id paragraph id
   * @return paragraph
   */
  public Paragraph getParagraph(int id) {
    return paragraphs.get(id);
  }

  /**
   * Add a paragraph.
   *
   * @param pragraph paragraph
   */
  public void appendParagraph(Paragraph pragraph) {
    paragraphs.add(pragraph);
  }

  /**
   * Append sentence.
   *
   * @param line    sentence
   * @param lineNum sentence number
   */
  public void appendSentence(String line, int lineNum) {
    if (paragraphs.size() == 0) {
      appendParagraph(new Paragraph());
    }
    Paragraph currentBlock = paragraphs.get(paragraphs.size() - 1);
    currentBlock.appendSentence(line, lineNum);
    if (currentBlock.getNumberOfSentences() == 1) {
      currentBlock.getSentence(0).isStartParagraph = true;
    }
  }

  /**
   * Append sentence.
   *
   * @param sentence sentence to append
   */
  public void appendSentence(Sentence sentence) {
    if (paragraphs.size() == 0) {
      appendParagraph(new Paragraph());
    }
    Paragraph currentBlock = paragraphs.get(paragraphs.size() - 1);
    currentBlock.appendSentence(sentence);
    if (currentBlock.getNumberOfSentences() == 1) {
      currentBlock.getSentence(0).isStartParagraph = true;
    }
  }

  /**
   * Get block id.
   *
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
   *
   * @param listLevel list level
   * @param contents  list content
   */
  public void appendListElement(int listLevel, List<Sentence> contents) {
    this.lists.get(lists.size() - 1).appendElement(listLevel, contents);
  }

  /**
   * Get size of list.
   *
   * @return number of list block
   */
  public int getNumberOfLists() {
    return lists.size();
  }

  /**
   * Get last list block.
   *
   * @return last list block in the section
   */
  public ListBlock getLastListBlock() {
    return lists.get(lists.size() - 1);
  }

  /**
   * Get specified list block.
   *
   * @param id id of list block
   * @return number of list block
   */
  public ListBlock getListBlock(int id) {
    return lists.get(id);
  }

  /**
   * Get specified list block.
   *
   * @return number of list block
   */
  public Iterator<ListBlock> getListBlocks() {
    return lists.iterator();
  }

  /**
   * Get the number of paragraphs in the section.
   *
   * @return number of paragraphs
   */
  public int getNumberOfParagraphs() {
    return paragraphs.size();
  }

  /* Header */
  private final List<Sentence> headerContent;

  /* Subsections */
  private final List<Section> subsections;

  /* Paragraphs in this section. */
  private final List<Paragraph> paragraphs;

  /* lists */
  private final List<ListBlock> lists;

  /* Section Level */
  private int level;

  /* parent Section */
  private Section parent;
}
