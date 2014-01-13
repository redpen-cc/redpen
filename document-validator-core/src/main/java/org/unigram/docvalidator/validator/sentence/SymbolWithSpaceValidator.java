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
 * Validate symbol has before and after symbols. Needed spaces is depend on
 * the symbol and defined in DVCharacterTable.
 */
public class SymbolWithSpaceValidator  implements SentenceValidator {
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

  public boolean initialize(ValidatorConfiguration validatorConf,
      CharacterTable characterConf)
      throws DocumentValidatorException {
    this.characterTable = characterConf;
    return true;
  }

  private ValidationError validateCharacter(Sentence sentence, String name) {
    String sentenceStr = sentence.content;
    DVCharacter character = characterTable.getCharacter(name);
    if (!character.isNeedAfterSpace() && !character.isNeedBeforeSpace()) {
        return null;
    }

    String target = character.getValue();
    int position = sentenceStr.indexOf(target);
    if (position != -1) {
      if (position > 0 && character.isNeedBeforeSpace()
          && !Character.isWhitespace(sentenceStr.charAt(position - 1))) {
        return new ValidationError(
            "Need white space before symbol (" +  character.getName()
            + "): " + sentenceStr.charAt(position) + ".",
            sentence);
      } else if (position < sentenceStr.length() - 1
          && character.isNeedAfterSpace()
          && !Character.isWhitespace(sentenceStr.charAt(position + 1))) {
        return new ValidationError(
            "Need white space after symbol (" + character.getName()
            + "): " + sentenceStr.charAt(position), sentence);
      }
    }
    return null;
  }

  protected CharacterTable characterTable;
}
