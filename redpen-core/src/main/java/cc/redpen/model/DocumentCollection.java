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
     * Get a file specifying with the Document id.
     *
     * @param id id of Document
     * @return a document
     */
    public Document getDocument(int id) {
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
         * Add a document in document collection.
         *
         * @param document
         * @return builder
         */
        public Builder addDocument(Document document) {
            collection.addDocument(document);
            return this;
        }


    }
}
