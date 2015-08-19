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
import java.util.Deque;
import java.util.ArrayDeque;

import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static cc.redpen.parser.latex.Tools.*;
import static cc.redpen.parser.latex.Assert.*;

public class StreamParserTest {
    @Test
    public void testParsingCountMatches() {
        assertEquals(3, StreamParser.Parsing.countMatches(Pattern.compile("abc"), "abc abcd abcde"));
    }

    @Test
    public void testParsingGuessRow() {
        final int r = 12;
        final int c = 34;
        final Token target = new Token("TEXTILE", "This is a pen.\nThat is not a pen.\n", new Position(r, c));
        assertEquals(0 + r, StreamParser.Parsing._guessRow(target, "This"));
        assertEquals(0 + r, StreamParser.Parsing._guessRow(target, "a pen"));
        assertEquals(1 + r, StreamParser.Parsing._guessRow(target, "That"));
        assertEquals(1 + r, StreamParser.Parsing._guessRow(target, "not a pen"));
        assertEquals(0 + r, StreamParser.Parsing._guessRow(target, "nonexistent"));
    }

    @Test
    public void testParsingGuessCol() {
        final int r = 12;
        final int c = 34;
        final Token target = new Token("TEXTILE", "This is a pen.\nThat is not a pen.\n", new Position(r, c));
        assertEquals(0 + c, StreamParser.Parsing._guessCol(target, "This"));
        assertEquals(8 + c, StreamParser.Parsing._guessCol(target, "a pen"));
        assertEquals(0    , StreamParser.Parsing._guessCol(target, "That"));
        assertEquals(8    , StreamParser.Parsing._guessCol(target, "not a pen"));
        assertEquals(0 + c, StreamParser.Parsing._guessCol(target, "nonexistent"));
    }

    @Test
    public void testParsingStripTextBlock() {
        assertEquals(
            "Get a life.",
            StreamParser.Parsing.stripTextBlock("\n\n\n   Get a life.")
        );
    }

    @Test
    public void testParsingCompactTextBlock() {
        assertEquals(
            "This is a text. Get a life.",
            StreamParser.Parsing.compactTextBlock("This  \t is \t\t\t a  text.\n\n\n   Get   a   life.")
        );
    }

    @Test
    public void testParsingMaskCharactersInTextBlock() {
        assertEquals(
            "cite ctrl",
            StreamParser.Parsing.maskCharactersInTextBlock("cite~ctrl")
        );
    }

    @Test
    public void testParsingValuesOf() {
        final List<Token> tokens = Arrays.asList(
            token("A", "a"),
            token("B", "bcd"),
            token("C", "ef"),
            token("D", "ghijk"),
            token("E", "l"),
            token("F", "mn"),
            token("G", "opqrstuvw"),
            token("H", "xyz")
        );

        assertEquals(
            takenFor(t -> t.v, tokens),
            StreamParser.Parsing.valuesOf(tokens)
       );
    }

    @Test
    public void testParsingTextileValuesOf() {
        final List<Token> tokens = Arrays.asList(
            token("TEXTILEa", "a"),
            token("CONTROL*", "bcd"),
            token("C", "ef"),
            token("D", "ghijk"),
            token("E", "l"),
            token("F", "mn"),
            token("G", "opqrstuvw"),
            token("H", "xyz")
        );

        assertEquals(
            takenFor(t -> t.asTextile(), tokens),
            StreamParser.Parsing.textileValuesOf(tokens)
        );
    }


    @Test
    public void testParsingUnescapeRegion() {
        final char E = StreamParser.Parsing.ESCAPE_CHAR;
        assertTokensLike(
            Arrays.asList(
                token("A", "a     b"),
                token("B", "c d e"),
                token("C", " fg ")
            ),
            StreamParser.Parsing.unescapeRegion(
                Arrays.asList(
                    token("A", "a"+E+E+E+E+E+"b"),
                    token("B", "c"+E+"d"+E+"e"),
                    token("C", E+"fg"+E)
                )
            )
        );
    }

