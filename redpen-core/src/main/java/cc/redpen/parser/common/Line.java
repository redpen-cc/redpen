/*
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.redpen.parser.common;

import java.util.ArrayList;
import java.util.List;

public class Line {
    // value returned for comparison if a character is escaped
    static final char ESCAPED_CHARACTER_VALUE = 'ø';

    // a list of offsets for each character
    protected List<Integer> offsets = new ArrayList<>();
    // the text for the line
    protected List<Character> text = new ArrayList<>();
    // marks erased characters as invalid
    protected List<Boolean> valid = new ArrayList<>();
    // remembers which characters were escaped in the original string
    protected List<Boolean> escaped = new ArrayList<>();
    // Whole line is erased.
    protected boolean erased = false;

    /**
     * Erase length characters in the line, starting at pos
     *
     * @param pos start position
     * @param length length to erase
     */
    public void erase(int pos, int length) {
        if ((pos >= 0) && (pos < valid.size())) {
            for (int i = pos; (i < valid.size()) && (i < pos + length); i++) {
                valid.set(i, false);
            }
        }
    }

    /**
     * Erase the whole line
     */
    public void erase() {
        for (int i = 0; i < valid.size(); i++) {
            valid.set(i, false);
        }
        erased = true;
    }

    /**
     * Erase all occurrences of the given string
     *
     * @param segment segment to be erased
     */
    public void erase(String segment) {
        for (int i = 0; i < text.size(); i++) {
            boolean found = true;
            for (int j = 0; j < segment.length(); j++) {
                if (charAt(j + i) != segment.charAt(j)) {
                    found = false;
                    break;
                }
            }
            if (found) {
                erase(i, segment.length());
                i += segment.length();
            }
        }
    }

    /**
     * Return the length of the line
     *
     * @return length of the line
     */
    public int length() {
        return text.size();
    }

    /**
     * Return the character at the given position. Erase characters will return 0 rather
     * than the actual character
     *
     * @param i index
     * @return extracted character
     */
    public char charAt(int i) {
        return charAt(i, false);
    }

    /**
     * Return the character at the given position, optionally including erased characters
     *
     * @param i index
     * @param includeInvalid true if include invalid
     * @return extracted character
     */
    public char charAt(int i, boolean includeInvalid) {
        if ((i >= 0) && (i < text.size())) {
            if (escaped.get(i)) {
                return ESCAPED_CHARACTER_VALUE;
            }
            if (includeInvalid || valid.get(i)) {
                return text.get(i);
            }
        }
        return 0;
    }

    /**
     * Does the line start with the given string?
     *
     * @param s string to test
     * @return true if the string starts with the specified string
     */
    public boolean startsWith(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (charAt(i) != s.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return the raw character at the specified position, ignoring its validity.
     *
     * @param i index
     * @return raw character at the specified position
     */
    public char rawCharAt(int i) {
        if ((i >= 0) && (i < text.size())) {
            return text.get(i);
        }
        return ' ';
    }

    /**
     * Is the character at the given position valid?
     *
     * @param i index
     * @return true if the character at the given position valid
     */
    public boolean isValid(int i) {
        if ((i >= 0) && (i < text.size())) {
            return valid.get(i);
        }
        return false;
    }

    /**
     * Is the character at the given position empty (ie: whitespace or invalid)
     *
     * @return true if it's empty
     */
    public boolean isEmpty() {
        for (int i = 0; i < text.size(); i++) {
            if (!Character.isWhitespace(text.get(i)))
                if (valid.get(i)) {
                    return false;
                }
        }

        return true;
    }

    /**
     * Return the offset for the character at the given position
     *
     * @param i index
     * @return offset
     */
    public int getOffset(int i) {
        if (i >= 0) {
            if (i < offsets.size()) {
                return offsets.get(i);
            }
            else {
                return offsets.size();
            }
        }
        return 0;
    }

}
