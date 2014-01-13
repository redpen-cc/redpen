/**
 * DocumentValidator
 * Copyright (c) 2013-, Takahiko Ito, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package org.unigram.docvalidator.validator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.unigram.docvalidator.store.FileContent;
import org.unigram.docvalidator.store.Section;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.ValidatorConfiguration;
import org.unigram.docvalidator.util.ResultDistributor;
import org.unigram.docvalidator.util.ValidationError;
/**
 * Validate sections in documents.
 */
public abstract class SectionValidator implements Validator {

  public abstract boolean loadConfiguration(ValidatorConfiguration conf,
      CharacterTable characterTable);

  public List<ValidationError> check(FileContent file,
      ResultDistributor distributor) {
    List<ValidationError> validationErrors = new ArrayList<ValidationError>();
    for (Iterator<Section> sectionIterator =
        file.getSections(); sectionIterator.hasNext();) {
      Section currentSection = sectionIterator.next();
      List<ValidationError> errors = this.check(currentSection);
      addFileInformation(errors, file, currentSection);
      validationErrors.addAll(errors);
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

  private void addFileInformation(List<ValidationError> errors,
                                  FileContent file, Section section) {
    for (ValidationError error : errors) {
      error.setFileName(file.getFileName());
      error.setLineNumber(section.getHeaderContent(0).position);
    }
  }
}
