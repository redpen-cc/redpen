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
    /**
     * Construct a line using the supplied string
     *
     * @param str    the text of the line
     * @param lineno the original line number
     */
    public AsciiDocLine(String str, int lineno) {
        super(lineno);
        this.lineNo = lineno;
        this.inlineMarkupDelimiters = " _*`#^~.,";
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

    @Override
    public String toString() {
        return super.toString();
    }
}
