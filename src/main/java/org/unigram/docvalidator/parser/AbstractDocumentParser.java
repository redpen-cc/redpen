package org.unigram.docvalidator.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.Configuration;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.DefaultSymbols;

/**
 * Abstract Parser class containing common procedures to
 * implements the concrete Parser classes.
 */
public abstract class AbstractDocumentParser implements Parser {
  /**
   * load basic configuration settings.
   * @param resource object containing configuration settings
   */
  public final boolean initialize(DVResource resource) {
    if (resource == null) {
      return false;
    }
    Configuration conf = resource.getConfiguration();
    CharacterTable characterTable = resource.getCharacterTable();

    this.comment = DefaultSymbols.DEFAULT_COMMENT.getValue();
    if (conf.hasAttribute("comment")) {
      this.comment = conf.getAttribute("comment");
    }

    this.comma = DefaultSymbols.DEFAULT_COMMA.getValue();
    if (characterTable.isContainCharacter("COMMA")) {
      this.comma = characterTable.getCharacter("COMMA").getValue();
      LOG.info("comma is set to \"" + this.comma + "\"");
    }

    this.period = DefaultSymbols.DEFAULT_PERIOD.getValue();
    if (characterTable.isContainCharacter("FULL_STOP")) {
      this.period = characterTable.getCharacter("FULL_STOP").getValue();
      LOG.info("full stop is set to \"" + this.period + "\"");
    }
    return true;
  }

  protected final InputStream loadStream(String fileName)
      throws DocumentValidatorException {
    InputStream inputStream = null;
    if (fileName == null || fileName.equals("")) {
      LOG.error("input file was not specified.");
      return null;
    } else {
      try {
        inputStream = new FileInputStream(fileName);
      } catch (FileNotFoundException e) {
        LOG.error("Input file is not fould: " + e.getMessage());
      }
    }
    return inputStream;
  }

  protected String comma;
  protected String comment;
  protected String period;

  private static Logger LOG = LoggerFactory.getLogger(Parser.class);
}
