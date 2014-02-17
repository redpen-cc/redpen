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
package org.unigram.docvalidator.store;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Document class represent input document, which consists
 * of more than one files.
 */
public final class Document implements Block {

  public Document() {
    super();
    files = new ArrayList<FileContent>();
  }

  /**
   * Get files contained by Document.
   *
   * @return iterator of file
   */
  public Iterator<FileContent> getFiles() {
    return files.iterator();
  }

  /**
   * Add a file to Document.
   *
   * @param file a file to be added to Document
   */
  public void appendFile(FileContent file) {
    files.add(file);
  }

  /**
   * Get a file specifying with the file id.
   *
   * @param id id of file
   * @return a file
   */
  public FileContent getFile(int id) {
    return files.get(id);
  }

  /**
   * Get last file in Document.
   *
   * @return a file added in the last
   */
  public FileContent getLastFile() {
    FileContent fileContent = null;
    if (files.size() > 0) {
      fileContent = files.get(files.size() - 1);
    }
    return fileContent;
  }

  /**
   * Return the file number in Document.
   *
   * @return the number of files
   */
  public int getNumberOfFiles() {
    return files.size();
  }

  public int getBlockID() {
    return BlockTypes.DOCUMENT;
  }

  private final List<FileContent> files;
}
