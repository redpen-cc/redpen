package cc.redpen.config;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for creating Configuration object with the Builder.
 */
public class ConfigurationBuilderTest {
  @Test
  public void testBuildSimpleConfiguration() {
    Configuration config = new Configuration.Builder()
        .addValidatorConfig(new ValidatorConfiguration("InvalidExpression"))
        .addValidatorConfig(new ValidatorConfiguration("SentenceLength"))
        .setCharacterTable("en").build();

    assertEquals(2, config.getValidatorConfigs().size());
    assertNotNull(config.getCharacterTable());
    assertEquals("InvalidExpression", config.getValidatorConfigs()
        .get(0).getConfigurationName());
    assertEquals("SentenceLength", config.getValidatorConfigs()
        .get(1).getConfigurationName());
  }

  @Test
  public void testBuildConfigurationWithoutCharacterTable() {
    Configuration config = new Configuration.Builder()
        .addValidatorConfig(new ValidatorConfiguration("InvalidExpression"))
        .addValidatorConfig(new ValidatorConfiguration("SentenceLength")).build();

    assertEquals(2, config.getValidatorConfigs().size());
    assertNotNull(config.getCharacterTable());
    assertEquals("InvalidExpression", config.getValidatorConfigs()
        .get(0).getConfigurationName());
    assertEquals("SentenceLength", config.getValidatorConfigs()
        .get(1).getConfigurationName());
  }

  @Test
  public void testBuildConfigurationAddingProperties() {
    Configuration config = new Configuration.Builder()
        .addValidatorConfig(new ValidatorConfiguration("InvalidExpression")
            .addAttribute("dict", "./foobar.dict"))
        .addValidatorConfig(new ValidatorConfiguration("SentenceLength")
            .addAttribute("max_length", "10")).build();

    assertEquals(2, config.getValidatorConfigs().size());
    assertNotNull(config.getCharacterTable());
    assertEquals("InvalidExpression", config.getValidatorConfigs()
        .get(0).getConfigurationName());
    assertEquals("./foobar.dict",
        config.getValidatorConfigs().get(0).getAttribute("dict"));
    assertEquals("SentenceLength", config.getValidatorConfigs()
        .get(1).getConfigurationName());
    assertEquals("10",
        config.getValidatorConfigs().get(1).getAttribute("max_length"));
  }

  @Test
  public void testBuildConfigurationSpecifyingLanguage() {
    Configuration config = new Configuration.Builder()
        .addValidatorConfig(new ValidatorConfiguration("InvalidExpression"))
        .setCharacterTable("ja")
        .build();

    assertNotNull(config.getCharacterTable());
    assertNotNull(config.getCharacterTable().getCharacter("FULL_STOP"));
    assertEquals("。", config.getCharacterTable().getCharacter("FULL_STOP").getValue());
  }

  @Test
  public void testBuildConfigurationOverrideCharacterSetting() {
    Configuration config = new Configuration.Builder()
        .addValidatorConfig(new ValidatorConfiguration("InvalidExpression"))
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
        .addValidatorConfig(new ValidatorConfiguration("InvalidExpression"))
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
