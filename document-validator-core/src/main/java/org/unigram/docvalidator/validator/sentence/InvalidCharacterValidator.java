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
package org.unigram.docvalidator.validator.sentence;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.ValidatorConfiguration;
import org.unigram.docvalidator.util.DVCharacter;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.validator.SentenceValidator;

/**
 * Validate if there is invalid characters in sentences.
 */
public class InvalidCharacterValidator implements SentenceValidator {
  public List<ValidationError> check(Sentence sentence) {
    List<ValidationError> errors = new ArrayList<ValidationError>();
    Set<String> names = characterTable.getNames();
    for (String name : names) {
      ValidationError error = validateCharacter(sentence, name);
      if (error != null) {
        errors.add(error);
      }
    }
    return errors;
  }

  public boolean initialize(ValidatorConfiguration conf, CharacterTable characterTable)
      throws DocumentValidatorException {
    this.characterTable = characterTable;
    return true;
  }

  private ValidationError validateCharacter(Sentence sentence, String name) {
    String sentenceStr = sentence.content;
    DVCharacter character = characterTable.getCharacter(name);
    List<String> invalidCharsList = character.getInvalidChars();
    for (String invalidChar : invalidCharsList) {
      if (sentenceStr.contains(invalidChar)) {
        return new ValidationError(
            "Invalid symbol found: \"" + invalidChar + "\"",
            sentence);
      }
    }
    return null;
  }

  protected CharacterTable characterTable;
}
