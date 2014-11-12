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
package cc.redpen.config;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static cc.redpen.config.SymbolType.FULL_STOP;

/**
 * Configuration table of characters used in {@link cc.redpen.RedPen}.
 */
public final class SymbolTable implements Serializable {
    private static final long serialVersionUID = 1612920745151501631L;
    private final Map<SymbolType, Symbol> symbolDictionary = new HashMap<>();
    private final Map<String, Symbol> valueDictionary = new HashMap<>();
    private String lang;

    /**
     * Constructor.
     */
    SymbolTable(String lang) {
        super();
        this.lang = lang;
        if (lang.equals("ja")) {
            JAPANESE_SYMBOLS.values().forEach(this::override);
        } else {
            DEFAULT_SYMBOLS.values().forEach(this::override);
        }
    }

    /**
     * Get the character names in the dictionary.
     *
     * @return names of characters
     */
    public Set<SymbolType> getNames() {
        return this.symbolDictionary.keySet();
    }

    /**
     * Get the character specified with the name.
     *
     * @param name character name
     * @return character containing the settings
     */
    public Symbol getSymbol(SymbolType name) {
        return this.symbolDictionary.get(name);
    }

    /**
     * Get the character specified with the value.
     *
     * @param value character name
     * @return character containing the settings
     */
    public Symbol getSymbolByValue(String value) {
        return this.valueDictionary.get(value);
    }

    /**
     * Detect the specified character is exit in the dictionary.
     *
     * @param name character name
     * @return character when exist, null when the specified
     * character does not exist
     */
    public String getValueOrFallbackToDefault(SymbolType name) {
        Symbol symbol = this.symbolDictionary.get(name);
        return symbol != null ? symbol.getValue() : DEFAULT_SYMBOLS.get(FULL_STOP).getValue();
    }

    /**
     * Detect the specified character is exit in the dictionary.
     *
     * @param value character value
     * @return character when exist, null when the specified
     * character does not exist
     */
    public boolean containsSymbolByValue(String value) {
        return this.valueDictionary.get(value) != null;
    }

    /**
     * Replace the current character setting.
     *
     * @param symbol symbol configuration
     */
    void override(Symbol symbol) {
        symbolDictionary.put(symbol.getType(), symbol);
        valueDictionary.put(symbol.getValue(), symbol);
    }

