package cc.redpen.validator;

import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;

public abstract class BaseValidatorTest {
  protected Configuration getConfiguration(String language) {
    return new Configuration.ConfigurationBuilder()
      .addValidatorConfig(new ValidatorConfiguration("SuccessiveWord"))
      .setLanguage(language).build();
  }
}

