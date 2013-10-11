package org.unigram.docvalidator.validator.sentence;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.CharacterTable;
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
    CharacterTable characterTable = new CharacterTable(stream);
    validator.loadCharacterTable(characterTable);
    Sentence str = new Sentence("わたしはカラオケが大好き！",0);
    List<ValidationError> errors = validator.process(str);
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
    CharacterTable characterTable = new CharacterTable(stream);
    validator.loadCharacterTable(characterTable);
    Sentence str = new Sentence("I like karaoke!",0);
    List<ValidationError> errors = validator.process(str);
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
    CharacterTable characterTable = new CharacterTable(stream);
    validator.loadCharacterTable(characterTable);
    Sentence str = new Sentence("わたしは、カラオケが好き！",0);
    List<ValidationError> errors = validator.process(str);
    assertEquals(2, errors.size());
  }

}
