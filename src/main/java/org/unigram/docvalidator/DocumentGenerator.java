package org.unigram.docvalidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.parser.Parser;
import org.unigram.docvalidator.parser.DocumentParserFactory;
import org.unigram.docvalidator.store.Document;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.DocumentValidatorException;

/**
 * Generate Document object loading input file.
 */
public final class DocumentGenerator {
  /**
   * Generate Document from input file.
   * @param inputFileNames input file name
   * @param resource configuration resource
   * @param format input file format
   * @return Document object
   */
  static Document generate(String[] inputFileNames,
        DVResource resource,
        String format) {
    Parser docparser = null;
    try {
      docparser = DocumentParserFactory.generate(format, resource);
    } catch (DocumentValidatorException e) {
      LOG.error("Failed to create document parser: " + e.getMessage());
    }
    Document document = new Document();
    for (int i = 0; i < inputFileNames.length; i++) {
      try {
        document.appendFile(docparser.generateDocument(inputFileNames[i]));
      } catch (DocumentValidatorException e) {
        e.printStackTrace();
        return null;
      }
    }
    // @TODO extract summary information to validate document effectively
    document.extractSummary();
    return document;
  }

  private static Logger LOG = LoggerFactory.getLogger(DocumentGenerator.class);

  private DocumentGenerator() {
    super();
  }
}
