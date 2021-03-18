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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestLine extends Line {

    public TestLine(String str, int lineNo) {
        super(str, lineNo);
        if (!str.isEmpty()) {
            allSameCharacter = true;
            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                offsets.add(i);
                characters.add(ch);
                valid.add(true);
                escaped.add(false);
            }
        }
    }
}

class LineTest {
    @Test
    void testEraseAll() {
        String testLine = "A line with is good";
        Line line = new TestLine(testLine, 1);
        line.erase();
        assertEquals("X 0-0-001: ·A· ·l·i·n·e· ·w·i·t·h· ·i·s· ·g·o·o·d", line.toString());
    }

    @Test
    void testEraseEnclosureForMarker() {
        String testLine = "A line **with** is good";
        Line line = new TestLine(testLine, 1);
        line.eraseEnclosure("**", "**", Line.EraseStyle.Markers);
        assertEquals("  0-0-001: A line ·*·*with·*·* is good", line.toString());
    }

    @Test
    void testEraseEnclosureWithAll() {
        String testLine = "A line **with** is good";
        Line line = new TestLine(testLine, 1);
        line.eraseEnclosure("**", "**", Line.EraseStyle.All);
        assertEquals("  0-0-001: A line ·*·*·w·i·t·h·*·* is good", line.toString());
    }

    @Test
    void testEraseEnclosureWithPreserveLabel() {
        String testLine = "A line [[with]] is good";
        Line line = new TestLine(testLine, 1);
        line.eraseEnclosure("[[", "]]", Line.EraseStyle.PreserveLabel);
        assertEquals("  0-0-001: A line ·[·[with·]·] is good", line.toString());
    }

    @Test
    void testEraseEnclosureWithPreserveAfterLabel() {
        String testLine = "A line @<kw>{with} is good";
        Line line = new TestLine(testLine, 1);
        line.eraseEnclosure("@<kw>{", "}", Line.EraseStyle.PreserveAfterLabel);
        assertEquals("  0-0-001: A line ·@·<·k·w·>·{with·} is good", line.toString());
    }

    @Test
    void testEraseSegment() {
        String testLine = "(TM) means trade mark";
        Line line = new TestLine(testLine, 1);
        line.erase("(TM)");
        assertEquals("  0-0-001: ·(·T·M·) means trade mark", line.toString());
    }
}
