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
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.JapaneseTokenizer;
import cc.redpen.validator.ValidationError;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JapaneseAnchorExpressionValidatorTest {
    @Test
    void testValid() throws RedPenException {
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("JapaneseAnchorExpression"))
                .build();

        List<Document> documents = new ArrayList<>();documents.add(
                Document.builder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("1章を参照されたい。", 1))
                        .build());

        RedPen redPen = new RedPen(config);
        List<ValidationError> errors = redPen.validate(documents).get(documents.get(0));
        assertEquals(0, errors.size());
    }

    @Test
    void testValid2() throws RedPenException {
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("JapaneseAnchorExpression"))
                .build();

        List<Document> documents = new ArrayList<>();documents.add(
                Document.builder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("上の例は氷山の一角である。", 1))
                        .build());

        RedPen redPen = new RedPen(config);
        List<ValidationError> errors = redPen.validate(documents).get(documents.get(0));
        assertEquals(0, errors.size());
    }

    @Test
    void testValidNumeric() throws RedPenException {
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("JapaneseAnchorExpression").addProperty("mode", "numeric"))
                .build();

        List<Document> documents = new ArrayList<>();documents.add(
                Document.builder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("1章を参照されたい。", 1))
                        .build());

        RedPen redPen = new RedPen(config);
        List<ValidationError> errors = redPen.validate(documents).get(documents.get(0));
        assertEquals(0, errors.size());
    }

    @Test
    void testValidZenkakuNumeric() throws RedPenException {
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("JapaneseAnchorExpression").addProperty("mode", "numeric-zenkaku"))
                .build();

        List<Document> documents = new ArrayList<>();documents.add(
                Document.builder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("１章を参照されたい。", 1))
                        .build());

        RedPen redPen = new RedPen(config);
        List<ValidationError> errors = redPen.validate(documents).get(documents.get(0));
        assertEquals(0, errors.size());
    }

    @Test
    void testValidKansuji() throws RedPenException {
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("JapaneseAnchorExpression").addProperty("mode", "kansuji"))
                .build();

        List<Document> documents = new ArrayList<>();documents.add(
                Document.builder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("一章を参照されたい。", 1))
                        .build());

        RedPen redPen = new RedPen(config);
        List<ValidationError> errors = redPen.validate(documents).get(documents.get(0));
        assertEquals(0, errors.size());
    }

    @Test
    void testInvalid() throws RedPenException {
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("JapaneseAnchorExpression"))
                .build();

        List<Document> documents = new ArrayList<>();documents.add(
                Document.builder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("一章３節を参照されたい。", 1))
                        .build());

        RedPen redPen = new RedPen(config);
        List<ValidationError> errors = redPen.validate(documents).get(documents.get(0));
        assertEquals(2, errors.size());
        assertEquals("JapaneseAnchorExpression", errors.get(0).getValidatorName());
        assertEquals("JapaneseAnchorExpression", errors.get(1).getValidatorName());
    }

    @Test
    void testVoid() throws RedPenException {
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("JapaneseAnchorExpression"))
                .build();

        List<Document> documents = new ArrayList<>();documents.add(
                Document.builder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("", 1))
                        .build());

        RedPen redPen = new RedPen(config);
        List<ValidationError> errors = redPen.validate(documents).get(documents.get(0));
        assertEquals(0, errors.size());
    }
}
