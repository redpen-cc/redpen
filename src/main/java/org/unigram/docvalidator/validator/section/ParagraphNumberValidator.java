package org.unigram.docvalidator.validator.section;

import java.util.List;
import java.util.ArrayList;

import org.unigram.docvalidator.store.Section;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.Configuration;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.validator.SectionValidator;
/**
 * Validate paragraph number. If a section has paragraphs more than spcified,
 * This validator reports it.
 */
public class ParagraphNumberValidator extends SectionValidator {
  /**
   * Default maximum number of paragraphs in a section.
   */
  public static final int DEFAULT_MAX_PARAGRAPHS_IN_A_SECTION = 100;

  @Override
  protected List<ValidationError> check(Section section) {
    List<ValidationError> validationErrors = new ArrayList<ValidationError>();
    int paragraphNumber = section.getParagraphNumber();
    if (maxParagraphs < paragraphNumber) {
      validationErrors.add(new ValidationError(
          "The number of the paragraphs exceeds the maximum "
              + String.valueOf(paragraphNumber))); // @TODO add file information
      return validationErrors;
    }
    return validationErrors;
  }

  @Override
  public boolean loadConfiguration(Configuration conf,
      CharacterTable characterTable) {
    if (conf.getAttribute("max_char_number") == null) {
      this.maxParagraphs = DEFAULT_MAX_PARAGRAPHS_IN_A_SECTION;
    } else {
      this.maxParagraphs = Integer.valueOf(conf.getAttribute("max_paragraphs"));
    }
    return true;
  }

  protected int maxParagraphs;
}