    @Test
    public void testParsingCollapseInterestsEnvironment() {
        assertTokensLike(
            Arrays.asList(
                token("ENVIRON_BEGIN", "begin", Arrays.asList(token("GROUP1_BEGIN", "{"), token("TEXTILE", "document"), token("GROUP1_END", "}"))),
                token("ENVIRON_END", "end", Arrays.asList(token("GROUP1_BEGIN", "{"), token("TEXTILE", "document"), token("GROUP1_END", "}")))
            ),
            StreamParser.Parsing.collapse(
                Arrays.asList(
                    token("CONTROL", "begin"),
                    token("GROUP1_BEGIN", "{"), token("TEXTILE", "document"), token("GROUP1_END", "}"),
                    token("CONTROL", "end"),
                    token("GROUP1_BEGIN", "{"), token("TEXTILE", "document"), token("GROUP1_END", "}")
                )
            )
        );
    }

    @Test
    public void testParsingCollapseInstrests() {
        final List<Token> garbage =
            Arrays.asList(
                token("GROUP1_BEGIN", "{"), token("TEXTILE", "a"), token("GROUP1_END", "}")
                );

        final List<Token> tokens = new ArrayList<>();
        final List<Token> expected = new ArrayList<>();

        for (String t:
                 Arrays.asList(
                     "part", "chapter", "section", "subsection", "subsubsection", "paragraph", "subparagraph", "item"
                 )) {
            tokens.add(token("CONTROL", t));
            tokens.addAll(garbage);

            expected.add(token("CONTROL*", t, garbage));
        }

        assertTokensLike(
            expected,
            StreamParser.Parsing.collapse(tokens)
        );
    }

    @Test
    public void testParsingCollapseIgnores() {
        final List<Token> garbage =
            Arrays.asList(
                token("GROUP2_BEGIN", "["), token("TEXTILE", "a"), token("GROUP2_END", "]"),
                token("GROUP2_BEGIN", "["), token("TEXTILE", "b"), token("GROUP2_END", "]"),
                token("GROUP1_BEGIN", "{"), token("TEXTILE", "c"), token("GROUP1_END", "}"),
                token("GROUP1_BEGIN", "{"), token("TEXTILE", "d"), token("GROUP1_END", "}")
                );

        final List<Token> tokens = new ArrayList<>();
        final List<Token> expected = new ArrayList<>();

        for (String t:
                 Arrays.asList(
                     "hfill", "hfill*", "vfill", "vfill*", "phantom", "documentclass", "usepackage", "author", "date", "label", "cite", "biblio*whatever*", "includegraphics"
                 )) {
            tokens.add(token("CONTROL", t));
            tokens.addAll(garbage);

            expected.add(token("CONTROL", t, garbage));
        }

        assertTokensLike(
            expected,
            StreamParser.Parsing.collapse(tokens)
        );
    }

    @Test
    public void testParsingCollapseNeutrals() {
        final List<Token> garbage =
            Arrays.asList(
                token("GROUP1_BEGIN", "{"), token("TEXTILE", "a"), token("GROUP1_END", "}")
                );

        final List<Token> tokens = new ArrayList<>();
        final List<Token> expected = new ArrayList<>();

        tokens.add(token("TEXTILE", "\n"));
        expected.add(token("TEXTILE", "\n"));

        tokens.add(token("VERBATIM", "\n"));
        expected.add(token("VERBATIM", "\n"));

        for (String t:
                 Arrays.asList(
                     "tt", "sc", "ft", "whatever"
                 )) {
            tokens.add(token("CONTROL", t));
            tokens.addAll(garbage);

            expected.addAll(garbage);
        }

        assertTokensLike(
            expected,
            StreamParser.Parsing.collapse(tokens)
        );
    }

