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
package org.unigram.docvalidator.symbol;

import org.unigram.docvalidator.config.Character;

import java.util.Map;

/**
 * Contain default settings for Japanese text.
 */
public class JapaneseSymbols extends AbstractSymbols {

  public static AbstractSymbols getInstance() {
    return INSTANCE;
  }

  public JapaneseSymbols() {
    /******************************************************************
     * Common symbols
     ******************************************************************/
    Map<String, Character> symbols = getSymbolTable();

    symbols.put("SPACE", new Character("SPACE", "　", " ", false, false));
    symbols.put("EXCLAMATION_MARK",
        new Character("EXCLAMATION_MARK", "！", "!", false, false));
    symbols.put("NUMBER_SIGN",
        new Character("NUMBER_SIGN", "＃", "#", false, false));
    symbols.put("DOLLAR_SIGN",
        new Character("DOLLAR_SIGN", "$", "＄", false, false));
    symbols.put("PERCENT_SIGN",
        new Character("PERCENT_SIGN", "％", "%", false, false));
    symbols.put("QUESTION_MARK",
        new Character("QUESTION_MARK", "？", "?", false, false));
    symbols.put("AMPERSAND",
        new Character("AMPERSAND", "＆", "&", false, false));
    symbols.put("LEFT_PARENTHESIS",
        new Character("LEFT_PARENTHESIS", "（", "(", false, false));
    symbols.put("RIGHT_PARENTHESIS",
        new Character("RIGHT_PARENTHESIS", "）", ")", false, false));
    symbols.put("ASTERISK",
        new Character("ASTERISK", "＊", "*", false, false));
    symbols.put("COMMA", new Character("COMMA", "、", ",", false, false));
    symbols.put("FULL_STOP",
        new Character("FULL_STOP", "。", ".", false, false));
    symbols.put("PLUS_SIGN",
        new Character("PLUS_SIGN", "＋", "+", false, false));
    symbols.put("HYPHEN_SIGN",
        new Character("HYPHEN_SIGN", "ー", "-", false, false));
    symbols.put("MINUS_SIGN",
        new Character("MINUS_SIGN", "ー", "-", false, false));
    symbols.put("SLASH", new Character("SLASH", "／", "/", false, false));
    symbols.put("COLON", new Character("COLON", "：", ":", false, false));
    symbols.put("SEMICOLON",
        new Character("SEMICOLON", "；", ";", false, false));
    symbols.put("LESS_THAN_SIGN",
        new Character("LESS_THAN_SIGN", "＜", "<", false, false));
    symbols.put("EQUAL_SIGN",
        new Character("EQUAL_SIGN", "＝", "=", false, false));
    symbols.put("GREATER_THAN_SIGN",
        new Character("GREATER_THAN_SIGN", "＞", ">", false, false));
    symbols.put("AT_MARK",
        new Character("AT_MARK", "＠", "@", false, false));
    symbols.put("LEFT_SQUARE_BRACKET",
        new Character("LEFT_SQUARE_BRACKET", "「", "", false, false));
    symbols.put("RIGHT_SQUARE_BRACKET",
        new Character("RIGHT_SQUARE_BRACKET", "」", "", false, false));
    symbols.put("BACKSLASH",
        new Character("BACKSLASH", "¥", "\\", false, false));
    symbols.put("CIRCUMFLEX_ACCENT",
        new Character("CIRCUMFLEX_ACCENT", "＾", "^", false, false));
    symbols.put("LOW_LINE",
        new Character("LOW_LINE", "＿", "_", false, false));
    symbols.put("LEFT_CURLY_BRACKET",
        new Character("LEFT_CURLY_BRACKET", "｛", "", false, false));
    symbols.put("RIGHT_CURLY_BRACKET",
        new Character("RIGHT_CURLY_BRACKET", "｝", "", false, false));
    symbols.put("VERTICAL_BAR",
        new Character("VERTICAL_BAR", "｜", "|", false, false));
    symbols.put("TILDE",
        new Character("TILDE", "〜", "~", false, false));

    /******************************************************************
     * Digits
     ******************************************************************/

    symbols.put("DIGIT_ZERO", new Character("０", "0", "", false, false));
    symbols.put("DIGIT_ONE", new Character("１", "1", "", false, false));
    symbols.put("DIGIT_TWO", new Character("２", "2", "", false, false));
    symbols.put("DIGIT_THREE",
        new Character("３", "3", "", false, false));
    symbols.put("DIGIT_FOUR", new Character("４", "4", "", false, false));
    symbols.put("DIGIT_FIVE", new Character("５", "5", "", false, false));
    symbols.put("DIGIT_SIX", new Character("６", "6", "", false, false));
    symbols.put("DIGIT_SEVEN",
        new Character("７", "7", "", false, false));
    symbols.put("DIGIT_EIGHT",
        new Character("８", "8", "", false, false));
    symbols.put("DIGIT_NINE", new Character("９", "9", "", false, false));
  }

  private static AbstractSymbols INSTANCE = new JapaneseSymbols();
}
