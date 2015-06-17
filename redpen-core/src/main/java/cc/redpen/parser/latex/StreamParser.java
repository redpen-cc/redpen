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

import java.util.List;
import java.util.Deque;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.NoSuchElementException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.apache.commons.lang3.StringUtils;

/**
 * Experimental stream mode LaTeX parser prototype.
 */
public class StreamParser {
    private static final char ESCAPE_CHAR = '\uFFFD';

    private char[] mTarget;

    private Listener mListener;

    public StreamParser(final char[] s, final Listener l) {
        mTarget = s;
        mListener = l;
    }

    public StreamParser(final String s, final Listener l) {
        mTarget = s.toCharArray();
        mListener = l;
    }

    public void parse() {
        final List<Token> tokens = Lexer.on(mTarget).parse();
        for (Token t:
                 (unescapeRegion
                  (markImplicitParagraphRegion
                   (assembleRegion
                    (pruneRegion
                     (normalizeTextileRegion
                      (maskTabularLikeRegion
                       (markVerbatimRegion
                        (collapse (tokens)))))))))) {
            mListener.element(t);
        }
    }

    private static List<Token> takeBlock(final Deque<Token> q) {
        final List<Token> o = new ArrayList<>();
        final Deque<String> reg = new ArrayDeque<>();
        try {
            while (true) {
                final Token t = q.getFirst();
                switch (t.t) {
                case "GROUP1_BEGIN":
                case "GROUP2_BEGIN":
                    reg.addLast(t.t);
                    break;
                default:
                    switch (t.t) {
                    case "GROUP1_END":
                        if ("GROUP1_BEGIN".equals(reg.getLast())) {
                            reg.removeLast();
                        }
                        break;
                    case "GROUP2_END":
                        if ("GROUP2_BEGIN".equals(reg.getLast())) {
                            reg.removeLast();
                        }
                        break;
                    }
                }
                if (!reg.isEmpty() || !o.isEmpty()) {
                    o.add(q.removeFirst());
                }
                if (reg.isEmpty()) {
                    break;
                }
            }
            return o;
        } catch (final NoSuchElementException e) {
            return o;
        }
    }

    private static List<Token> takeTrailingBlocks(final Deque<Token> q) {
        final List<Token> o = new ArrayList<>();
        while (true) {
            final List<Token> block = takeBlock(q);
            if (!block.isEmpty()) {
                o.addAll(block);
            } else {
                break;
            }
        }
        return o;
    }

    private static Token makeVerbatim(final List<Token> tokens) {
        final Token beginning = tokens.get(0);
        final List<String> values = new ArrayList<>();
        for (Token t : tokens) {
            values.add(t.v);
        }
        return new Token("VERBATIM", StringUtils.join(values, ""), beginning.pos);
    }


    private static List<Token> collapse(final List<Token> tokens) {
        final List<Token> o = new ArrayList<>();
        final Deque<Token> q = new ArrayDeque<>(tokens);
        final Pattern interests = Pattern.compile("(?:caption|part|(?:sub)*(chapter|section|paragraph)|item|title)\\*?");
        final Pattern ignores = Pattern.compile(".?(?:space|fill)\\*?|phantom|documentclass|usepackage|author|date|label|ref|cite|biblio.*|includegraphics");
        try {
            while (true) {
                final Token t = q.removeFirst();
                if ("CONTROL".equals(t.t)) {
                    if ("begin".equals(t.v) || "end".equals(t.v)) {
                        t.p = takeTrailingBlocks(q);
                        switch (t.t) {
                        case "begin":
                            t.t = "ENVIRON_BEGIN";
                            break;
                        case "end":
                            t.t = "ENVIRON_END";
                            break;
                        }
                        o.add(t);
                    } else if (interests.matcher(t.v).matches()) {
                        t.p = takeBlock(q);
                        t.t = t.t + "*";
                        o.add(t);
                    } else if (ignores.matcher(t.v).matches()) {
                        t.p = takeTrailingBlocks(q);
                        o.add(t);
                    }
                } else {
                    o.add(t);
                }
            }
        } catch (final NoSuchElementException e) {
            return o;
        }
    }

    private static List<String> valuesOf(final Iterable<Token> tokens) {
        final List<String> o = new ArrayList<>();
        for (Token t : tokens) {
            o.add(t.v);
        }
        return o;
    }

    private static List<Token> markVerbatimRegion(final List<Token> tokens) {
        final List<Token> o = new ArrayList<>();
        final Deque<Token> q = new ArrayDeque<>(tokens);
        try {
            while (true) {
                final Token t = q.removeFirst();
                if ( ! ("ENVIRON_BEGIN".equals(t.t) && valuesOf(t.p).contains("verbatim")) ) {
                    o.add(t);
                } else {
                    final List<Token> verbatim = new ArrayList<>();
                    while (true) {
                        final Token vt = q.removeFirst();
                        if ( ! ("ENVIRON_BEGIN".equals(vt.t) && valuesOf(vt.p).contains("verbatim")) ) {
                            verbatim.add(vt);
                        } else {
                            o.add(makeVerbatim(verbatim));
                            o.add(vt);
                            break;
                        }
                    }
                }
            }
        } catch (final NoSuchElementException e) {
            return o;
        }
    }