    @Test
    public void testParsingTakeBlock() {
        final Deque<Token> q = new ArrayDeque<Token>(
            Arrays.asList(
                token("GROUP2_BEGIN", "["), token("GROUP1_BEGIN", "{"), token("TEXTILE", "text"), token("GROUP2_BEGIN", "["), token("GROUP2_END", "]"), token("GROUP1_END", "}"), token("GROUP2_END", "]"),
                token("GROUP1_BEGIN", "{"), token("TEXTILE", "text2"), token("GROUP2_BEGIN", "["), token("GROUP2_END", "]"), token("GROUP1_END", "}"),
                token("GROUP2_BEGIN", "["), token("TEXTILE", "text3"), token("GROUP2_END", "]"),

                token("CONTROL", "test"),
                token("TEXTILE", "test")
                )
            );

        assertTokensLike(
            Arrays.asList(
                token("GROUP2_BEGIN", "["), token("GROUP1_BEGIN", "{"), token("TEXTILE", "text"), token("GROUP2_BEGIN", "["), token("GROUP2_END", "]"), token("GROUP1_END", "}"), token("GROUP2_END", "]")
                ),
            StreamParser.Parsing.takeBlock(q));
        assertTokensLike(
            Arrays.asList(
                token("GROUP1_BEGIN", "{"), token("TEXTILE", "text2"), token("GROUP2_BEGIN", "["), token("GROUP2_END", "]"), token("GROUP1_END", "}"),
                token("GROUP2_BEGIN", "["), token("TEXTILE", "text3"), token("GROUP2_END", "]"),
                token("CONTROL", "test"),
                token("TEXTILE", "test")
                ),
            new ArrayList(q)
        );

        assertTokensLike(
            Arrays.asList(
                token("GROUP1_BEGIN", "{"), token("TEXTILE", "text2"), token("GROUP2_BEGIN", "["), token("GROUP2_END", "]"), token("GROUP1_END", "}")
            ),
            StreamParser.Parsing.takeBlock(q));
        assertTokensLike(
            Arrays.asList(
                token("GROUP2_BEGIN", "["), token("TEXTILE", "text3"), token("GROUP2_END", "]"),
                token("CONTROL", "test"),
                token("TEXTILE", "test")
                ),
            new ArrayList(q)
        );

        assertTokensLike(
            Arrays.asList(
                token("GROUP2_BEGIN", "["), token("TEXTILE", "text3"), token("GROUP2_END", "]")
            ),
            StreamParser.Parsing.takeBlock(q));
        assertTokensLike(
            Arrays.asList(
                token("CONTROL", "test"),
                token("TEXTILE", "test")
                ),
            new ArrayList(q)
        );

        assertTokensLike(
            Arrays.asList(),
            StreamParser.Parsing.takeBlock(q));
        assertTokensLike(
            Arrays.asList(
                token("CONTROL", "test"),
                token("TEXTILE", "test")
                ),
            new ArrayList(q)
        );
    }

    @Test
    public void testParsingTakeTrailingBlocks() {
        final Deque<Token> q = new ArrayDeque<Token>(
            Arrays.asList(
                token("GROUP2_BEGIN", "["), token("GROUP1_BEGIN", "{"), token("TEXTILE", "text"), token("GROUP2_BEGIN", "["), token("GROUP2_END", "]"), token("GROUP1_END", "}"), token("GROUP2_END", "]"),
                token("GROUP1_BEGIN", "{"), token("TEXTILE", "text2"), token("GROUP2_BEGIN", "["), token("GROUP2_END", "]"), token("GROUP1_END", "}"),
                token("GROUP2_BEGIN", "["), token("TEXTILE", "text3"), token("GROUP2_END", "]"),

                token("CONTROL", "test"),
                token("TEXTILE", "test")
                )
            );

        assertTokensLike(
            Arrays.asList(
                token("GROUP2_BEGIN", "["), token("GROUP1_BEGIN", "{"), token("TEXTILE", "text"), token("GROUP2_BEGIN", "["), token("GROUP2_END", "]"), token("GROUP1_END", "}"), token("GROUP2_END", "]"),
                token("GROUP1_BEGIN", "{"), token("TEXTILE", "text2"), token("GROUP2_BEGIN", "["), token("GROUP2_END", "]"), token("GROUP1_END", "}"),
                token("GROUP2_BEGIN", "["), token("TEXTILE", "text3"), token("GROUP2_END", "]")
                ),
            StreamParser.Parsing.takeTrailingBlocks(q));
        assertTokensLike(
            Arrays.asList(
                token("CONTROL", "test"),
                token("TEXTILE", "test")
                ),
            new ArrayList(q)
        );

        assertTokensLike(
            Arrays.asList(),
            StreamParser.Parsing.takeTrailingBlocks(q));
        assertTokensLike(
            Arrays.asList(
                token("CONTROL", "test"),
                token("TEXTILE", "test")
                ),
            new ArrayList(q)
        );
    }

