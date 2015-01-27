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

import java.io.Serializable;
import java.util.Arrays;

/**
 * Represent a character settings.
 */
public final class Symbol implements Serializable {
    private static final long serialVersionUID = 3826499136262740992L;
    private final SymbolType name;
    private final char value;
    private final char[] invalidChars;
    private final boolean needBeforeSpace;
    private final boolean needAfterSpace;

    /**
     * Constructor.
     *
     * @param symbolType  name of target character
     * @param charValue character
     */
    public Symbol(SymbolType symbolType, char charValue) {
        this(symbolType, charValue, "", false, false);
    }

    /**
     * Constructor.
     *
     * @param symbolType        name of target character
     * @param charValue       character
     * @param invalidCharsStr list of invalid characters
     */
    public Symbol(SymbolType symbolType, char charValue, String invalidCharsStr) {
        this(symbolType, charValue, invalidCharsStr, false, false);
    }

    /**
     * Constructor.
     *
     * @param symbolType        name of target character
     * @param charValue       character
     * @param invalidCharsStr list of invalid characters
     * @param haveBeforeSpace flag to have a space before the character
     * @param haveAfterSpace  flag to have a pace after the character
     */
    public Symbol(SymbolType symbolType, char charValue, String invalidCharsStr,
                  boolean haveBeforeSpace, boolean haveAfterSpace) {
        this.name = symbolType;
        this.value = charValue;
        this.invalidChars = invalidCharsStr.toCharArray();
        this.needBeforeSpace = haveBeforeSpace;
        this.needAfterSpace = haveAfterSpace;
    }

    /**
     * Get name of character.
     *
     * @return character name
     */
    public SymbolType getType() {
        return name;
    }

    /**
     * Get value of character.
     *
     * @return character
     */
    public char getValue() {
        return value;
    }

    /**
     * Get invalid characters.
     *
     * @return a list of invalid characters
     */
    public char[] getInvalidChars() {
        return invalidChars;
    }

    /**
     * Get the flag to know the character should have a space.
     *
     * @return flag to determine the character should have a space before it
     */
    public boolean isNeedBeforeSpace() {
        return needBeforeSpace;
    }

    /**
     * Get the flag to know the character should have a space.
     *
     * @return flag to determine the character should have a space after it
     */
    public boolean isNeedAfterSpace() {
        return needAfterSpace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Symbol symbol = (Symbol) o;

        if (needAfterSpace != symbol.needAfterSpace) return false;
        if (needBeforeSpace != symbol.needBeforeSpace) return false;
        if (value != symbol.value) return false;
        if (!Arrays.equals(invalidChars, symbol.invalidChars)) return false;
        if (name != symbol.name) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (int) value;
        result = 31 * result + (invalidChars != null ? Arrays.hashCode(invalidChars) : 0);
        result = 31 * result + (needBeforeSpace ? 1 : 0);
        result = 31 * result + (needAfterSpace ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "name=" + name +
                ", value=" + value +
                ", invalidChars=" + Arrays.toString(invalidChars) +
                ", needBeforeSpace=" + needBeforeSpace +
                ", needAfterSpace=" + needAfterSpace +
                '}';
    }
}
