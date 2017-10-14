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
import cc.redpen.tokenizer.NeologdJapaneseTokenizer;
import cc.redpen.validator.ValidationError;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KatakanaSpellCheckValidatorTest {
    @Test
    void testSingleSentence() {
        KatakanaSpellCheckValidator validator
                = new KatakanaSpellCheckValidator();
        Sentence st = new Sentence("ハロー、ハロ。"
                + "あのインデクスとこのインデックス"
                , 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(st);
        // We do not detect "ハロー" and "ハロ" as a similar pair,
        // but "インデクス" and "インデックス".
        assertEquals(1, errors.size(), st.toString());
    }

    @Test
    void testMultiSentence() {
        KatakanaSpellCheckValidator validator
                = new KatakanaSpellCheckValidator();
        List<ValidationError> errors = new ArrayList<>();
        Sentence st;
        st = new Sentence("フレーズ・アナライズにバグがある", 0);
        validator.setErrorList(errors);
        validator.validate(st);
        assertEquals(0, errors.size(), st.toString());
        st = new Sentence("バグのあるフェーズ・アナライシス", 1);
        validator.validate(st);
        // We detect a similar pair of "フレーズ・アナライズ"
        // and "フェーズ・アナライシス".
        assertEquals(1, errors.size(), st.toString());
    }

    @Test
    void testLoadDefaultDictionary() throws RedPenException {
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("KatakanaSpellCheck"))
                .build();

        List<Document> documents = new ArrayList<>();
        documents.add(
                Document.builder(new NeologdJapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("あのインデクスとこのインデックス", 1))
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    void testDefaultSetting() throws RedPenException {
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("KatakanaSpellCheck"))
                .build();

        List<Document> documents = new ArrayList<>();
        documents.add(
                Document.builder(new NeologdJapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("あのミニマムサポートとこのミニマムサポータ", 1))
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(1, errors.get(documents.get(0)).size());
    }

    @Test
    void testSetMinimumRatio() throws RedPenException {
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("KatakanaSpellCheck").addProperty("min_ratio", "0.001"))
                .build();

        List<Document> documents = new ArrayList<>();
        documents.add(
                Document.builder(new NeologdJapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("あのミニマムサポートとこのミニマムサポータ", 1))
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    void testSetMinimumFrequency() throws RedPenException {
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("KatakanaSpellCheck").addProperty("min_freq", "0"))
                .build();

        List<Document> documents = new ArrayList<>();
        documents.add(
                Document.builder(new NeologdJapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("あのミニマムサポートとこのミニマムサポータ", 1))
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    void testLoadUserDictionary() throws RedPenException {
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("KatakanaSpellCheck").addProperty("list", "ミニマムサポート,ミニマムサポータ"))
                .build();

        List<Document> documents = new ArrayList<>();
        documents.add(
                Document.builder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("あのミニマムサポートとこのミニマムサポータ。", 1))
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    void testDisableDefaultDictionary() throws RedPenException {
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("KatakanaSpellCheck").addProperty("disable-default", "true"))
                .build();

        List<Document> documents = new ArrayList<>();documents.add(
                Document.builder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("あのインデクスとこのインデックス", 1))
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(1, errors.get(documents.get(0)).size());
    }
}
