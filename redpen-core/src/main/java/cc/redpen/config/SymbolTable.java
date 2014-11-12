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
public final class SymbolTable implements Serializable{
    private static final long serialVersionUID = 1612920745151501631L;
    private final Map<SymbolType, Symbol> symbolDictionary = new HashMap<>();
    private final Map<String, Symbol> valueDictionary = new HashMap<>();
    private String lang;

    /**
     * Constructor.
     */
    public SymbolTable() {
        super();
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
        return symbol != null ? symbol.getValue() : Symbols.DEFAULT_SYMBOLS.get(FULL_STOP).getValue();
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
    public void override(Symbol symbol) {
        symbolDictionary.put(symbol.getType(), symbol);
        valueDictionary.put(symbol.getValue(), symbol);
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
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
}
