/**
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
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

public class JapaneseTokenizerTest {
    @Test
    public void testTokenize() {
        JapaneseTokenizer tokenizer = new JapaneseTokenizer();
        List<TokenElement> tokens = tokenizer.tokenize("今日も晴天だ。");
        assertEquals(5, tokens.size());
        assertEquals("今日", tokens.get(0).getSurface());
    }
}