    public String getLang() {
        return lang;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SymbolTable that = (SymbolTable) o;

        if (lang != null ? !lang.equals(that.lang) : that.lang != null)
            return false;
        if (symbolDictionary != null ? !symbolDictionary.equals(that.symbolDictionary) : that.symbolDictionary != null)
            return false;
        if (valueDictionary != null ? !valueDictionary.equals(that.valueDictionary) : that.valueDictionary != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = symbolDictionary != null ? symbolDictionary.hashCode() : 0;
        result = 31 * result + (valueDictionary != null ? valueDictionary.hashCode() : 0);
        result = 31 * result + (lang != null ? lang.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SymbolTable{" +
                "symbolDictionary=" + symbolDictionary +
                ", valueDictionary=" + valueDictionary +
                ", lang='" + lang + '\'' +
                '}';
    }

    private static final Map<SymbolType, Symbol> DEFAULT_SYMBOLS;
    private static final Map<SymbolType, Symbol> JAPANESE_SYMBOLS;

    private static Map<SymbolType, Symbol> initializeSymbols(Symbol... newSymbols) {
        HashMap<SymbolType, Symbol> symbolTypeSymbolMap = new HashMap<>();
        for (Symbol symbol : newSymbols) {
            symbolTypeSymbolMap.put(symbol.getType(), symbol);
        }
        return symbolTypeSymbolMap;
    }

    static {
        DEFAULT_SYMBOLS = initializeSymbols(
                // Common symbols
                new Symbol(SymbolType.SPACE, " ", "")
                , new Symbol(SymbolType.EXCLAMATION_MARK, "!", "！")
                , new Symbol(SymbolType.NUMBER_SIGN, "#", "＃")
                , new Symbol(SymbolType.DOLLAR_SIGN, "$", "＄")
                , new Symbol(SymbolType.PERCENT_SIGN, "%", "％")
                , new Symbol(SymbolType.QUESTION_MARK, "?", "？")
                , new Symbol(SymbolType.AMPERSAND, "&", "＆")
                , new Symbol(SymbolType.LEFT_PARENTHESIS, "(", "（")
                , new Symbol(SymbolType.RIGHT_PARENTHESIS, ")", "）")
                , new Symbol(SymbolType.ASTERISK, "*", "＊")
                , new Symbol(SymbolType.COMMA, ",", "，、")
                , new Symbol(SymbolType.FULL_STOP, ".", "．。")
                , new Symbol(SymbolType.PLUS_SIGN, "+", "＋")
                , new Symbol(SymbolType.HYPHEN_SIGN, "-", "ー")
                , new Symbol(SymbolType.SLASH, "/", "／")
                , new Symbol(SymbolType.COLON, ":", "：")
                , new Symbol(SymbolType.SEMICOLON, ";", "；")
                , new Symbol(SymbolType.LESS_THAN_SIGN, "<", "＜")
                , new Symbol(SymbolType.EQUAL_SIGN, "=", "＝")
                , new Symbol(SymbolType.GREATER_THAN_SIGN, ">", "＞")
                , new Symbol(SymbolType.AT_MARK, "@", "＠")
                , new Symbol(SymbolType.LEFT_SQUARE_BRACKET, "[", "")
                , new Symbol(SymbolType.RIGHT_SQUARE_BRACKET, "]", "")
                , new Symbol(SymbolType.BACKSLASH, "\\", "")
                , new Symbol(SymbolType.CIRCUMFLEX_ACCENT, "^", "")
                , new Symbol(SymbolType.LOW_LINE, "_", "")
                , new Symbol(SymbolType.LEFT_CURLY_BRACKET, "{", "｛")
                , new Symbol(SymbolType.RIGHT_CURLY_BRACKET, "}", "｝")
                , new Symbol(SymbolType.VERTICAL_BAR, "|", "｜")
                , new Symbol(SymbolType.TILDE, "~", "〜")
                , new Symbol(SymbolType.LEFT_SINGLE_QUOTATION_MARK, "'", "")
                , new Symbol(SymbolType.RIGHT_SINGLE_QUOTATION_MARK, "'", "")
                , new Symbol(SymbolType.LEFT_DOUBLE_QUOTATION_MARK, "\"", "")
                , new Symbol(SymbolType.RIGHT_DOUBLE_QUOTATION_MARK, "\"", "")

                // Digits
                , new Symbol(SymbolType.DIGIT_ZERO, "0", "")
                , new Symbol(SymbolType.DIGIT_ONE, "1", "")
                , new Symbol(SymbolType.DIGIT_TWO, "2", "")
                , new Symbol(SymbolType.DIGIT_THREE, "3", "")
                , new Symbol(SymbolType.DIGIT_FOUR, "4", "")
                , new Symbol(SymbolType.DIGIT_FIVE, "5", "")
                , new Symbol(SymbolType.DIGIT_SIX, "6", "")
                , new Symbol(SymbolType.DIGIT_SEVEN, "7", "")
                , new Symbol(SymbolType.DIGIT_EIGHT, "8", "")
                , new Symbol(SymbolType.DIGIT_NINE, "9", ""));

        JAPANESE_SYMBOLS = initializeSymbols(
                // Common symbols
                new Symbol(SymbolType.SPACE, "　", " ")
                , new Symbol(SymbolType.EXCLAMATION_MARK, "！", "!")
                , new Symbol(SymbolType.NUMBER_SIGN, "＃", "#")
                , new Symbol(SymbolType.DOLLAR_SIGN, "$", "＄")
                , new Symbol(SymbolType.PERCENT_SIGN, "％", "%")
                , new Symbol(SymbolType.QUESTION_MARK, "？", "?")
                , new Symbol(SymbolType.AMPERSAND, "＆", "&")
                , new Symbol(SymbolType.LEFT_PARENTHESIS, "（", "(")
                , new Symbol(SymbolType.RIGHT_PARENTHESIS, "）", ")")
                , new Symbol(SymbolType.ASTERISK, "＊", "*")
                , new Symbol(SymbolType.COMMA, "、", ",，")
                , new Symbol(SymbolType.FULL_STOP, "。", ".．")
                , new Symbol(SymbolType.PLUS_SIGN, "＋", "+")
                , new Symbol(SymbolType.HYPHEN_SIGN, "ー", "-")
                , new Symbol(SymbolType.SLASH, "／", "/")
                , new Symbol(SymbolType.COLON, "：", ":")
                , new Symbol(SymbolType.SEMICOLON, "；", ";")
                , new Symbol(SymbolType.LESS_THAN_SIGN, "＜", "<")
                , new Symbol(SymbolType.EQUAL_SIGN, "＝", "=")
                , new Symbol(SymbolType.GREATER_THAN_SIGN, "＞", ">")
                , new Symbol(SymbolType.AT_MARK, "＠", "@")
                , new Symbol(SymbolType.LEFT_SQUARE_BRACKET, "「", "")
                , new Symbol(SymbolType.RIGHT_SQUARE_BRACKET, "」", "")
                , new Symbol(SymbolType.BACKSLASH, "¥", "\\")
                , new Symbol(SymbolType.CIRCUMFLEX_ACCENT, "＾", "^")
                , new Symbol(SymbolType.LOW_LINE, "＿", "_")
                , new Symbol(SymbolType.LEFT_CURLY_BRACKET, "｛", "")
                , new Symbol(SymbolType.RIGHT_CURLY_BRACKET, "｝", "")
                , new Symbol(SymbolType.VERTICAL_BAR, "｜", "|")
                , new Symbol(SymbolType.TILDE, "〜", "~")
                , new Symbol(SymbolType.LEFT_SINGLE_QUOTATION_MARK, "‘", "")
                , new Symbol(SymbolType.RIGHT_SINGLE_QUOTATION_MARK, "’", "")
                , new Symbol(SymbolType.LEFT_SINGLE_QUOTATION_MARK, "“", "")
                , new Symbol(SymbolType.RIGHT_DOUBLE_QUOTATION_MARK, "”", "")
                /******************************************************************
                 * Digits
                 ******************************************************************/
                , new Symbol(SymbolType.DIGIT_ZERO, "0", "")
                , new Symbol(SymbolType.DIGIT_ONE, "1", "")
                , new Symbol(SymbolType.DIGIT_TWO, "2", "")
                , new Symbol(SymbolType.DIGIT_THREE, "3", "")
                , new Symbol(SymbolType.DIGIT_FOUR, "4", "")
                , new Symbol(SymbolType.DIGIT_FIVE, "5", "")
                , new Symbol(SymbolType.DIGIT_SIX, "6", "")
                , new Symbol(SymbolType.DIGIT_SEVEN, "7", "")
                , new Symbol(SymbolType.DIGIT_EIGHT, "8", "")
                , new Symbol(SymbolType.DIGIT_NINE, "9", ""));
    }
}
