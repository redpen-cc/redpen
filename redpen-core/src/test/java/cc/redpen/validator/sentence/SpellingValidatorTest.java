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
package cc.redpen.validator.sentence;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;
import cc.redpen.validator.ValidatorFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class SpellingValidatorTest {

    @Test
    public void testValidate() throws Exception {
        List<Document> documents = new ArrayList<>();
        documents.add(new Document.DocumentBuilder()
                .addSection(1)
                .addParagraph()
                .addSentence("this iz a pen", 1)
                .build());

        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("Spelling").addAttribute("list", "this,a,pen"))
                .setLanguage("en").build();
        Validator validator = ValidatorFactory.getInstance(config.getValidatorConfigs().get(0), config.getSymbolTable());

        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(documents.get(0).getLastSection().getParagraph(0).getSentence(0));
        assertEquals(1, errors.size());
    }

    @Test
    public void testLoadDefaultDictionary() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("Spelling"))
                .setLanguage("en").build();

        List<Document> documents = new ArrayList<>();
        documents.add(new Document.DocumentBuilder()
                .addSection(1)
                .addParagraph()
                .addSentence("this iz goody", 1)
                .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(1, errors.get(documents.get(0)).size());
    }

    @Test
    public void testUpperCase() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("Spelling"))
                .setLanguage("en").build();

        List<Document> documents = new ArrayList<>();
        documents.add(new Document.DocumentBuilder()
                .addSection(1)
                .addParagraph()
                .addSentence("This iz goody", 1)
                .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(1, errors.get(documents.get(0)).size());
    }


    @Test
    public void testSkipCharacterCase() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("Spelling"))
                .setLanguage("en").build();

        List<Document> documents = new ArrayList<>();
        documents.add(new Document.DocumentBuilder()
                .addSection(1)
                .addParagraph()
                .addSentence("That is true, but there is a condition", 1)
                .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void testUserSkipList() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("Spelling").addAttribute("list", "abeshi,baz"))
                .setLanguage("en").build();

        List<Document> documents = new ArrayList<>();
        documents.add(new Document.DocumentBuilder()
                .addSection(1)
                .addParagraph()
                .addSentence("Abeshi is a word used in a comic.", 1)
                .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void testEndPeriod() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("Spelling"))
                .setLanguage("en").build();

        List<Document> documents = new ArrayList<>();
        documents.add(new Document.DocumentBuilder()
                .addSection(1)
                .addParagraph()
                .addSentence("That is true.", 1)
                .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void testVoid() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("Spelling"))
                .setLanguage("en").build();

        List<Document> documents = new ArrayList<>();
        documents.add(new Document.DocumentBuilder()
                .addSection(1)
                .addParagraph()
                .addSentence("", 1)
                .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.get(0)).size());
    }
}
