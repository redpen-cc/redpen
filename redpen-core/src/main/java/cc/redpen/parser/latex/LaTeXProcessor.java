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
import java.util.ArrayList;
import java.util.LinkedList;

import java.util.regex.Pattern;

import org.pegdown.ast.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Deque;
import java.util.ArrayDeque;

/**
 * Lame Pegdown adapter for the LaTeX parser.
 */
public class LaTeXProcessor {
    public RootNode parse(char[] stream) {
        final List<Token> tokens = new ArrayList<>();
        new StreamParser(stream, t -> tokens.add(t)).parse();
        return (P.walk (tokens));
    }

    /*package*/ static class P {
        private static List<Node> asTextileNodes(final Deque<Token> q) {
            final List<Node> o = new ArrayList<>();
            final List<Character> reg = new ArrayList<>();
            final Pattern NOT_EMPTY = Pattern.compile("[^ \\t\\r\\n]");

            final Runnable flusher = () -> {
                final String content = StringUtils.join(reg, "");
                if (NOT_EMPTY.matcher(content).find()) {
                    o.add(new TextNode(content));
                }
                reg.clear();
            };

            for (Token t: q) {
                for (char c: t.asTextile().toCharArray()) {
                    switch (c) {
                    case '\n':
                        flusher.run();
                        o.add(new SimpleNode(SimpleNode.Type.Linebreak));
                        break;
                    default:
                        reg.add(c);
                    }
                }
            }
            flusher.run();
            return o;
        }

        public static int outlineLevelOf(final Token t) {
            switch (t.t) {
            case "PART":
                return 1;
            case "CHAPTER":
                return 2;
            case "SECTION":
                return 3;
            case "SUBSECTION":
                return 4;
            case "SUBSUBSECTION":
                return 5;
            default:
                throw new IllegalStateException("token is not outline");
            }
        }

        public static int outlineLevelOf(final SuperNode n) {
            if (n instanceof RootNode) {
                return 0;
            } else if (n instanceof HeaderNode) {
                return (((HeaderNode)n).getLevel());
            } else {
                throw new IllegalStateException(String.format("Node %s is not outline", n));
            }
        }

        private static SuperNode parentNodeFor(final Deque<SuperNode> reg, final SuperNode n) {
            while (true) {
                final SuperNode top = reg.getLast();
                if (outlineLevelOf(n) <= outlineLevelOf(top)) {
                    reg.removeLast();
                } else {
                    return top;
                }
            }
        }

        public static RootNode walk(final List<Token> tokens) {
            final Deque<SuperNode> reg = new ArrayDeque<>();
            final Deque<Token> regTextiles = new ArrayDeque<>();

            final Runnable flusher = () -> {
                if (!regTextiles.isEmpty()) {
                    reg.getLast().getChildren().add(new ParaNode(asTextileNodes(regTextiles))); /* XXX */
                    regTextiles.clear();
                }
            };

            final RootNode o = new RootNode();
            final HeaderNode implicitSection = new HeaderNode(3);
            o.getChildren().add(implicitSection);
            reg.addLast(o);
            reg.addLast(implicitSection);

            for (Token t : tokens) {
                final SuperNode scope = reg.getLast();

                switch (t.t) {
                case "PART":
                case "CHAPTER":
                case "SECTION":
                case "SUBSECTION":
                case "SUBSUBSECTION": {
                    flusher.run();
                    final SuperNode h = new HeaderNode(outlineLevelOf(t), new TextNode(t.v));
                    parentNodeFor(reg, h).getChildren().add(h);
                    reg.addLast(h);
                    break;
                }
                case "ITEM":
                    flusher.run();
                    if (t.v.length() > 0) {
                        scope.getChildren().add(new ParaNode(new TextNode(t.v)));
                    }
                    break;
                case "VERBATIM":
                    flusher.run();
                    scope.getChildren().add(new ParaNode(new VerbatimNode(t.v)));
                    break;
                case "TEXTILE":
                    regTextiles.addLast(t);
                    break;
                }
            }

            flusher.run();

            return o;
        }
    }
}
