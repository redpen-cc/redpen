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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represent a character settings.
 */
public final class Symbol implements Serializable {
    private static final long serialVersionUID = 3826499136262740992L;
    private final SymbolType name;
    private final String value;
    private final List<String> invalidChars;
    private final boolean needBeforeSpace;
    private final boolean needAfterSpace;

    /**
     * Constructor.
     *
     * @param symbolType  name of target character
     * @param charValue character
     */
    public Symbol(SymbolType symbolType, String charValue) {
        this(symbolType, charValue, "", false, false);
    }

    /**
     * Constructor.
     *
     * @param symbolType        name of target character
     * @param charValue       character
     * @param invalidCharsStr list of invalid characters
     */
    public Symbol(SymbolType symbolType, String charValue, String invalidCharsStr) {
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
    public Symbol(SymbolType symbolType, String charValue, String invalidCharsStr,
                  boolean haveBeforeSpace, boolean haveAfterSpace) {
        this.name = symbolType;
        this.value = charValue;
        this.invalidChars = new ArrayList<>(charValue.length());
        if (invalidCharsStr.length() > 0) {
            this.invalidChars.addAll
                    (Arrays.asList(invalidCharsStr.split("(?!^)")));
        }
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
    public String getValue() {
        return value;
    }

    /**
     * Get invalid characters.
     *
     * @return a list of invalid characters
     */
    public List<String> getInvalidSymbols() {
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

    public void addInvalid(String invalid) {
        this.invalidChars.add(invalid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Symbol symbol = (Symbol) o;

        if (needAfterSpace != symbol.needAfterSpace) return false;
        if (needBeforeSpace != symbol.needBeforeSpace) return false;
        if (invalidChars != null ? !invalidChars.equals(symbol.invalidChars) : symbol.invalidChars != null)
            return false;
        if (name != null ? !name.equals(symbol.name) : symbol.name != null) return false;
        if (value != null ? !value.equals(symbol.value) : symbol.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (invalidChars != null ? invalidChars.hashCode() : 0);
        result = 31 * result + (needBeforeSpace ? 1 : 0);
        result = 31 * result + (needAfterSpace ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", invalidChars=" + invalidChars +
                ", needBeforeSpace=" + needBeforeSpace +
                ", needAfterSpace=" + needAfterSpace +
                '}';
    }
}
