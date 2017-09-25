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
package cc.redpen.parser.latex;

import org.junit.jupiter.api.Test;
import java.util.Arrays;

import static cc.redpen.parser.latex.Tools.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenTest {
    @Test
    void testAsVerbatim() {
        assertEquals(
            Arrays.asList(
                "test1",
                "test2",
                "test3",
                "test4"
            ),
            takenFor(
                t -> t.asVerbatim(),
                Arrays.asList(
                    new Token("A", "test1", new Position(0,0)),
                    new Token("B", "test2", new Position(0,0)),
                    new Token("C", "test3", new Position(0,0)),
                    new Token("D", "test4", new Position(0,0))
                )
            )
        );
    }

    @Test
    void testAsTextile() {
        assertEquals(
            Arrays.asList(
                "test1",
                "test2",
                "",
                "test4",
                "",
                "",
                "test7",
                ""
            ),
            takenFor(
                t -> t.asTextile(),
                Arrays.asList(
                    new Token("CONTROL*", "test1", new Position(0,0)),
                    new Token("GROUP2_BEGIN*", "test2", new Position(0,0)),
                    new Token("CONTROL", "test3", new Position(0,0)),
                    new Token("TEXTILE", "test4", new Position(0,0)),
                    new Token("GROUP2_END", "test5", new Position(0,0)),
                    new Token("GROUP1_BEGIN", "test6", new Position(0,0)),
                    new Token("TEXTILE", "test7", new Position(0,0)),
                    new Token("GROUP1_END", "test8", new Position(0,0))
                )
            )
        );
    }
}
