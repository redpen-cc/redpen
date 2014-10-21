package cc.redpen.validator;

import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Sentence;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.fail;

class NotImplementInterfaceValidator {}

class NoConstructorWithConfigsValidator extends Validator<Sentence> {
    @Override
    public List<ValidationError> validate(Sentence block) {
        return null;
    }
}

public class ValidatorFactoryTest {
    @Test
    public void testCreateValidator() {
        Configuration conf = new Configuration.Builder()
                .setLanguage("en")
                .addValidatorConfig(new ValidatorConfiguration("SentenceLength"))
                .build();
        try {
            ValidatorFactory.getInstance(
                    conf.getValidatorConfigs().get(0), conf.getSymbolTable());
        } catch (RedPenException e) {
            fail();
        }
    }

    @Test(expected = RedPenException.class)
    public void testThrowExceptionWhenCreateNonExistValidator() throws RedPenException {
        Configuration conf = new Configuration.Builder()
                .setLanguage("en")
                .addValidatorConfig(new ValidatorConfiguration("Foobar"))
                .build();
        ValidatorFactory.getInstance(
                conf.getValidatorConfigs().get(0), conf.getSymbolTable());
    }

    @Test(expected = RuntimeException.class)
    public void testThrowExceptionWhenCreateValidatorNotImplementsInterface() throws RedPenException {
        Configuration conf = new Configuration.Builder()
                .setLanguage("en")
                .addValidatorConfig(new ValidatorConfiguration("NotImplementInterface"))
                .build();
        ValidatorFactory.getInstance(
                conf.getValidatorConfigs().get(0), conf.getSymbolTable());
    }

    @Test(expected = RuntimeException.class)
    public void testThrowExceptionWhenCreateValidatorWithoutConstructorWithConfigs() throws RedPenException {
        Configuration conf = new Configuration.Builder()
                .setLanguage("en")
                .addValidatorConfig(new ValidatorConfiguration("NoConstructorWithConfigs"))
                .build();
        ValidatorFactory.getInstance(
                conf.getValidatorConfigs().get(0), conf.getSymbolTable());
    }

}