    private static List<Token> maskTabularLikeRegion(final List<Token> tokens) {
        final List<Token> o = new ArrayList<>();
        final Deque<Token> q = new ArrayDeque<>(tokens);
        try {
            while (true) {
                final Token t = q.removeFirst();
                if ( ! ("ENVIRON_BEGIN".equals(t.t) && valuesOf(t.p).contains("tabular")) ) {
                    o.add(t);
                } else {
                    final List<Token> verbatim = new ArrayList<>();
                    while (true) {
                        final Token tt = q.removeFirst();
                        if ( ! ("ENVIRON_END".equals(tt.t) && valuesOf(tt.p).contains("tabular")) ) {
                            tt.t = "MASKED";
                            o.add(tt);
                        } else {
                            tt.t = "MASKED";
                            o.add(tt);
                            break;
                        }
                    }
                }
            }
        } catch (final NoSuchElementException e) {
            return o;
        }
    }

    private static List<Token> normalizeTextileRegion(final List<Token> tokens) {
        final List<Token> o = new ArrayList<>();
        final Deque<Token> q = new ArrayDeque<>(tokens);
        try {
            while (true) {
                final Token t = q.removeFirst();
                if ( ! ("TEXTILE".equals(t.t)) ) {
                    o.add(t);
                } else {
                    t.v = Pattern.compile("``|’’|''").matcher(t.v).replaceAll("\" ");
                    t.v = Pattern.compile("`|’").matcher(t.v).replaceAll("'");
                    o.add(t);
                }
            }
        } catch (final NoSuchElementException e) {
            return o;
        }
    }

    private static List<Token> pruneRegion(final List<Token> tokens) {
        final List<Token> o = new ArrayList<>();
        final Deque<Token> q = new ArrayDeque<>(tokens);
        try {
            while (true) {
                final Token t = q.removeFirst();
                if ("TEXTILE".equals(t.t)) {
                    o.add(t);
                } else if ("CONTROL*".equals(t.t)) {
                    o.add(new Token(t.v.toUpperCase(), StringUtils.join(valuesOf(pruneRegion(t.p)), ""), t.pos));
                } else {
                    o.add(new Token("TEXTILE", StringUtils.repeat(ESCAPE_CHAR, t.v.length()), t.pos));
                }
            }
        } catch (final NoSuchElementException e) {
            return o;
        }
    }

    private static List<Token> assembleRegion(final List<Token> tokens) {
        final List<Token> o = new ArrayList<>();
        final Deque<Token> q = new ArrayDeque<>(tokens);
        final List<Token> reg = new ArrayList<>();
        try {
            while (true) {
                final Token t = q.removeFirst();
                if ("TEXTILE".equals(t.t)) {
                    reg.add(t);
                } else {
                    if (!reg.isEmpty()) {
                        final Token first = reg.get(0);
                        o.add(new Token(first.t, StringUtils.join(valuesOf(reg), ""), first.pos));
                        reg.clear();
                    }
                    o.add(t);
                }
            }
        } catch (final NoSuchElementException e) {
            if (!reg.isEmpty()) {
                final Token first = reg.get(0);
                o.add(new Token(first.t, StringUtils.join(valuesOf(reg), ""), first.pos));
                reg.clear();
            }
            return o;
        }
    }

    private static List<Token> markImplicitParagraphRegion(final List<Token> tokens) {
        final List<Token> o = new ArrayList<>();
        final Deque<Token> q = new ArrayDeque<>(tokens);
        final Pattern EMPTY = Pattern.compile(String.format("^[ \\t\\r\\n%c]*$", ESCAPE_CHAR));
        final Pattern LINEBREAK = Pattern.compile("(\\r?\\n){2,}");
        try {
            while (true) {
                final Token t = q.removeFirst();
                if ("TEXTILE".equals(t.t)) {
                    for (String b : LINEBREAK.split(t.v)) {
                        if (!EMPTY.matcher(b).matches()) {
                            final String stripped = stripTextBlock(b);
                            o.add(new Token(t.t, compactTextBlock(maskCharactersInTextBlock(stripped)), new Position(_guessRow(t, stripped), _guessCol(t, stripped))));
                        }
                    }
                } else {
                    o.add(t);
                }
            }
        } catch (final NoSuchElementException e) {
            return o;
        }
    }

    private static String stripTextBlock(final String b) {
        return Pattern.compile(String.format("^[ \\t\\r\\n%c]+", ESCAPE_CHAR)).matcher(b).replaceAll("");
    }

    private static String compactTextBlock(final String b) {
        return Pattern.compile(String.format("[ \\t\\r\\n%c]{2,}", ESCAPE_CHAR)).matcher(b).replaceAll(" ");
    }

    private static String maskCharactersInTextBlock(final String b) {
        return b.replace('~', ' ');
    }

    private static List<Token> unescapeRegion(final List<Token> tokens) {
        final List<Token> o = new ArrayList<>();
        for (Token t : tokens) {
            t.v = t.v.replace(ESCAPE_CHAR, ' ');
            o.add(t);
        }
        return o;
    }

    private static int countMatches(final Pattern p, final String s) {
        int ret = 0;
        for (Matcher m = p.matcher(s); m.find(); ++ret);
        return ret;
    }

    private static int _guessRow(final Token t, final String needle) {
        final int lead = t.v.indexOf(needle);
        return t.pos.row + countMatches(Pattern.compile("\\r?\\n"), t.v.substring(0, lead));
    }

    private static int _guessCol(final Token t, final String needle) {
        final int lead = t.v.indexOf(needle);
        final int lastLinebreak = t.v.substring(0, lead).lastIndexOf("\n");
        if (lastLinebreak >= 0) {
            return lead - lastLinebreak;
        } else {
            return t.pos.col;
        }
    }

    public static interface Listener {
        public void element(Token t);
    }
}
