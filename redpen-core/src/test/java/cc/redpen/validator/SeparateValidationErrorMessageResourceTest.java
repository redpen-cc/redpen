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
import cc.redpen.model.Sentence;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class SeparateValidationErrorMessageResourceTest extends Validator {

    @Override
    public void validate(Sentence sentence) {
        addValidationError(sentence, 1, 2, 3, "sentence");
        addValidationError("withKey", sentence, "sentence");
    }

    @Test
    public void testValidationErrorCreation() throws RedPenException {
        SeparateValidationErrorMessageResourceTest validationErrorMessageTest = new SeparateValidationErrorMessageResourceTest();
        validationErrorMessageTest.preInit(null, null);
        // loads SeparateValidationErrorMessageResourceTest.properties
        validationErrorMessageTest.setLocale(Locale.ENGLISH);
        List<ValidationError> validationErrors = new ArrayList<>();
        validationErrorMessageTest.setErrorList(validationErrors);
        validationErrorMessageTest.validate(new Sentence("sentence", 1));
        // if message resource doesn't found, fallback to [ValidatorClassName].properties
        assertEquals("separate resource error str:sentence 1:1 2:2 3:3", validationErrors.get(0).getMessage());
        assertEquals("separate resource with Key :sentence", validationErrors.get(1).getMessage());

        // loads SeparateValidationErrorMessageResourceTest_ja.properties
        validationErrorMessageTest.setLocale(Locale.JAPAN);
        validationErrors = new ArrayList<>();
        validationErrorMessageTest.setErrorList(validationErrors);
        validationErrorMessageTest.validate(new Sentence("sentence", 1));
        assertEquals("separate resource エラー ストリング:sentence 1:1 2:2 3:3", validationErrors.get(0).getMessage());
        assertEquals("separate resource キー指定 :sentence", validationErrors.get(1).getMessage());
    }

}
