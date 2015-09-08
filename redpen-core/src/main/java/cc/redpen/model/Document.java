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

import cc.redpen.tokenizer.RedPenTokenizer;
import cc.redpen.tokenizer.WhiteSpaceTokenizer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Document represents a file with many elements
 * such as sentences, lists and headers.
 */
public class Document implements Iterable<Section>, Serializable {

    private static final long serialVersionUID = 1628589004095293831L;
    private final List<Section> sections;
    private final Optional<String> fileName;

    /**
     * Constructor.
     *
     * @param sections list of sections
     * @param fileName file name
     */
    public Document(List<Section> sections, Optional<String> fileName) {
        this.sections = sections;
        this.fileName = fileName;
    }

    /**
     * Get last Section.
     *
     * @return last section in the Document
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
    public int size() {
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
     * Get file name.
     *
     * @return file name
     */
    public Optional<String> getFileName() {
        return fileName;
    }

    @Override
    public Iterator<Section> iterator() {
        return sections.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Document sections1 = (Document) o;

        if (fileName != null ? !fileName.equals(sections1.fileName) : sections1.fileName != null) return false;
        if (sections != null ? !sections.equals(sections1.sections) : sections1.sections != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sections != null ? sections.hashCode() : 0;
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Document{" +
                "sections=" + sections +
                ", fileName=" + fileName +
                '}';
    }

    public static class DocumentBuilder {
        private final RedPenTokenizer tokenizer;
        boolean built = false;
        private final List<Section> sections;
        Optional<String> fileName;

        /**
         * Constructor.
         */
        public DocumentBuilder() {
            this(new WhiteSpaceTokenizer());
        }

        /**
         * Constructor.
         * @param tokenizer tokenizer
         */
        public DocumentBuilder(RedPenTokenizer tokenizer) {
            sections = new ArrayList<>();
            fileName = Optional.empty();
            this.tokenizer = tokenizer;
        }

        /**
         * Add a section.
         *
         * @param section a section in file content
         * @return DocumentBuilder itself
         */
        public DocumentBuilder appendSection(Section section) {
            ensureNotBuilt();
            sections.add(section);
            return this;
        }

        /**
         * Add a sentence.
         *
         * @param sentence sentence
         * @return DocumentBuilder itself
         */
        public DocumentBuilder addSentence(Sentence sentence) {
            ensureNotBuilt();
            if (sections.size() == 0) {
                throw new IllegalStateException("No section to add a sentence");
            }
            Section lastSection = getSection(sections.size() - 1);
            if (lastSection.getNumberOfParagraphs() == 0) {
                addParagraph(); // Note: add paragraph automatically
            }
            Paragraph lastParagraph = lastSection.getParagraph(
                    lastSection.getNumberOfParagraphs() - 1);

            lastParagraph.appendSentence(sentence);
            if (lastParagraph.getNumberOfSentences() == 1) {
                sentence.setIsFirstSentence(true);
            }
            sentence.setTokens(tokenizer.tokenize(sentence.getContent()));
            return this;
        }

        /**
         * Get last Section.
         *
         * @return last section in the Document
         */
        public Section getLastSection() {
            Section section = null;
            if (sections.size() > 0) {
                section = sections.get(sections.size() - 1);
            }
            return section;
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
         * Add paragraph to document.
         *
         * @return builder
         */
        public DocumentBuilder addParagraph() {
            ensureNotBuilt();
            if (sections.size() == 0) {
                throw new IllegalStateException("No section to add paragraph");
            }
            Section lastSection = getSection(sections.size() - 1);
            lastSection.appendParagraph(new Paragraph());
            return this;
        }


        /**
         * Add a new list block.
         *
         * @return builder itself
         */
        public DocumentBuilder addListBlock() {
            ensureNotBuilt();
            if (sections.size() == 0) {
                throw new IllegalStateException("No section to add a sentence");
            }
            Section lastSection = getSection(sections.size() - 1);
            lastSection.appendListBlock();
            return this;
        }

        /**
         * Add list element to the last list block.
         *
         * @param level    indentation level
         * @param contents content of list element
         * @return builder
         */
        public DocumentBuilder addListElement(int level, List<Sentence> contents) {
            ensureNotBuilt();
            if (sections.size() == 0) {
                throw new IllegalStateException("No section to add a sentence");
            }
            Section lastSection = getSection(sections.size() - 1);
            lastSection.appendListElement(level, contents);
            return this;
        }

        /**
         * Add list element to the last list block.
         *
         * @param level indentation level
         * @param str   content of list element
         * @return builder
         * NOTE: parameter str is not split into more than one Sentence object.
         */
        public DocumentBuilder addListElement(int level, String str) {
            ensureNotBuilt();
            List<Sentence> elementSentence = new ArrayList<>();
            elementSentence.add(new Sentence(str, 0));
            this.addListElement(level, elementSentence);
            return this;
        }

        /**
         * Add a section to the document.
         *
         * @param level  section level
         * @param header header contents
         * @return builder
         */
        public DocumentBuilder addSection(int level, List<Sentence> header) {
            ensureNotBuilt();
            appendSection(new Section(level, header));
            return this;
        }

        /**
         * Add section header content to the last section.
         *
         * @param header header content
         * @return builder
         * NOTE: parameter header is not split into more than one Sentence object.
         */
        public DocumentBuilder addSectionHeader(String header) {
            ensureNotBuilt();
            Section lastSection = getLastSection();
            if (null == lastSection) {
                throw new IllegalStateException("Document does not have any section");
            }
            List<Sentence> headers = lastSection.getHeaderContents();
            headers.add(new Sentence(header, headers.size()));
            return this;
        }

        /**
         * Add a section without header content.
         *
         * @param level section level
         * @return builder
         */
        public DocumentBuilder addSection(int level) {
            ensureNotBuilt();
            addSection(level, new ArrayList<>());
            return this;
        }


        /**
         * Add sentence to document.
         *
         * @param content    sentence content
         * @param lineNumber line number
         * @return builder
         */
        public DocumentBuilder addSentence(String content, int lineNumber) {
            ensureNotBuilt();
            addSentence(new Sentence(content, lineNumber));
            return this;
        }

        /**
         * Set file name.
         *
         * @param name file name
         * @return builder
         */
        public DocumentBuilder setFileName(String name) {
            ensureNotBuilt();
            this.fileName = Optional.of(name);
            return this;
        }

        private void ensureNotBuilt() {
            if (built) {
                throw new IllegalStateException("already built");
            }
        }

        public Document build() {
            built = true;
            return new Document(sections, fileName);
        }
    }


}
