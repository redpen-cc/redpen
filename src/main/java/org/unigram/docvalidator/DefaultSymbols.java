package org.unigram.docvalidator;

/**
 * Contain the default symbols and characters.
 */
public final class DefaultSymbols {
  /**
   * Default COMMA character.
   * If comma used in validations is not specified, this value
   * is used as the defaults.
   */
  public static final String DEFAULT_COMMA = ",";

  /**
   * Default comment character.
   * If no comment character in the configuration, this value is used as the
   * default.
   */
  public static final String DEFAULT_COMMENT = "#";

  /**
   * Default PERIOD (FULL_STOP) character.
   * If period character is not specified, this cahracter is used as the
   * default.
   */
  public static final String DEFAULT_PERIOD = ".";

  private DefaultSymbols() { }
}
