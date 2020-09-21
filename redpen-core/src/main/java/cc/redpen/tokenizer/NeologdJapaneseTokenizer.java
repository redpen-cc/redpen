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
package cc.redpen.tokenizer;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.AttributeFactory;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseTokenizer;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.BaseFormAttribute;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.InflectionAttribute;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.ReadingAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NeologdJapaneseTokenizer implements RedPenTokenizer {

    private JapaneseTokenizer tokenizer;

    public NeologdJapaneseTokenizer() {
        this.tokenizer = new JapaneseTokenizer(AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY,
                null,
                false,
                JapaneseTokenizer.Mode.NORMAL);
    }

    @Override
    public List<TokenElement> tokenize(String content) {
        List<TokenElement> tokens = new ArrayList<>();
        try {
            for (TokenElement token : kuromojineologd(content)) {
                tokens.add(token);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tokens;
    }

    private List<TokenElement> kuromojineologd(String src) throws IOException {
        tokenizer.setReader(new StringReader(src));
        List<TokenElement> tokens = new ArrayList<>();
        BaseFormAttribute baseAttr = tokenizer.addAttribute(BaseFormAttribute.class);
        CharTermAttribute charAttr = tokenizer.addAttribute(CharTermAttribute.class);
        PartOfSpeechAttribute posAttr = tokenizer.addAttribute(PartOfSpeechAttribute.class);
        ReadingAttribute readAttr = tokenizer.addAttribute(ReadingAttribute.class);
        OffsetAttribute offsetAttr  = tokenizer.addAttribute(OffsetAttribute.class);
        InflectionAttribute inflectionAttr = tokenizer.addAttribute(InflectionAttribute.class);
        tokenizer.reset();
        while (tokenizer.incrementToken()) {
            String surface = charAttr.toString();
            tokens.add(new TokenElement(surface,
                    getTagList(posAttr, inflectionAttr),
                    offsetAttr.startOffset(),
                    readAttr.getReading(),
                    baseAttr.getBaseForm()
            ));
        }
        tokenizer.close();
        return tokens;
    }

    private List<String> getTagList(PartOfSpeechAttribute posAttr, InflectionAttribute inflectionAttr) {
        List<String> posList = new ArrayList<>();
        posList.addAll(Arrays.asList(posAttr.getPartOfSpeech().split("-")));
        String form = inflectionAttr.getInflectionForm() == null ? "*" : inflectionAttr.getInflectionForm();
        String type = inflectionAttr.getInflectionType() == null ? "*" : inflectionAttr.getInflectionType();
        posList.add(type);
        posList.add(form);
        return posList;
    }
}