    @Test
    public void testParsingMakeVerbatim() {
        assertTokensLike(
            Arrays.asList(
                token("VERBATIM", "this is a text.")
            ),
            Arrays.asList(
                StreamParser.Parsing.makeVerbatim(
                    Arrays.asList(
                        token("A", "this "),
                        token("B", "is "),
                        token("C", "a text"),
                        token("D", ".")
                    )
                )
            )
        );
    }

    @Test
    public void testParsingMarkVerbatimRegion() {
        assertTokensLike(
            Arrays.asList(
                token("VERBATIM", "abegin{center}bend{center}c")
                ),
            StreamParser.Parsing.markVerbatimRegion(
                StreamParser.Parsing.collapse(
                    Arrays.asList(
                        token("CONTROL", "begin"),
                        token("GROUP1_BEGIN", "{"), token("TEXTILE", "verbatim"), token("GROUP1_END", "}"),
                        token("TEXTILE", "a"),
                        token("CONTROL", "begin"),
                        token("GROUP1_BEGIN", "{"), token("TEXTILE", "center"), token("GROUP1_END", "}"),
                        token("TEXTILE", "b"),
                        token("CONTROL", "end"),
                        token("GROUP1_BEGIN", "{"), token("TEXTILE", "center"), token("GROUP1_END", "}"),
                        token("TEXTILE", "c"),
                        token("CONTROL", "end"),
                        token("GROUP1_BEGIN", "{"), token("TEXTILE", "verbatim"), token("GROUP1_END", "}")
                        )
                    )
                )
            );
    }

    @Test
    public void testParsingMaskTabularLikeRegion() {
        assertTokensLike(
            Arrays.asList(
                token("TEXTILE", "This is the table."),
                token("TEXTILE", "That was the table.")
            ),
            StreamParser.Parsing.maskTabularLikeRegion(
                StreamParser.Parsing.collapse(
                    Arrays.asList(
                        token("TEXTILE", "This is the table."),
                        token("CONTROL", "begin"),
                        token("GROUP1_BEGIN", "{"), token("TEXTILE", "tabular"), token("GROUP1_END", "}"),
                        token("GROUP1_BEGIN", "{"), token("TEXTILE", "l | r | l | l | l"), token("GROUP1_END", "}"),
                        token("TEXTILE", " a & a & a & a & a"), token("TEXTILE", "\\"), token("TEXTILE", "\\"), token("TEXTILE", "\\"), token("TEXTILE", "\\"),
                        token("TEXTILE", " a & a & a & a & a"), token("TEXTILE", "\\"), token("TEXTILE", "\\"), token("TEXTILE", "\\"), token("TEXTILE", "\\"),
                        token("CONTROL", "end"),
                        token("GROUP1_BEGIN", "{"), token("TEXTILE", "tabular"), token("GROUP1_END", "}"),
                        token("TEXTILE", "That was the table.")
                        )
                    )
                )

            );
    }

