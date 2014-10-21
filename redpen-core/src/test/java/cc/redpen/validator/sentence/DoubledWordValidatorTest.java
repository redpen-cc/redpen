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

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.distributor.FakeResultDistributor;
import cc.redpen.model.DocumentCollection;
import cc.redpen.validator.ValidationError;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class DoubledWordValidatorTest {
    @Test
    public void testDoubledWord() throws RedPenException {
        Configuration config = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("DoubledWord"))
                .setLanguage("en").build();

        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument("")
                .addSection(1)
                .addParagraph()
                .addSentence(
                        "the good item is a good example.",
                        1)
                .build();

        RedPen validator = new RedPen.Builder()
                .setConfiguration(config)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        List<ValidationError> errors = validator.check(documents);
        assertEquals(1, errors.size());
    }

    @Test
    public void testDoubledSkipListWord() throws RedPenException {
        Configuration config = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("DoubledWord"))
                .setLanguage("en").build();

        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument("")
                .addSection(1)
                .addParagraph()
                .addSentence(
                        "That is true, as far as I know.",
                        1)
                .build();

        RedPen validator = new RedPen.Builder()
                .setConfiguration(config)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        List<ValidationError> errors = validator.check(documents);
        assertEquals(0, errors.size());
    }

    @Test
    public void testDoubledWordInJapaneseSentence() throws RedPenException {
        Configuration config = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("DoubledWord"))
                .setLanguage("ja").build();

        DocumentCollection documents = new DocumentCollection.Builder("ja") // TODO fix redundant language settings
                .addDocument("")
                .addSection(1)
                .addParagraph()
                .addSentence(
                        "それは真実であり，それが正しい",
                        1)
                .build();

        RedPen validator = new RedPen.Builder()
                .setConfiguration(config)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        List<ValidationError> errors = validator.check(documents);
        assertEquals(1, errors.size());
    }
}
