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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    public void customValidator() throws RedPenException {
        ValidatorFactory.registerValidator(CustomValidator.class);
        Configuration conf = Configuration.builder().addValidatorConfig(new ValidatorConfiguration("Custom")).build();
        assertEquals(CustomValidator.class, ValidatorFactory.getInstance(conf.getValidatorConfigs().get(0), conf).getClass());
    }

    @Test(expected = RedPenException.class)
    public void validatorDoesNotExist() throws RedPenException {
        Configuration conf = Configuration.builder().addValidatorConfig(new ValidatorConfiguration("Foobar")).build();
        ValidatorFactory.getInstance(conf.getValidatorConfigs().get(0), conf);
    }

    @Test(expected = RedPenException.class)
    public void noDefaultConstructor() throws RedPenException {
        ValidatorFactory.registerValidator(NoDefaultConstructorValidator.class);
        Configuration conf = Configuration.builder().addValidatorConfig(new ValidatorConfiguration("NoDefaultConstructor")).build();
        ValidatorFactory.getInstance(conf.getValidatorConfigs().get(0), conf);
    }

    @Test
    public void allDefaultValidatorsAreRegistered() throws Exception {
        String validatorsPackage = Validator.class.getPackage().getName();
        File classes = new File(Validator.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        File validators = new File(classes, validatorsPackage.replace(".", "/"));
        checkValidators(validatorsPackage, validators);
    }

    @SuppressWarnings({"ConstantConditions", "SuspiciousMethodCalls"})
    private void checkValidators(String validatorsPackage, File validators) throws ClassNotFoundException {
        for (File file : validators.listFiles()) {
            String name = file.getName();
            if (file.isDirectory()) {
                checkValidators(validatorsPackage + '.' + name, file);
            }
            else if (name.endsWith("Validator.class")) {
                Class<?> validator = Class.forName(validatorsPackage + "." + name.substring(0, name.length() - 6));
                if (validator == Validator.class) continue;
                assertTrue(validator + " must extend " + Validator.class, Validator.class.isAssignableFrom(validator));
                assertTrue(validator + " must be registered in " + ValidatorFactory.class, ValidatorFactory.validators.containsValue(validator));
            }
        }
    }
}
