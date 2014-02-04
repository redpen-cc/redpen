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
package org.unigram.docvalidator.parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.symbol.DefaultSymbols;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.DVResource;

/**
 * Abstract Parser class containing common procedures to
 * implements the concrete Parser classes.
 */
public abstract class BasicDocumentParser implements Parser {
  /**
   * Given configuration resource, return basic configuration settings.
   *
   * @param resource object containing configuration settings
   */
  public final boolean initialize(DVResource resource) {
    if (resource == null) {
      LOG.error("Given resource is null");
      return false;
    }
    if (resource.getCharacterTable() == null) {
      LOG.error("Character table in the given resource is null");
      return false;
    }

    CharacterTable characterTable = resource.getCharacterTable();

    // set full stop characters
    if (characterTable.isContainCharacter("FULL_STOP")) {
      this.periods.add(
          characterTable.getCharacter("FULL_STOP").getValue());
    } else {
      this.periods.add(
          DefaultSymbols.getInstance().get("FULL_STOP").getValue());
    }

    if (characterTable.isContainCharacter("QUESTION_MARK")) {
      this.periods.add(
          characterTable.getCharacter("QUESTION_MARK").getValue());
    } else {
      this.periods.add(
          DefaultSymbols.getInstance().get("QUESTION_MARK").getValue());
    }

    if (characterTable.isContainCharacter("EXCLAMATION_MARK")) {
      this.periods.add(
          characterTable.getCharacter("EXCLAMATION_MARK").getValue());
    } else {
      this.periods.add(
          DefaultSymbols.getInstance().get("EXCLAMATION_MARK").getValue());
    }

    for (String period : this.periods) {
      LOG.info("\"" + period + "\" is added as a end of sentence character");
    }

    this.sentenceExtractor = new SentenceExtractor(this.periods);
    return true;
  }

  protected BufferedReader createReader(InputStream is) {
    BufferedReader br;
    try {
      br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      LOG.error(e.getMessage());
      return null;
    }
    return br;
  }

  protected final InputStream loadStream(String fileName) {
    InputStream inputStream = null;
    if (fileName == null || fileName.equals("")) {
      LOG.error("input file was not specified.");
      return null;
    } else {
      try {
        inputStream = new FileInputStream(fileName);
      } catch (FileNotFoundException e) {
        LOG.error("Input file is not found: " + e.getMessage());
      }
    }
    return inputStream;
  }

  /**
   * Get SentenceExtractor object.
   *
   * @return sentence extractor object
   */
  protected SentenceExtractor getSentenceExtractor() {
    return sentenceExtractor;
  }

  private SentenceExtractor sentenceExtractor;

  private List<String> periods = new ArrayList<String>();

  private static final Logger LOG = LoggerFactory.getLogger(
      BasicDocumentParser.class);

}
