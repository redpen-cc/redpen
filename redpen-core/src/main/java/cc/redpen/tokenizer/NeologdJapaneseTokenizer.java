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

import com.atilika.kuromoji.unidic.Tokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseAnalyzer;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseTokenizer;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.BaseFormAttribute;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.ReadingAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class NeologdJapaneseTokenizer implements RedPenTokenizer {

    private Tokenizer tokenizer;
    private JapaneseAnalyzer analyzer;

    public NeologdJapaneseTokenizer() {
        this.tokenizer = new Tokenizer();
        this.analyzer = new JapaneseAnalyzer(null, JapaneseTokenizer.DEFAULT_MODE, null, new HashSet<String>());
    }

    @Override
    public List<TokenElement> tokenize(String content) {
        List<TokenElement> tokens = new ArrayList<>();
        try {
            for (TokenElement token : kuromojineologd(content)) {
                System.out.println(token);
                tokens.add(token);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tokens;
    }

    private List<TokenElement> kuromojineologd(String src) throws IOException {
        List<TokenElement> tokens = new ArrayList<>();
        try (TokenStream tokenStream = analyzer.tokenStream("", new StringReader(src))) {
            BaseFormAttribute baseAttr = tokenStream.addAttribute(BaseFormAttribute.class);
            CharTermAttribute charAttr = tokenStream.addAttribute(CharTermAttribute.class);
            PartOfSpeechAttribute posAttr = tokenStream.addAttribute(PartOfSpeechAttribute.class);
            ReadingAttribute readAttr = tokenStream.addAttribute(ReadingAttribute.class);
            OffsetAttribute offsetAttr  = tokenStream.addAttribute(OffsetAttribute.class);

            tokenStream.reset();
            int offset = 0;
            while (tokenStream.incrementToken()) {
                String surface = charAttr.toString();
                tokens.add(new TokenElement(surface,
                        Arrays.asList(posAttr.getPartOfSpeech().split("-")),
                        offsetAttr.startOffset(),
                        readAttr.getReading()
                        ));
                offset += surface.length();
            }
        }
        return tokens;
    }
}
