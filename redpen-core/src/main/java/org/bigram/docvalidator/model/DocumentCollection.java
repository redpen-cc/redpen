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
package org.bigram.docvalidator.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * DocumentCollection class represent input document, which consists
 * of more than one documents.
 */
public final class DocumentCollection implements Iterable<Document> {

  public DocumentCollection() {
    super();
    documents = new ArrayList<Document>();
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

  private final List<Document> documents;

  @Override
  public Iterator<Document> iterator() {
    return documents.iterator();
  }

  /**
   * Builder for DocumentCollection.
   */
  public static class Builder {
    public Builder() {
      this.collection = new DocumentCollection();
    }

    public DocumentCollection build() {
      return collection;
    }

    public Builder addDocument(String fileName) {
      Document document = new Document();
      document.setFileName(fileName);
      collection.addDocument(document);
      return this;
    }

    public Builder addSection(int level, List<Sentence> header) {
      Document lastDocument = collection.getFile(collection.size()-1);
      lastDocument.appendSection(new Section(level, header));
      return this;
    }

    public Builder addSection(int level) {
      Document lastDocument = collection.getFile(collection.size()-1);
      lastDocument.appendSection(new Section(level, new ArrayList<Sentence>()));
      return this;
    }

    public Builder addSectionHeader(String header) {
      Document lastDocument = collection.getFile(collection.size()-1);
      List<Sentence> headers = lastDocument.getLastSection().getHeaderContents();
      headers.add(new Sentence(header, headers.size()));
      return this;
    }

    public Builder addParagraph() {
      Document lastDocument = collection.getFile(collection.size()-1);
      Section lastSection = lastDocument.getSection(
          lastDocument.getNumberOfSections()-1);
      lastSection.appendParagraph(new Paragraph());
      return this;
    }

    public Builder addSentence(String content, int lineNumber) {
      Document lastDocument = collection.getFile(collection.size()-1);
      Section lastSection = lastDocument.getSection(
          lastDocument.getNumberOfSections()-1);
      Paragraph lastParagraph = lastSection.getParagraph(
          lastSection.getNumberOfParagraphs()-1);
      lastParagraph.appendSentence(content, lineNumber);
      return this;
    }

    public Builder addListBlock() {
      Document lastDocument = collection.getFile(collection.size()-1);
      Section lastSection = lastDocument.getSection(
          lastDocument.getNumberOfSections()-1);
      lastSection.appendListBlock();
      return this;
    }

    public Builder addListElement(int level, List<Sentence> contents) {
      Document lastDocument = collection.getFile(collection.size()-1);
      Section lastSection = lastDocument.getSection(
          lastDocument.getNumberOfSections()-1);
      lastSection.appendListElement(level, contents);
      return this;
    }

    public Builder addListElement(int level, String str) {
      List<Sentence> elementSentence = new ArrayList<Sentence>();
      elementSentence.add(new Sentence(str, 0));
      this.addListElement(level, elementSentence);
      return this;
    }

    private DocumentCollection collection;


  }
}
