package org.unigram.docvalidator.validator.section;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.store.Paragraph;
import org.unigram.docvalidator.store.Section;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.Configuration;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.validator.SectionValidator;

/**
 * Validate whether paragraph start as specified.
 */
public class ParagraphStartWithValidator extends SectionValidator {
  /**
   * Default matter paragraph start with.
   */
  public static final String DEFAULT_PARAGRAPH_START_WITH = " ";

  public ParagraphStartWithValidator() {
    super();
    this.beginingOfParagraph = DEFAULT_PARAGRAPH_START_WITH;
  }

  @Override
  protected List<ValidationError> check(Section section) {
    List<ValidationError> validationErrors = new ArrayList<ValidationError>();
    for (Iterator<Paragraph> paraIterator =
        section.getParagraph(); paraIterator.hasNext();) {
      Paragraph currentParagraph = paraIterator.next();
      Sentence firstSentence = currentParagraph.getLine(0);
      if (firstSentence.content.indexOf(this.beginingOfParagraph) != 0) {
        validationErrors.add(new ValidationError(
            firstSentence.position,
            "Found invalid begining of paragraph: \""
            + "\" in line: " + firstSentence.content));
      }
    }
    return validationErrors;
  }

  @Override
  public boolean loadConfiguration(Configuration conf,
      CharacterTable characterTable) {
    if (conf.getAttribute("paragraph_start_with") == null) {
      this.beginingOfParagraph = DEFAULT_PARAGRAPH_START_WITH;
      LOG.info("Using the default valude of paragraph_start_with.");
    } else {
      this.beginingOfParagraph = conf.getAttribute("paragraph_start_with");
    }
    return true;
  }

  private String beginingOfParagraph;

  private static Logger LOG =
      LoggerFactory.getLogger(ParagraphStartWithValidator.class);

}
