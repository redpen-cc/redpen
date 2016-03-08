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
import java.util.stream.IntStream;

import static cc.redpen.parser.WikiParser.LinePattern.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

/**
 * Parser for wiki formatted file.
 */
class WikiParser extends BaseDocumentParser {
    private static final Logger LOG = LoggerFactory.getLogger(WikiParser.class);

    /*
     * *************************************************************************
     * patterns to handle wiki syntax.
     * *************************************************************************
     */

    private static final Pattern HEADER_PATTERN
            = Pattern.compile("^h([1-6])\\. (.*)$");
    private static final Pattern LIST_PATTERN = Pattern.compile("^(-+) (.*)$");
    private static final Pattern NUMBERED_LIST_PATTERN =
            Pattern.compile("^(#+) (.*)$");
    private static final Pattern LINK_PATTERN =
            Pattern.compile("\\[\\[\\s*(.*?)(?:\\s*\\|\\s*(.*?)(?:\\s*\\|.*?)?)?\\s*\\]\\]");
    private static final Pattern BEGIN_COMMENT_PATTERN =
            Pattern.compile("^\\s*\\[!--");
    private static final Pattern END_COMMENT_PATTERN =
            Pattern.compile("--\\]\\s*$");
    private static final Pattern ITALIC_PATTERN =
            Pattern.compile("//(.+?)//");
    private static final Pattern UNDERLINE_PATTERN =
            Pattern.compile("__(.+?)__");
    private static final Pattern BOLD_PATTERN =
            Pattern.compile("\\*\\*(.+?)\\*\\*");
    private static final Pattern STRIKETHROUGH_PATTERN =
            Pattern.compile("--(.+?)--");
    private static final Pattern[] INLINE_PATTERNS = {
            ITALIC_PATTERN,
            BOLD_PATTERN,
            UNDERLINE_PATTERN,
            STRIKETHROUGH_PATTERN
    };

