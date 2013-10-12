package org.unigram.docvalidator.validator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.unigram.docvalidator.store.FileContent;
import org.unigram.docvalidator.store.Section;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.Configuration;
import org.unigram.docvalidator.util.ResultDistributor;
import org.unigram.docvalidator.util.ValidationError;
/**
 * Validate sections in documents.
 */
public abstract class SectionValidator implements Validator {
  public List<ValidationError> check(FileContent file,
      ResultDistributor distributor) {
    List<ValidationError> validationErrors = new ArrayList<ValidationError>();
    for (Iterator<Section> sectionIterator =
        file.getChilds(); sectionIterator.hasNext();) {
      Section currentSection = sectionIterator.next();
      validationErrors.addAll(this.check(currentSection));
    }
    return validationErrors;
  }

  /**
   * To append a new Validator which use section or paragraph information, we
   * make a new class implementing this method.
   * @param section input section
   * @return list of errors
   */
  protected abstract List<ValidationError> check(Section section);

  public abstract boolean loadConfiguration(Configuration conf,
      CharacterTable characterTable);

}
