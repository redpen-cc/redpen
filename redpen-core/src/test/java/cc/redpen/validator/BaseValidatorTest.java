package cc.redpen.validator;

import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.model.Sentence;

public abstract class BaseValidatorTest {
  protected String validatorName;
  protected Configuration config;

  public BaseValidatorTest(String validatorName) {
    this.validatorName = validatorName;
    this.config = getConfiguration("en");
  }

  protected Configuration getConfiguration(String language) {
    return Configuration.builder()
      .addValidatorConfig(new ValidatorConfiguration(validatorName))
      .setLanguage(language).build();
  }

  protected Document prepareSimpleDocument(String sentrence) {
    return Document.builder(config.getTokenizer())
      .addSection(1)
      .addParagraph()
      .addSentence(new Sentence(sentrence, 1))
      .build();
  }
}

