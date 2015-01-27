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
package cc.redpen.validator.sentence;

import cc.redpen.model.Sentence;
import cc.redpen.validator.ValidationError;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WordNumberValidatorTest {

    @Test
    public void testWithShortSentence() {
        WordNumberValidator maxWordNumberValidator = new WordNumberValidator();
        Sentence str = new Sentence(
                "this sentence is short.", 0);
        List<ValidationError> errors = new ArrayList<>();
        maxWordNumberValidator.validate(errors, str);
        assertNotNull(errors);
        assertEquals(0, errors.size());
    }

    @Test
    public void testWithLongSentence() {
        WordNumberValidator maxWordNumberValidator = new WordNumberValidator();
        Sentence str = new Sentence(
                "this sentence is very very very very very very very very very very" +
                        " very very very very very very very very very very very very very very long", 0);
        List<ValidationError> errors = new ArrayList<>();
        maxWordNumberValidator.validate(errors, str);
        assertNotNull(errors);
        assertEquals(0, errors.size());
    }

    @Test
    public void testWithZeroLengthSentence() {
        WordNumberValidator maxWordNumberValidator = new WordNumberValidator();
        Sentence str = new Sentence("", 0);
        List<ValidationError> errors = new ArrayList<>();
        maxWordNumberValidator.validate(errors, str);
        assertNotNull(errors);
        assertEquals(0, errors.size());
    }
}
