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

import cc.redpen.RedPenException;
import cc.redpen.ValidationError;
import cc.redpen.config.*;
import cc.redpen.model.Sentence;
import cc.redpen.validator.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Validate symbol has before and after symbols. Needed spaces is depend on
 * the symbol and defined in DVCharacterTable.
 */
public class SymbolWithSpaceValidator implements Validator<Sentence> {

  public SymbolWithSpaceValidator(ValidatorConfiguration config,
                                  SymbolTable symbolTable) throws
      RedPenException {
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

  private boolean initialize(SymbolTable symbolConf)
      throws RedPenException {
    this.symbolTable = symbolConf;
    return true;
  }

  protected void setSymbolTable(SymbolTable symbols) {
    this.symbolTable = symbols;
  }

  private ValidationError validateSymbol(Sentence sentence, String name) {
    String sentenceStr = sentence.content;
    Symbol symbol = symbolTable.getSymbol(name);
    if (!symbol.isNeedAfterSpace() && !symbol.isNeedBeforeSpace()) {
        return null;
    }

    String target = symbol.getValue();
    int position = sentenceStr.indexOf(target);
    if (position != -1) {
      if (position > 0 && symbol.isNeedBeforeSpace()
          && !Character.isWhitespace(sentenceStr.charAt(position - 1))) {
        return new ValidationError(
            this.getClass(),
            "Need white space before symbol (" +  symbol.getName()
            + "): " + sentenceStr.charAt(position) + ".",
            sentence);
      } else if (position < sentenceStr.length() - 1
          && symbol.isNeedAfterSpace()
          && !Character.isWhitespace(sentenceStr.charAt(position + 1))) {
        return new ValidationError(
            this.getClass(),
            "Need white space after symbol (" + symbol.getName()
            + "): " + sentenceStr.charAt(position), sentence);
      }
    }
    return null;
  }

  private SymbolTable symbolTable;
}
