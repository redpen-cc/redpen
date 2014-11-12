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
import cc.redpen.model.Document;
import cc.redpen.model.DocumentCollection;
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.JapaneseTokenizer;
import cc.redpen.validator.ValidationError;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class InvalidExpressionValidatorTest {

    @Test
    public void testSimpleRun() {
        InvalidExpressionValidator validator = new InvalidExpressionValidator();
        validator.addInvalid("may");
        List<ValidationError> errors = validator.validate(new Sentence("The experiments may be true.", 0));
        assertEquals(1, errors.size());
    }

    @Test
    public void testVoid() {
        InvalidExpressionValidator validator = new InvalidExpressionValidator();
        validator.addInvalid("may");
        List<ValidationError> errors = validator.validate(new Sentence("", 0));
        assertEquals(0, errors.size());
    }

    @Test
    public void testLoadDefaultDictionary() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("InvalidExpression"))
                .setLanguage("en").build();

        DocumentCollection documents = new DocumentCollection.Builder().addDocument(
                new Document.DocumentBuilder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence(
                                "You know. He is a super man.",
                                1)
                        .build()).build();

        RedPen redPen = new RedPen.RedPenBuilder()
                .setConfiguration(config)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(1, errors.get(documents.getDocument(0)).size());
    }

    @Test
    public void testLoadJapaneseDefaultDictionary() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("InvalidExpression"))
                .setLanguage("ja").build();

        DocumentCollection documents = new DocumentCollection.Builder().addDocument(
                new Document.DocumentBuilder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence("明日地球が滅亡するってマジですか。", 1)
                        .build()).build();

        RedPen redPen = new RedPen.RedPenBuilder()
                .setConfiguration(config)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(1, errors.get(documents.getDocument(0)).size());
    }


    @Test
    public void testLoadJapaneseInvalidList() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("InvalidExpression").addAttribute("list", "うふぉ,ガチ"))
                .setLanguage("ja").build();

        DocumentCollection documents = new DocumentCollection.Builder().addDocument(
                new Document.DocumentBuilder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence("うふぉっ本当ですか？", 1)
                        .build()).build();

        RedPen redPen = new RedPen.RedPenBuilder()
                .setConfiguration(config)
                .setResultDistributor(new FakeResultDistributor())
                .build();

        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(1, errors.get(documents.getDocument(0)).size());
    }
}
