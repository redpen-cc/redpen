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
import cc.redpen.config.Symbol;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.parser.DocumentParser;
import cc.redpen.parser.LineOffset;
import cc.redpen.parser.SentenceExtractor;
import cc.redpen.validator.BaseValidatorTest;
import cc.redpen.validator.ValidationError;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cc.redpen.config.SymbolType.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class SymbolWithSpaceValidatorTest extends BaseValidatorTest {

    public SymbolWithSpaceValidatorTest() {
        super("SymbolWithSpace");
    }

    @Test
    public void testNotNeedSpace() throws RedPenException {
        Document document = prepareSimpleDocument("I like apple/orange");

        config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SymbolWithSpace"))
                .addSymbol(new Symbol(SLASH, '/'))
                .build();

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(0, errors.get(document).size());
    }

    @Test
    public void testNeedAfterSpace() throws RedPenException {
        Document document = prepareSimpleDocument("I like her:yes it is");

        config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SymbolWithSpace"))
                .addSymbol(new Symbol(COLON, ':', "", false, true))
                .build();

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(1, errors.get(document).size());
        assertEquals("Need whitespace after symbol \":\".", errors.get(document).get(0).getMessage());
        assertEquals(10, errors.get(document).get(0).getStartPosition().get().offset);
    }

    @Test
    public void testDoNotNeedAfterSpaceAtTheEndOfSentence() throws RedPenException {
        Document document = prepareSimpleDocument("Hello (world).");

        config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SymbolWithSpace"))
                .addSymbol(new Symbol(RIGHT_PARENTHESIS, ')', "", false, true))
                .build();

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(0, errors.get(document).size());
    }

    @Test
    public void testNeedBeforeSpace() throws RedPenException {
        Document document = prepareSimpleDocument("I like her(Nancy) very much.");

        config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SymbolWithSpace"))
                .addSymbol(new Symbol(LEFT_PARENTHESIS, '(', "", true, false))
                .build();

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(1, errors.get(document).size());
        assertEquals("Need whitespace before symbol \"(\".", errors.get(document).get(0).getMessage());
        assertEquals(10, errors.get(document).get(0).getStartPosition().get().offset);
    }

    @Test
    public void testNeedSpaceInMultiplePosition() throws RedPenException {
        Document document = prepareSimpleDocument("I like her(Nancy)very much.");

        config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SymbolWithSpace"))
                .addSymbol(new Symbol(LEFT_PARENTHESIS, '(', "", true, false))
                .addSymbol(new Symbol(RIGHT_PARENTHESIS, ')', "", false, true))
                .build();

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(2, errors.get(document).size());
        assertEquals(asList("Need whitespace after symbol \")\".", "Need whitespace before symbol \"(\"."),
          errors.get(document).stream().map(ValidationError::getMessage).sorted().collect(toList()));
    }

    @Test
    public void testReturnOnlyOneForHitBothBeforeAndAfter() throws RedPenException {
        Document document = prepareSimpleDocument("I like 1*10.");

        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SymbolWithSpace"))
                .addSymbol(new Symbol(ASTERISK, '*', "", true, true))
                .build();

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(1, errors.get(document).size());
        assertEquals("Need whitespace before and after symbol \"*\".", errors.get(document).get(0).getMessage());
    }

    @Test
    public void testErrorBeforePosition() throws RedPenException {
        String sampleText = "I like her(Nancy) very much.";
        Configuration configuration = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SymbolWithSpace"))
                .build();
        DocumentParser parser = DocumentParser.MARKDOWN;
        List<Document> documents = new ArrayList<>();
        Document document  = parser.parse(
                sampleText, new SentenceExtractor(configuration.getSymbolTable()),
                configuration.getTokenizer());
        documents.add(document);

        RedPen redPen = new RedPen(configuration);
        List<ValidationError> errors = redPen.validate(documents).get(documents.get(0));
        assertEquals(1, errors.size());
        assertEquals("SymbolWithSpace", errors.get(0).getValidatorName());
        assertEquals(new LineOffset(1, 10), errors.get(0).getStartPosition().get());
        assertEquals(new LineOffset(1, 11), errors.get(0).getEndPosition().get());
    }

    @Test
    public void testErrorAfterPosition() throws RedPenException {
        String sampleText = "I like her (Nancy)very much.";
        Configuration configuration = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SymbolWithSpace"))
                .build();
        DocumentParser parser = DocumentParser.MARKDOWN;
        List<Document> documents = new ArrayList<>();
        Document document  = parser.parse(
                sampleText, new SentenceExtractor(configuration.getSymbolTable()),
                configuration.getTokenizer());
        documents.add(document);

        RedPen redPen = new RedPen(configuration);
        List<ValidationError> errors = redPen.validate(documents).get(documents.get(0));
        assertEquals(1, errors.size());
        assertEquals("SymbolWithSpace", errors.get(0).getValidatorName());
        assertEquals(new LineOffset(1, 17), errors.get(0).getStartPosition().get());
        assertEquals(new LineOffset(1, 18), errors.get(0).getEndPosition().get());
    }
}
