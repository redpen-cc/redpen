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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Base class of the symbol settings.
 */
public final class Symbols implements Iterable<SymbolType> , Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(Symbols.class);
    private static final long serialVersionUID = 6178585902768329260L;
    private final Map<SymbolType, Symbol> symbolTable = new HashMap<>();
    public static final Symbols DEFAULT_SYMBOLS = new Symbols(
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

    static final Symbols JAPANESE_SYMBOLS = new Symbols(
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

    private Symbols(Symbol... newSymbols) {
        for (Symbol symbol : newSymbols) {
            symbolTable.put(symbol.getType(), symbol);
        }
    }

    /**
     * Get the specified character or symbol.
     *
     * @param name name of symbol
     * @return specified character
     */
    public Symbol get(SymbolType name) {
        if (!symbolTable.containsKey(name)) {
            LOG.info(name + " is not defined in DefaultSymbols.");
            return null;
        }
        return symbolTable.get(name);
    }

    @Override
    public Iterator<SymbolType> iterator() {
        return symbolTable.keySet().iterator();
    }

    protected Map<SymbolType, Symbol> getSymbolTable() {
        return symbolTable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Symbols that = (Symbols) o;

        if (symbolTable != null ? !symbolTable.equals(that.symbolTable) : that.symbolTable != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return symbolTable != null ? symbolTable.hashCode() : 0;
    }


    @Override
    public String toString() {
        return "Symbols{" +
                "symbolTable=" + symbolTable +
                '}';
    }
}
