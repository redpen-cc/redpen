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
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class KatakanaSpellCheckValidatorTest {
    @Test
    public void testSingleSentence() {
        KatakanaSpellCheckValidator validator
                = new KatakanaSpellCheckValidator();
        Sentence st = new Sentence("ハロー、ハロ。"
                + "あのインデクスとこのインデックス"
                , 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.validate(errors, st);
        // We do not detect "ハロー" and "ハロ" as a similar pair,
        // but "インデクス" and "インデックス".
        assertEquals(st.toString(), 1, errors.size());
    }

    @Test
    public void testMultiSentence() {
        KatakanaSpellCheckValidator validator
                = new KatakanaSpellCheckValidator();
        List<ValidationError> errors = new ArrayList<>();
        Sentence st;
        st = new Sentence("フレーズ・アナライズにバグがある", 0);
        validator.validate(errors, st);
        assertEquals(st.toString(), 0, errors.size());
        st = new Sentence("バグのあるフェーズ・アナライシス", 1);
        validator.validate(errors, st);
        // We detect a similar pair of "フレーズ・アナライズ"
        // and "フェーズ・アナライシス".
        assertEquals(st.toString(), 1, errors.size());
    }

    @Test
    public void testLoadDefaultDictionary() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("KatakanaSpellCheck"))
                .setLanguage("ja").build();

        List<Document> documents = new ArrayList<>();documents.add(
                new Document.DocumentBuilder(new JapaneseTokenizer())
                                .addSection(1)
                                .addParagraph()
                                .addSentence("あのインデクスとこのインデックス", 1)
                                .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(0, errors.get(documents.get(0)).size());
    }

}
