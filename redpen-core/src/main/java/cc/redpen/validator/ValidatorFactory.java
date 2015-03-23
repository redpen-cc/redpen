/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.redpen.validator;

import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.SymbolTable;
import cc.redpen.config.ValidatorConfiguration;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    public static Validator getInstance(String valiadtorName) throws RedPenException {
        Configuration conf = new Configuration.ConfigurationBuilder()
                .setLanguage("en")
                .addValidatorConfig(new ValidatorConfiguration(valiadtorName))
                .build();
        return getInstance(conf.getValidatorConfigs().get(0), conf.getSymbolTable());
    }

    // store validator constructors to save reflection API call costs
    private static final Map<String, Constructor> validatorConstructorMap = new ConcurrentHashMap<>();

    public static Validator getInstance(ValidatorConfiguration config, SymbolTable symbolTable)
            throws RedPenException {
        Constructor<?> constructor = validatorConstructorMap.computeIfAbsent(config.getValidatorClassName(), validatorClassName -> {
            try {
                for (String validatorPackage : VALIDATOR_PACKAGES) {
                    String fqValidatorClassName = validatorPackage + "." + validatorClassName;
                    try {
                        Class<?> clazz = Class.forName(fqValidatorClassName);
                        // ensure the class extends Validator
                        Class<?> superclass = clazz.getSuperclass();
                        if (!superclass.equals(cc.redpen.validator.Validator.class)) {
                            throw new RuntimeException(fqValidatorClassName + " doesn't extend cc.redpen.validator.Validator");
                        }
                        return clazz.getConstructor();
                    } catch (ClassNotFoundException ignore) {
                    }
                }
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            // unable to find Validator
            return null;
        });

        if (constructor == null) {
            throw new RedPenException("There is no such Validator: " + config.getConfigurationName());
        }
        try {
            Validator validator = (Validator) constructor.newInstance();
            validator.preInit(config, symbolTable);
            return validator;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
