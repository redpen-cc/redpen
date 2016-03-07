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

import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.validator.sentence.SentenceLengthValidator;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

class CustomValidator extends Validator {
}

class NoDefaultConstructorValidator extends Validator {
    @SuppressWarnings("UnusedParameters")
    public NoDefaultConstructorValidator(String blah) {
    }
}

public class ValidatorFactoryTest {
    @Test
    public void createValidator() throws RedPenException {
        Configuration conf = Configuration.builder().addValidatorConfig(new ValidatorConfiguration("SentenceLength")).build();
        assertEquals(SentenceLengthValidator.class, ValidatorFactory.getInstance(conf.getValidatorConfigs().get(0), conf).getClass());
    }

    @Test
    public void validatorPluginsAreCreatedAndRegistered() throws RedPenException {
        Configuration conf = Configuration.builder().addValidatorConfig(new ValidatorConfiguration("Custom")).build();
        assertEquals(CustomValidator.class, ValidatorFactory.getInstance(conf.getValidatorConfigs().get(0), conf).getClass());
        assertTrue(ValidatorFactory.getConfigurations("en").contains(new ValidatorConfiguration("Custom")));
    }

    @Test(expected = RedPenException.class)
    public void validatorDoesNotExist() throws RedPenException {
        Configuration conf = Configuration.builder().addValidatorConfig(new ValidatorConfiguration("Foobar")).build();
        ValidatorFactory.getInstance(conf.getValidatorConfigs().get(0), conf);
    }

    @Test(expected = RuntimeException.class)
    public void noDefaultConstructor() {
        ValidatorFactory.registerValidator(NoDefaultConstructorValidator.class);
    }

    @Test
    public void allDefaultValidatorsAreRegistered() throws Exception {
        String validatorsPackage = Validator.class.getPackage().getName();
        File classes = new File(Validator.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        File validators = new File(classes, validatorsPackage.replace(".", "/"));
        checkValidators(validatorsPackage, validators);
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
                assertNotNull(validatorName + " must be registered in " + ValidatorFactory.class, validator);
                assertTrue(validatorClass + " must extend " + Validator.class, validator instanceof Validator);
                assertTrue("Registered validator " + name + " must be of " + validatorClass, validatorClass.isAssignableFrom(validator.getClass()));
            }
        }
    }
}
