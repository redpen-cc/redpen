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
import cc.redpen.parser.DocumentParser;
import cc.redpen.parser.LineOffset;
import cc.redpen.parser.SentenceExtractor;
import cc.redpen.tokenizer.NeologdJapaneseTokenizer;
import cc.redpen.validator.ValidationError;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EndOfSentenceValidatorTest {
    private EndOfSentenceValidator validator = new EndOfSentenceValidator();

    @BeforeEach
    void setUp() throws Exception {
        validator.preInit(new ValidatorConfiguration("EndOfSentence"), Configuration.builder().build());
    }

    @Test
    void testInvalidEndOfSentence() {
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(new Sentence("He said \"that is right\".", 0));
        assertEquals(1, errors.size());
    }

    @Test
    void testValidEndOfSentence() {
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(new Sentence("He said \"that is right.\"", 0));
        assertEquals(0, errors.size());
    }

    @Test
    void testInValidEndOfSentenceWithQuestionMark() {
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(new Sentence("He said \"Is it right\"?", 0));
        assertEquals(1, errors.size());
    }

    @Test
    void testVoid() {
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(new Sentence("", 0));
        assertEquals(0, errors.size());
    }

    @Test
    void testJapaneseInvalidEndOfSentence() throws RedPenException {
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("EndOfSentence"))
                .build();

        List<Document> documents = new ArrayList<>();documents.add(
                Document.builder(new NeologdJapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("彼は言った，“今日は誕生日”。", 1))
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(1, errors.get(documents.get(0)).size());
    }

    @Test
    void testErrorPosition() throws RedPenException {
        String sampleText = "He said \"that is right\".";
        Configuration configuration = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("EndOfSentence"))
                .build();
        DocumentParser parser = DocumentParser.MARKDOWN;
        List<Document> documents = new ArrayList<>();
        Document document  = parser.parse(sampleText, new SentenceExtractor(configuration.getSymbolTable()),
                configuration.getTokenizer());
        documents.add(document);

        RedPen redPen = new RedPen(configuration);
        List<ValidationError> errors = redPen.validate(documents).get(documents.get(0));
        assertEquals(1, errors.size());
        assertEquals("EndOfSentence", errors.get(0).getValidatorName());
        assertEquals(new LineOffset(1, 22), errors.get(0).getStartPosition().get());
        assertEquals(new LineOffset(1, 23), errors.get(0).getEndPosition().get());
    }
}
