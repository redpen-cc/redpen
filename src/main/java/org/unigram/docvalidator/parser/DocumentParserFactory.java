package org.unigram.docvalidator.parser;

import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.DocumentValidatorException;

/**
 * Factory class of DoucmentParser.
 */
public final class DocumentParserFactory {
  /**
   * Create DocuemntParser object following specified input file format.
   * @param parserType type of parser (plain text, wiki etc.)
   * @param resource configuration settings
   * @return Parser object
   * @throws DocumentValidatorException
   */
  public static Parser generate(String parserType, DVResource resource)
        throws DocumentValidatorException {
    Parser docparser = null;
    if (parserType == "w") {
      docparser = new WikiParser();
    } else if (parserType == "t") {
      docparser = new PlainTextParser();
    } else {
      throw new DocumentValidatorException("Specified parser type not exist: "
          + parserType);
    }
    docparser.initialize(resource);
    return docparser;
  }

  private DocumentParserFactory() {
    super();
  }
}
