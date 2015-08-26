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
import static cc.redpen.parser.latex.Tools.*;
import static cc.redpen.parser.latex.Assert.*;

public class LexerTest {
    @Test
    public void testCommentShouldNotAppear() {
        final String corpse = "% This is a comment\n";
        final List<Token> tokens = Lexer.on(corpse).parse();
        assertEquals(0, tokens.size());
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
                token("TEXTILE", "is"),
                token("CONTROL", "s3_q"),
                token("TEXTILE", "a "),
                token("CONTROL", "gimmeAbre@k"),
                token("TEXTILE", ".")
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
        assertTokensLike(
            Arrays.asList(
                token("TEXTILE", "This is .\n")
                ),
            tokens);
    }
}
