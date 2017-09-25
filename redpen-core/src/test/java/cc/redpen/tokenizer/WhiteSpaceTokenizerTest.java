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

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WhiteSpaceTokenizerTest {
    @Test
    void testTokenize() {
        RedPenTokenizer tokenizer = new WhiteSpaceTokenizer();
        List<TokenElement> results = tokenizer.tokenize("this is a\u00A0pen");
        assertEquals(4, results.size());
        assertEquals("this", results.get(0).getSurface());
        assertEquals(0, results.get(0).getTags().size());
        assertEquals("is", results.get(1).getSurface());
        assertEquals(0, results.get(1).getTags().size());
        assertEquals("a", results.get(2).getSurface());
        assertEquals(0, results.get(2).getTags().size());
        assertEquals("pen", results.get(3).getSurface());
        assertEquals(0, results.get(3).getTags().size());
    }

    @Test
    void testTokenizeSentenceWithNoSpaceBracket() {
        RedPenTokenizer tokenizer = new WhiteSpaceTokenizer();
        List<TokenElement> results = tokenizer.tokenize("distributed(cluster) systems are good");
        assertEquals(7, results.size());
        assertEquals("distributed", results.get(0).getSurface());
        assertEquals(0, results.get(0).getTags().size());
        assertEquals("cluster", results.get(2).getSurface());
        assertEquals(0, results.get(1).getTags().size());
        assertEquals("systems", results.get(4).getSurface());
        assertEquals(0, results.get(2).getTags().size());
        assertEquals("are", results.get(5).getSurface());
        assertEquals(0, results.get(3).getTags().size());
        assertEquals("good", results.get(6).getSurface());
        assertEquals(0, results.get(4).getTags().size());
    }

    @Test
    void testTokenizeSentenceEndsWithPeriod() {
        RedPenTokenizer tokenizer = new WhiteSpaceTokenizer();
        List<TokenElement> results = tokenizer.tokenize("I am an engineer.");
        assertEquals(5, results.size());
        assertEquals("I", results.get(0).getSurface());
        assertEquals("am", results.get(1).getSurface());
        assertEquals("an", results.get(2).getSurface());
        assertEquals("engineer", results.get(3).getSurface());
        assertEquals(".", results.get(4).getSurface());
    }

    @Test
    void testTokenizeSentenceWithContraction() {
        RedPenTokenizer tokenizer = new WhiteSpaceTokenizer();
        List<TokenElement> results = tokenizer.tokenize("I'm an engineer");
        assertEquals(3, results.size());
        assertEquals("I'm", results.get(0).getSurface());
        assertEquals("an", results.get(1).getSurface());
        assertEquals("engineer", results.get(2).getSurface());
    }
}
