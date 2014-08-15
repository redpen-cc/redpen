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
import cc.redpen.ValidationError;
import cc.redpen.config.Configuration;
import cc.redpen.config.Symbol;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.distributor.FakeResultDistributor;
import cc.redpen.model.DocumentCollection;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class InvalidSymbolValidatorTest {
    @Test
    public void testWithInvalidSymbol() throws RedPenException {
        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument("")
                .addSection(1, new ArrayList<>())
                .addParagraph()
                .addSentence("わたしはカラオケが大好き！", 1)
                .build();

        Configuration conf = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("InvalidSymbol"))
                .setSymbolTable("en")
                .setSymbol(new Symbol("EXCLAMATION_MARK", "!", "！"))
                .build();

        RedPen validator = new RedPen.Builder()
                .setConfiguration(conf)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        List<ValidationError> errors = validator.check(documents);
        assertEquals(1, errors.size());
    }

    @Test
    public void testWithoutInvalidSymbol() throws RedPenException {
        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument("")
                .addSection(1, new ArrayList<>())
                .addParagraph()
                .addSentence("I like Karaoke", 1)
                .build();

        Configuration conf = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("InvalidSymbol"))
                .setSymbolTable("en")
                .setSymbol(new Symbol("EXCLAMATION_MARK", "!", "！"))
                .build();

        RedPen validator = new RedPen.Builder()
                .setConfiguration(conf)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        List<ValidationError> errors = validator.check(documents);
        assertEquals(0, errors.size());
    }

    @Test
    public void testWithoutMultipleInvalidSymbol() throws RedPenException {

        DocumentCollection documents = new DocumentCollection.Builder()
                .addDocument("")
                .addSection(1, new ArrayList<>())
                .addParagraph()
                .addSentence("わたしは、カラオケが大好き！", 1) // NOTE: two invalid symbols
                .build();

        Configuration conf = new Configuration.Builder()
                .addValidatorConfig(new ValidatorConfiguration("InvalidSymbol"))
                .setSymbolTable("en")
                .setSymbol(new Symbol("EXCLAMATION_MARK", "!", "！"))
                .setSymbol(new Symbol("COMMA", ",", "、"))
                .build();

        RedPen validator = new RedPen.Builder()
                .setConfiguration(conf)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        List<ValidationError> errors = validator.check(documents);
        assertEquals(2, errors.size());
    }
}
