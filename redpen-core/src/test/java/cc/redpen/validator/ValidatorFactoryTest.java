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
import cc.redpen.model.Sentence;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.fail;

class NotImplementInterfaceValidator {}

class NoConstructorWithConfigsValidator extends Validator {
    @Override
    public void validate(Sentence sentence) {
    }
}

public class ValidatorFactoryTest {
    @Test
    public void testCreateValidator() {
        Configuration conf = new Configuration.ConfigurationBuilder()
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
        Configuration conf = new Configuration.ConfigurationBuilder()
                .setLanguage("en")
                .addValidatorConfig(new ValidatorConfiguration("Foobar"))
                .build();
        ValidatorFactory.getInstance(
                conf.getValidatorConfigs().get(0), conf.getSymbolTable());
    }

    @Test(expected = RuntimeException.class)
    public void testThrowExceptionWhenCreateValidatorNotImplementsInterface() throws RedPenException {
        Configuration conf = new Configuration.ConfigurationBuilder()
                .setLanguage("en")
                .addValidatorConfig(new ValidatorConfiguration("NotImplementInterface"))
                .build();
        ValidatorFactory.getInstance(
                conf.getValidatorConfigs().get(0), conf.getSymbolTable());
    }

    @Test(expected = RuntimeException.class)
    public void testThrowExceptionWhenCreateValidatorWithoutConstructorWithConfigs() throws RedPenException {
        Configuration conf = new Configuration.ConfigurationBuilder()
                .setLanguage("en")
                .addValidatorConfig(new ValidatorConfiguration("NoConstructorWithConfigs"))
                .build();
        ValidatorFactory.getInstance(
                conf.getValidatorConfigs().get(0), conf.getSymbolTable());
    }

}
