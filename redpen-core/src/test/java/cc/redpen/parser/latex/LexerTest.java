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
package cc.redpen.parser.latex;

import org.junit.Test;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class LexerTest {
    @Test
    public void testCommentShouldNotAppear() {
        final String corpse = "% This is a comment\n";
        final List<Token> tokens = Lexer.on(corpse).parse();
        assertEquals(1, tokens.size());
        assertEquals("TEXTILE", tokens.get(0).t);
        assertEquals("\n", tokens.get(0).v);
    }

    @Test
    public void testTextile() {
        final String corpse = "This is a comment\n";
        final List<Token> tokens = Lexer.on(corpse).parse();
        assertEquals(1, tokens.size());
        assertEquals("TEXTILE", tokens.get(0).t);
        assertEquals("This is a comment\n", tokens.get(0).v);
    }

    @Test
    public void testEscapedCharacters() {
        final String corpse = "This is an \\\\escaped\\\\ backslash ( \\\\ ).";
        final List<Token> tokens = Lexer.on(corpse).parse();
        assertTokensLike(
            Arrays.asList(
                token("TEXTILE", "This is an "),
                token("TEXTILE", "\\escaped"),
                token("TEXTILE", "\\ backslash ( "),
                token("TEXTILE", "\\ ).")
                ),
            tokens);
    }

    @Test
    public void testControl() {
        final String corpse = "This\\control is\\s3_q a \\gimmeAbre@k .";
        final List<Token> tokens = Lexer.on(corpse).parse();
        assertTokensLike(
            Arrays.asList(
                token("TEXTILE", "This"),
                token("CONTROL", "control"),
                token("TEXTILE", " is"),
                token("CONTROL", "s3_q"),
                token("TEXTILE", " a "),
                token("CONTROL", "gimmeAbre@k"),
                token("TEXTILE", " .")
                ),
            tokens);
    }

    @Test
    public void testVerbatimText() {
        final String corpse = "This is a \\verb|v$e$r$b{a}\t\ri\nm,| \\verb,v|e|r+b+atim, \\verb+v,e|rba\\tim+ text\n";
        final List<Token> tokens = Lexer.on(corpse).parse();
        assertTokensLike(
            Arrays.asList(
                token("TEXTILE", "This is a "),
                token("VERBATIM", "v$e$r$b{a}\t\ri\nm,"),
                token("TEXTILE", " "),
                token("VERBATIM", "v|e|r+b+atim"),
                token("TEXTILE", " "),
                token("VERBATIM", "v,e|rba\\tim"),
                token("TEXTILE", " text\n")
                ),
            tokens);
    }

    @Test
    public void testFormulaShouldNotAppear() {
        final String corpse = "This is $\\mathrm{science, formulated:} e^{i\\pi} = \\mathrm{cos}\\pi + i\\mathrm{sin}\\pi = 1$.\n";
        final List<Token> tokens = Lexer.on(corpse).parse();
        System.out.println(tokens);
        assertTokensLike(
            Arrays.asList(
                token("TEXTILE", "This is "),
                token("TEXTILE", ".\n")
                ),
            tokens);
    }

    private static Token token(final String type, final String value) {
        return new Token(type, value, new Position(0,0));
    }

    private static void assertTokensLike(final List<Token> expected, final List<Token> actual) {
        assertEquals("token streams differ in size", expected.size(), actual.size());
        for (int i=0; i<expected.size(); ++i) {
            final Token p = expected.get(i);
            final Token q = actual.get(i);
            assertTrue(String.format("token streams do not look like at index %d: expected: <%s>, got: <%s>", i, p, q), isTokenLikeTo(p, q));
        }
    }

    private static void assertTokensEqual(final List<Token> expected, final List<Token> actual) {
        assertEquals("token streams differ in size", expected.size(), actual.size());
        for (int i=0; i<expected.size(); ++i) {
            final Token p = expected.get(i);
            final Token q = actual.get(i);
            assertTrue(String.format("token streams differ at index %d: expected: <%s>, got: <%s>", i, p, q), isTokenEqualTo(p, q));
        }
    }

    private static boolean isTokenLikeTo(final Token p, final Token q) {
        return (p.t.equals(q.t) && p.v.equals(q.v));
    }

    private static boolean isTokenEqualTo(final Token p, final Token q) {
        return String.valueOf(p).equals(String.valueOf(q));
    }

}
