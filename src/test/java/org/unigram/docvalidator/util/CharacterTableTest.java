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
    CharacterTable characterTable = new CharacterTable(stream);
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
    CharacterTable characterTable = new CharacterTable(stream);
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
    CharacterTable characterTable = new CharacterTable(stream);
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
    CharacterTable characterTable = new CharacterTable(stream);
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
    CharacterTable characterTable = new CharacterTable(stream);
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
    CharacterTable characterTable = new CharacterTable(stream);
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
    CharacterTable characterTable = new CharacterTable(stream);
    // NOTE: HADOOP_CHARACTER does not exist even in default settings
    DVCharacter ch= characterTable.getCharacter("HADOOP_CHARACTER");
    assertNull(ch);
  }
}
