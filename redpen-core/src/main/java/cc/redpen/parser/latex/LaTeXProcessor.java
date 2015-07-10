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

import org.apache.commons.lang3.StringUtils;

import java.util.Deque;
import java.util.ArrayDeque;

import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import static cc.redpen.model.Document.DocumentBuilder;
import cc.redpen.model.Section;
import cc.redpen.model.Paragraph;
import cc.redpen.model.Sentence;
import cc.redpen.parser.LineOffset;
import cc.redpen.parser.SentenceExtractor;
import cc.redpen.parser.markdown.CandidateSentence;
import cc.redpen.parser.markdown.MergedCandidateSentence;
import cc.redpen.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class LaTeXProcessor {
    private static final Logger LOG =
        LoggerFactory.getLogger(LaTeXProcessor.class);

    public void parse(final char[] stream, final DocumentBuilder builder, final SentenceExtractor sentenceExtractor) {
        final List<Token> tokens = new ArrayList<>();
        new StreamParser(stream, t -> tokens.add(t)).parse();
        P.walkWith(Context.of(builder, sentenceExtractor), tokens);
    }

    /*package*/ static class Context {
        public DocumentBuilder builder;
        public SentenceExtractor sentenceExtractor;
        public List<CandidateSentence> candidateSentences = new ArrayList<>();

        public static Context of(final DocumentBuilder builder, final SentenceExtractor sentenceExtractor) {
            final Context o = new Context();
            o.builder = builder;
            o.sentenceExtractor = sentenceExtractor;
            return o;
        }
    }

    /*package*/ static class RP {
        private static void fixSentencesInto(final Context c) {
            // 1. remain sentence append currentSection
            //TODO need line number
            for (Sentence sentence : compileAsSentenceList(c)) {
                c.builder.addSentence(sentence);
            }
        }

        private static CandidateSentence candidateOfSentence(Token t, Textizer textizer) {
            return candidateOfSentence(t, null, textizer);
        }

        private static CandidateSentence candidateOfSentence(Token t, String link, Textizer textizer) {
            return new CandidateSentence(t.pos.row, textizer.do_(t), link, t.pos.col);
        }

        private static List<Sentence> compileAsSentenceList(final Context c) {
            try {
                try {
                    return sentencesIn(c, MergedCandidateSentence.merge(c.candidateSentences).get());
                } catch (final NoSuchElementException e) {
                    return new ArrayList<Sentence>();
                }
            } finally {
                try {
                    c.candidateSentences.clear();
                } catch (final UnsupportedOperationException ignore) {
                }
            }
        }

        private static List<Sentence> sentencesIn(final Context c, final MergedCandidateSentence mergedCandidateSentence) {
            final List<Sentence> outputSentences = new ArrayList<>();
            List<Pair<Integer, Integer>> sentencePositions = new ArrayList<>();
            final String line = mergedCandidateSentence.getContents();
            int lastPosition = c.sentenceExtractor.extract(line , sentencePositions);

            for (Pair<Integer, Integer> sentencePosition : sentencePositions) {
                List<LineOffset> offsetMap =
                    mergedCandidateSentence.getOffsetMap().subList(sentencePosition.first,
                                                                   sentencePosition.second);
                outputSentences.add(new Sentence(line.substring(
                                                     sentencePosition.first, sentencePosition.second), offsetMap,
                                                 mergedCandidateSentence.getRangedLinks(sentencePosition.first, sentencePosition.second - 1)));
            }
            if (lastPosition < mergedCandidateSentence.getContents().length()) {
                List<LineOffset> offsetMap = mergedCandidateSentence.getOffsetMap().subList(lastPosition,
                                                                                            mergedCandidateSentence.getContents().length());
                outputSentences.add(new Sentence(line.substring(
                                                     lastPosition, mergedCandidateSentence.getContents().length()),
                                                 offsetMap,
                                                 mergedCandidateSentence.getRangedLinks(lastPosition,
                                                                                        mergedCandidateSentence.getContents().length())));
            }
            return outputSentences;
        }

        //FIXME wikiparser have same method. pull up or expand to utils
        private static boolean addChild(final Section candidate, final Section child) {
            if (candidate.getLevel() < child.getLevel()) {
                candidate.appendSubSection(child);
                child.setParentSection(candidate);
            } else { // search parent
                Section parent = candidate.getParentSection();
                while (parent != null) {
                    if (parent.getLevel() < child.getLevel()) {
                        parent.appendSubSection(child);
                        child.setParentSection(parent);
                        break;
                    }
                    parent = parent.getParentSection();
                }
                if (parent == null) {
                    return false;
                }
            }
            return true;
        }

        // To deal with a header content as a paragraph
        private static List<Sentence> asHeaderContents(final List<Sentence> headerContents) {
            if (headerContents.size() > 0) {
                headerContents.get(0).setIsFirstSentence(true);
            }
            return headerContents;
        }

        private static int outlineLevelOf(final Token t) {
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
                return 6;
            }
        }

        public static void appendSection(final Context c, final Token t) {
            // 1. remain sentence flush to current section
            fixSentencesInto(c);

            // 2. retrieve children for header content create;
            addAsCandidate(c, t, token -> token.asVerbatim());
            final List<Sentence> headerContents = asHeaderContents(compileAsSentenceList(c));

            // 3. create new Section
            Section currentSection = c.builder.getLastSection();
            c.builder.appendSection(new Section(outlineLevelOf(t), headerContents));
            //FIXME move this validate process to addChild
            if (!addChild(currentSection, c.builder.getLastSection())) {
                LOG.warn("Failed to add parent for a Section: "
                         + c.builder.getLastSection().getHeaderContents().get(0));
            }
        }

        private static List<CandidateSentence> candidatesOfSentence(final Token t, final String sep, final Textizer textizer) {
            final Deque<CandidateSentence> o = new ArrayDeque<>();
            for (String s: textizer.do_(t).split("\n")) {
                o.addLast(new CandidateSentence(t.pos.row /* TBD */, s, null, t.pos.col /* TBD */));
                o.addLast(new CandidateSentence(t.pos.row /* TBD */, sep, null, t.pos.col /* TBD */));
            }

            try {
                o.removeLast();
            } catch (final NoSuchElementException ignore) {
            }

            return new ArrayList<CandidateSentence>(o);
        }

        private static void addAsCandidate(final Context c, final Token t, final Textizer textizer) {
            c.candidateSentences.addAll(candidatesOfSentence(t, c.sentenceExtractor.getBrokenLineSeparator(), textizer));
        }

        public static void addAsCandidate(final Context c, final Token t) {
            c.candidateSentences.addAll(candidatesOfSentence(t, c.sentenceExtractor.getBrokenLineSeparator(), token -> token.asTextile()));
        }

        public static void appendParagraph(final Context c) {
            fixSentencesInto(c);
            c.builder.addParagraph();
        }

        private static interface Textizer {
            String do_(Token t);
        }
    }

    /*package*/ static class P {
        public static void walkWith(final Context c, final List<Token> tokens) {
            int itemDepth = 0;

            for (Token t : tokens) {
                switch (t.t) {
                case "PART":
                case "CHAPTER":
                case "SECTION":
                case "SUBSECTION":
                case "SUBSUBSECTION":
                    RP.appendSection(c, t);
                    break;
                case "TEXTILE":
                    if (t.isBlankLine()) {
                        RP.appendParagraph(c);
                    } else {
                        RP.addAsCandidate(c, t);
                    }
                    break;
                }
            }

            RP.fixSentencesInto(c);
        }
    }
}
