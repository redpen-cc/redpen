package cc.redpen.validator;

import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.model.Sentence;

public abstract class BaseValidatorTest {
  protected Configuration config = getConfiguration("en");

  protected Configuration getConfiguration(String language) {
    return new Configuration.ConfigurationBuilder()
      .addValidatorConfig(new ValidatorConfiguration("SuccessiveWord"))
      .setLanguage(language).build();
  }

  protected Document prepareSimpleDocument(String sentrence) {
    return new Document.DocumentBuilder(config.getTokenizer())
      .addSection(1)
      .addParagraph()
      .addSentence(new Sentence(sentrence, 1))
      .build();
  }
}

