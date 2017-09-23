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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReVIEWLineTest {
    @Test
    void testEraseInlineEnclosure() {
        String testLine = "A line with an @<b>{enclosure}.";
        String expectedResult =
                "  0-0-001: A line with an ·@·<·b·>·{enclosure·}.";
        ReVIEWLine line = new ReVIEWLine(testLine, 1);
        line.eraseEnclosure("@<b>{", "}", ReVIEWLine.EraseStyle.InlineMarkup);
        assertEquals(expectedResult, line.toString());
    }

    @Test
    void testEraseWholeEnclosure() {
        String testLine = "This is a comment: @<comment>{TODO}.";
        String expectedResult =
                "  0-0-001: This is a comment: ·@·<·c·o·m·m·e·n·t·>·{·T·O·D·O·}.";
        ReVIEWLine line = new ReVIEWLine(testLine, 1);
        line.eraseEnclosure("@<comment>{", "}", ReVIEWLine.EraseStyle.All);
        assertEquals(expectedResult, line.toString());
    }
}
