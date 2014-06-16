package org.bigram.docvalidator.config;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for creating Configuration object with the Builder.
 */
public class ConfigurationBuilderTest {
  @Test
  public void testBuildSimpleConfiguration() {
    Configuration config = new Configuration.Builder()
        .addSectionValidatorConfig(new ValidatorConfiguration("InvalidExpression"))
        .addSentenceValidatorConfig(new ValidatorConfiguration("SentenceLength"))
        .setCharacterTable("en").build();

    assertEquals(1, config.getSentenceValidatorConfigs().size());
    assertEquals(1, config.getSectionValidatorConfigs().size());
    assertEquals(0, config.getParagraphValidatorConfigs().size());
    assertEquals(0, config.getDocumentValidatorConfigs().size());
    assertNotNull(config.getCharacterTable());
    assertEquals("InvalidExpression", config.getSectionValidatorConfigs()
        .get(0).getConfigurationName());
    assertEquals("SentenceLength", config.getSentenceValidatorConfigs()
        .get(0).getConfigurationName());
  }

  @Test
  public void testBuildConfigurationWithoutCharacterTable() {
    Configuration config = new Configuration.Builder()
        .addSectionValidatorConfig(new ValidatorConfiguration("InvalidExpression"))
        .addSentenceValidatorConfig(new ValidatorConfiguration("SentenceLength")).build();

    assertEquals(1, config.getSentenceValidatorConfigs().size());
    assertEquals(1, config.getSectionValidatorConfigs().size());
    assertEquals(0, config.getParagraphValidatorConfigs().size());
    assertEquals(0, config.getDocumentValidatorConfigs().size());
    assertNotNull(config.getCharacterTable());
    assertEquals("InvalidExpression", config.getSectionValidatorConfigs()
        .get(0).getConfigurationName());
    assertEquals("SentenceLength", config.getSentenceValidatorConfigs()
        .get(0).getConfigurationName());
  }

  @Test
  public void testBuildConfigurationAddingProperties() {
    Configuration config = new Configuration.Builder()
        .addSectionValidatorConfig(new ValidatorConfiguration("InvalidExpression")
            .addAttribute("dict", "./foobar.dict"))
        .addSentenceValidatorConfig(new ValidatorConfiguration("SentenceLength")
            .addAttribute("max_length", "10")).build();

    assertEquals(1, config.getSentenceValidatorConfigs().size());
    assertEquals(1, config.getSectionValidatorConfigs().size());
    assertEquals(0, config.getParagraphValidatorConfigs().size());
    assertEquals(0, config.getDocumentValidatorConfigs().size());
    assertNotNull(config.getCharacterTable());
    assertEquals("InvalidExpression", config.getSectionValidatorConfigs()
        .get(0).getConfigurationName());
    assertEquals("./foobar.dict",
        config.getSectionValidatorConfigs().get(0).getAttribute("dict"));
    assertEquals("SentenceLength", config.getSentenceValidatorConfigs()
        .get(0).getConfigurationName());
    assertEquals("10",
        config.getSentenceValidatorConfigs().get(0).getAttribute("max_length"));
  }

  @Test
  public void testBuildConfigurationSpecifyingLanguage() {
    Configuration config = new Configuration.Builder()
        .addSectionValidatorConfig(new ValidatorConfiguration("InvalidExpression"))
        .setCharacterTable("ja")
        .build();

    assertNotNull(config.getCharacterTable());
    assertNotNull(config.getCharacterTable().getCharacter("FULL_STOP"));
    assertEquals("。", config.getCharacterTable().getCharacter("FULL_STOP").getValue());
  }

  @Test
  public void testBuildConfigurationOverrideCharacterSetting() {
    Configuration config = new Configuration.Builder()
        .addSectionValidatorConfig(new ValidatorConfiguration("InvalidExpression"))
        .setCharacterTable("ja")
        .setCharacter("FULL_STOP", ".")
        .build();

    assertNotNull(config.getCharacterTable());
    assertNotNull(config.getCharacterTable().getCharacter("FULL_STOP"));
    assertEquals(".", config.getCharacterTable().getCharacter("FULL_STOP").getValue());
  }

  @Test
  public void testBuildConfigurationOverrideAddInvalidCharacterSetting() {
    Configuration config = new Configuration.Builder()
        .addSectionValidatorConfig(new ValidatorConfiguration("InvalidExpression"))
        .setCharacterTable("ja")
        .addInvalidPattern("FULL_STOP", "●")
        .build();

    assertNotNull(config.getCharacterTable());
    assertNotNull(config.getCharacterTable().getCharacter("FULL_STOP"));
    assertEquals("。", config.getCharacterTable().getCharacter("FULL_STOP").getValue());
    assertTrue(config.getCharacterTable()
        .getCharacter("FULL_STOP").getInvalidChars().contains("●"));
  }
}
