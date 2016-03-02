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
import cc.redpen.tokenizer.TokenElement;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ValidatorTest {
    private final Configuration globalConfig = Configuration.builder().build();

    @Test
    public void validationErrorCreation() throws RedPenException {
        ValidationErrorMessageTest validationErrorMessageTest = new ValidationErrorMessageTest();
        validationErrorMessageTest.preInit(new ValidatorConfiguration("blah"), Configuration.builder().build());
        validationErrorMessageTest.setLocale(Locale.ENGLISH);
        List<ValidationError> validationErrors = new ArrayList<>();
        validationErrorMessageTest.setErrorList(validationErrors);
        Sentence sentence = new Sentence("sentence", 1);
        sentence.setTokens(singletonList(new TokenElement("word", singletonList(""), 0)));
        validationErrorMessageTest.validate(sentence);
        assertEquals("error str:sentence 1:1 2:2 3:3", validationErrors.get(0).getMessage());
        assertEquals("with Key :sentence", validationErrors.get(1).getMessage());

        validationErrorMessageTest.setLocale(Locale.JAPAN);
        validationErrors = new ArrayList<>();
        validationErrorMessageTest.setErrorList(validationErrors);
        validationErrorMessageTest.validate(sentence);
        assertEquals("エラー ストリング:sentence 1:1 2:2 3:3", validationErrors.get(0).getMessage());
        assertEquals("キー指定 :sentence", validationErrors.get(1).getMessage());
    }

    @Test
    public void configOverridesDefaultAttributes() throws Exception {
        Validator validator = new Validator("hello", 123) {};
        assertEquals(123, validator.getInt("hello"));

        validator.preInit(new ValidatorConfiguration("blah").addProperty("hello", "234"), globalConfig);
        assertEquals(234, validator.getInt("hello"));
    }

    @Test
    public void equalsAndHashCode() throws Exception {
        Validator validator = new ValidationErrorMessageTest();
        Validator validator2 = new ValidationErrorMessageTest();
        assertEquals(validator, validator2);
        assertEquals(validator.hashCode(), validator2.hashCode());

        validator.preInit(new ValidatorConfiguration("blah"), globalConfig);
        assertFalse(validator.equals(validator2));
        assertFalse(validator.hashCode() == validator2.hashCode());

        validator2.preInit(new ValidatorConfiguration("blah"), globalConfig);
        assertEquals(validator, validator2);
        assertEquals(validator.hashCode(), validator2.hashCode());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("ValidationErrorMessageTest{}", new ValidationErrorMessageTest().toString());
        assertEquals("ValidationErrorMessageTest{hello=123}", new ValidationErrorMessageTest("hello", 123).toString());
    }
}

