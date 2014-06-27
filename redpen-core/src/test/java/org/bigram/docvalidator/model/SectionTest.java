/**
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
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
package org.bigram.docvalidator.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SectionTest {
  @Test
  public void testGetJoinedHeaderFromSectionHasOneSentenceHeader() {
    Section section = new Section(0, "header");
    assertEquals("header", section.getJoinedHeaderContents().content);
    assertEquals(0, section.getJoinedHeaderContents().position);
  }

  @Test
  public void testGetJoinedHeaderFromSectionHasMultipleSentenceHeader() {
    List<Sentence> headers = new ArrayList<Sentence>();
    headers.add(new Sentence("header1.",0));
    headers.add(new Sentence("header2.",0));
    Section section = new Section(0, headers);
    assertEquals("header1. header2.", section.getJoinedHeaderContents().content);
    assertEquals(0, section.getJoinedHeaderContents().position);
  }

  @Test
  public void testGetJoinedHeaderFromSectionWithoutHeader() {
    Section section = new Section(0);
    assertEquals("", section.getJoinedHeaderContents().content);
    assertEquals(0, section.getJoinedHeaderContents().position);
  }
}
