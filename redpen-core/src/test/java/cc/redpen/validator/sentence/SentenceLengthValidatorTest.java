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

import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Sentence;
import cc.redpen.validator.ValidationError;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SentenceLengthValidatorTest {
    private SentenceLengthValidator validator = new SentenceLengthValidator();

    @BeforeEach
    void setUp() throws Exception {
        validator.preInit(new ValidatorConfiguration("SentenceLength").addProperty("max_len", "30"), Configuration.builder().build());
    }

    @Test
    void testWithLongSentence() {
        Sentence str = new Sentence("this is a very long long long long long long"
                + "long long long long long long sentence.", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(1, errors.size());
    }

    @Test
    void testWithShortSentence() {
        Sentence str = new Sentence("this is a sentence.", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(0, errors.size());
    }

    @Test
    void testWithZeroLengthSentence() {
        Sentence str = new Sentence("", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(0, errors.size());
    }
}
