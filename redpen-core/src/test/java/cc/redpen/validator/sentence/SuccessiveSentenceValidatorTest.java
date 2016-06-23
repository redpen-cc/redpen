/*
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
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.model.Sentence;
import cc.redpen.validator.ValidationError;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class SuccessiveSentenceValidatorTest {
    @Test
    public void testIdenticalSentences() throws Exception {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SuccessiveSentence"))
                .build();

        List<Document> documents = new ArrayList<>();
        documents.add(
                Document.builder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("This is a pen.", 1))
                        .addSentence(new Sentence("This is a pen.", 2))
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(1, errors.get(documents.get(0)).size());
    }


    @Test
    public void testSimilarButNotSame() throws Exception {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SuccessiveSentence"))
                .build();

        List<Document> documents = new ArrayList<>();
        documents.add(
                Document.builder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("This is a pen.", 1))
                        .addSentence(new Sentence("That is another pen.", 2))
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void testChangeProperty() throws Exception {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SuccessiveSentence").addProperty("dist", 10))
                .build();

        List<Document> documents = new ArrayList<>();
        documents.add(
                Document.builder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("This is a pen.", 1))
                        .addSentence(new Sentence("That is another pen.", 2))
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(1, errors.get(documents.get(0)).size());
    }

    @Test
    public void testIdenticalButDifferentLetterCases() throws Exception {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SuccessiveSentence"))
                .build();

        List<Document> documents = new ArrayList<>();
        documents.add(
                Document.builder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("THIS IS A PEN.", 1))
                        .addSentence(new Sentence("This is a pen.", 2))
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(1, errors.get(documents.get(0)).size());
    }


}
