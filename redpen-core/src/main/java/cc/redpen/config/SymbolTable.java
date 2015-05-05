/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

import static cc.redpen.config.SymbolType.*;

/**
 * Configuration table of characters used in {@link cc.redpen.RedPen}.
 */
public final class SymbolTable implements Serializable {
    private static final long serialVersionUID = 1612920745151501631L;
    private final Map<SymbolType, Symbol> symbolDictionary = new HashMap<>();
    private final Map<Character, Symbol> valueDictionary = new HashMap<>();
    private final String type;
    private String lang;
    private static final Logger LOG = LoggerFactory.getLogger(SymbolTable.class);

    /**
     * Constructor.
     */
    SymbolTable(String lang, Optional<String> type, List<Symbol> customSymbols) {
        super();
        this.lang = lang;
        this.type = type.orElse("");
        if (lang.equals("ja")) {
            LOG.info("\"ja\" is specified.");
            if (this.type.equals("hankaku")) {
                LOG.info("\"hankaku\" type is specified");
                JAPANESE_HANKAKU_SYMBOLS.values().forEach(this::overrideSymbol);
            } else if (this.type.equals("zenkaku2")) {
                LOG.info("\"zenkaku2\" type is specified");
                JAPANESE_SYMBOLS.values().forEach(this::overrideSymbol);
                this.overrideSymbol(new Symbol(FULL_STOP, '．', "。."));
                this.overrideSymbol(new Symbol(COMMA, '，', "、,"));
            } else {
                LOG.info("\"normal\" type is specified");
                JAPANESE_SYMBOLS.values().forEach(this::overrideSymbol);
            }
        } else {
            LOG.info("Default symbol settings are loaded");
            DEFAULT_SYMBOLS.values().forEach(this::overrideSymbol);
        }
        customSymbols.forEach(this::overrideSymbol);
    }

