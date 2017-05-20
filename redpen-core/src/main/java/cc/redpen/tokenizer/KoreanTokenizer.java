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

import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;

import java.util.ArrayList;
import java.util.List;

public class KoreanTokenizer implements RedPenTokenizer {
    private Komoran tokenizer;

    public KoreanTokenizer() {
        this.tokenizer = new Komoran(DEFAULT_MODEL.FULL);
     }

    @Override
    public List<TokenElement> tokenize(String content) {
        List<TokenElement> tokens = new ArrayList<>();
        if (content == "") { return tokens; }

        KomoranResult resultList = tokenizer.analyze(content);
        List<Token> tokenList = resultList.getTokenList();

        for (Token token : tokenList) {
            List<String> pos = new ArrayList<String>();
            pos.add(token.getPos());
            tokens.add(new TokenElement(token.getMorph(), pos, token.getBeginIndex()));
        }

        return tokens;
    }
}
