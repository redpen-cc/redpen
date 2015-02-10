/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
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
package cc.redpen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent a section in semi-structured text format such as wiki.
 */
public final class Section {
    /* Subsections */
    private final List<Section> subsections;
    /* Paragraphs in this section. */
    private final List<Paragraph> paragraphs;
    /* lists */
    private final List<ListBlock> lists;
    /* Header */
    private List<Sentence> headerContent;
    /* Section Level */
    private int level;
    /* parent Section */
    private Section parent;

    /**
     * Constructor.
     *
     * @param sectionLevel section level
     */
    public Section(int sectionLevel) {
        super();
        this.level = sectionLevel;
        this.headerContent = new ArrayList<>();
        this.subsections = new ArrayList<>();
        this.paragraphs = new ArrayList<>();
        this.lists = new ArrayList<>();
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
        this.subsections = new ArrayList<>();
        this.paragraphs = new ArrayList<>();
        this.lists = new ArrayList<>();
    }

    /**
     * Constructor.
     *
     * @param sectionLevel section level
     * @param headerString header content string
     */
    public Section(int sectionLevel, String headerString) {
        Sentence headerSentence = new Sentence(headerString, 0);
        List<Sentence> headers = new ArrayList<>();
        headers.add(headerSentence);
        this.level = sectionLevel;
        this.headerContent = headers;
        this.subsections = new ArrayList<>();
        this.paragraphs = new ArrayList<>();
        this.lists = new ArrayList<>();
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
    public List<Sentence> getHeaderContents() {
        return headerContent;
    }

    /**
     * Get iterator of header sentences. When there is not specified
     * header in the section, return null otherwise return specified id.
     *
     * @param id id of sentence in header
     * @return contents of header when there is specified header, otherwise null
     */
    public Sentence getHeaderContent(int id) {
        if (headerContent.size() > id) {
            return headerContent.get(id);
        } else {
            return null;
        }
    }

    /**
     * Get iterator of header sentences. When there is not specified
     * header in the section, return null otherwise return specified id.
     *
     * @return header sentence containing all header contents in the section
     */
    public Sentence getJoinedHeaderContents() {
        StringBuilder joinedHeader = new StringBuilder();
        int lineNum = 0;
        if (headerContent.size() > 0) {
            lineNum = headerContent.get(0).getLineNumber();
        }
        int i = 0;
        for (Sentence header : headerContent) {
            if (i != 0) {
                joinedHeader.append(" ");

            }
            joinedHeader.append(header.getContent());
            i++;
        }
        return new Sentence(joinedHeader.toString(), lineNum);
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
     * Get the iterator of paragraphs of section.
     *
     * @return Iterator of Paragraph
     */
    public List<Paragraph> getParagraphs() {
        return paragraphs;
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
    public List<ListBlock> getListBlocks() {
        return lists;
    }

    /**
     * Get the number of paragraphs in the section.
     *
     * @return number of paragraphs
     */
    public int getNumberOfParagraphs() {
        return paragraphs.size();
    }
}

