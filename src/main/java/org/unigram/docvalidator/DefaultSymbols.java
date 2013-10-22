package org.unigram.docvalidator;

import org.unigram.docvalidator.util.DVCharacter;

/**
 * Contain the default symbols and characters.
 */
public final class DefaultSymbols {
  /******************************************************************
   * Common symbols
   ******************************************************************/

  /**
   * Default SPACE character.
   */
  public static final DVCharacter DEFAULT_SPACE =
      new DVCharacter("SPACE", " ", "", false, false);

  /**
   * Default EXCLAMATION_MARK character.
   */
  public static final DVCharacter DEFAULT_EXCLAMATION_MARK =
      new DVCharacter("EXCLAMATION_MARK", "!", "", false, false);

  /**
   * Default LEFT_SINGLE_QUOTATION_MARK character.
   */
  public static final DVCharacter DEFAULT_LEFT_SINGLE_QUOTATION_MARK =
      new DVCharacter("LEFT_SINGLE_QUOTATION_MARK", "\'", "", false, false);

  /**
   * Default RIGHT_SINGLE_QUOTATION_MARK character.
   */
  public static final DVCharacter DEFAULT_RIGHT_SINGLE_QUOTATION_MARK =
      new DVCharacter("RIGHT_SINGLE_QUOTATION_MARK", "\'", "", false, false);

  /**
   * Default LEFT_DOUBLE_QUOTATION_MARK character.
   */
  public static final DVCharacter DEFAULT_LEFT_DOUBLE_QUOTATION_MARK =
      new DVCharacter("LEFT_DOUBLE_QUOTATION_MARK", "\"", "", false, false);

  /**
   * Default RIGHT_DOUBLE_QUOTATION_MARK character.
   */
  public static final DVCharacter DEFAULT_RIGHT_DOUBLE_QUOTATION_MARK =
      new DVCharacter("RIGHT_DOBULE_QUOTATION_MARK", "\"", "", false, false);

  /**
   * Default NUMBER_SIGN character.
   */
  public static final DVCharacter DEFAULT_NUMBER_SIGN =
      new DVCharacter("NUMBER_SIGN", "#", "", false, false);

  /**
   * Default DOLLAR_SIGN character.
   */
  public static final DVCharacter DEFAULT_DOLLAR_SIGN =
      new DVCharacter("DOLLAR_SIGN", "?", "", false, false);

  /**
   * Default PERCENT_SIGN character.
   */
  public static final DVCharacter DEFAULT_PERCENT_SIGN =
      new DVCharacter("PERCENT_SIGN", "%", "", false, false);

  /**
   * Default AMPERSAND character.
   */
  public static final DVCharacter DEFAULT_AMPERSAND =
      new DVCharacter("AMPERSAND", "&", "", false, false);

  /**
   * Default LEFT_PARENTHESIS character.
   */
  public static final DVCharacter DEFAULT_LEFT_PARENTHESIS =
      new DVCharacter("LEFT_LEFT_PARENTHESIS", "(", "", false, false);

  /**
   * Default RIGHT_PARENTHESIS character.
   */
  public static final DVCharacter DEFAULT_RIGHT_PARENTHESIS =
      new DVCharacter("LEFT_LEFT_PARENTHESIS", ")", "", false, false);

  /**
   * Default ASTERISK character.
   */
  public static final DVCharacter DEFAULT_ASTERISK =
      new DVCharacter("ASTERISK", ",", "", false, false);

  /**
   * Default COMMA character.
   */
  public static final DVCharacter DEFAULT_COMMA =
      new DVCharacter("COMMA", ",", "", false, false);

  /**
   * Default comment character.
   * @todo move this element into other class, since comment is dependent on
   * document format.
   */
  public static final DVCharacter DEFAULT_COMMENT =
      new DVCharacter("COMMENT", "#", "", false, false);

  /**
   * Default PERIOD (FULL_STOP) character.
   */
  public static final DVCharacter DEFAULT_PERIOD =
      new DVCharacter("FULL_STOP", ".", "", false, false);

  /**
   * Default PLUS_SIGN character.
   */
  public static final DVCharacter DEFAULT_PLUS_SIGN =
      new DVCharacter("+", ",", "", false, false);

  /**
   * Default HYPHEN_SIGN character.
   */
  public static final DVCharacter DEFAULT_HYPHEN =
      new DVCharacter("-", ",", "", false, false);

  /**
   * Default MINUS_SIGN character.
   */
  public static final DVCharacter DEFAULT_MINUS_SIGN =
      new DVCharacter("-", ",", "", false, false);

  /**
   * Default SLASH character.
   */
  public static final DVCharacter DEFAULT_SLAH =
      new DVCharacter("/", ",", "", false, false);

