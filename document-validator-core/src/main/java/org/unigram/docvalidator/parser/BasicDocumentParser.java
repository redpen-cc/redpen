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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.DefaultSymbols;

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
      return false;
    }
    CharacterTable characterTable = resource.getCharacterTable();

    this.period = DefaultSymbols.get("FULL_STOP").getValue();
    if (characterTable.isContainCharacter("FULL_STOP")) {
      this.period = characterTable.getCharacter("FULL_STOP").getValue();
      LOG.info("full stop is set to \"" + this.period + "\"");
    }
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

  protected String period;

  private static final Logger LOG = LoggerFactory.getLogger(
      BasicDocumentParser.class);
}
