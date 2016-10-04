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

import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Sentence;
import cc.redpen.validator.BaseValidatorTest;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;
import cc.redpen.validator.ValidatorFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class SuggestExpressionValidatorTest extends BaseValidatorTest {
    private SuggestExpressionValidator validator;

    public SuggestExpressionValidatorTest() {
        super("SuggestExpression");
    }

    @Test
    public void testSynonym() throws RedPenException {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SuggestExpression").addProperty("map", "{like,such " +
                        "as}, {info,information}"))
                .build();

        Validator validator = ValidatorFactory.getInstance(config.getValidatorConfigs().get(0), config);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(new Sentence("they like a piece of a cake.", 0));
        assertEquals(1, errors.size());
    }

    @Test
    public void testSynonymSplitPlusWhiteSpace() throws RedPenException {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SuggestExpression").addProperty("map", "{like, such " +
                        "as}"))
                .build();

        Validator validator = ValidatorFactory.getInstance(config.getValidatorConfigs().get(0), config);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(new Sentence("they like a piece of a cake.", 0));
        assertEquals(1, errors.size());
    }

    @Test
    public void testWithoutSynonym() throws RedPenException {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SuggestExpression").addProperty("map", "{like,such " +
                        "as}, {info,information}"))
                .build();

        Validator validator = ValidatorFactory.getInstance(config.getValidatorConfigs().get(0), config);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(new Sentence("it loves a piece of a cake.", 0));
        assertEquals(0, errors.size());
    }

    @Test
    public void testWithMultipleSynonyms() throws RedPenException {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SuggestExpression").addProperty("map", "{like,such " +
                        "as}, {info,information}"))
                .build();

        Validator validator = ValidatorFactory.getInstance(config.getValidatorConfigs().get(0), config);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(new Sentence("it like a the info.", 0));
        assertEquals(2, errors.size());
    }

    @Test
    public void japanese() throws RedPenException {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SuggestExpression").addProperty("map", "{like,such " +
                        "as}, {info,information},{おはよう,お早う}"))
                .build();

        Validator validator = ValidatorFactory.getInstance(config.getValidatorConfigs().get(0), config);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(new Sentence("おはよう世界", 0));
        assertEquals(1, errors.size());
    }

    @Test
    public void testWithZeroLengthSentence() throws RedPenException {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SuggestExpression").addProperty("map", "{like,such " +
                        "as}, {info,information},{おはよう,お早う}"))
                .build();

        Validator validator = ValidatorFactory.getInstance(config.getValidatorConfigs().get(0), config);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(new Sentence("", 0));
        assertEquals(0, errors.size());
    }

    @Test
    public void testErrorMessageIsProperlyFormatted() throws RedPenException {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SuggestExpression").addProperty("map", "{like,such " +
                        "as}, {info,information}"))
                .build();

        Validator validator = ValidatorFactory.getInstance(config.getValidatorConfigs().get(0), config);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(new Sentence("Thank you for the info.", 0));
        assertEquals(1, errors.size());
        assertEquals("Found invalid word \"info\". Use the synonym \"information\" instead.", errors.get(0).getMessage());
    }

    @Test
    public void initDoesNotFailIfDictionaryIsNotSpecified() {
        try {
            Configuration config = Configuration.builder()
                    .addValidatorConfig(new ValidatorConfiguration("SuggestExpression")).build();
        } catch(Exception e) {
            fail();
        }
    }
}
