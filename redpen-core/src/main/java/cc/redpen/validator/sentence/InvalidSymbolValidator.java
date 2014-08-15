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
package cc.redpen.validator.sentence;

import cc.redpen.DocumentValidatorException;
import cc.redpen.ValidationError;
import cc.redpen.config.*;
import cc.redpen.model.Sentence;
import cc.redpen.validator.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Validate if there is invalid characters in sentences.
 */
public class InvalidSymbolValidator implements Validator<Sentence> {
  public InvalidSymbolValidator(ValidatorConfiguration config,
                                SymbolTable symbolTable)
      throws DocumentValidatorException {
    initialize(symbolTable);
  }

  public List<ValidationError> validate(Sentence sentence) {
    List<ValidationError> errors = new ArrayList<>();
    Set<String> names = symbolTable.getNames();
    for (String name : names) {
      ValidationError error = validateSymbol(sentence, name);
      if (error != null) {
        errors.add(error);
      }
    }
    return errors;
  }

  private boolean initialize(SymbolTable symbols)
      throws DocumentValidatorException {
    this.symbolTable = symbols;
    return true;
  }

  protected void setSymbolTable(SymbolTable symbols) {
    this.symbolTable = symbols;
  }

  private ValidationError validateSymbol(Sentence sentence, String name) {
    String sentenceStr = sentence.content;
    Symbol symbol = symbolTable.getSymbol(name);
    List<String> invalidCharsList = symbol.getInvalidSymbols();
    for (String invalidChar : invalidCharsList) {
      if (sentenceStr.contains(invalidChar)) {
        return new ValidationError(
            this.getClass(),
            "Invalid symbol found: \"" + invalidChar + "\"",
            sentence);
      }
    }
    return null;
  }

  private SymbolTable symbolTable;
}