  /**
   * Default COLON character.
   */
  public static final DVCharacter DEFAULT_COLON =
      new DVCharacter(":", ",", "", false, false);

  /**
   * Default SEMICOLON character.
   */
  public static final DVCharacter DEFAULT_SEMICOLON =
      new DVCharacter(";", ",", "", false, false);

  /**
   * Default LESS_THAN_SIGN character.
   */
  public static final DVCharacter DEFAULT_LESS_THAN_SIGN =
      new DVCharacter("<", ",", "", false, false);

  /**
   * Default EQUAL_SIGN character.
   */
  public static final DVCharacter DEFAULT_EQUAL_SIGN =
      new DVCharacter("=", ",", "", false, false);

  /**
   * Default GREATER_THAN_SIGN character.
   */
  public static final DVCharacter DEFAULT_GREATER_THAN_SIGN =
      new DVCharacter(">", ",", "", false, false);

  /**
   * Default AT_SIGN character.
   */
  public static final DVCharacter DEFAULT_AT_SIGN =
      new DVCharacter("@", ",", "", false, false);

  /**
   * Default LEFT_SQUARE_BRACKET character.
   */
  public static final DVCharacter DEFAULT_LEFT_SQUARE_BRACKET =
      new DVCharacter("[", ",", "", false, false);

  /**
   * Default RIGHT_SQUARE_BRACKET character.
   */
  public static final DVCharacter DEFAULT_RIGHT_SQUARE_BRACKET =
      new DVCharacter("[", ",", "", false, false);

  /**
   * Default BACKSLASH character.
   */
  public static final DVCharacter DEFAULT_BACKSLASH =
      new DVCharacter("\\", ",", "", false, false);

  /**
   * Default CIRCUMFLEX_ACCENT character.
   */
  public static final DVCharacter DEFAULT_CIRCUMFLEX_ACCENT =
      new DVCharacter("^", ",", "", false, false);

  /**
   * Default LOW_LINE character.
   */
  public static final DVCharacter DEFAULT_LOW_LINE =
      new DVCharacter("_", ",", "", false, false);

  /**
   * Default LEFT_SQUARE_BRACKET character.
   */
  public static final DVCharacter DEFAULT_LEFT_CURLY_BRACKET =
      new DVCharacter("{", ",", "", false, false);

  /**
   * Default RIGHT_SQUARE_BRACKET character.
   */
  public static final DVCharacter DEFAULT_RIGHT_CURRY_BRACKET =
      new DVCharacter("}", ",", "", false, false);

  /**
   * Default VERTICAL_BAR character.
   */
  public static final DVCharacter DEFAULT_VERTICAL_BAR =
      new DVCharacter("|", ",", "", false, false);

  /**
   * Default TILDE character.
   */
  public static final DVCharacter DEFAULT_TILDE =
      new DVCharacter("TILDE", "~", "", false, false);

  /******************************************************************
   * Digits
   ******************************************************************/

  /**
   * Default DIGIT_ZERO character.
   */
  public static final DVCharacter DEFAULT_DIGIT_ZERO =
      new DVCharacter("0", ",", "", false, false);

  /**
   * Default DIGIT_ONE character.
   */
  public static final DVCharacter DEFAULT_DIGIT_ONE =
      new DVCharacter("1", ",", "", false, false);

  /**
   * Default DIGIT_TWO character.
   */
  public static final DVCharacter DEFAULT_DIGIT_TWO =
      new DVCharacter("2", ",", "", false, false);

  /**
   * Default DIGIT_THREE character.
   */
  public static final DVCharacter DEFAULT_DIGIT_THREE =
      new DVCharacter("3", ",", "", false, false);

  /**
   * Default DIGIT_FOUR character.
   */
  public static final DVCharacter DEFAULT_DIGIT_FOUR =
      new DVCharacter("4", ",", "", false, false);

  /**
   * Default DIGIT_FIVE character.
   */
  public static final DVCharacter DEFAULT_DIGIT_FIVE =
      new DVCharacter("5", ",", "", false, false);

  /**
   * Default DIGIT_SIX character.
   */
  public static final DVCharacter DEFAULT_DIGIT_SIX =
      new DVCharacter("6", ",", "", false, false);

  /**
   * Default DIGIT_SEVEN character.
   */
  public static final DVCharacter DEFAULT_DIGIT_SEVEN =
      new DVCharacter("7", ",", "", false, false);

  /**
   * Default DIGIT_EIGHT character.
   */
  public static final DVCharacter DEFAULT_DIGIT_EIGHT =
      new DVCharacter("8", ",", "", false, false);

  /**
   * Default DIGIT_NINE character.
   */
  public static final DVCharacter DEFAULT_DIGIT_NINE =
      new DVCharacter("9", ",", "", false, false);

  private DefaultSymbols() { }
}
