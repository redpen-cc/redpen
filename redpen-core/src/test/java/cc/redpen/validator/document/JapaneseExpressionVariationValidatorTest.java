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
import cc.redpen.validator.BaseValidatorTest;
import cc.redpen.validator.ValidationError;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class JapaneseExpressionVariationValidatorTest extends BaseValidatorTest {
    JapaneseExpressionVariationValidatorTest() {
        super("JapaneseExpressionVariation");
    }

    @Test
    void detectSameReadingsInJapaneseCharacters() throws RedPenException {
        config = Configuration.builder("ja")
                         .addValidatorConfig(new ValidatorConfiguration(validatorName))
                         .build();

        Document document = prepareSimpleDocument("之は山です。これは川です。");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(1, errors.get(document).size());
    }

    @Test
    void detectSameReadingsInJapaneseCharactersInDefaultDictionary() throws RedPenException {
        config = Configuration.builder("ja")
                         .addValidatorConfig(new ValidatorConfiguration(validatorName))
                         .build();

        Document document = prepareSimpleDocument("nodeは英語です。ノードはカタカナです。");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(1, errors.get(document).size());
    }

    @Test
    void detectSameReadingsInJapaneseCharactersInDefaultDictionaryWithUpperCase() throws RedPenException {
        config = Configuration.builder("ja")
                         .addValidatorConfig(new ValidatorConfiguration(validatorName))
                         .build();

        Document document = prepareSimpleDocument("Nodeは英語です。ノードはカタカナです。");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(1, errors.get(document).size());
    }

    @Test
    void detectSameAlphabecicalReadings() throws RedPenException {
        config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration(validatorName))
                .build();

        Document document = prepareSimpleDocument("このExcelはあのエクセルとは違います。");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(1, errors.get(document).size());
    }

    @Test
    void detectNormalizedReadings() throws RedPenException {
        config = Configuration.builder("ja")
                         .addValidatorConfig(new ValidatorConfiguration(validatorName))
                         .build();

        Document document = prepareSimpleDocument("このインデックスはあのインデクスとは違います。");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(1, errors.get(document).size());
    }

    @Test
    void detectNormalizedReadings2() throws RedPenException {
        config = Configuration.builder("ja")
                         .addValidatorConfig(new ValidatorConfiguration(validatorName))
                         .build();

        Document document = prepareSimpleDocument("このヴェトナムはあのベトナムとは違います。");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(1, errors.get(document).size());
    }

    @Test
    void detectSameReadingsInConcatinatedJapaneseWord() throws RedPenException {
        config = Configuration.builder("ja")
                         .addValidatorConfig(new ValidatorConfiguration(validatorName))
                         .build();

        Document document = prepareSimpleDocument("身分証明書は紙です。身分証明所は間違い。");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(1, errors.get(document).size());
    }

    @Test
    void detectMultipleSameReadings() throws RedPenException {
        config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration(validatorName))
                .build();

        Document document = prepareSimpleDocument("このExcelはあのエクセルともこのエクセルとも違います。");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(1, errors.get(document).size());
    }
}
