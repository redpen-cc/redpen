package org.unigram.docvalidator.validator;

import java.util.Iterator;
import java.util.Vector;

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

  public Vector<ValidationError> check(FileContent file,
      ResultDistributor distributor) {
    Vector<ValidationError> validationErrors = new Vector<ValidationError>();
    for (Iterator<Section> sectionIterator =
        file.getChilds(); sectionIterator.hasNext();) {
      Section currentSection = sectionIterator.next();
      validationErrors.addAll(this.check(currentSection));
    }
    return validationErrors;
  }

  protected abstract Vector<ValidationError> check(Section section);

  public abstract boolean loadConfiguration(Configuration conf,
      CharacterTable characterTable);

}
