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
package org.bigram.docvalidator.validator.sentence;

import org.bigram.docvalidator.DocumentValidatorException;
import org.bigram.docvalidator.ValidationError;
import org.bigram.docvalidator.config.CharacterTable;
import org.bigram.docvalidator.config.ValidatorConfiguration;
import org.bigram.docvalidator.model.Sentence;
import org.bigram.docvalidator.validator.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Validate symbol has before and after symbols. Needed spaces is depend on
 * the symbol and defined in DVCharacterTable.
 */
public class SymbolWithSpaceValidator implements Validator<Sentence> {

  public SymbolWithSpaceValidator(ValidatorConfiguration config,
                                  CharacterTable characterTable) throws
      DocumentValidatorException {
    initialize(characterTable);
  }

  public List<ValidationError> validate(Sentence sentence) {
    List<ValidationError> errors = new ArrayList<>();
    Set<String> names = characterTable.getNames();
    for (String name : names) {
      ValidationError error = validateCharacter(sentence, name);
      if (error != null) {
        errors.add(error);
      }
    }
    return errors;
  }

  private boolean initialize(CharacterTable characterConf)
      throws DocumentValidatorException {
    this.characterTable = characterConf;
    return true;
  }

  protected void setCharacterTable(CharacterTable characters) {
    this.characterTable = characters;
  }

  private ValidationError validateCharacter(Sentence sentence, String name) {
    String sentenceStr = sentence.content;
    org.bigram.docvalidator.config.Character character = characterTable.getCharacter(name);
    if (!character.isNeedAfterSpace() && !character.isNeedBeforeSpace()) {
        return null;
    }

    String target = character.getValue();
    int position = sentenceStr.indexOf(target);
    if (position != -1) {
      if (position > 0 && character.isNeedBeforeSpace()
          && !java.lang.Character.isWhitespace(sentenceStr.charAt(position - 1))) {
        return new ValidationError(
            this.getClass(),
            "Need white space before symbol (" +  character.getName()
            + "): " + sentenceStr.charAt(position) + ".",
            sentence);
      } else if (position < sentenceStr.length() - 1
          && character.isNeedAfterSpace()
          && !java.lang.Character.isWhitespace(sentenceStr.charAt(position + 1))) {
        return new ValidationError(
            this.getClass(),
            "Need white space after symbol (" + character.getName()
            + "): " + sentenceStr.charAt(position), sentence);
      }
    }
    return null;
  }

  private CharacterTable characterTable;
}
