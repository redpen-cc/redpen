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

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JapaneseTokenizer implements RedPenTokenizer {

    private Tokenizer tokenizer;

    public JapaneseTokenizer() {
        this.tokenizer = new Tokenizer();
    }

    @Override
    public List<TokenElement> tokenize(String content) {
        List<TokenElement> tokens = new ArrayList<>();
        for (Token token : tokenizer.tokenize(content)) {
            tokens.add(new TokenElement(token.getSurface(), Arrays.asList(token.getAllFeaturesArray()), token.getPosition()));
        }
        return tokens;
    }
}