    @Test
    public void testParsingNormalizeTextileRegion() {
        assertTokensLike(
            Arrays.asList(
                token("TEXTILE", "This is \"the\" te'X't."),
                token("VERBATIM", "It’s ``The’’ te`x’t."),
                token("TEXTILE", "That was \"THE\" te'x't.")
            ),
            StreamParser.Parsing.normalizeTextileRegion(
                Arrays.asList(
                    token("TEXTILE", "This is ``the’’ te`X’t."),
                    token("VERBATIM", "It’s ``The’’ te`x’t."),
                    token("TEXTILE", "That was ``THE’’ te`x’t.")
                )
            )
        );
    }

    @Test
    public void testParsingPruneRegion() {
        final String E = String.valueOf(StreamParser.Parsing.ESCAPE_CHAR);
        assertTokensLike(
            Arrays.asList(
                token("TEXTILE", "This"),
                token("PART", "A"),
                token("CHAPTER", "A"),
                token("SECTION", "A"),
                token("SUBSECTION", "A"),
                token("PARAGRAPH", "A"),
                token("SUBPARAGRAPH", "A"),
                token("ITEM", "")
            ),
            StreamParser.Parsing.pruneRegion(
                Arrays.asList(
                    token("TEXTILE", "This"),
                    token("VERBATIM", "That"),
                    token("CONTROL", "What"),
                    token("CONTROL*", "part", Arrays.asList(token("GROUP1_BEGIN", "{"), token("TEXTILE", "A"), token("GROUP1_END", "}"))),
                    token("CONTROL*", "chapter", Arrays.asList(token("GROUP1_BEGIN", "{"), token("TEXTILE", "A"), token("GROUP1_END", "}"))),
                    token("CONTROL*", "section", Arrays.asList(token("GROUP1_BEGIN", "{"), token("TEXTILE", "A"), token("GROUP1_END", "}"))),
                    token("CONTROL*", "subsection", Arrays.asList(token("GROUP1_BEGIN", "{"), token("TEXTILE", "A"), token("GROUP1_END", "}"))),
                    token("CONTROL*", "paragraph", Arrays.asList(token("GROUP1_BEGIN", "{"), token("TEXTILE", "A"), token("GROUP1_END", "}"))),
                    token("CONTROL*", "subparagraph", Arrays.asList(token("GROUP1_BEGIN", "{"), token("TEXTILE", "A"), token("GROUP1_END", "}"))),
                    token("CONTROL*", "item")
                )
            )
        );
    }

    @Test
    public void testParsingAssembleRegion() {
        assertTokensLike(
            Arrays.asList(
                token("TEXTILE", "ThisThatWhat?"),
                token("CONTROL", "control")
            ),
            StreamParser.Parsing.assembleRegion(
                Arrays.asList(
                    token("TEXTILE", "This"),
                    token("TEXTILE", ""),
                    token("TEXTILE", "That"),
                    token("TEXTILE", "What"),
                    token("TEXTILE", "?"),
                    token("CONTROL", "control")
                )
            )
        );
    }

    @Test
    public void testParsingStyleTextileRegion() {
        assertTokensLike(
            Arrays.asList(
                token("TEXTILE", "This is a sentence.  Okay, this is IT.\nDo you hear that?"),
                token("TEXTILE", ""),
                token("TEXTILE", "(You open the door.)"),
                token("TEXTILE", ""),
                token("TEXTILE", "**YAAAHHHH**"),
                token("TEXTILE", ""),
                token("TEXTILE", "That was close...  Take care of yourself.")
                ),
            StreamParser.Parsing.styleTextileRegion(
                Arrays.asList(
                    token("TEXTILE",
                            "\nThis is a sentence.  Okay, this is IT.\n"
                          + "Do you hear that?\n\n"
                          + "(You open the door.)\n\n\n"
                          + "**YAAAHHHH**\n\n"
                          + "That was close...  Take care of yourself.")
                )
            )
        );
    }

}
