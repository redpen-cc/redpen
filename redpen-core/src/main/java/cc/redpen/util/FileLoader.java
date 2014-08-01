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
package cc.redpen.util;

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
   * Given a input stream, load the contents.
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
