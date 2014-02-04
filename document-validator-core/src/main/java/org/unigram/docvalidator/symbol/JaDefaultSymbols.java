package org.unigram.docvalidator.symbol;

import org.unigram.docvalidator.util.DVCharacter;

import java.util.Map;

/**
 * Contain default settings for Japanese text.
 */
public class JaDefaultSymbols extends DVSymbols {

  public static DVSymbols getInstance() {
    return INSTANCE;
  }

  public JaDefaultSymbols() {
    /******************************************************************
     * Common symbols
     ******************************************************************/
    Map<String, DVCharacter> symbols = getSymbolTable();

    symbols.put("SPACE", new DVCharacter("SPACE", " ", "", false, false));
    symbols.put("EXCLAMATION_MARK",
        new DVCharacter("EXCLAMATION_MARK", "！", "!", false, false));
    symbols.put("NUMBER_SIGN",
        new DVCharacter("NUMBER_SIGN", "＃", "#", false, false));
    symbols.put("DOLLAR_SIGN",
        new DVCharacter("DOLLAR_SIGN", "$", "＄", false, false));
    symbols.put("PERCENT_SIGN",
        new DVCharacter("PERCENT_SIGN", "％", "%", false, false));
    symbols.put("QUESTION_MARK",
        new DVCharacter("QUESTION_MARK", "？", "?", false, false));
    symbols.put("AMPERSAND",
        new DVCharacter("AMPERSAND", "＆", "&", false, false));
    symbols.put("LEFT_PARENTHESIS",
        new DVCharacter("LEFT_PARENTHESIS", "（", "(", false, false));
    symbols.put("RIGHT_PARENTHESIS",
        new DVCharacter("RIGHT_PARENTHESIS", "）", ")", false, false));
    symbols.put("ASTERISK",
        new DVCharacter("ASTERISK", "＊", "*", false, false));
    symbols.put("COMMA", new DVCharacter("COMMA", "、", ",", false, false));
    symbols.put("FULL_STOP",
        new DVCharacter("FULL_STOP", "。", ".", false, false));
    symbols.put("PLUS_SIGN",
        new DVCharacter("PLUS_SIGN", "＋", "+", false, false));
    symbols.put("HYPHEN_SIGN",
        new DVCharacter("HYPHEN_SIGN", "ー", "-", false, false));
    symbols.put("MINUS_SIGN",
        new DVCharacter("MINUS_SIGN", "ー", "-", false, false));
    symbols.put("SLASH", new DVCharacter("SLASH", "／", "/", false, false));
    symbols.put("COLON", new DVCharacter("COLON", "：", ":", false, false));
    symbols.put("SEMICOLON",
        new DVCharacter("SEMICOLON", "；", ";", false, false));
    symbols.put("LESS_THAN_SIGN",
        new DVCharacter("LESS_THAN_SIGN", "＜", "<", false, false));
    symbols.put("EQUAL_SIGN",
        new DVCharacter("EQUAL_SIGN", "＝", "=", false, false));
    symbols.put("GREATER_THAN_SIGN",
        new DVCharacter("GREATER_THAN_SIGN", "＞", ">", false, false));
    symbols.put("AT_MARK",
        new DVCharacter("AT_MARK", "＠", "@", false, false));
    symbols.put("LEFT_SQUARE_BRACKET",
        new DVCharacter("LEFT_SQUARE_BRACKET", "「", "", false, false));
    symbols.put("RIGHT_SQUARE_BRACKET",
        new DVCharacter("RIGHT_SQUARE_BRACKET", "」", "", false, false));
    symbols.put("BACKSLASH",
        new DVCharacter("BACKSLASH", "¥", "\\", false, false));
    symbols.put("CIRCUMFLEX_ACCENT",
        new DVCharacter("CIRCUMFLEX_ACCENT", "＾", "^", false, false));
    symbols.put("LOW_LINE",
        new DVCharacter("LOW_LINE", "＿", "_", false, false));
    symbols.put("LEFT_CURLY_BRACKET",
        new DVCharacter("LEFT_CURLY_BRACKET", "｛", "", false, false));
    symbols.put("RIGHT_CURLY_BRACKET",
        new DVCharacter("RIGHT_CURLY_BRACKET", "｝", "", false, false));
    symbols.put("VERTICAL_BAR",
        new DVCharacter("VERTICAL_BAR", "｜", "|", false, false));
    symbols.put("TILDE",
        new DVCharacter("TILDE", "〜", "~", false, false));

    /******************************************************************
     * Digits
     ******************************************************************/

    symbols.put("DIGIT_ZERO", new DVCharacter("1", ",", "", false, false));
    symbols.put("DIGIT_ONE", new DVCharacter("2", ",", "", false, false));
    symbols.put("DIGIT_TWO", new DVCharacter("3", ",", "", false, false));
    symbols.put("DIGIT_THREE",
        new DVCharacter("3", ",", "", false, false));
    symbols.put("DIGIT_FOUR", new DVCharacter("4", ",", "", false, false));
    symbols.put("DIGIT_FIVE", new DVCharacter("5", ",", "", false, false));
    symbols.put("DIGIT_SIX", new DVCharacter("6", ",", "", false, false));
    symbols.put("DIGIT_SEVEN",
        new DVCharacter("7", ",", "", false, false));
    symbols.put("DIGIT_EIGHT",
        new DVCharacter("8", ",", "", false, false));
    symbols.put("DIGIT_NINE", new DVCharacter("9", ",", "", false, false));
  }

  private static DVSymbols INSTANCE = new JaDefaultSymbols();
}
