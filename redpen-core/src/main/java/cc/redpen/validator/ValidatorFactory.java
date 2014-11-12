package cc.redpen.validator;

import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.SymbolTable;
import cc.redpen.config.ValidatorConfiguration;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory class of validators.
 */
public class ValidatorFactory {
    private static final List<String> VALIDATOR_PACKAGES = new ArrayList<>();

    static {
        addValidatorPackage("cc.redpen.validator");
        addValidatorPackage("cc.redpen.validator.sentence");
        addValidatorPackage("cc.redpen.validator.section");
    }

    // can be made public if package needs to be added outside RedPen.
    private static void addValidatorPackage(String packageToAdd) {
        VALIDATOR_PACKAGES.add(packageToAdd);
    }

    public static Validator<?> getInstance(String valiadtorName) throws RedPenException {
        Configuration conf = new Configuration.ConfigurationBuilder()
                .setLanguage("en")
                .addValidatorConfig(new ValidatorConfiguration(valiadtorName))
                .build();
        return getInstance(conf.getValidatorConfigs().get(0), conf.getSymbolTable());
    }

    public static Validator<?> getInstance(ValidatorConfiguration config, SymbolTable symbolTable)
            throws RedPenException {
        try {
            for (String validatorPackage : VALIDATOR_PACKAGES) {
                String validatorClassName = validatorPackage + "." + config.getConfigurationName() + "Validator";
                try {
                    Class<?> clazz = Class.forName(validatorClassName);
                    // ensure the class extends Validator
                    if (!clazz.getSuperclass().equals(cc.redpen.validator.Validator.class)) {
                        throw new RuntimeException(validatorClassName + " doesn't extend cc.redpen.validator.Validator");
                    }

                    Constructor<?> constructor = clazz.getConstructor();
                    Validator<?> validator = (Validator<?>) constructor.newInstance();
                    validator.preInit(config, symbolTable);
                    return validator;
                } catch (ClassNotFoundException ignore) {
                }
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
        throw new RedPenException(
                "There is no such Validator: " + config.getConfigurationName());
    }
}
