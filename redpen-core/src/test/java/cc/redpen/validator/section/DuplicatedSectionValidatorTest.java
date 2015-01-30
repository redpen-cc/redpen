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
package cc.redpen.validator.section;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.validator.ValidationError;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DuplicatedSectionValidatorTest {

    @Test
    public void testDetectDuplicatedSection() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("DuplicatedSection"))
                .setLanguage("en").build();

        List<Document> documents = new ArrayList<>();
        documents.add(
                new Document.DocumentBuilder()
                        .addSection(1)
                        .addSectionHeader("this is header")
                        .addParagraph()
                        .addSentence("he is a super man.", 1)
                        .addSection(1)
                        .addSectionHeader("this is header 2")
                        .addParagraph()
                        .addSentence("he is a super man.", 2)
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(2, errors.get(documents.get(0)).size());
    }


    @Test
    public void testDetectNonDuplicatedSection() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("DuplicatedSection"))
                .setLanguage("en").build();

        List<Document> documents = new ArrayList<>();
        documents.add(
                new Document.DocumentBuilder()
                        .addSection(1)
                        .addSectionHeader("foobar")
                        .addParagraph()
                        .addSentence("baz baz baz", 1)
                        .addSection(1)
                        .addSectionHeader("aho")
                        .addParagraph()
                        .addSentence("zoo zoo zoo", 2)
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void testDetectDuplicatedSectionWithSameHeader() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("DuplicatedSection"))
                .setLanguage("en").build();

        List<Document> documents = new ArrayList<>();
        documents.add(
                new Document.DocumentBuilder()
                        .addSection(1)
                        .addSectionHeader("this is header.")
                        .addParagraph()
                        .addSentence("he is a super man.", 1)
                        .addSection(1)
                        .addSectionHeader("this is header.")
                        .addParagraph()
                        .addSentence("he is a super man.", 2)
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(2, errors.get(documents.get(0)).size());
    }

    @Test
    public void testDetectNonDuplicatedSectionWithLowThreshold() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("DuplicatedSection").addAttribute("threshold", "0.0"))
                .setLanguage("en").build();

        List<Document> documents = new ArrayList<>();
        // create two section which contains only one same word, "baz"
        documents.add(
        new Document.DocumentBuilder()
                .addSection(1)
                .addSectionHeader("foobar")
                .addParagraph()
                .addSentence("baz foo foo", 1)
                .addSection(1)
                .addSectionHeader("aho")
                .addParagraph()
                .addSentence("baz zoo zoo", 2)
                .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(2, errors.get(documents.get(0)).size());
    }
}
