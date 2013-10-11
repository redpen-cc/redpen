package org.unigram.docvalidator.store;

import java.util.Iterator;
import java.util.Vector;

/**
 * Document class represent input document, which consists
 * of more than one files.
 */
public final class Document implements Block {

  public Iterator<FileContent> getFiles() {
    return files.iterator();
  }

  public void appendFile(FileContent file) {
    files.add(file);
  }

  public Document() {
    super();
    files = new Vector<FileContent>();
  }

  public FileContent getLastSection() {
    return files.lastElement();
  }

  public int getSizeOfChildren() {
    return files.size();
  }

  public int getBlockID() {
    return BlockTypes.DOCUMENT;
  }

  public int extractSummary() {
    // extract summary information from files
    // extract total summary
    return 0;
  }

  private Vector<FileContent> files;
}
