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
package cc.redpen.validator.document;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.WhiteSpaceTokenizer;
import cc.redpen.validator.ValidationError;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnexpandedAcronymValidatorTest {

    @Test
    void testDocument() throws RedPenException {
        List<Document> documents = new ArrayList<>();
        documents.add(Document.builder(new WhiteSpaceTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("When it comes to the Subject Of Cake (the sweet and delicious baked delicacy), one should" +
                                " always remember (or at least consider)" +
                                " this foodstuff's effect on one's ever-expanding waistline.", 1))
                        .addSentence(new Sentence("Now we know what SOC stands for but there is no mention of TTP.", 2))
                        .addSentence(new Sentence("The acronym CPU stands for Central Processing Unit (CPU).", 3))
                        .addSentence(new Sentence("The acronym AAAS is the American Association for the Advancement of Science.", 4))
                        .addSentence(new Sentence("ABC can stand form the Australian Broadcasting Commission, but HELLO is just a capitalized word.", 5))
                        .build());

        Configuration config = Configuration.builder()
                                       .addValidatorConfig(new ValidatorConfiguration("UnexpandedAcronym"))
                                       .build();

        RedPen redPen = new RedPen(config);
        List<ValidationError> errors = redPen.validate(documents).get(documents.get(0));
        assertEquals(1, errors.size());
        assertEquals(2, errors.get(0).getLineNumber());
    }

    @Test
    void testSimpleSentence() throws Exception {
        List<Document> documents = new ArrayList<>();
        documents.add(
                Document.builder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("The JSON data is a output from the command.", 1))
                        .build());

        Configuration config;
        config = Configuration.builder()
                         .addValidatorConfig(new ValidatorConfiguration("UnexpandedAcronym"))
                         .build();

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(1, errors.get(documents.get(0)).size());
    }

    @Test
    void testLoadSkipList() throws Exception {
        List<Document> documents = new ArrayList<>();documents.add(
                Document.builder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("The output is JSON format.", 1))
                        .build());

        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("UnexpandedAcronym").addProperty("list", "JSON"))
                .build();

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.get(0)).size());
    }
}
