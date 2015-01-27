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
import cc.redpen.config.Symbol;
import cc.redpen.config.SymbolType;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Sentence;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;
import cc.redpen.validator.ValidatorFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class QuotationValidatorTest {
    @Test
    public void testDoubleQuotationMakrs() throws RedPenException {
        Validator validator = ValidatorFactory.getInstance("Quotation");
        Sentence str = new Sentence("I said “That is true”.", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, str);
        assertNotNull(errors);
        assertEquals(0, errors.size());
    }

    @Test
    public void testSingleQuotationMakrs() throws RedPenException {
        Validator validator = ValidatorFactory.getInstance("Quotation");
        Sentence str = new Sentence("I said ‘that is true’.", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, str);
        assertNotNull(errors);
        assertEquals(0, errors.size());
    }

    @Test
    public void testDoubleQuotationMakrWithoutRight() throws RedPenException {
        Validator validator = ValidatorFactory.getInstance("Quotation");
        Sentence str = new Sentence("I said “That is true.", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, str);
        assertNotNull(errors);
        assertEquals(1, errors.size());
    }

    @Test
    public void testSingleQuotationMakrWithoutRight() throws RedPenException {
        Validator validator = ValidatorFactory.getInstance("Quotation");
        Sentence str = new Sentence("I said ‘that is true.", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, str);
        assertNotNull(errors);
        assertEquals(1, errors.size());
    }

    @Test
    public void testDoubleQuotationMakrWithoutLeft() throws RedPenException {
        Validator validator = ValidatorFactory.getInstance("Quotation");
        Sentence str = new Sentence("I said That is true”.", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, str);
        assertNotNull(errors);
        assertEquals(1, errors.size());
    }

    @Test
    public void testSingleQuotationMakrkWithoutLeft() throws RedPenException {
        Validator validator = ValidatorFactory.getInstance("Quotation");
        Sentence str = new Sentence("I said that is true’.", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, str);
        assertNotNull(errors);
        assertEquals(1, errors.size());
    }

    @Test
    public void testExceptionCase() throws RedPenException {
        Validator validator = ValidatorFactory.getInstance("Quotation");
        Sentence str = new Sentence("I’m a jedi knight.", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, str);
        assertNotNull(errors);
        assertEquals(0, errors.size());
    }

    @Test
    public void testQuotedExceptionCase() throws RedPenException {
        Validator validator = ValidatorFactory.getInstance("Quotation");
        Sentence str = new Sentence("he said ‘I’m a jedi knight’.", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, str);
        assertNotNull(errors);
        assertEquals(0, errors.size());
    }

    @Test
    public void testDoubleLeftSingleQuotationMakrk() throws RedPenException {
        Validator validator = ValidatorFactory.getInstance("Quotation");
        Sentence str = new Sentence("I said ‘that is true‘.", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, str);
        assertNotNull(errors);
        assertEquals(1, errors.size());
    }

    @Test
    public void testDoubleLeftDoubleQuotationMakrk() throws RedPenException {
        Validator validator = ValidatorFactory.getInstance("Quotation");
        Sentence str = new Sentence("I said “that is true.“", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, str);
        assertNotNull(errors);
        assertEquals(1, errors.size());
    }

    @Test
    public void testDoubleRightSingleQuotationMakrk() throws RedPenException {
        Validator validator = ValidatorFactory.getInstance("Quotation");
        Sentence str = new Sentence("I said ’that is true’.", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, str);
        assertNotNull(errors);
        assertEquals(1, errors.size());
    }

    @Test
    public void testDoubleRightDoubleQuotationMakrk() throws RedPenException {
        Validator validator = ValidatorFactory.getInstance("Quotation");
        Sentence str = new Sentence("I said ”that is true”.", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, str);
        assertNotNull(errors);
        assertEquals(1, errors.size());
    }

    @Test
    public void testAsciiExceptionCase() throws RedPenException {
        Configuration conf = new Configuration.ConfigurationBuilder()
                .setLanguage("en")
                .addValidatorConfig(new ValidatorConfiguration("Quotation").addAttribute("use_ascii", false))
                .build();
        Validator validator = ValidatorFactory.getInstance(conf.getValidatorConfigs().get(0), conf.getSymbolTable());
        Sentence str = new Sentence("I'm a jedi knight.", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, str);
        assertNotNull(errors);
        assertEquals(0, errors.size());
    }

    @Test
    public void testAsciiDoubleQuotationMakrk() throws RedPenException {
        Configuration conf = new Configuration.ConfigurationBuilder()
                .setLanguage("en")
                .addValidatorConfig(new ValidatorConfiguration("Quotation").addAttribute("use_ascii", false))
                .build();
        Validator validator = ValidatorFactory.getInstance(conf.getValidatorConfigs().get(0), conf.getSymbolTable());
        Sentence str = new Sentence("I said \"that is true\".", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, str);
        assertNotNull(errors);
        assertEquals(0, errors.size());
    }

    @Test
    public void testNoQuotationMakrk() throws RedPenException {
        Configuration conf = new Configuration.ConfigurationBuilder()
                .setLanguage("en")
                .addValidatorConfig(new ValidatorConfiguration("Quotation").addAttribute("use_ascii", true))
                .build();
        Validator validator = ValidatorFactory.getInstance(conf.getValidatorConfigs().get(0), conf.getSymbolTable());
        Sentence str = new Sentence("I said that is true.", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, str);
        assertNotNull(errors);
        assertEquals(0, errors.size());
    }

    @Test
    public void testNoInput() throws RedPenException {
        Configuration conf = new Configuration.ConfigurationBuilder()
                .setLanguage("en")
                .addValidatorConfig(new ValidatorConfiguration("Quotation").addAttribute("use_ascii", true))
                .build();
        Validator validator = ValidatorFactory.getInstance(conf.getValidatorConfigs().get(0), conf.getSymbolTable());
        Sentence str = new Sentence("", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, str);
        assertNotNull(errors);
        assertEquals(0, errors.size());
    }

    @Test
    public void testTwiceQuotations() throws RedPenException {
        Validator validator = ValidatorFactory.getInstance("Quotation");
        Sentence str = new Sentence("I said ‘that is true’ and not said ‘that is false’", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, str);
        assertNotNull(errors);
        assertEquals(0, errors.size());
    }

    @Test
    public void testOneOfFailureInTwiceQuotations() throws RedPenException {
        Validator validator = ValidatorFactory.getInstance("Quotation");
        Sentence str = new Sentence("I said ‘that is true and not said ‘that is false’", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, str);
        assertNotNull(errors);
        assertEquals(1, errors.size());
    }

    @Test
    public void testLeftDoubleQuotationsWihtoutSpace() throws RedPenException {
        Validator validator = ValidatorFactory.getInstance("Quotation");
        Sentence str = new Sentence("I said“that is true”.", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, str);
        assertNotNull(errors);
        assertEquals(1, errors.size());
    }

    @Test
    public void testLeftAsciiDoubleQuotationsWihtoutSpace() throws RedPenException {
        Configuration conf = new Configuration.ConfigurationBuilder()
                .setLanguage("en")
                .addValidatorConfig(new ValidatorConfiguration("Quotation").addAttribute("use_ascii", true))
                .build();
        Validator validator = ValidatorFactory.getInstance(conf.getValidatorConfigs().get(0), conf.getSymbolTable());
        Sentence str = new Sentence("I said\"that is true\".", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, str);
        assertNotNull(errors);
        assertEquals(1, errors.size());
    }

    @Test
    public void testRightDoubleQuotationsWihtoutSpace() throws RedPenException {
        Validator validator = ValidatorFactory.getInstance("Quotation");
        Sentence str = new Sentence("I said “that is true”is true.", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, str);
        assertNotNull(errors);
        assertEquals(1, errors.size());
    }

    @Test
    public void testRightAsciiDoubleQuotationsWihtoutSpace() throws RedPenException {
        Configuration conf = new Configuration.ConfigurationBuilder()
                .setLanguage("en")
                .addValidatorConfig(new ValidatorConfiguration("Quotation").addAttribute("use_ascii", true))
                .build();
        Validator validator = ValidatorFactory.getInstance(conf.getValidatorConfigs().get(0), conf.getSymbolTable());
        Sentence str = new Sentence("I said \"that is true\"is true.", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, str);
        assertNotNull(errors);
        assertEquals(1, errors.size());
    }

    @Test
    public void testDoubleQuotationsWithNonAsciiPeriod() throws RedPenException {
        Configuration conf = new Configuration.ConfigurationBuilder()
                .setLanguage("en")
                .addValidatorConfig(new ValidatorConfiguration("Quotation").addAttribute("use_ascii", true))
                .setSymbol(new Symbol(SymbolType.FULL_STOP, '。'))
                .build();
        Validator validator = ValidatorFactory.getInstance(conf.getValidatorConfigs().get(0), conf.getSymbolTable());

//        QuotationValidator validator =
//                new QuotationValidator(true, '。');
        Sentence str = new Sentence("I said \"that is true\"。", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, str);
        assertNotNull(errors);
        assertEquals(0, errors.size());
    }
}
