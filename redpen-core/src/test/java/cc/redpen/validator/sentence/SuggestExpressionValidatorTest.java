/**
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
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

import cc.redpen.ValidationError;
import cc.redpen.model.Sentence;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class SuggestExpressionValidatorTest {

    private SuggestExpressionValidator suggestExpressionValidator;

    @Before
    public void init() {
        suggestExpressionValidator = new SuggestExpressionValidator();
        Map<String, String> synonymSamples = new HashMap<>();
        synonymSamples.put("like", "such as");
        synonymSamples.put("info", "infomation");
        suggestExpressionValidator.setSynonyms(synonymSamples);
    }

    @Test
    public void testSynonym() {
        Sentence str = new Sentence("it like a piece of a cake.", 0);
        List<ValidationError> error = suggestExpressionValidator.validate(str);
        assertNotNull(error);
        assertEquals(1, error.size());
    }

    @Test
    public void testWitoutSynonym() {
        Sentence str = new Sentence("it love a piece of a cake.", 0);
        List<ValidationError> error = suggestExpressionValidator.validate(str);
        assertNotNull(error);
        assertEquals(0, error.size());
    }

    @Test
    public void testWithMultipleSynonyms() {
        Sentence str = new Sentence("it like a the info.", 0);
        List<ValidationError> error = suggestExpressionValidator.validate(str);
        assertNotNull(error);
        assertEquals(2, error.size());
    }

    @Test
    public void testWitoutZeroLengthSentence() {
        Sentence str = new Sentence("", 0);
        List<ValidationError> error = suggestExpressionValidator.validate(str);
        assertNotNull(error);
        assertEquals(0, error.size());
    }
}
