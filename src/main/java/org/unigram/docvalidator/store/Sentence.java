/**
 * DocumentValidator
 * Copyright (c) 2013-, Takahiko Ito, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package org.unigram.docvalidator.store;

import java.util.ArrayList;
import java.util.List;

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
    this.links = new ArrayList<String>();
  }

  public int getBlockID() {
    return 0;
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

  /**
   * links (including internal and external ones)
   */
  public List<String> links;

}
