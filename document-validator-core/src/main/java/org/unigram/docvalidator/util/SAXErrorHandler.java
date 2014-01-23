package org.unigram.docvalidator.util;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Error handler class of SAX parser. Without this class SAXParser flush the error messages into standard
 * error.
 */
public class SAXErrorHandler implements ErrorHandler {

  @Override
  public void error(SAXParseException exception) throws SAXException {
    throw exception;
  }

  @Override
  public void fatalError(SAXParseException exception) throws SAXException {
    throw exception;
  }

  @Override
  public void warning(SAXParseException exception) throws SAXException {
    throw exception;
  }
}
