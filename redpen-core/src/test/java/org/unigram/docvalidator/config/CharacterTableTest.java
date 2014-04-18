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
package org.unigram.docvalidator.config;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.unigram.docvalidator.config.*;
import org.unigram.docvalidator.config.Character;

public class CharacterTableTest {

  @Test
  public void testReturnedCharacter() {
    String sampleCharTable = new String(
        "<?xml version=\"1.0\"?>"+
        "<character-table>" +
        "<character name=\"EXCLAMATION_MARK\" value=\"!\" invalid-chars=\"！\" before-space=\"false\" after-space=\"true\"/>" +
        "</character-table>");
    InputStream stream = IOUtils.toInputStream(sampleCharTable);
    CharacterTable characterTable = CharacterTableLoader.load(stream);
    org.unigram.docvalidator.config.Character ch= characterTable.getCharacter("EXCLAMATION_MARK");
    assertNotNull(ch);
    assertEquals("!", ch.getValue());
    assertEquals(1, ch.getInvalidChars().size());
    assertEquals("！", ch.getInvalidChars().get(0));
    assertFalse(ch.isNeedBeforeSpace());
    assertTrue(ch.isNeedAfterSpace());
  }

  @Test
  public void testReturnedCharacterWithoutSpaceOption() {
    String sampleCharTable = new String(
        "<?xml version=\"1.0\"?>"+
        "<character-table>" +
        "<character name=\"EXCLAMATION_MARK\" value=\"!\" invalid-chars=\"！\"/>" +
        "</character-table>");
    InputStream stream = IOUtils.toInputStream(sampleCharTable);
    CharacterTable characterTable = CharacterTableLoader.load(stream);
    Character ch= characterTable.getCharacter("EXCLAMATION_MARK");
    assertNotNull(ch);
    assertEquals("!", ch.getValue());
    assertEquals(1, ch.getInvalidChars().size());
    assertEquals("！", ch.getInvalidChars().get(0));
    assertFalse(ch.isNeedBeforeSpace());
    assertFalse(ch.isNeedAfterSpace());
  }

  @Test
  public void testCharacterWithMultipleInvalids() {
    String sampleCharTable = new String(
        "<?xml version=\"1.0\"?>"+
        "<character-table>" +
        "<character name=\"LEFT_QUOTATION_MARK\" value=\"\'\" invalid-chars=\"‘’\"/>" +
        "</character-table>");
    InputStream stream = IOUtils.toInputStream(sampleCharTable);
    CharacterTable characterTable = CharacterTableLoader.load(stream);
    Character ch= characterTable.getCharacter("LEFT_QUOTATION_MARK");
    assertEquals(2, ch.getInvalidChars().size());
    assertEquals("‘", ch.getInvalidChars().get(0));
    assertEquals("’", ch.getInvalidChars().get(1));
  }

  @Test
  public void testCharacterWithoutInvalids() {
    String sampleCharTable = new String(
        "<?xml version=\"1.0\"?>"+
        "<character-table>" +
        "<character name=\"LEFT_QUOTATION_MARK\" value=\"\'\"/>" +
        "</character-table>");
    InputStream stream = IOUtils.toInputStream(sampleCharTable);
    CharacterTable characterTable = CharacterTableLoader.load(stream);
    Character ch= characterTable.getCharacter("LEFT_QUOTATION_MARK");
    assertEquals(0, ch.getInvalidChars().size());
  }

  @Test
  public void testDefaultSeting() {
    String sampleCharTable = new String(
        "<?xml version=\"1.0\"?>"+
        "<character-table>" +
        "</character-table>");
    InputStream stream = IOUtils.toInputStream(sampleCharTable);
    CharacterTable characterTable = CharacterTableLoader.load(stream);
    Character ch= characterTable.getCharacter("COMMA");
    assertEquals(0, ch.getInvalidChars().size());
    assertNotNull(ch);
    assertEquals(",", ch.getValue());
  }

  @Test
  public void testOverrideDefaultSeting() {
    String sampleCharTable = new String(
        "<?xml version=\"1.0\"?>"+
        "<character-table>" +
        "<character name=\"EXCLAMATION_MARK\" value=\"！\" />" +
        "</character-table>");
    InputStream stream = IOUtils.toInputStream(sampleCharTable);
    CharacterTable characterTable = CharacterTableLoader.load(stream);
    Character ch= characterTable.getCharacter("EXCLAMATION_MARK");
    assertNotNull(ch);
    assertEquals("！", ch.getValue());
  }

  @Test
  public void testAccessNotRegisteredCharacter() {
    String sampleCharTable = new String(
        "<?xml version=\"1.0\"?>"+
        "<character-table>" +
        "<character name=\"EXCLAMATION_MARK\" value=\"!\" invalid-chars=\"！\"/>" +
        "</character-table>");
    InputStream stream = IOUtils.toInputStream(sampleCharTable);
    CharacterTable characterTable = CharacterTableLoader.load(stream);
    // NOTE: HADOOP_CHARACTER does not exist even in default settings
    Character ch= characterTable.getCharacter("HADOOP_CHARACTER");
    assertNull(ch);
  }

  @Test
  public void testNoConfiguration() {
    String sampleCharTable = new String("<?xml version=\"1.0\"?>");
    InputStream stream = IOUtils.toInputStream(sampleCharTable);
    CharacterTable characterTable = CharacterTableLoader.load(stream);
    assertNull(characterTable);
  }

  @Test
  public void testNoConfiguration2() {
    String sampleCharTable = new String("");
    InputStream stream = IOUtils.toInputStream(sampleCharTable);
    CharacterTable characterTable = CharacterTableLoader.load(stream);
    assertNull(characterTable);
  }

  @Test
  public void testInvalidConfiguration() {
    String sampleCharTable = new String(
        "<?xml version=\"1.0\"?>"+
        "<character-table>" +
        "<character name=\"EXCLAMATION_MARK\" value=\"!\" invalid-chars=\"！\"/>" +
        "<character-table>"); // NOTE: no slash.
    InputStream stream = IOUtils.toInputStream(sampleCharTable);
    CharacterTable characterTable = CharacterTableLoader.load(stream);
    assertNull(characterTable);
  }

  @Test
  public void testConfigurationWithMisspelledBlock() {
    String sampleCharTable = new String(
        "<?xml version=\"1.0\"?>"+
        "<character-table>" +
        "<chrcter name=\"EXCLAMATION_MARK\" value=\"!\" invalid-chars=\"！\"/>" +
        "</character-table>");
    InputStream stream = IOUtils.toInputStream(sampleCharTable);
    CharacterTable characterTable = CharacterTableLoader.load(stream);
    assertNull(characterTable);
  }

  @Test
  public void testConfigurationWithCharacterWithoutName() {
    String defaultSettingCharTable = new String(
        "<?xml version=\"1.0\"?>"+
        "<character-table>" +
        "</character-table>");
    CharacterTable defaultCharacterTable =
        CharacterTableLoader.load(IOUtils.toInputStream(defaultSettingCharTable));

    String sampleCharTable = new String(
        "<?xml version=\"1.0\"?>"+
        "<character-table>" +
        "<character value=\"!\" invalid-chars=\"！\"/>" +  // NOTE: skip this element
        "</character-table>");
    InputStream stream = IOUtils.toInputStream(sampleCharTable);
    CharacterTable sampleCharacterTable = CharacterTableLoader.load(stream);
    assertNotNull(sampleCharacterTable);
    assertEquals(defaultCharacterTable.getSizeDictionarySize(),
        sampleCharacterTable.getSizeDictionarySize());
  }
}
