/**
 * DocumentValidator
 * Copyright (c) 2013-, Takahiko Ito, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package org.unigram.docvalidator.util;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

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
    DVCharacter ch= characterTable.getCharacter("EXCLAMATION_MARK");
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
    DVCharacter ch= characterTable.getCharacter("EXCLAMATION_MARK");
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
    DVCharacter ch= characterTable.getCharacter("LEFT_QUOTATION_MARK");
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
    DVCharacter ch= characterTable.getCharacter("LEFT_QUOTATION_MARK");
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
    DVCharacter ch= characterTable.getCharacter("COMMA");
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
    DVCharacter ch= characterTable.getCharacter("EXCLAMATION_MARK");
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
    DVCharacter ch= characterTable.getCharacter("HADOOP_CHARACTER");
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