    /**
     * Override a symbol with a new definition
     *
     * @param symbol
     */
    public void overrideSymbol(Symbol symbol) {
        symbolDictionary.put(symbol.getType(), symbol);
        valueDictionary.put(symbol.getValue(), symbol);
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
    public Symbol getSymbolByValue(char value) {
        return this.valueDictionary.get(value);
    }

    /**
     * Detect the specified character is exit in the dictionary.
     *
     * @param type character name
     * @return character when exist, null when the specified
     *         character does not exist
     */
    public char getValueOrFallbackToDefault(SymbolType type) {
        Symbol symbol = this.symbolDictionary.get(type);
        return symbol != null ? symbol.getValue() : DEFAULT_SYMBOLS.get(type).getValue();
    }

    /**
     * Detect the specified character is exit in the dictionary.
     *
     * @param value character value
     * @return character when exist, null when the specified
     *         character does not exist
     */
    public boolean containsSymbolByValue(char value) {
        return this.valueDictionary.get(value) != null;
    }

    public String getLang() {
        return lang;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SymbolTable that = (SymbolTable) o;

        if (lang != null ? !lang.equals(that.lang) : that.lang != null) return false;
        if (symbolDictionary != null ? !symbolDictionary.equals(that.symbolDictionary) : that.symbolDictionary != null)
            return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (valueDictionary != null ? !valueDictionary.equals(that.valueDictionary) : that.valueDictionary != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = symbolDictionary != null ? symbolDictionary.hashCode() : 0;
        result = 31 * result + (valueDictionary != null ? valueDictionary.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (lang != null ? lang.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SymbolTable{" +
                "symbolDictionary=" + symbolDictionary +
                ", valueDictionary=" + valueDictionary +
                ", type='" + type + '\'' +
                ", lang='" + lang + '\'' +
                '}';
    }

    private static final Map<SymbolType, Symbol> DEFAULT_SYMBOLS;
    private static final Map<SymbolType, Symbol> JAPANESE_SYMBOLS;
    private static final Map<SymbolType, Symbol> JAPANESE_HANKAKU_SYMBOLS;

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
                new Symbol(SPACE, ' ', "")
                , new Symbol(EXCLAMATION_MARK, '!', "！")
                , new Symbol(NUMBER_SIGN, '#', "＃")
                , new Symbol(DOLLAR_SIGN, '$', "＄")
                , new Symbol(PERCENT_SIGN, '%', "％")
                , new Symbol(QUESTION_MARK, '?', "？")
                , new Symbol(AMPERSAND, '&', "＆")
                , new Symbol(LEFT_PARENTHESIS, '(', "（", true, false)
                , new Symbol(RIGHT_PARENTHESIS, ')', "）", false, true)
                , new Symbol(ASTERISK, '*', "＊")
                , new Symbol(COMMA, ',', "，、", false, true)
                , new Symbol(FULL_STOP, '.', "．。")
                , new Symbol(PLUS_SIGN, '+', "＋")
                , new Symbol(HYPHEN_SIGN, '-', "ー")
                , new Symbol(SLASH, '/', "／")
                , new Symbol(COLON, ':', "：")
                , new Symbol(SEMICOLON, ';', "；")
                , new Symbol(LESS_THAN_SIGN, '<', "＜")
                , new Symbol(EQUAL_SIGN, '=', "＝")
                , new Symbol(GREATER_THAN_SIGN, '>', "＞")
                , new Symbol(AT_MARK, '@', "＠")
                , new Symbol(LEFT_SQUARE_BRACKET, '[', "", true, false)
                , new Symbol(RIGHT_SQUARE_BRACKET, ']', "", false, true)
                , new Symbol(BACKSLASH, '\\', "")
                , new Symbol(CIRCUMFLEX_ACCENT, '^', "")
                , new Symbol(LOW_LINE, '_', "")
                , new Symbol(LEFT_CURLY_BRACKET, '{', "｛", true, false)
                , new Symbol(RIGHT_CURLY_BRACKET, '}', "｝", false, true)
                , new Symbol(VERTICAL_BAR, '|', "｜")
                , new Symbol(TILDE, '~', "〜")
                , new Symbol(LEFT_SINGLE_QUOTATION_MARK, '\'', "")
                , new Symbol(RIGHT_SINGLE_QUOTATION_MARK, '\'', "")
                , new Symbol(LEFT_DOUBLE_QUOTATION_MARK, '\"', "")
                , new Symbol(RIGHT_DOUBLE_QUOTATION_MARK, '\"', "")

                // Digits
                , new Symbol(DIGIT_ZERO, '0', "")
                , new Symbol(DIGIT_ONE, '1', "")
                , new Symbol(DIGIT_TWO, '2', "")
                , new Symbol(DIGIT_THREE, '3', "")
                , new Symbol(DIGIT_FOUR, '4', "")
                , new Symbol(DIGIT_FIVE, '5', "")
                , new Symbol(DIGIT_SIX, '6', "")
                , new Symbol(DIGIT_SEVEN, '7', "")
                , new Symbol(DIGIT_EIGHT, '8', "")
                , new Symbol(DIGIT_NINE, '9', ""));

        JAPANESE_SYMBOLS = initializeSymbols(
                // Common symbols
                new Symbol(SPACE, '　', "")
                , new Symbol(EXCLAMATION_MARK, '！', "!")
                , new Symbol(NUMBER_SIGN, '＃', "#")
                , new Symbol(DOLLAR_SIGN, '＄', "$")
                , new Symbol(PERCENT_SIGN, '％', "%")
                , new Symbol(QUESTION_MARK, '？', "?")
                , new Symbol(AMPERSAND, '＆', "&")
                , new Symbol(LEFT_PARENTHESIS, '（', "(")
                , new Symbol(RIGHT_PARENTHESIS, '）', ")")
                , new Symbol(ASTERISK, '＊', "") // not add "*" to invalidChars for markdown format
                , new Symbol(COMMA, '、', ",，")
                , new Symbol(FULL_STOP, '。', ".．")
                , new Symbol(PLUS_SIGN, '＋', "")
                , new Symbol(HYPHEN_SIGN, 'ー', "")
                , new Symbol(SLASH, '／', "")
                , new Symbol(COLON, '：', ":")
                , new Symbol(SEMICOLON, '；', ";")
                , new Symbol(LESS_THAN_SIGN, '＜', "<")
                , new Symbol(EQUAL_SIGN, '＝', "")
                , new Symbol(GREATER_THAN_SIGN, '＞', ">")
                , new Symbol(AT_MARK, '＠', "@")
                , new Symbol(LEFT_SQUARE_BRACKET, '「', "")
                , new Symbol(RIGHT_SQUARE_BRACKET, '」', "")
                , new Symbol(BACKSLASH, '¥', "\\")
                , new Symbol(CIRCUMFLEX_ACCENT, '＾', "") // not add "*" to invalidChars for markdown format
                , new Symbol(LOW_LINE, '＿', "")
                , new Symbol(LEFT_CURLY_BRACKET, '｛', "")
                , new Symbol(RIGHT_CURLY_BRACKET, '｝', "")
                , new Symbol(VERTICAL_BAR, '｜', "|")
                , new Symbol(TILDE, '〜', "~")
                , new Symbol(LEFT_SINGLE_QUOTATION_MARK, '‘', "")
                , new Symbol(RIGHT_SINGLE_QUOTATION_MARK, '’', "")
                , new Symbol(LEFT_SINGLE_QUOTATION_MARK, '“', "")
                , new Symbol(RIGHT_DOUBLE_QUOTATION_MARK, '”', "")
                /******************************************************************
                 * Digits
                 ******************************************************************/
                , new Symbol(DIGIT_ZERO, '0', "")
                , new Symbol(DIGIT_ONE, '1', "")
                , new Symbol(DIGIT_TWO, '2', "")
                , new Symbol(DIGIT_THREE, '3', "")
                , new Symbol(DIGIT_FOUR, '4', "")
                , new Symbol(DIGIT_FIVE, '5', "")
                , new Symbol(DIGIT_SIX, '6', "")
                , new Symbol(DIGIT_SEVEN, '7', "")
                , new Symbol(DIGIT_EIGHT, '8', "")
                , new Symbol(DIGIT_NINE, '9', ""));

        JAPANESE_HANKAKU_SYMBOLS = initializeSymbols(
                new Symbol(SPACE, '　', " ")
                , new Symbol(EXCLAMATION_MARK, '!', "！")
                , new Symbol(NUMBER_SIGN, '#', "＃")
                , new Symbol(DOLLAR_SIGN, '$', "＄")
                , new Symbol(PERCENT_SIGN, '%', "％")
                , new Symbol(QUESTION_MARK, '?', "？")
                , new Symbol(AMPERSAND, '&', "＆")
                , new Symbol(LEFT_PARENTHESIS, '(', "（", true, false)
                , new Symbol(RIGHT_PARENTHESIS, ')', "）", false, true)
                , new Symbol(ASTERISK, '*', "＊")
                , new Symbol(COMMA, ',', "，、", false, true)
                , new Symbol(FULL_STOP, '.', "．。")
                , new Symbol(PLUS_SIGN, '+', "＋")
                , new Symbol(HYPHEN_SIGN, '-', "ー")
                , new Symbol(SLASH, '/', "／")
                , new Symbol(COLON, ':', "：")
                , new Symbol(SEMICOLON, ';', "；")
                , new Symbol(LESS_THAN_SIGN, '<', "＜")
                , new Symbol(EQUAL_SIGN, '=', "＝")
                , new Symbol(GREATER_THAN_SIGN, '>', "＞")
                , new Symbol(AT_MARK, '@', "＠")
                , new Symbol(LEFT_SQUARE_BRACKET, '[', "", true, false)
                , new Symbol(RIGHT_SQUARE_BRACKET, ']', "", false, true)
                , new Symbol(BACKSLASH, '\\', "")
                , new Symbol(CIRCUMFLEX_ACCENT, '^', "")
                , new Symbol(LOW_LINE, '_', "")
                , new Symbol(LEFT_CURLY_BRACKET, '{', "｛", true, false)
                , new Symbol(RIGHT_CURLY_BRACKET, '}', "｝", false, true)
                , new Symbol(VERTICAL_BAR, '|', "｜")
                , new Symbol(TILDE, '~', "〜")
                , new Symbol(LEFT_SINGLE_QUOTATION_MARK, '\'', "")
                , new Symbol(RIGHT_SINGLE_QUOTATION_MARK, '\'', "")
                , new Symbol(LEFT_DOUBLE_QUOTATION_MARK, '\"', "")
                , new Symbol(RIGHT_DOUBLE_QUOTATION_MARK, '\"', "")

                // Digits
                , new Symbol(DIGIT_ZERO, '0', "０")
                , new Symbol(DIGIT_ONE, '1', "１")
                , new Symbol(DIGIT_TWO, '2', "２")
                , new Symbol(DIGIT_THREE, '3', "３")
                , new Symbol(DIGIT_FOUR, '4', "４")
                , new Symbol(DIGIT_FIVE, '5', "５")
                , new Symbol(DIGIT_SIX, '6', "６")
                , new Symbol(DIGIT_SEVEN, '7', "７")
                , new Symbol(DIGIT_EIGHT, '8', "８")
                , new Symbol(DIGIT_NINE, '9', "９"));
    }

}
