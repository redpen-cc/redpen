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
package cc.redpen.parser;

import org.junit.Test;

import static org.junit.Assert.*;

public class LineOffsetTest {
    @Test
    public void testCompareToDifferentOffset() {
        LineOffset offsetOne = new LineOffset(1, 1);
        LineOffset offsetTwo = new LineOffset(1, 2);
        assertTrue(offsetOne.compareTo(offsetTwo) < 0);
        assertTrue(offsetTwo.compareTo(offsetOne) > 0);
    }

    @Test
    public void testCompareToDifferentLineNum() {
        LineOffset offsetOne = new LineOffset(1, 1);
        LineOffset offsetTwo = new LineOffset(2, 1);
        assertTrue(offsetOne.compareTo(offsetTwo) < 0);
        assertTrue(offsetTwo.compareTo(offsetOne) > 0);
    }

    @Test
    public void testCompareToSameOffset() {
        LineOffset offsetOne = new LineOffset(1, 1);
        LineOffset offsetTwo = new LineOffset(1, 1);
        assertTrue(offsetOne.compareTo(offsetTwo) == 0);
    }
}
