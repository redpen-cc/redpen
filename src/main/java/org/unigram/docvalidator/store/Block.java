package org.unigram.docvalidator.store;

/**
 * Interface of the elements in documents.
 */
public interface Block {
 /**
  * get the Identifier of the block.
  * @return block id
  */
  int getBlockID();

  /**
   * extract summary of the block.
   * @return
   */
  int extractSummary();
}
