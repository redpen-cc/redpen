/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.redpen.validator;

import cc.redpen.NoDefaultConstructorValidator;
import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.validator.sentence.SentenceLengthValidator;
import cc.redpen.validator.sentence.SpaceBeginningOfSentenceValidator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

class CustomValidator extends Validator {
}

class ValidatorFactoryTest {
    @Test
    void createValidator() throws RedPenException {
        Configuration conf = Configuration.builder().addValidatorConfig(new ValidatorConfiguration("SentenceLength")).build();
        assertEquals(SentenceLengthValidator.class, ValidatorFactory.getInstance(conf.getValidatorConfigs().get(0), conf).getClass());
    }

    @Test
    void registerDeprecatedValidator() {
        ValidatorFactory.registerValidator(SpaceBeginningOfSentenceValidator.class);
        assertNotNull(ValidatorFactory.validators.get("SpaceBeginningOfSentence"));
    }

    @Test
    void getConfigurationsDoesNotReturnsDeprecatedOnes() {
        List<ValidatorConfiguration> configurations = ValidatorFactory.getConfigurations("en");
        assertTrue(configurations.stream().filter(s->s.toString().equals("SentenceLength")).count() == 1);
        assertTrue(configurations.stream().filter(s->s.toString().equals("SpaceBeginningOfSentence")).count() == 0); //NOTE: SpaceBeginningOfSentenceValidator is deprecated
    }

    @Test
    void validatorPluginsAreCreatedAndRegistered() throws RedPenException {
        Configuration conf = Configuration.builder().addValidatorConfig(new ValidatorConfiguration("Custom")).build();
        assertEquals(CustomValidator.class, ValidatorFactory.getInstance(conf.getValidatorConfigs().get(0), conf).getClass());
        assertTrue(ValidatorFactory.getConfigurations("en").contains(new ValidatorConfiguration("Custom")));
        List<ValidatorConfiguration> en = ValidatorFactory.getConfigurations("");
        boolean foundEmbeddedJSValidator = false;
        for (ValidatorConfiguration configuration : en) {
            if (configuration.getConfigurationName().equals("MyEmbeddedJS")) {
                foundEmbeddedJSValidator = true;
                break;
            }
        }
        assertTrue(foundEmbeddedJSValidator);
    }

    @Test
    void validatorDoesNotExist() throws RedPenException {
        assertThrows(RedPenException.class, () -> {
            Configuration conf = Configuration.builder().addValidatorConfig(new ValidatorConfiguration("Foobar")).build();
            ValidatorFactory.getInstance(conf.getValidatorConfigs().get(0), conf);
        });
    }

    @Test
    void noDefaultConstructor() {
        assertThrows(RuntimeException.class, ()-> {
            ValidatorFactory.registerValidator(NoDefaultConstructorValidator.class);
        });
    }

    @Test
    void allDefaultValidatorsAreRegistered() throws Exception {
        String validatorsPackage = Validator.class.getPackage().getName();
        File classes = new File(Validator.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        File validators = new File(classes, validatorsPackage.replace(".", "/"));
        checkValidators(validatorsPackage, validators);
    }

    @Test
    void mapToStrings() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("string", "bar");
        properties.put("integer", 100);
        properties.put("boolean", false);
        properties.put("collection", asList("foo", "bar"));

        Map<String, String> result = ValidatorFactory.toStrings(properties);

        assertEquals("bar", result.get("string"));
        assertEquals("100", result.get("integer"));
        assertEquals("false", result.get("boolean"));
        assertEquals("foo,bar", result.get("collection"));
    }

    @SuppressWarnings("ConstantConditions")
    private void checkValidators(String validatorsPackage, File validators) throws ClassNotFoundException {
        for (File file : validators.listFiles()) {
            String name = file.getName();
            if (file.isDirectory()) {
                checkValidators(validatorsPackage + '.' + name, file);
            }
            else if (name.endsWith("Validator.class")) {
                Class<?> validatorClass = Class.forName(validatorsPackage + "." + name.substring(0, name.length() - 6));
                if (Modifier.isAbstract(validatorClass.getModifiers())) continue;
                String validatorName = name.substring(0, name.length() - "Validator.class".length());
                Validator validator = ValidatorFactory.validators.get(validatorName);
                assertNotNull(validator, validatorName + " must be registered in " + ValidatorFactory.class);
                assertTrue(validator instanceof Validator, validatorClass + " must extend " + Validator.class);
                assertTrue(validatorClass.isAssignableFrom(validator.getClass()), "Registered validator " + name + " must be of " + validatorClass);
            }
        }
    }
}
