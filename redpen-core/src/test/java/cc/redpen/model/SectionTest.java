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
package cc.redpen.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SectionTest {
    @Test
    public void testGetJoinedHeaderFromSectionHasOneSentenceHeader() {
        Section section = new Section(0, "header");
        assertEquals("header", section.getJoinedHeaderContents().getContent());
        assertEquals(0, section.getJoinedHeaderContents().getLineNumber());
    }

    @Test
    public void testGetJoinedHeaderFromSectionHasMultipleSentenceHeader() {
        List<Sentence> headers = new ArrayList<>();
        headers.add(new Sentence("header1.", 0));
        headers.add(new Sentence("header2.", 0));
        Section section = new Section(0, headers);
        assertEquals("header1. header2.", section.getJoinedHeaderContents().getContent());
        assertEquals(0, section.getJoinedHeaderContents().getLineNumber());
    }

    @Test
    public void testGetJoinedHeaderFromSectionWithoutHeader() {
        Section section = new Section(0);
        assertEquals("", section.getJoinedHeaderContents().getContent());
        assertEquals(0, section.getJoinedHeaderContents().getLineNumber());
    }
}
