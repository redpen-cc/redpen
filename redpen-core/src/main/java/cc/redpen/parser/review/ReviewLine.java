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
package cc.redpen.parser.review;

import java.util.ArrayList;
import java.util.List;

public class ReviewLine {
    private final int lineNo;

    // a list of offsets for each character
    List<Integer> offsets = new ArrayList<>();
    // the text for the line
    List<Character> text = new ArrayList<>();
    // marks erased characters as invalid
    List<Boolean> valid = new ArrayList<>();
    // remembers which characters were escaped in the original string
    List<Boolean> escaped = new ArrayList<>();

    /**
     * Construct a line using the supplied string
     *
     * @param str    the text of the line
     * @param lineno the original line number
     */
    public ReviewLine(String str, int lineno) {
        this.lineNo = lineno;
        if (!str.isEmpty()) {
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
            }
        }

        // trim the end
        while (!text.isEmpty() &&
                Character.isWhitespace(text.get(text.size() - 1))) {
            text.remove(text.size() - 1);
        }
    }
}
