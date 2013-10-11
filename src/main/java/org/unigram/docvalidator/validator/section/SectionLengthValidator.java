package org.unigram.docvalidator.validator.section;

import java.util.Iterator;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.store.Paragraph;
import org.unigram.docvalidator.store.Section;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.util.Configuration;
import org.unigram.docvalidator.validator.SectionValidator;
/**
 * Validate the length of one section.
 */
public class SectionLengthValidator extends SectionValidator {

  public SectionLengthValidator() {
    super();
  }

  @Override
  protected Vector<ValidationError> check(Section section) {
    Vector<ValidationError> validationErrors = new Vector<ValidationError>();
    int sectionCharNumber = 0;
    for (Iterator<Paragraph> paraIterator =
        section.getParagraph(); paraIterator.hasNext();) {
      Paragraph currentPraParagraph = paraIterator.next();
      for (Iterator<Sentence> sentenceIterator =
          currentPraParagraph.getChilds(); sentenceIterator.hasNext();) {
        Sentence sentence = sentenceIterator.next();
        sectionCharNumber += sentence.content.length();
      }
      if (sectionCharNumber > maxSectionCharNumber) {
        validationErrors.add(new ValidationError(
            "The number of the charractor exceeds the maximum "
            + String.valueOf(sectionCharNumber))); // @TODO add file information
      }
    }
    return validationErrors;
  }

  @Override
  public boolean loadConfiguration(Configuration conf,
      CharacterTable characterTable) {
    if (conf.getAttribute("max_char_number") == null) {
      this.maxSectionCharNumber = DEFAULT_MAXIMUM_CHAR_NUMBER_IN_A_SECTION;
      LOG.info("max_char_number was not set.");
      LOG.info("Using the default value of max_char_number.");
    } else {
      this.maxSectionCharNumber = Integer.valueOf(
          conf.getAttribute("max_char_number"));
    }
    return true;
  }

  private static final int DEFAULT_MAXIMUM_CHAR_NUMBER_IN_A_SECTION = 1000;

  private static Logger LOG =
      LoggerFactory.getLogger(SectionLengthValidator.class);

  protected int maxSectionCharNumber;

}
