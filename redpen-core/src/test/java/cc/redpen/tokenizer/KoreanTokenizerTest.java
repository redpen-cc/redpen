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

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class KoreanTokenizerTest {
    @Test
    public void testTokenize() {
        KoreanTokenizer tokenizer = new KoreanTokenizer();
        List<TokenElement> tokens = tokenizer.tokenize("도움이 될 것이다.");
        assertEquals(8, tokens.size());
        assertEquals("도움", tokens.get(0).getSurface());
        assertEquals("NNG", tokens.get(0).getTags().get(0));
        assertEquals("이", tokens.get(1).getSurface());
        assertEquals("JKS", tokens.get(1).getTags().get(0));
        assertEquals("되", tokens.get(2).getSurface());
        assertEquals("VV", tokens.get(2).getTags().get(0));
        assertEquals("ㄹ", tokens.get(3).getSurface());
        assertEquals("ETM", tokens.get(3).getTags().get(0));
        assertEquals("것", tokens.get(4).getSurface());
        assertEquals("NNB", tokens.get(4).getTags().get(0));
        assertEquals("이", tokens.get(5).getSurface());
        assertEquals("VCP", tokens.get(5).getTags().get(0));
        assertEquals("다", tokens.get(6).getSurface());
        assertEquals("EF", tokens.get(6).getTags().get(0));
        assertEquals(".", tokens.get(7).getSurface());
        assertEquals("SF", tokens.get(7).getTags().get(0));
    }

    @Test
    public void testTokenizeVoid() {
        KoreanTokenizer tokenizer = new KoreanTokenizer();
        List<TokenElement> tokens = tokenizer.tokenize("");
        assertEquals(0, tokens.size());
    }
}
