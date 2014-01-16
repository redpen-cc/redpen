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
package org.unigram.docvalidator.validator.sentence;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.CharacterTableLoader;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.validator.sentence.InvalidCharacterValidator;

class InvalidCharacterValidatorForTest extends InvalidCharacterValidator {
  void loadCharacterTable (CharacterTable characterTable) {
    this.characterTable = characterTable;
  }
}

public class InvalidCharacterValidatorTest {
  @Test
  public void testWithInvalidCharacter() {
    InvalidCharacterValidatorForTest validator = new InvalidCharacterValidatorForTest();
    String sampleCharTable = new String(
        "<?xml version=\"1.0\"?>"+
        "<character-table>" +
        "<character name=\"EXCLAMATION_MARK\" value=\"!\" invalid-chars=\"！\"/>" +
        "</character-table>");
    InputStream stream = IOUtils.toInputStream(sampleCharTable);
    CharacterTable characterTable = CharacterTableLoader.load(stream);
    validator.loadCharacterTable(characterTable);
    Sentence str = new Sentence("わたしはカラオケが大好き！",0);
    List<ValidationError> errors = validator.check(str);
    assertEquals(1, errors.size());
  }

  @Test
  public void testWithoutInvalidCharacter() {
    InvalidCharacterValidatorForTest validator = new InvalidCharacterValidatorForTest();
    String sampleCharTable = new String(
        "<?xml version=\"1.0\"?>"+
        "<character-table>" +
        "<character name=\"EXCLAMATION_MARK\" value=\"!\" invalid-chars=\"！\"/>" +
        "</character-table>");
    InputStream stream = IOUtils.toInputStream(sampleCharTable);
    CharacterTable characterTable = CharacterTableLoader.load(stream);
    validator.loadCharacterTable(characterTable);
    Sentence str = new Sentence("I like karaoke!",0);
    List<ValidationError> errors = validator.check(str);
    assertEquals(0, errors.size());
  }

  @Test
  public void testWithoutMultipleInvalidCharacter() {
    InvalidCharacterValidatorForTest validator = new InvalidCharacterValidatorForTest();
    String sampleCharTable = new String(
        "<?xml version=\"1.0\"?>"+
        "<character-table>" +
        "<character name=\"EXCLAMATION_MARK\" value=\"!\" invalid-chars=\"！\"/>" +
        "<character name=\"COMMA\" value=\",\" invalid-chars=\"、\"/>" +
        "</character-table>");
    InputStream stream = IOUtils.toInputStream(sampleCharTable);
    CharacterTable characterTable = CharacterTableLoader.load(stream);
    validator.loadCharacterTable(characterTable);
    Sentence str = new Sentence("わたしは、カラオケが好き！",0);
    List<ValidationError> errors = validator.check(str);
    assertEquals(2, errors.size());
  }

}
