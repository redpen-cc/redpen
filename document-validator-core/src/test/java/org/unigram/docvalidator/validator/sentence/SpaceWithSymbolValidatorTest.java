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
package org.unigram.docvalidator.validator.sentence;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.unigram.docvalidator.model.Sentence;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.CharacterTableLoader;
import org.unigram.docvalidator.util.ValidationError;

class SpaceWithSymbolValidatorForTest extends SymbolWithSpaceValidator {
  void loadCharacterTable (CharacterTable characterTable) {
    this.setCharacterTable(characterTable);
  }
}

public class SpaceWithSymbolValidatorTest {
  @Test
  public void testNotNeedSpace() {
    SpaceWithSymbolValidatorForTest validator = new SpaceWithSymbolValidatorForTest();
    String sampleCharTable = new String(
        "<?xml version=\"1.0\"?>"+
        "<character-table>" +
        "<character name=\"SLASH\" value=\"/\" />" +
        "</character-table>");
    InputStream stream = IOUtils.toInputStream(sampleCharTable);
    CharacterTable characterTable = CharacterTableLoader.load(stream);
    validator.loadCharacterTable(characterTable);
    Sentence str = new Sentence("I like apple/orange",0);
    List<ValidationError> errors = validator.check(str);
    assertEquals(0, errors.size());
  }

  @Test
  public void testNeedAfterSpace() {
    SpaceWithSymbolValidatorForTest validator = new SpaceWithSymbolValidatorForTest();
    String sampleCharTable = new String(
        "<?xml version=\"1.0\"?>"+
        "<character-table>" +
        "<character name=\"COLLON\" value=\":\" after-space=\"true\" />" +
        "</character-table>");
    InputStream stream = IOUtils.toInputStream(sampleCharTable);
    CharacterTable characterTable = CharacterTableLoader.load(stream);
    validator.loadCharacterTable(characterTable);
    Sentence str = new Sentence("I like her:yes it is.",0);
    List<ValidationError> errors = validator.check(str);
    assertEquals(1, errors.size());
  }

  @Test
  public void testNeedBeforeSpace() {
    SpaceWithSymbolValidatorForTest validator = new SpaceWithSymbolValidatorForTest();
    String sampleCharTable = new String(
        "<?xml version=\"1.0\"?>"+
        "<character-table>" +
        "<character name=\"LEFT_PARENTHEIS\" value=\"(\" invalid-chars=\"（\" before-space=\"true\" />" +
        "</character-table>");
    InputStream stream = IOUtils.toInputStream(sampleCharTable);
    CharacterTable characterTable = CharacterTableLoader.load(stream);
    validator.loadCharacterTable(characterTable);
    Sentence str = new Sentence("I like her(Nancy)very much.",0);
    List<ValidationError> errors = validator.check(str);
    assertEquals(1, errors.size());
  }

  @Test
  public void testNeedSpaceInMultiplePostion() {
    SpaceWithSymbolValidatorForTest validator = new SpaceWithSymbolValidatorForTest();
    String sampleCharTable = new String(
        "<?xml version=\"1.0\"?>"+
        "<character-table>" +
        "<character name=\"LEFT_PARENTHEIS\" value=\"(\" before-space=\"true\" />" +
        "<character name=\"RIGHT_PARENTHEIS\" value=\")\" after-space=\"true\" />" +
        "</character-table>");
    InputStream stream = IOUtils.toInputStream(sampleCharTable);
    CharacterTable characterTable = CharacterTableLoader.load(stream);
    validator.loadCharacterTable(characterTable);
    Sentence str = new Sentence("I like her(Nancy)very much.",0);
    List<ValidationError> errors = validator.check(str);
    assertEquals(2, errors.size());
  }

  @Test
  public void testReturnOnlyOneForHitBothBeforeAndAfter() {
    SpaceWithSymbolValidatorForTest validator = new SpaceWithSymbolValidatorForTest();
    String sampleCharTable = new String(
        "<?xml version=\"1.0\"?>"+
        "<character-table>" +
        "<character name=\"ASTARISK\" value=\"*\" before-space=\"true\" after-space=\"true\" />" +
        "</character-table>");
    InputStream stream = IOUtils.toInputStream(sampleCharTable);
    CharacterTable characterTable = CharacterTableLoader.load(stream);
    validator.loadCharacterTable(characterTable);
    Sentence str = new Sentence("I like 1*10",0);
    List<ValidationError> errors = validator.check(str);
    assertEquals(1, errors.size());
  }
}
