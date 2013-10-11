package org.unigram.docvalidator.util;

/**
 * ResourceExtractor is called from FileLoader. To support a file format,
 * we create a class implementing ResourceExtractor.
 */
public interface ResourceExtractor {
  /**
   * load line.
   * @param line line in a file
   * @return
   */
  int load(String line);
}
