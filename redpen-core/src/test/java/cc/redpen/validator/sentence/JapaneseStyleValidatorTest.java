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
import cc.redpen.parser.DocumentParser;
import cc.redpen.parser.SentenceExtractor;
import cc.redpen.tokenizer.JapaneseTokenizer;
import cc.redpen.validator.ValidationError;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JapaneseStyleValidatorTest {
    @Test
    public void mixedtStyles() throws RedPenException {
        String sampleText =
                "今日はいい天気ですね。\n" +
                "昨日は雨だったのだが、持ち直した。\n" +
                "明日もいい天気だといいですね。";
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(
                        new ValidatorConfiguration("JapaneseStyle"))
                .setLanguage("ja")
                .build();
        DocumentParser parser = DocumentParser.MARKDOWN;
        List<Document> documents = new ArrayList<>();
        Document document  = parser.parse(sampleText, new SentenceExtractor(config.getSymbolTable()),
                config.getTokenizer());
        documents.add(document);

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);

        Assert.assertEquals(1, errors.get(documents.get(0)).size());
        Assert.assertEquals(2, errors.get(documents.get(0)).get(0).getLineNumber());
        Assert.assertEquals(7, errors.get(documents.get(0)).get(0).getStartPosition().get().offset);
        Assert.assertEquals(10, errors.get(documents.get(0)).get(0).getEndPosition().get().offset);
        Assert.assertEquals("JapaneseStyle", errors.get(documents.get(0)).get(0).getValidatorName());
    }

    @Test
    public void desuMasuStyle() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("JapaneseStyle"))
                .setLanguage("ja").build();

        List<Document> documents = new ArrayList<>();
        documents.add(
                new Document.DocumentBuilder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence("今日はいい天気ですね。", 1)
                        .addSentence("昨日は雨だったのですが、持ち直しました。", 2)
                        .addSentence("明日もいい天気だといいですね。", 3)
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void dearuStyle() throws RedPenException {
        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("JapaneseStyle"))
                .setLanguage("ja").build();

        List<Document> documents = new ArrayList<>();
        documents.add(
                new Document.DocumentBuilder(new JapaneseTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence("今日はいい天気である。", 1)
                        .addSentence("昨日は雨だったのであったが、持ち直した。", 2)
                        .addSentence("明日もいい天気だとに期待する。", 3)
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        Assert.assertEquals(0, errors.get(documents.get(0)).size());
    }
}
