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
package cc.redpen.parser;

import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.model.Section;
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.RedPenTokenizer;
import cc.redpen.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;
import org.apache.commons.lang3.StringUtils;

/**
 * Parser for LaTeX formatted file.
 */
final class LaTeXParser extends BaseDocumentParser {
    private static final Logger LOG = LoggerFactory.getLogger(LaTeXParser.class);

    LaTeXParser() {
        super();
    }

    private static boolean check(Pattern p, String target, List<String> head) {
        Matcher m = p.matcher(target);
        if (m.matches()) {
            for (int i = 1; i <= m.groupCount(); i++) {
                head.add(m.group(i));
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Document parse(InputStream is, Optional<String> filename, SentenceExtractor sentenceExtractor,
                          RedPenTokenizer tokenizer) throws RedPenException{
        final Document.DocumentBuilder documentBuilder = new Document.DocumentBuilder(tokenizer);
        filename.ifPresent(documentBuilder::setFileName);
        BufferedReader br;

        // for sentences right below the beginning of document
        List<Sentence> headers = new ArrayList<>();
        headers.add(new Sentence("", 1));
        documentBuilder.addSection(0, headers);

        // begin parsing
        String line;
        int lineNum = 1;
        StringBuilder remain = new StringBuilder();
        String source = new Scanner(createReader(is)).useDelimiter("\\A").next();
        System.out.println(source);
        new Parser(source, (final String type_, final int linenr, final int start, final String body) -> {
                       switch (type_) {
                       case "paragraph":
                           System.out.println(String.format("%s(%d:%d): %s", type_, linenr, start, Pattern.compile(" {2,}|(?:\\r?\\n)", Pattern.DOTALL).matcher(body).replaceAll(" ")));
                           break;
                       default:
                           System.out.println(String.format("%s(%d:%d): %s", type_, linenr, start, body));
                       }
        }).parse();
        /*
        try {
            // XXX: cannot look further line-boundary yet
            while ((line = br.readLine()) != null) {
                prevPattern = currentPattern;
                List<String> head = new ArrayList<>();
                if (check(HEADER_PATTERN, line, head)) {
                    currentPattern = LinePattern.HEADER;
                    appendSection(head, lineNum, sentenceExtractor, documentBuilder);
                } else if (line.equals("")) { // new paragraph content
                    documentBuilder.addParagraph();
                } else { // usual sentence.
                    currentPattern = LinePattern.SENTENCE;
                    String remainStr = appendSentencesIntoSection(lineNum,
                            remain.append(line).toString(), sentenceExtractor, documentBuilder);
                    remain.delete(0, remain.length());
                    remain.append(remainStr);
                }
                lineNum++;
            }
        } catch (IOException e) {
            throw new RedPenException(e);
        }
        if (remain.length() > 0) {
            appendLastSentence(lineNum-1, remain.toString(), documentBuilder); // lineNum-1 since the lineNum is incremented.
        }
        */
        return documentBuilder.build();

    }


    private Section appendSection(List<String> head, int lineNum, SentenceExtractor sentenceExtractor, Document.DocumentBuilder builder) {
        Integer level = Integer.valueOf(head.get(0));
        List<Sentence> outputSentences = new ArrayList<>();
        String remainHeader =
                obtainSentences(lineNum, head.get(1), outputSentences, sentenceExtractor);
        // NOTE: for header without period
        if (remainHeader != null && remainHeader.length() > 0) {
            outputSentences.add(new Sentence(remainHeader, lineNum));
        }

        // To deal with header content as a paragraph
        if (outputSentences.size() > 0) {
            outputSentences.get(0).setIsFirstSentence(true);
        }
        Section currentSection = builder.getLastSection();
        builder.addSection(level, outputSentences);
        Section tmpSection = builder.getLastSection();
        if (!addChild(currentSection, tmpSection)) {
            LOG.warn("Failed to add parent for a Section: "
                    + tmpSection.getHeaderContents().get(0));
        }
        currentSection = tmpSection;
        return currentSection;
    }

    private void appendLastSentence(int lineNum, String remain, Document.DocumentBuilder builder) {
        Sentence sentence = new Sentence(remain, lineNum);
        parseSentence(sentence); // extract inline elements
        builder.addSentence(sentence);
    }

    private void parseSentence(Sentence sentence) {
        extractLinks(sentence);
        removeTags(sentence);
    }

    private void removeTags(Sentence sentence) {
        String content = sentence.getContent();
        sentence.setContent(content);
    }

    private void extractLinks(Sentence sentence) {
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean addChild(Section candidate, Section child) {
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

    private String obtainSentences(int lineNum, String line,
                                   List<Sentence> outputSentences, SentenceExtractor sentenceExtractor) {
        List<Pair<Integer,Integer>> positions = new ArrayList<>();
        int lastPosition = sentenceExtractor.extract(line, positions);

        for (Pair<Integer, Integer> position : positions) {
            outputSentences.add(new Sentence(line.substring(
                    position.first, position.second), lineNum));
        }

        for (Sentence sentence : outputSentences) {
            parseSentence(sentence); // extract inline elements
        }
        return line.substring(lastPosition, line.length());
    }

    private String appendSentencesIntoSection(int lineNum, String line, SentenceExtractor sentenceExtractor, Document.DocumentBuilder builder) {
        List<Sentence> outputSentences = new ArrayList<>();
        String remain = obtainSentences(lineNum, line, outputSentences, sentenceExtractor);

        for (Sentence sentence : outputSentences) {
            builder.addSentence(sentence);
        }
        return remain;
    }

    /**
     * Experimental LaTeX parser prototype.
     */
    private static class Parser {
        private String mSource;
        private String mContext = null;
        private int mParagraphStart = -1;
        private List<String> mParagraphLines = new ArrayList<>();
        private int mLineNr = 1;
        private Listener mListener = null;
        private static final String MASK = " ";

        public Parser(final String s, final Listener l) {
            mSource = s;
            mListener = l;
        }

        // TBD: cannot handle multilined structure yet
        public void parse() {
            final List<String> ret = new ArrayList<>();
            final Scanner s = new Scanner(mSource).useDelimiter("\r?\n");

            mLineNr = 1;
            while (s.hasNext()) {
                final String orig = s.nextLine();
                final Morpher l = Morpher.on(orig);

                mContext = null;

                // 1. comment
                l.replace("%.*", (final Matcher m) -> mark_comment(m.group(0)));
                // 2. verbatim alikes
                l.replace("\\\\verbatim(.).*?\\1|\\\\begin\\{verbatim\\}.*?\\end\\{verbatim\\}", (final Matcher m) -> mark_verbatim_alike(m.group(0)));
                // 3. outlines
                l.replace("(\\\\((?:sub)*(?:section|chapter|paragraph)|item|title|abstract).?\\{)(.*?)(\\})", (final Matcher m) -> mark_outline(m.group(1), m.group(2), m.group(3), m.group(4)));
                l.replace("(\\\\(item)) +(.*)", (final Matcher m) -> mark_outline(m.group(1), m.group(2), m.group(3), ""));
                // 4. invisible stuff (or ones need not to be visible)
                l.replace("\\\\(begin|end|.?space|.?fill|phantom|label)\\{.*?\\}", (final Matcher m) -> mark_invisible(m.group(0)));
                // 5. uninterested control sequences
                l.replace("\\\\[^ ]+(\\s)?", (final Matcher m) -> mark_generic(m.group(0) + (m.groupCount() > 0 ? m.group(1) : "")));
                // 6. double quotations
                l.replace("``|’’|''", (final Matcher m) -> mark_quote_double(m.group(0)));
                // 7. single quotations
                l.replace("`|’|'", (final Matcher m) -> mark_quote_single(m.group(0)));
                // 8. uninterested symbols
                l.replace("\\{|\\}", (final Matcher m) -> mark_generic(m.group(0)));
                // 9. formulas
                l.replace("\\$.*?\\$", (final Matcher m) -> mark_verbatim_alike(m.group(0)));
                // remains are assumed to contributing to visible textile (thusly a paragraph)
                if (mContext == null) {
                    maybe_paragraph(l.toString(), orig);
                } else {
                    if (mParagraphLines.size() > 0) {
                        flush_paragraph();
                    }
                }

                mLineNr += 1;
            }

            if (mParagraphLines.size() > 0) {
                flush_paragraph();
            }
        }

        /**
         * Returns a masking string for the given string.
         */
        private static String masked(final String t) {
            return mask(t.length());
        }

        /**
         * Returns a masking string of the given length.
         */
        private static String mask(final int n) {
            return StringUtils.repeat(MASK, n);
        }

        /**
         * Returns an effective starting position for the given string (1-based.)
         */
        private static int literal_start_of(final String s) {
            final Matcher m = Pattern.compile("\\S").matcher(s);
            return 1 + (m.find() ? m.start() : 0);
        }

        /**
         * Marks the given string as comment.
         */
        private static String mark_comment(final String region) {
            return masked(region);
        }

        /**
         * Marks the given string as verbatim-alike.
         */
        private static String mark_verbatim_alike(final String region) {
            return masked(region);
        }
        /**
         * Marks the given string as an outline.
         */
        private String mark_outline(final String marker, final String type_, final String content, final String trailer) {
            final String block = masked(marker) + content + masked(trailer);
            mContext = type_;
            grok_outline(type_, block, mLineNr);
            return block;
        }
        /**
         * Marks the given string so that it should not be visible on typeset.
         */
        private static String mark_invisible(final String region) {
            return masked(region);
        }
        /**
         * Marks the given string as uninterested.
         */
        private static String mark_generic(final String region) {
            return masked(region);
        }
        /**
         * Marks the given string as the double-quotation.
         */
        private static String mark_quote_double(final String region) {
            return "\"" + mask(1);
        }
        /**
         * Marks the given string as the single-quotation.
         */
        private static String mark_quote_single(final String region) {
            return "'";
        }

        private void flush_paragraph() {
            maybe_paragraph("", "");
        }


        /**
         * Accumlates the given string to the paragraph buffer, initiating a paragraph parse on paragraph boundary (i.e. empty line.)
         */
        private void maybe_paragraph(final String l, final String orig) {
            if (!Pattern.matches("^\\s*$", l)) {
                mParagraphStart = mLineNr;
                mParagraphLines.add(l);
            } else {
                if (Pattern.matches("^\\s*$", orig)) {
                    // true empty line; terminate current paragraph
                    if (mParagraphLines.size() > 0) {
                        grok_paragraph(StringUtils.join(mParagraphLines, "\n"), mParagraphStart);
                        mParagraphLines.clear();
                        mParagraphStart = -1;
                    }
                } else {
                    // lone control seq
                }
            }
        }

        /**
         * Parses outlines; simply calls back.
         */
        private void grok_outline(final String type_, final String h, final int linenr) {
            final int start = literal_start_of(h);
            mListener.element(type_, linenr, start, h.substring(start-1));
        }

        /**
         * Parses paragraphs; calls back.
         */
        private void grok_paragraph(final String p, final int linenr) {
            final int start = literal_start_of(p);
            mListener.element("paragraph", linenr, start, p);
        }

        public static interface Listener {
            public void element(String type_, int linenr, int start, String body);
        }

        private static class Morpher {
            private String mTarget;

            private Morpher(final String s) {
                mTarget = s;
            }

            public static Morpher on(final String s) {
                return new Morpher(s);
            }

            public String replace(final String r, final String t) {
                return Pattern.compile(r).matcher(mTarget).replaceAll(t);
            }

            public String replace(final String r, final Listener l) {
                final Pattern p = Pattern.compile(r);
                for (Matcher m = p.matcher(mTarget); m.find(); m = p.matcher(mTarget)) {
                    mTarget = m.replaceFirst(l.hit(m));
                }
                return mTarget;
            }

            public String toString() {
                return mTarget;
            }

            public static interface Listener {
                public String hit(Matcher m);
            }
        }
    }
}
