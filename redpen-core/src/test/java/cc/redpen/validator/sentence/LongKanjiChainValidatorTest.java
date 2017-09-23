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
import cc.redpen.validator.Validator;
import cc.redpen.validator.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LongKanjiChainValidatorTest {

    @Test
    void testSimpleRun() throws RedPenException {
        List<Document> documents = new ArrayList<>();documents.add(
                Document.builder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("圧倒的な当事者意識を一身に浴びている。", 1))
                        .build());

        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("LongKanjiChain").addProperty("max_len", 3))
                .build();
        Validator validator = ValidatorFactory.getInstance(config.getValidatorConfigs().get(0), config);

        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(documents.get(0).getLastSection().getParagraph(0).getSentence(0));
        assertEquals(1, errors.size());
    }

    @Test
    void testVoid() throws RedPenException {
        List<Document> documents = new ArrayList<>();documents.add(
                Document.builder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("", 1))
                        .build());

        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("LongKanjiChain"))
                .build();
        Validator validator = ValidatorFactory.getInstance(config.getValidatorConfigs().get(0), config);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(documents.get(0).getLastSection().getParagraph(0).getSentence(0));
        assertEquals(0, errors.size());
    }

    @Test
    void testLoadDefaultDictionary() throws RedPenException {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("LongKanjiChain"))
                .build();

        List<Document> documents = new ArrayList<>();documents.add(
                Document.builder(new JapaneseTokenizer())
                         .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("圧倒的当事者意識を一身に浴びることによって加速された物体は亜光速で運動し、その挙動は特殊相対性理論に従う。", 1))
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(2, errors.get(documents.get(0)).size());
        assertTrue(errors.get(documents.get(0)).get(0).getMessage().contains("圧倒的当事者意識"));
        assertTrue(errors.get(documents.get(0)).get(1).getMessage().contains("特殊相対性理論"));
    }


    @Test
    void testLoadUserDictionary() throws RedPenException {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("LongKanjiChain").addProperty("list", "特殊相対性理論"))
                .build();

        List<Document> documents = new ArrayList<>();documents.add(
                Document.builder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("圧倒的当事者意識を一身に浴びることによって加速された物体は亜光速で運動し、その挙動は特殊相対性理論に従う。", 1))
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(1, errors.get(documents.get(0)).size());
    }

    /**
     * Assert not throw a exception even when there is no default dictionary.
     */
    @Test
    void testLoadNotExistingDefaultDictionary() throws RedPenException {
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("LongKanjiChain"))
                .build(); // NOTE: no dictionary for japanese or other languages whose words are not split by white space.

        List<Document> documents = new ArrayList<>();documents.add(
                Document.builder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("本日は晴天なり。", 1))
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.get(0)).size());
    }
}
