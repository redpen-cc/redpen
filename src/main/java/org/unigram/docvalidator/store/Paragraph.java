package org.unigram.docvalidator.store;

import java.util.Iterator;
import java.util.Vector;
/**
 * Represent a paragraph of text.
 */
public final class Paragraph implements Block {
  /**
   * constructor.
   */
  public Paragraph() {
    super();
    sentences = new Vector<Sentence>();
  }

  /**
   * get the iterator of sentences.
   * @return Iterator of Sentence in the paragraph
   */
  public Iterator<Sentence> getChilds() {
    return sentences.iterator();
  }

  /**
   * get the sentence of specified number.
   * @param lineNumber sentence number
   * @return sentence
   */
  public Sentence getLine(int lineNumber) {
    return sentences.get(lineNumber);
  }

  public void appendSentence(String childBlock, int lineNum) {
    sentences.add(new Sentence(childBlock, lineNum));
  }

  public int getNumverOfSentences() {
    return sentences.size();
  }

  public int getBlockID() {
    return BlockTypes.PARAGRAPH;
  }

  public int extractSummary() {
    return 0;
  }

  Vector<Sentence> sentences;
}
