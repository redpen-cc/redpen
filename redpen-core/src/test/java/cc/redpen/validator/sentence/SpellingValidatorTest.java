/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.redpen.validator.sentence;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.validator.BaseValidatorTest;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;
import cc.redpen.validator.ValidatorFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static junit.framework.Assert.assertEquals;

public class SpellingValidatorTest extends BaseValidatorTest {
    public SpellingValidatorTest() {
        super("Spelling");
    }

    @Test
    public void testValidate() throws Exception {
        config = Configuration.builder()
          .addValidatorConfig(new ValidatorConfiguration(validatorName).addProperty("list", "this,a,pen"))
          .build();

        Document document = prepareSimpleDocument("this iz a pen");

        Validator validator = ValidatorFactory.getInstance(config.getValidatorConfigs().get(0), config);

        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(document.getLastSection().getParagraph(0).getSentence(0));
        assertEquals(1, errors.size());
    }

    @Test
    public void testLoadDefaultDictionary() throws RedPenException {
        Document document = prepareSimpleDocument("this iz goody");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(1, errors.get(document).size());
    }

    @Test
    public void testUpperCase() throws RedPenException {
        Document document = prepareSimpleDocument("This iz goody");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(1, errors.get(document).size());
    }


    @Test
    public void testSkipCharacterCase() throws RedPenException {
        Document document = prepareSimpleDocument("That is true, but there is a condition");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(0, errors.get(document).size());
    }

    @Test
    public void testUserSkipList() throws RedPenException {
        config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration(validatorName).addProperty("list", "abeshi,baz"))
                .build();

        Document document = prepareSimpleDocument("Abeshi is a word used in a comic.");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(0, errors.get(document).size());
    }

    @Test
    public void nonLatin() throws RedPenException {
        config = Configuration.builder("ru")
                .addValidatorConfig(new ValidatorConfiguration(validatorName).addProperty("list", "привет"))
                .build();

        Document document = prepareSimpleDocument("Привет, мир!");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(1, errors.get(document).size());
    }

    @Test
    public void punctuationInsideOfWord() throws RedPenException {
        config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration(validatorName).addProperty("list", "can-do"))
                .build();

        Document document = prepareSimpleDocument("can-do");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(0, errors.get(document).size());
    }

    @Test
    public void testPunctuation() throws RedPenException {
        RedPen redPen = new RedPen(config);

        Document document = prepareSimpleDocument("That is true.");
        assertEquals(0, redPen.validate(singletonList(document)).get(document).size());

        document = prepareSimpleDocument("That is true!");
        assertEquals(0, redPen.validate(singletonList(document)).get(document).size());

        document = prepareSimpleDocument("That is true?");
        assertEquals(0, redPen.validate(singletonList(document)).get(document).size());

        document = prepareSimpleDocument("That: true");
        assertEquals(0, redPen.validate(singletonList(document)).get(document).size());

        document = prepareSimpleDocument("That; and also this");
        assertEquals(0, redPen.validate(singletonList(document)).get(document).size());

        document = prepareSimpleDocument("That - is good");
        assertEquals(0, redPen.validate(singletonList(document)).get(document).size());

        document = prepareSimpleDocument("That / That");
        assertEquals(0, redPen.validate(singletonList(document)).get(document).size());

        document = prepareSimpleDocument("Number 1");
        assertEquals(0, redPen.validate(singletonList(document)).get(document).size());

        document = prepareSimpleDocument("The price is $123 or 100€ or ¥1000");
        assertEquals(0, redPen.validate(singletonList(document)).get(document).size());

        document = prepareSimpleDocument("100%");
        assertEquals(0, redPen.validate(singletonList(document)).get(document).size());
    }

    @Test
    public void testVoid() throws RedPenException {
        Document document = prepareSimpleDocument("");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(0, errors.get(document).size());
    }

    @Test
    public void doNotShowErrorsInCaseOfMissingDictionary() throws Exception {
        Document document = prepareSimpleDocument("test");
        config = Configuration.builder("foo")
          .addValidatorConfig(new ValidatorConfiguration(validatorName))
          .build();

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(0, errors.get(document).size());
    }
}
