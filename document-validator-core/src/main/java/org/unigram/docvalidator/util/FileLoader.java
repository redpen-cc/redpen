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
package org.unigram.docvalidator.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Load file from input file name or stream.
 */
public class FileLoader {
  /**
   * Constructor.
   *
   * @param ex ResourceExtractor
   */
  public FileLoader(ResourceExtractor ex) {
    this.resourceExtractor = ex;
  }

  /**
   * Load input file.
   *
   * @param fileName input file name.
   * @return 0 when succeeded to load, 1 otherwise
   */
  public int loadFile(String fileName) {
    InputStream inputStream;
    try {
      LOG.warn("input file: " + fileName);
      inputStream = new FileInputStream(fileName);
    } catch (IOException e) {
      LOG.error("IO Error ", e);
      return 1;
    }
    if (loadFile(inputStream) != 0) {
      LOG.error("Failed to load file: " + fileName);
    }
    IOUtils.closeQuietly(inputStream);
    return 0;
  }

  /**
   * Load input stream
   *
   * @param inputStream input stream
   * @return 0 when succeeded to load input stream, 1 otherwise
   */
  public int loadFile(InputStream inputStream) {
    if (inputStream == null) {
      LOG.error("Input Stream is null");
      return 1;
    }
    InputStreamReader inputStreamReader = null;
    BufferedReader bufferedReader = null;
    try {
      inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
      bufferedReader = new BufferedReader(inputStreamReader);
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        if (this.resourceExtractor.load(line) != 0) {
          LOG.error("Failed to load line:" + line);
          return 1;
        }
      }
    } catch (IOException e) {
      LOG.error("IO Error ", e);
      return 1;
    } finally {
      IOUtils.closeQuietly(bufferedReader);
      IOUtils.closeQuietly(inputStreamReader);
    }
    return 0;
  }

  private static final Logger LOG = LoggerFactory.getLogger(FileLoader.class);

  private final ResourceExtractor resourceExtractor;
}
