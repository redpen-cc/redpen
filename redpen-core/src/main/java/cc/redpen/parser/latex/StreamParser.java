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
                 (P.unescapeRegion
                  (P.styleTextileRegion
                   (P.assembleRegion
                    (P.pruneRegion
                     (P.normalizeTextileRegion
                      (P.maskTabularLikeRegion
                       (P.markVerbatimRegion
                        (P.collapse (tokens)))))))))) {
            mListener.element(t);
        }
    }

    /*package*/ static class P {
        public static final char ESCAPE_CHAR = '\uFFFD';

        public static List<Token> takeBlock(final Deque<Token> q) {
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

        public static List<Token> takeTrailingBlocks(final Deque<Token> q) {
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

        public static Token makeVerbatim(final List<Token> tokens) {
            final Token beginning = tokens.get(0);
            final List<String> values = new ArrayList<>();
            for (Token t : tokens) {
                values.add(t.asVerbatim());
                if (!t.p.isEmpty()) {
                    values.add(makeVerbatim(t.p).v);
                }
            }
            return new Token("VERBATIM", StringUtils.join(values, ""), beginning.pos);
        }


        public static List<Token> collapse(final List<Token> tokens) {
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
                            switch (t.v) {
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

        public static List<String> valuesOf(final Iterable<Token> tokens) {
            final List<String> o = new ArrayList<>();
            for (Token t : tokens) {
                o.add(t.v);
            }
            return o;
        }

        public static List<String> textileValuesOf(final Iterable<Token> tokens) {
            final List<String> o = new ArrayList<>();
            for (Token t : tokens) {
                o.add(t.asTextile());
            }
            return o;
        }

        public static List<Token> markVerbatimRegion(final List<Token> tokens) {
            final List<Token> o = new ArrayList<>();
            final Deque<Token> q = new ArrayDeque<>(tokens);
            try {
                while (true) {
                    final Token t = q.removeFirst();
                    if ( ! ("ENVIRON_BEGIN".equals(t.t) && textileValuesOf(t.p).contains("verbatim")) ) {
                        o.add(t);
                    } else {
                        final List<Token> verbatim = new ArrayList<>();
                        while (true) {
                            final Token vt = q.removeFirst();
                            if ( ! ("ENVIRON_END".equals(vt.t) && textileValuesOf(vt.p).contains("verbatim")) ) {
                                verbatim.add(vt);
                            } else {
                                o.add(makeVerbatim(verbatim));
                                break;
                            }
                        }
                    }
                }
            } catch (final NoSuchElementException e) {
                return o;
            }
        }

        public static List<Token> maskTabularLikeRegion(final List<Token> tokens) {
            final List<Token> o = new ArrayList<>();
            final Deque<Token> q = new ArrayDeque<>(tokens);
            try {
                while (true) {
                    final Token t = q.removeFirst();
                    if ( ! ("ENVIRON_BEGIN".equals(t.t) && textileValuesOf(t.p).contains("tabular")) ) {
                        o.add(t);
                    } else {
                        while (true) {
                            final Token tt = q.removeFirst();
                            if ("ENVIRON_END".equals(tt.t) && textileValuesOf(tt.p).contains("tabular")) {
                                break;
                            }
                        }
                    }
                }
            } catch (final NoSuchElementException e) {
                return o;
            }
        }

        public static List<Token> normalizeTextileRegion(final List<Token> tokens) {
            final List<Token> o = new ArrayList<>();
            final Deque<Token> q = new ArrayDeque<>(tokens);
            try {
                while (true) {
                    final Token t = q.removeFirst();
                    if ( ! ("TEXTILE".equals(t.t)) ) {
                        o.add(t);
                    } else {
                        t.v = Pattern.compile("``|’’|''").matcher(t.v).replaceAll("\"");
                        t.v = Pattern.compile("`|’").matcher(t.v).replaceAll("'");
                        o.add(t);
                    }
                }
            } catch (final NoSuchElementException e) {
                return o;
            }
        }

        public static List<Token> pruneRegion(final List<Token> tokens) {
            final List<Token> o = new ArrayList<>();
            final Deque<Token> q = new ArrayDeque<>(tokens);
            try {
                while (true) {
                    final Token t = q.removeFirst();
                    if ("TEXTILE".equals(t.t)) {
                        o.add(t);
                    } else if ("CONTROL*".equals(t.t)) {
                        o.add(new Token(t.v.toUpperCase(), StringUtils.join(textileValuesOf(pruneRegion(t.p)), ""), t.pos));
                    }
                }
            } catch (final NoSuchElementException e) {
                return o;
            }
        }

        public static List<Token> assembleRegion(final List<Token> tokens) {
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
                            o.add(new Token(first.t, StringUtils.join(textileValuesOf(reg), ""), first.pos));
                            reg.clear();
                        }
                        o.add(t);
                    }
                }
            } catch (final NoSuchElementException e) {
                if (!reg.isEmpty()) {
                    final Token first = reg.get(0);
                    o.add(new Token(first.t, StringUtils.join(textileValuesOf(reg), ""), first.pos));
                    reg.clear();
                }
                return o;
            }
        }

        public static List<Token> styleTextileRegion(final List<Token> tokens) {
            final List<Token> o = new ArrayList<>();
            final Deque<Token> q = new ArrayDeque<>(tokens);
            final Pattern EMPTY = Pattern.compile(String.format("^[ \\t\\r\\n%c]*$", ESCAPE_CHAR));
            final Pattern LINEBREAK = Pattern.compile("(\\r?\\n){2,}");
            try {
                while (true) {
                    final Token t = q.removeFirst();
                    if ("TEXTILE".equals(t.t)) {
                        if (!EMPTY.matcher(t.v).matches()) {
                            final String stripped = stripTextBlock(t.v);
                            for (String s : LINEBREAK.split(stripped)) {
                                final Position p = new Position(_guessRow(t, s), _guessCol(t, s));
                                o.add(new Token(t.t, maskCharactersInTextBlock(s), p));
                                o.add(new Token(t.t, Token.BLANK_LINE, p));
                            }
                        }
                    } else {
                        o.add(t);
                    }
                }
            } catch (final NoSuchElementException e) {
                try {
                    final Token last = o.get(o.size() - 1);
                    if (last.isBlankLine()) {
                        o.remove(last);
                    }
                    return o;
                } catch (final ArrayIndexOutOfBoundsException ignore) {
                    return o;
                }
            }
        }

        public static String stripTextBlock(final String b) {
            return Pattern.compile(String.format("^[ \\t\\r\\n%c]+", ESCAPE_CHAR)).matcher(b).replaceAll("");
        }

        public static String compactTextBlock(final String b) {
            return Pattern.compile(String.format("[ \\t\\r\\n%c]{2,}", ESCAPE_CHAR)).matcher(b).replaceAll(" ");
        }

        public static String maskCharactersInTextBlock(final String b) {
            return b.replace('~', ' ');
        }

        public static List<Token> unescapeRegion(final List<Token> tokens) {
            final List<Token> o = new ArrayList<>();
            for (Token t : tokens) {
                t.v = t.v.replace(ESCAPE_CHAR, ' ');
                o.add(t);
            }
            return o;
        }

        public static int countMatches(final Pattern p, final String s) {
            int ret = 0;
            for (Matcher m = p.matcher(s); m.find(); ++ret);
            return ret;
        }

        public static int _guessRow(final Token t, final String needle) {
            final int lead = t.v.indexOf(needle);
            if (lead >= 0) {
                return t.pos.row + countMatches(Pattern.compile("\\r?\\n"), t.v.substring(0, lead));
            } else {
                return t.pos.row;
            }
        }

        public static int _guessCol(final Token t, final String needle) {
            final int lead = t.v.indexOf(needle);
            if (lead >= 0) {
                final int lastLinebreak = t.v.substring(0, lead).lastIndexOf("\n");
                if (lastLinebreak >= 0) {
                    return 0 + (lead - (lastLinebreak + 1));
                } else {
                    return t.pos.col + lead;
                }
            } else {
                return t.pos.col;
            }
        }
    }


    public static interface Listener {
        public void element(Token t);
    }
}
