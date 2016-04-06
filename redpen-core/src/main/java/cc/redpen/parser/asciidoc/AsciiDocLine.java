/**
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

package cc.redpen.parser.asciidoc;

import java.util.ArrayList;
import java.util.List;


/**
 * An 'erasing' string utility class that stores the original offset for each preserved character
 */
public class AsciiDocLine {
    // value returned for comparison if a character is escaped
    static final char ESCAPED_CHARACTER_VALUE = 'ø';
    static final String INLINE_MARKUP_DELIMITERS = " _*`#^~.,";

    /**
     * The different ways embedded inline markers can be erased
     */
    public enum EraseStyle {
        All,
        None,
        Markers,
        InlineMarkup,
        PreserveLabel,
        CloseMarkerContainsDelimiters
    }

    // a list of offsets for each character
    List<Integer> offsets = new ArrayList<>();
    // the text for the line
    List<Character> text = new ArrayList<>();
    // marks erased characters as invalid
    List<Boolean> valid = new ArrayList<>();
    // remembers which characters were escaped in the original string
    List<Boolean> escaped = new ArrayList<>();

    private int lineNo = 0;
    private boolean allSameCharacter = false;
    private boolean erased = false;
    private boolean inBlock = false;

    private int sectionLevel = 0;
    private int listLevel = 0;
    private boolean listStart = false;

    /**
     * Construct a line using the supplied string
     *
     * @param str    the text of the line
     * @param lineno the original line number
     */
    public AsciiDocLine(String str, int lineno) {
        this.lineNo = lineno;
        if (!str.isEmpty()) {
            allSameCharacter = true;
            char lastCh = 0;
            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);

                if ((i < str.length() - 1) && (ch == '\\')) {
                    i++;
                    ch = str.charAt(i);
                    escaped.add(true);
                }
                else {
                    escaped.add(false);
                }

                offsets.add(i);
                text.add(ch);
                valid.add(true);

                if ((lastCh != 0) && (lastCh != ch)) {
                    allSameCharacter = false;
                }
                lastCh = ch;
            }
        }

        // trim the end
        while (!text.isEmpty() &&
                Character.isWhitespace(text.get(text.size() - 1))) {
            text.remove(text.size() - 1);
        }
    }

    /**
     * Is this line a repeating set of the same character?
     *
     * @return true is all same character
     */
    public boolean isAllSameCharacter() {
        return allSameCharacter;
    }

    /**
     * Has this line been completely erased?
     *
     * @return true if erased
     */
    public boolean isErased() {
        return erased;
    }

    /**
     * Is this line in a block?
     *
     * @return true is it's in a block
     */
    public boolean isInBlock() {
        return inBlock;
    }


    /**
     * Is this line the start of a new list item?
     *
     * @return true if it's list start
     */
    public boolean isListStart() {
        return listStart;
    }

    /**
     * Return the list level for this line, or zero if it is not in a list.
     *
     * @return the list level, or zero if not in a list
     */
    public int getListLevel() {
        return listLevel;
    }

    /**
     * If the line is a section header, return the section level,
     * or zero if the line is not a section header
     *
     * @return the section level or zero if not a section header
     */
    public int getSectionLevel() {
        return sectionLevel;
    }

    public void setInBlock(boolean inBlock) {
        this.inBlock = inBlock;
    }

    public void setSectionLevel(int newSectionLevel) {
        this.sectionLevel = newSectionLevel;
    }

    public void setListLevel(int newListLevel) {
        this.listLevel = newListLevel;
    }

    public void setListStart(boolean listStart) {
        this.listStart = listStart;
    }

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
     * Erase the open and close markers, and optionally all the text inside them
     * Returns the position of the first enclosure or -1 if no enclosure was found
     *
     * @param open open marker to be erased
     * @param close close marker to be erased
     * @param eraseStyle erase style
     * @return position of first enclosure
     */
    public int eraseEnclosure(String open,
                              String close,
                              EraseStyle eraseStyle) {
        boolean inEnclosure = false;
        int firstEnclosurePosition = -1;
        int lastCommaPosition = -1;
        int enclosureStart = 0;
        for (int i = 0; i < length(); i++) {
            if (!valid.get(i)) {
                continue;
            }
            if (!inEnclosure) {
                // look for the open string
                boolean foundOpen = true;
                for (int j = 0; j < open.length(); j++) {
                    if (charAt(i + j) != open.charAt(j)) {
                        foundOpen = false;
                        break;
                    }
                }
                // inline requires start of line or a space before the marker
                if (foundOpen && (eraseStyle == EraseStyle.InlineMarkup)) {
                    if ((i != 0) &&
                            (INLINE_MARKUP_DELIMITERS.indexOf(charAt(i - 1)) == -1)) {
                        foundOpen = false;
                    }
                }
                if (foundOpen) {
                    enclosureStart = i;
                    inEnclosure = true;
                    firstEnclosurePosition = i;
                }
            }
            else {
                // look for the close string
                boolean foundClose = true;
                if (eraseStyle == EraseStyle.CloseMarkerContainsDelimiters) {
                    foundClose = (i == length() - 1) || (close.indexOf(charAt(i)) != -1);
                }
                else {
                    for (int j = 0; j < close.length(); j++) {
                        if (charAt(i + j) != close.charAt(j)) {
                            foundClose = false;
                            break;
                        }
                    }
                }

                if (foundClose && (eraseStyle == EraseStyle.InlineMarkup)) {
                    if ((i != length() - 1) &&
                            (INLINE_MARKUP_DELIMITERS.indexOf(charAt(i + close.length())) == -1)) {
                        foundClose = false;
                    }
                }

                if (foundClose) {
                    eraseWithStyle(open, close, eraseStyle, lastCommaPosition, enclosureStart, i);
                    inEnclosure = false;
                    lastCommaPosition = -1;
                }
                else if (charAt(i) == ',') {
                    lastCommaPosition = i;
                }
            }
        }
        return firstEnclosurePosition;
    }

    private void eraseWithStyle(String open,
                                String close,
                                EraseStyle eraseStyle,
                                int lastCommaPosition,
                                int enclosureStart,
                                int start) {
        switch (eraseStyle) {
            case All:
                erase(enclosureStart, (start - enclosureStart) + close.length());
                break;
            case Markers:
            case InlineMarkup:
                erase(enclosureStart, open.length());
                erase(start, close.length());
                break;
            case PreserveLabel:
                if (lastCommaPosition != -1) {
                    erase(enclosureStart, (lastCommaPosition + 1) - enclosureStart);
                    erase(start, close.length());
                }
                else {
                    erase(enclosureStart, open.length());
                    erase(start, close.length());
                }
                break;
            case CloseMarkerContainsDelimiters:
                erase(enclosureStart, (start == length() - 1)
                        ? (length() - enclosureStart)
                        : (start - enclosureStart));
                break;
            case None:
                break;
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

    /**
     * Return the original line number for this line
     *
     * @return line number
     */
    public int getLineNo() {
        return lineNo;
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
     * Render the line as a string, showing what's been erased
     * <p>
     * X         = whole line erased
     * [         = block
     * nn-nnn-nn = section - listlevel - lineno
     * *         = list item
     * ·         = next character erased
     *
     * @return string representation of this instance
     */
    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < text.size(); i++) {
            if (valid.get(i)) {
                result += text.get(i);
            }
            else {
                result += "·" + text.get(i);
            }
        }
        return (erased ? "X" : " ") +
                (inBlock ? "[" : " ") +
                sectionLevel + "-" +
                listLevel + "-" +
                String.format("%03d", lineNo) +
                (listStart ? "*" : ":") +
                " " +
                result;
    }
}