    private static boolean check(Pattern p, String target, int lineNum, List<ValueWithOffsets> groups) {
        Matcher m = p.matcher(target);
        if (m.matches()) {
            for (int i = 1; i <= m.groupCount(); i++) {
                groups.add(new ValueWithOffsets(m.group(i), offsets(lineNum, range(m.start(i), m.end(i)))));
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Document parse(InputStream is, Optional<String> filename, SentenceExtractor sentenceExtractor,
                          RedPenTokenizer tokenizer) throws RedPenException{
        Document.DocumentBuilder documentBuilder = Document.builder(tokenizer);
        filename.ifPresent(documentBuilder::setFileName);
        BufferedReader br;

        // for sentences right below the beginning of document
        List<Sentence> headers = new ArrayList<>();
        headers.add(new Sentence("", 1));
        documentBuilder.addSection(0, headers);

        // begin parsing
        LinePattern prevPattern, currentPattern = VOID;
        String line;
        int lineNum = 1;
        ValueWithOffsets remain = new ValueWithOffsets();
        br = createReader(is);
        try {
            while ((line = br.readLine()) != null) {
                prevPattern = currentPattern;
                List<ValueWithOffsets> groups = new ArrayList<>();
                if (currentPattern == COMMENT && check(END_COMMENT_PATTERN, line, lineNum, groups)) {
                    currentPattern = VOID;
                } else if (check(HEADER_PATTERN, line, lineNum, groups)) {
                    currentPattern = HEADER;
                    appendSection(groups, sentenceExtractor, documentBuilder);
                } else if (check(LIST_PATTERN, line, lineNum, groups)) {
                    currentPattern = LIST;
                    appendListElement(prevPattern, groups, sentenceExtractor, documentBuilder);
                } else if (check(NUMBERED_LIST_PATTERN, line, lineNum, groups)) {
                    currentPattern = LIST;
                    appendListElement(prevPattern, groups, sentenceExtractor, documentBuilder);
                } else if (check(BEGIN_COMMENT_PATTERN, line, lineNum, groups) && !check(END_COMMENT_PATTERN, line, lineNum, groups)) { // skip comment
                    currentPattern = COMMENT;
                } else if (line.equals("")) { // new paragraph content
                    documentBuilder.addParagraph();
                } else { // usual sentence.
                    currentPattern = SENTENCE;
                    remain = appendSentencesIntoSection(remain.append(line, offsets(lineNum, range(0, line.length()))), sentenceExtractor, documentBuilder);
                }
                lineNum++;
            }
        } catch (IOException e) {
            throw new RedPenException(e);
        }
        if (!remain.isEmpty()) {
            appendLastSentence(remain, documentBuilder);
        }
        return documentBuilder.build();

    }

    private void appendListElement(LinePattern prevPattern, List<ValueWithOffsets> head, SentenceExtractor sentenceExtractor, Document.DocumentBuilder builder) {
        if (prevPattern != LIST) {
            builder.addListBlock();
        }
        List<Sentence> outputSentences = new ArrayList<>();
        ValueWithOffsets remainSentence = obtainSentences(head.get(1), outputSentences, sentenceExtractor);
        builder.addListElement(extractListLevel(head.get(0).getContent()), outputSentences);
        // NOTE: for list content without period
        if (!remainSentence.isEmpty()) {
            outputSentences.add(new Sentence(remainSentence.getContent(), remainSentence.getOffsetMap(), new ArrayList<>()));
        }
    }

    private Section appendSection(List<ValueWithOffsets> head, SentenceExtractor sentenceExtractor, Document.DocumentBuilder builder) {
        Integer level = Integer.valueOf(head.get(0).getContent());
        List<Sentence> outputSentences = new ArrayList<>();
        ValueWithOffsets remainHeader = obtainSentences(head.get(1), outputSentences, sentenceExtractor);
        // NOTE: for header without period
        if (!remainHeader.isEmpty()) {
            outputSentences.add(new Sentence(remainHeader.getContent(), remainHeader.getOffsetMap(), new ArrayList<>()));
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

    private void appendLastSentence(ValueWithOffsets remain, Document.DocumentBuilder builder) {
        Sentence sentence = new Sentence(remain.getContent(), remain.getOffsetMap(), new ArrayList<>());
        parseSentence(sentence); // extract inline elements
        builder.addSentence(sentence);
    }

    private void parseSentence(Sentence sentence) {
        extractLinks(sentence);
        removeTags(sentence);
    }

    private void removeTags(Sentence sentence) {
        String content = sentence.getContent();
        List<LineOffset> offsets = new ArrayList<>(sentence.getOffsetMap());
        for (Pattern inlinePattern : INLINE_PATTERNS) {
            Matcher m = inlinePattern.matcher(content);
            StringBuffer sb = new StringBuffer();
            List<LineOffset> o = new ArrayList<>(sentence.getOffsetMap().size());
            int lastPos = 0;
            while (m.find()) {
                m.appendReplacement(sb, "$1");
                o.addAll(offsets.subList(lastPos, m.start()));
                o.addAll(offsets.subList(m.start(1), m.end(1)));
                lastPos = m.end();
            }
            m.appendTail(sb);
            o.addAll(offsets.subList(lastPos, offsets.size()));
            content = sb.toString();
            offsets = o;
        }
        sentence.setContent(content);
        sentence.setOffsetMap(offsets);
    }

    private void extractLinks(Sentence sentence) {
        StringBuilder modContent = new StringBuilder();
        List<LineOffset> modOffsets = new ArrayList<>();
        int start = 0;
        Matcher m = LINK_PATTERN.matcher(sentence.getContent());
        while (m.find()) {
            modContent.append(sentence.getContent().substring(start, m.start()));
            modOffsets.addAll(sentence.getOffsetMap().subList(start, m.start()));

            modContent.append(sentence.getContent().substring(m.start(1), m.end(1)));
            modOffsets.addAll(sentence.getOffsetMap().subList(m.start(1), m.end(1)));

            if (m.start(2) < 0)
                sentence.addLink(sentence.getContent().substring(m.start(1), m.end(1)));
            else
                sentence.addLink(sentence.getContent().substring(m.start(2), m.end(2)));
            start = m.end();
        }

        if (start > 0) {
            modContent.append(sentence.getContent().substring(start, sentence.getContent().length()));
            modOffsets.addAll(sentence.getOffsetMap().subList(start, sentence.getContent().length()));
            sentence.setContent(modContent.toString());
            sentence.setOffsetMap(modOffsets);
        }
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

    private ValueWithOffsets obtainSentences(ValueWithOffsets value, List<Sentence> outputSentences, SentenceExtractor sentenceExtractor) {
        List<Pair<Integer,Integer>> positions = new ArrayList<>();
        int lastPosition = sentenceExtractor.extract(value.getContent(), positions);

        for (Pair<Integer, Integer> position : positions) {
            outputSentences.add(value.extract(position.first, position.second));
        }

        outputSentences.forEach(this::parseSentence); // extract inline elements
        return value.extract(lastPosition, value.getContent().length());
    }

    private ValueWithOffsets appendSentencesIntoSection(ValueWithOffsets value, SentenceExtractor sentenceExtractor, Document.DocumentBuilder builder) {
        List<Sentence> outputSentences = new ArrayList<>();
        ValueWithOffsets newRemain = obtainSentences(value, outputSentences, sentenceExtractor);
        outputSentences.forEach(builder::addSentence);
        return newRemain;
    }

    private static List<LineOffset> offsets(int lineNum, IntStream stream) {
        return stream.mapToObj(p -> new LineOffset(lineNum, p)).collect(toList());
    }

    private int extractListLevel(String listPrefix) {
        return listPrefix.length();
    }

    /**
     * List of elements used in wiki format.
     */
    enum LinePattern {
        SENTENCE, LIST, NUM_LIST, VOID, HEADER, COMMENT
    }
}
