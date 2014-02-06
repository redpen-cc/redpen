package org.unigram.docvalidator.symbol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.util.DVCharacter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Base class of the symbol settings.
 */
public abstract class DVSymbols {
  /**
   * Get the specified character or symbol.
   *
   * @param name name of symbol
   * @return specified character
   */
  public DVCharacter get(String name) {
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

  protected Map<String, DVCharacter> getSymbolTable() {
    return symbolTable;
  }

  private final Map<String, DVCharacter> symbolTable
      = new HashMap<String, DVCharacter>();

  private static final Logger LOG = LoggerFactory.getLogger(DVSymbols.class);
}
