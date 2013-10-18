package org.unigram.docvalidator;

import org.unigram.docvalidator.util.DVCharacter;

/**
 * Contain the default symbols and characters.
 */
public final class DefaultSymbols {
  /**
   * Default COMMA character.
   * If comma used in validations is not specified, this value
   * is used as the defaults.
   */
  public static final DVCharacter DEFAULT_COMMA =
      new DVCharacter("COMMA", ",", "", false, true);

  /**
   * Default comment character.
   * If no comment character in the configuration, this value is used as the
   * default.
   */
  public static final DVCharacter DEFAULT_COMMENT =
      new DVCharacter("COMMENT", "#", "", false, false);

  /**
   * Default PERIOD (FULL_STOP) character.
   * If period character is not specified, this cahracter is used as the
   * default.
   */
  public static final DVCharacter DEFAULT_PERIOD =
      new DVCharacter("FULL_STOP", ".", "", false, true);

  private DefaultSymbols() { }
}
