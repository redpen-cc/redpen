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

import cc.redpen.parser.common.Line;


/**
 * An 'erasing' string utility class that stores the original offset for each preserved character
 */
public class AsciiDocLine extends Line {
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

    /**
     * Construct a line using the supplied string
     *
     * @param str    the text of the line
     * @param lineno the original line number
     */
    public AsciiDocLine(String str, int lineno) {
        super(lineno);
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
