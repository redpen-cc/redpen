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

  public boolean initialize(ValidatorConfiguration conf,
      CharacterTable characters)
      throws DocumentValidatorException {
    this.characterTable = characters;
    return true;
  }

  protected void setCharacterTable(CharacterTable characters) {
    this.characterTable = characters;
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

  private CharacterTable characterTable;
}
