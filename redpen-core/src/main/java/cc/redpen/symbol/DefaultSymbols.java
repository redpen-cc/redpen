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
package cc.redpen.symbol;

import cc.redpen.config.Symbol;

import java.util.Map;

/**
 * Contain the default symbols and characters.
 */
public final class DefaultSymbols extends AbstractSymbols {

    private static AbstractSymbols INSTANCE = new DefaultSymbols();

    private DefaultSymbols() {
        /******************************************************************
         * Common symbols
         ******************************************************************/
        Map<String, Symbol> symbols = getSymbolTable();

        symbols.put("SPACE", new Symbol("SPACE", " ", "", false, false));
        symbols.put("EXCLAMATION_MARK",
                new Symbol("EXCLAMATION_MARK", "!", "！", false, false));
        symbols.put("NUMBER_SIGN",
                new Symbol("NUMBER_SIGN", "#", "＃", false, false));
        symbols.put("DOLLAR_SIGN",
                new Symbol("DOLLAR_SIGN", "$", "＄", false, false));
        symbols.put("PERCENT_SIGN",
                new Symbol("PERCENT_SIGN", "%", "％", false, false));
        symbols.put("QUESTION_MARK",
                new Symbol("QUESTION_MARK", "?", "？", false, false));
        symbols.put("AMPERSAND",
                new Symbol("AMPERSAND", "&", "＆", false, false));
        symbols.put("LEFT_PARENTHESIS",
                new Symbol("LEFT_PARENTHESIS", "(", "（", false, false));
        symbols.put("RIGHT_PARENTHESIS",
                new Symbol("RIGHT_PARENTHESIS", ")", "）", false, false));
        symbols.put("ASTERISK",
                new Symbol("ASTERISK", "*", "＊", false, false));
        symbols.put("COMMA", new Symbol("COMMA", ",", "，、", false, false));
        symbols.put("FULL_STOP",
                new Symbol("FULL_STOP", ".", "．。", false, false));
        symbols.put("PLUS_SIGN",
                new Symbol("PLUS_SIGN", "+", "＋", false, false));
        symbols.put("HYPHEN_SIGN",
                new Symbol("HYPHEN_SIGN", "-", "ー", false, false));
        symbols.put("SLASH", new Symbol("SLASH", "/", "／", false, false));
        symbols.put("COLON", new Symbol("COLON", ":", "：", false, false));
        symbols.put("SEMICOLON",
                new Symbol("SEMICOLON", ";", "；", false, false));
        symbols.put("LESS_THAN_SIGN",
                new Symbol("LESS_THAN_SIGN", "<", "＜", false, false));
        symbols.put("EQUAL_SIGN",
                new Symbol("EQUAL_SIGN", "=", "＝", false, false));
        symbols.put("GREATER_THAN_SIGN",
                new Symbol("GREATER_THAN_SIGN", ">", "＞", false, false));
        symbols.put("AT_MARK",
                new Symbol("AT_MARK", "@", "＠", false, false));
        symbols.put("LEFT_SQUARE_BRACKET",
                new Symbol("LEFT_SQUARE_BRACKET", "[", "", false, false));
        symbols.put("RIGHT_SQUARE_BRACKET",
                new Symbol("RIGHT_SQUARE_BRACKET", "]", "", false, false));
        symbols.put("BACKSLASH",
                new Symbol("BACKSLASH", "\\", "", false, false));
        symbols.put("CIRCUMFLEX_ACCENT",
                new Symbol("CIRCUMFLEX_ACCENT", "^", "", false, false));
        symbols.put("LOW_LINE",
                new Symbol("LOW_LINE", "_", "", false, false));
        symbols.put("LEFT_CURLY_BRACKET",
                new Symbol("LEFT_CURLY_BRACKET", "{", "｛", false, false));
        symbols.put("RIGHT_CURLY_BRACKET",
                new Symbol("RIGHT_CURLY_BRACKET", "}", "｝", false, false));
        symbols.put("VERTICAL_BAR",
                new Symbol("VERTICAL_BAR", "|", "｜", false, false));
        symbols.put("TILDE",
                new Symbol("TILDE", "~", "〜", false, false));
        symbols.put("LEFT_SINGLE_QUOTATION_MARK",
                new Symbol("LEFT_SINGLE_QUOTATION_MARK", "'", "", false, false));
        symbols.put("RIGHT_SINGLE_QUOTATION_MARK",
                new Symbol("RIGHT_SINGLE_QUOTATION_MARK", "'", "", false, false));
        symbols.put("LEFT_DOUBLE_QUOTATION_MARK",
                new Symbol("LEFT_DOUBLE_QUOTATION_MARK", "\"", "", false, false));
        symbols.put("RIGHT_DOUBLE_QUOTATION_MARK",
                new Symbol("RIGHT_DOUBLE_QUOTATION_MARK", "\"", "", false, false));

        /******************************************************************
         * Digits
         ******************************************************************/

        symbols.put("DIGIT_ZERO", new Symbol("0", ",", "", false, false));
        symbols.put("DIGIT_ONE", new Symbol("1", ",", "", false, false));
        symbols.put("DIGIT_TWO", new Symbol("2", ",", "", false, false));
        symbols.put("DIGIT_THREE",
                new Symbol("3", ",", "", false, false));
        symbols.put("DIGIT_FOUR", new Symbol("4", ",", "", false, false));
        symbols.put("DIGIT_FIVE", new Symbol("5", ",", "", false, false));
        symbols.put("DIGIT_SIX", new Symbol("6", ",", "", false, false));
        symbols.put("DIGIT_SEVEN",
                new Symbol("7", ",", "", false, false));
        symbols.put("DIGIT_EIGHT",
                new Symbol("8", ",", "", false, false));
        symbols.put("DIGIT_NINE", new Symbol("9", ",", "", false, false));

        // not create instance
        INSTANCE = null;
    }

    public static AbstractSymbols getInstance() {
        return INSTANCE;
    }
}
