/**
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unigram.docvalidator.validator.section;

import org.unigram.docvalidator.model.Document;
import org.unigram.docvalidator.model.Section;
import org.unigram.docvalidator.util.ValidationError;

import java.util.List;

/**
 * Validate sections in documents.
 */
public abstract class AbstractSectionValidator implements SectionValidator {

//  public abstract boolean loadConfiguration(ValidatorConfiguration conf,
//      CharacterTable characterTable);

  /**
   * To append a new Validator which use section or paragraph information, we
   * make a new class implementing this method.
   * @param section input section
   */
//  protected abstract List<ValidationError> validate(Section section);

  private void addFileInformation(List<ValidationError> errors,
                                  Document file, Section section) {
    for (ValidationError error : errors) {
      error.setFileName(file.getFileName());
      error.setLineNumber(section.getHeaderContent(0).position);
    }
  }
}
