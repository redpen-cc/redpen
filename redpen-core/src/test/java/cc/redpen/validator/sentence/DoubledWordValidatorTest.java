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

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.validator.BaseValidatorTest;
import cc.redpen.validator.ValidationError;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static junit.framework.Assert.assertEquals;

public class DoubledWordValidatorTest extends BaseValidatorTest {

    public DoubledWordValidatorTest() {
        super("DoubledWord");
    }

    @Test
    public void testDoubledWord() throws RedPenException {
        Document document = prepareSimpleDocument("the good item is a good example.");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(1, errors.get(document).size());
        assertEquals("Found repeated word \"good\".", errors.get(document).get(0).getMessage());
    }

    @Test
    public void testDoubledWordWithDifferentCase() throws RedPenException {
        Document document = prepareSimpleDocument("Good item is a good example.");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(1, errors.get(document).size());
        assertEquals("Found repeated word \"good\".", errors.get(document).get(0).getMessage());
    }

    @Test
    public void testDoubledSkipListWord() throws RedPenException {
        Document document = prepareSimpleDocument("That is true, as far as I know.");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(0, errors.get(document).size());
    }

    @Test
    public void testDoubledUserDefinedSkipWord() throws RedPenException {
        config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration(validatorName)
                        .addProperty("list", "redpen,tool"))
                .build();

        Document document = prepareSimpleDocument("RedPen is RedPen right?");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(0, errors.get(document).size());
    }

    @Test
    public void testDoubledUserDefinedSkipWordWithoutNormalization() throws RedPenException {
        config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration(validatorName).addProperty("list", "RedPen,Tool"))
                .build();

        Document document = prepareSimpleDocument("redPen is redPen right?");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(0, errors.get(document).size());
    }

    @Test
    public void testDoubledWordInJapaneseSentence() throws RedPenException {
        config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration(validatorName))
                .build();

        Document document = prepareSimpleDocument("それは真実であり，それが正しい");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(1, errors.get(document).size());
    }
}
