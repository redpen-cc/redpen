/**
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
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
import java.util.Iterator;
import java.util.List;

/**
 * DocumentCollection class represent input document, which consists
 * of more than one documents.
 */
public final class DocumentCollection implements Iterable<Document> {

    private final List<Document> documents;

    public DocumentCollection() {
        super();
        documents = new ArrayList<>();
    }

    /**
     * Get documents contained by DocumentCollection.
     *
     * @return iterator of file
     */
    public Iterator<Document> getDocuments() {
        return documents.iterator();
    }

    /**
     * Add a file to DocumentCollection.
     *
     * @param file a file to be added to DocumentCollection
     */
    public void addDocument(Document file) {
        documents.add(file);
    }

    /**
     * Get a file specifying with the file id.
     *
     * @param id id of file
     * @return a file
     */
    public Document getFile(int id) {
        return documents.get(id);
    }

    /**
     * Returns the size of the DocumentCollection.
     *
     * @return the number of documents
     */
    public int size() {
        return documents.size();
    }

    @Override
    public Iterator<Document> iterator() {
        return documents.iterator();
    }

    public int getNumberOfDocuments() {
        return documents.size();
    }

    /**
     * Builder for DocumentCollection. This class is used to create a document in
     * not only testing but also implementing parsers.
     */
    public static class Builder {
        private DocumentCollection collection;

        public Builder() {
            this.collection = new DocumentCollection();
        }

        /**
         * Return the built DocumentCollection object.
         *
         * @return built document
         */
        public DocumentCollection build() {
            return collection;
        }

        /**
         * Return last Document object.
         * NOTE: This method is created to follow the Parser class api.
         * Maybe this should be removed...
         *
         * @return Last Document object in the document collection.
         */
        public Document getLastDocument() {
            if (collection.size() == 0) {
                return null;
            }
            return collection.getFile(collection.size() - 1);
        }

        /**
         * Return the last section in the document under construction.
         *
         * @return last section
         */
        public Section getLastSection() {
            if (collection.size() == 0) {
                return null;
            }
            Document lastDocument = collection.getFile(collection.size() - 1);

            if (lastDocument.getNumberOfSections() == 0) {
                return null;
            }
            return lastDocument.getSection(lastDocument.getNumberOfSections() - 1);
        }

        /**
         * Add a document in document collection.
         *
         * @param fileName input file name
         * @return builder
         */
        public Builder addDocument(String fileName) {
            Document document = new Document();
            document.setFileName(fileName);
            collection.addDocument(document);
            return this;
        }

        /**
         * Add a section to the document.
         *
         * @param level  section level
         * @param header header contents
         * @return builder
         */
        public Builder addSection(int level, List<Sentence> header) {
            if (collection.size() == 0) {
                throw new IllegalStateException("DocumentCollection does no have any document");
            }
            Document lastDocument = collection.getFile(collection.size() - 1);
            lastDocument.appendSection(new Section(level, header));
            return this;
        }

        /**
         * Add a section without header content.
         *
         * @param level section level
         * @return builder
         */
        public Builder addSection(int level) {
            addSection(level, new ArrayList<>());
            return this;
        }

        /**
         * Add section header content to the last section.
         *
         * @param header header content
         * @return builder
         * NOTE: parameter header is not split into more than one Sentence object.
         */
        public Builder addSectionHeader(String header) {
            if (collection.size() == 0) {
                throw new IllegalStateException("DocumentCollection does no have any document");
            }
            Document lastDocument = collection.getFile(collection.size() - 1);
            Section lastSection = lastDocument.getLastSection();
            if (null == lastSection) {
                throw new IllegalStateException("Document does not have any section");
            }
            List<Sentence> headers = lastSection.getHeaderContents();
            headers.add(new Sentence(header, headers.size()));
            return this;
        }

        /**
         * Add paragraph to document.
         *
         * @return builder
         */
        public Builder addParagraph() {
            if (collection.size() == 0) {
                throw new IllegalStateException("DocumentCollection does no have any document");
            }
            Document lastDocument = collection.getFile(collection.size() - 1);
            if (lastDocument.getNumberOfSections() == 0) {
                throw new IllegalStateException("No section to add paragraph");
            }
            Section lastSection = lastDocument.getSection(
                    lastDocument.getNumberOfSections() - 1);
            lastSection.appendParagraph(new Paragraph());
            return this;
        }

        /**
         * Add sentence to document.
         *
         * @param content    sentence content
         * @param lineNumber line number
         * @return builder
         */
        public Builder addSentence(String content, int lineNumber) {
            addSentence(new Sentence(content, lineNumber));
            return this;
        }

        /**
         * Add sentence to document.
         *
         * @param sentence sentence
         * @return builder
         * NOTE: this method assign isFirstSentence to true when the sentence
         * is the first sentence of a paragraph.
         */
        public Builder addSentence(Sentence sentence) {
            if (collection.size() == 0) {
                throw new IllegalStateException("DocumentCollection does no have any document");
            }
            Document lastDocument = collection.getFile(collection.size() - 1);
            if (lastDocument.getNumberOfSections() == 0) {
                throw new IllegalStateException("No section to add a sentence");
            }
            Section lastSection = lastDocument.getSection(
                    lastDocument.getNumberOfSections() - 1);

            if (lastSection.getNumberOfParagraphs() == 0) {
                addParagraph(); // Note: add paragraph automatically
            }
            Paragraph lastParagraph = lastSection.getParagraph(
                    lastSection.getNumberOfParagraphs() - 1);
            lastParagraph.appendSentence(sentence);
            if (lastParagraph.getNumberOfSentences() == 1) {
                sentence.isFirstSentence = true;
            }
            return this;
        }

        /**
         * Add a new list block.
         *
         * @return builder
         */
        public Builder addListBlock() {
            if (collection.size() == 0) {
                throw new IllegalStateException("DocumentCollection does no have any document");
            }
            Document lastDocument = collection.getFile(collection.size() - 1);

            if (lastDocument.getNumberOfSections() == 0) {
                throw new IllegalStateException("No section to add a sentence");
            }
            Section lastSection = lastDocument.getSection(
                    lastDocument.getNumberOfSections() - 1);
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
        public Builder addListElement(int level, List<Sentence> contents) {
            if (collection.size() == 0) {
                throw new IllegalStateException("DocumentCollection does no have any document");
            }
            Document lastDocument = collection.getFile(collection.size() - 1);

            if (lastDocument.getNumberOfSections() == 0) {
                throw new IllegalStateException("No section to add a sentence");
            }
            Section lastSection = lastDocument.getSection(
                    lastDocument.getNumberOfSections() - 1);
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
        public Builder addListElement(int level, String str) {
            List<Sentence> elementSentence = new ArrayList<>();
            elementSentence.add(new Sentence(str, 0));
            this.addListElement(level, elementSentence);
            return this;
        }
    }
}
