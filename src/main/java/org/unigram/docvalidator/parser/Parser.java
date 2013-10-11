package org.unigram.docvalidator.parser;

import java.io.InputStream;

import org.unigram.docvalidator.store.FileContent;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.DocumentValidatorException;
/**
 * Parser generates FileContent from input.
 */
public interface Parser {
  /**
   * generate FileContent instance from a stream.
   * @param io input stream containing input content
   * @return FileContent object
   * @throws DocumentValidatorException
   */
  FileContent generateDocument(InputStream io)
      throws DocumentValidatorException;

  /**
   * generate FileContent instance for the specified file.
   * @param fileName input file name
   * @return FileContent object
   * @throws DocumentValidatorException
   */
  FileContent generateDocument(String fileName)
        throws DocumentValidatorException;

  /**
   * initialize parser.
   * @param resource configuration resrouces
   * @return true if the configurations are loaded, otherwise false
   */
  boolean initialize(DVResource resource);
}
