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
