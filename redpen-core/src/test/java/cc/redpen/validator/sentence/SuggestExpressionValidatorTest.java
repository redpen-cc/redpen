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
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;


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
        List<ValidationError> errors = new ArrayList<>();
        suggestExpressionValidator.validate(errors, str);
        assertEquals(1, errors.size());
    }

    @Test
    public void testWithoutSynonym() {
        Sentence str = new Sentence("it love a piece of a cake.", 0);
        List<ValidationError> errors = new ArrayList<>();
        suggestExpressionValidator.validate(errors, str);
        assertEquals(0, errors.size());
    }

    @Test
    public void testWithMultipleSynonyms() {
        Sentence str = new Sentence("it like a the info.", 0);
        List<ValidationError> errors = new ArrayList<>();
        suggestExpressionValidator.validate(errors, str);
        assertEquals(2, errors.size());
    }

    @Test
    public void testWitoutZeroLengthSentence() {
        Sentence str = new Sentence("", 0);
        List<ValidationError> errors = new ArrayList<>();
        suggestExpressionValidator.validate(errors, str);
        assertEquals(0, errors.size());
    }
}
