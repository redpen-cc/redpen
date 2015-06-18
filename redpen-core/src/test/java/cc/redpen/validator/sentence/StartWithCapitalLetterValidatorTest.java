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
import cc.redpen.tokenizer.JapaneseTokenizer;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;
import cc.redpen.validator.ValidatorFactory;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class StartWithCapitalLetterValidatorTest {
    @Test
    public void testDetectStartWithSmallCharacter() throws RedPenException {
        List<Document> documents = new ArrayList<>();
        documents.add(new Document.DocumentBuilder()
                .addSection(1)
                .addParagraph()
                .addSentence("this is it.", 1)
                .build());
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("StartWithCapitalLetter"))
                .setLanguage("en").build();
        Validator validator = ValidatorFactory.getInstance(config.getValidatorConfigs().get(0), config.getSymbolTable());
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(documents.get(0).getLastSection().getParagraph(0).getSentence(0));
        assertEquals(1, errors.size());
    }

    @Test
    public void testDetectStartWithCapitalCharacter() throws RedPenException {
        List<Document> documents = new ArrayList<>();
        documents.add(new Document.DocumentBuilder()
                .addSection(1)
                .addParagraph()
                .addSentence("This is it.", 1)
                .build());
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("StartWithCapitalLetter"))
                .setLanguage("en").build();
        Validator validator = ValidatorFactory.getInstance(config.getValidatorConfigs().get(0), config.getSymbolTable());
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(documents.get(0).getLastSection().getParagraph(0).getSentence(0));
        assertEquals(0, errors.size());
    }

    @Test
    public void testStartWithElementOfWhiteList() throws RedPenException {
        List<Document> documents = new ArrayList<>();
        documents.add(new Document.DocumentBuilder()
                .addSection(1)
                .addParagraph()
                .addSentence("iPhone is a mobile computer.", 1)
                .build());
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("StartWithCapitalLetter").addAttribute("list", "iPhone"))
                .setLanguage("en").build();
        Validator validator = ValidatorFactory.getInstance(config.getValidatorConfigs().get(0), config.getSymbolTable());
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(documents.get(0).getLastSection().getParagraph(0).getSentence(0));
        assertEquals(0, errors.size());
    }

    @Test
    public void testStartWithWhiteListItemInJapaneseSentence() throws RedPenException {
        List<Document> documents = new ArrayList<>();
        documents.add(new Document.DocumentBuilder(new JapaneseTokenizer())
                .addSection(1)
                .addParagraph()
                .addSentence("iPhone はカッコイイ．", 1)
                .build());
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("StartWithCapitalLetter").addAttribute("list", "iPhone"))
                .setLanguage("en").build();
        Validator validator = ValidatorFactory.getInstance(config.getValidatorConfigs().get(0), config.getSymbolTable());
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(documents.get(0).getLastSection().getParagraph(0).getSentence(0));
        assertEquals(0, errors.size());
    }

    @Test
    public void testStartWithWhiteSpaceAndThenItemOfWhiteList() throws RedPenException {
        List<Document> documents = new ArrayList<>();
        documents.add(new Document.DocumentBuilder()
                .addSection(1)
                .addParagraph()
                .addSentence(" iPhone is a mobile computer.", 1)
                .build());
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("StartWithCapitalLetter").addAttribute("list", "iPhone"))
                .setLanguage("en").build();
        Validator validator = ValidatorFactory.getInstance(config.getValidatorConfigs().get(0), config.getSymbolTable());
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(documents.get(0).getLastSection().getParagraph(0).getSentence(0));
        assertEquals(0, errors.size());
    }

    @Test
    public void testVoid() throws RedPenException {
        List<Document> documents = new ArrayList<>();
        documents.add(new Document.DocumentBuilder()
                .addSection(1)
                .addParagraph()
                .addSentence("", 1)
                .build());
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("StartWithCapitalLetter").addAttribute("list", "iPhone"))
                .setLanguage("en").build();
        Validator validator = ValidatorFactory.getInstance(config.getValidatorConfigs().get(0), config.getSymbolTable());
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(documents.get(0).getLastSection().getParagraph(0).getSentence(0));
        assertEquals(0, errors.size());
    }

    @Test
    public void testLoadDefaultDictionary() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("StartWithCapitalLetter"))
                .setLanguage("en").build();

        List<Document> documents = new ArrayList<>();
        documents.add(new Document.DocumentBuilder()
                .addSection(1)
                .addParagraph()
                .addSentence("mixi is a Japanese company.", 1)
                .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(0, errors.get(documents.get(0)).size());
    }


    @Test
    public void testDetectStartWithSmallCharacterInSecondSentence() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("StartWithCapitalLetter"))
                .setLanguage("en").build();

        List<Document> documents = new ArrayList<>();
        documents.add(new Document.DocumentBuilder()
                .addSection(1)
                .addParagraph()
                .addSentence("This is true.", 1)
                .addSentence(" that is also true.", 1)
                .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(1, errors.get(documents.get(0)).size());
    }
}
