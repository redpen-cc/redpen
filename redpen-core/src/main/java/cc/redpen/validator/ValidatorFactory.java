package cc.redpen.validator;

import cc.redpen.RedPenException;
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

    public static Validator<?> getInstance(ValidatorConfiguration config,
                                           SymbolTable symbolTable)
            throws RedPenException {
        try {
            for (String validatorPackage : VALIDATOR_PACKAGES) {
                String validatorClassName = validatorPackage + "." + config.getConfigurationName() + "Validator";
                try {
                    Class<?> clazz = Class.forName(validatorClassName);
                    // ensure the class implements Validator
                    boolean implementsValidator = false;
                    for (Class<?> aClass : clazz.getInterfaces()) {
                        if (aClass.equals(cc.redpen.validator.Validator.class)) {
                            implementsValidator = true;
                            break;
                        }
                    }
                    if (!implementsValidator) {
                        throw new RuntimeException(validatorClassName + " doesn't implement cc.redpen.validator.Validator");
                    }

                    Constructor<?> constructor = clazz.getConstructor();
                    Validator<?> validator = (Validator<?>) constructor.newInstance();
                    validator.init(config, symbolTable);
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
