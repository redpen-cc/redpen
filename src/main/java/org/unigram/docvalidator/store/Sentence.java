package org.unigram.docvalidator.store;

/**
 * Represent a sentence in a text file.
 */
public final class Sentence implements Block {
  /**
   * constructor.
   * @param sentenceContent content of sentence
   * @param sentencePosition sentence position
   */
  public Sentence(String sentenceContent, int sentencePosition) {
    super();
    this.content = sentenceContent;
    this.position = sentencePosition;
    this.isStartaragraph = false;
  }

  /**
   * content of string.
   */
  public String content;

  /**
   * sentence position in a file.
   */
  public int position;

  /**
   * first sentence in a paragraph.
   */
  public boolean isStartaragraph;

  public int getBlockID() {
    return 0;
  }

  public int extractSummary() {
    return 0;
  }
}
