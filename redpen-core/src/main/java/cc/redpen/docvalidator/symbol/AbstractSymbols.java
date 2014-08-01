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
package cc.redpen.docvalidator.symbol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cc.redpen.docvalidator.config.Character;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Base class of the symbol settings.
 */
public abstract class AbstractSymbols {
  /**
   * Get the specified character or symbol.
   *
   * @param name name of symbol
   * @return specified character
   */
  public Character get(String name) {
    if (!symbolTable.containsKey(name)) {
      LOG.info(name + " is not defined in DefaultSymbols.");
      return null;
    }
    return symbolTable.get(name);
  }

  /**
   * Return all the names of registered characters.
   *
   * @return all names of characters
   */
  public Iterator<String> getAllCharacterNames() {
    return symbolTable.keySet().iterator();
  }

  protected Map<String, Character> getSymbolTable() {
    return symbolTable;
  }

  private final Map<String, Character> symbolTable
      = new HashMap<>();

  private static final Logger LOG = LoggerFactory.getLogger(AbstractSymbols.class);
}
