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

/**
 * Parser for wiki formatted file.
 */
final class WikiParser extends BaseDocumentParser {
    private static final Logger LOG = LoggerFactory.getLogger(WikiParser.class);
    /**
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
            Pattern.compile("\\[\\[(.*?)\\]\\]");
    private static final Pattern BEGIN_COMMENT_PATTERN =
            Pattern.compile("\\s*^\\[!--");
    private static final Pattern END_COMMENT_PATTERN =
            Pattern.compile("--\\]$\\s*");
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

    /**
     * Constructor.
     */
    WikiParser() {
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
        Document.DocumentBuilder documentBuilder = new Document.DocumentBuilder(tokenizer);
        filename.ifPresent(documentBuilder::setFileName);
        BufferedReader br;

        // for sentences right below the beginning of document
        List<Sentence> headers = new ArrayList<>();
        headers.add(new Sentence("", 1));
        documentBuilder.addSection(0, headers);

        // begin parsing
        LinePattern prevPattern, currentPattern = LinePattern.VOID;
        String line;
        int lineNum = 1;
        StringBuilder remain = new StringBuilder();
        br = createReader(is);
        try {
            while ((line = br.readLine()) != null) {
                prevPattern = currentPattern;
                List<String> head = new ArrayList<>();
                if (currentPattern == LinePattern.COMMENT) {
                    if (check(END_COMMENT_PATTERN, line, head)) {
                        currentPattern = LinePattern.VOID;
                    }
                } else if (check(HEADER_PATTERN, line, head)) {
                    currentPattern = LinePattern.HEADER;
                    appendSection(head, lineNum, sentenceExtractor, documentBuilder);
                } else if (check(LIST_PATTERN, line, head)) {
                    currentPattern = LinePattern.LIST;
                    appendListElement(prevPattern, head, lineNum, sentenceExtractor,documentBuilder);
                } else if (check(NUMBERED_LIST_PATTERN, line, head)) {
                    currentPattern = LinePattern.LIST;
                    appendListElement(prevPattern, head, lineNum, sentenceExtractor,documentBuilder);
                } else if (check(BEGIN_COMMENT_PATTERN, line, head)) {
                    if (!check(END_COMMENT_PATTERN, line, head)) { // skip comment
                        currentPattern = LinePattern.COMMENT;
                    }
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
        return documentBuilder.build();

    }

    private void appendListElement(LinePattern prevPattern,
                                   List<String> head, int lineNum, SentenceExtractor sentenceExtractor, Document.DocumentBuilder builder) {
        if (prevPattern != LinePattern.LIST) {
            builder.addListBlock();
        }
        List<Sentence> outputSentences = new ArrayList<>();
        String remainSentence = obtainSentences(0, head.get(1), outputSentences, sentenceExtractor);
        builder.addListElement(extractListLevel(head.get(0)),
                outputSentences);
        // NOTE: for list content without period
        if (remainSentence != null && remainSentence.length() > 0) {
            outputSentences.add(new Sentence(remainSentence, lineNum));
        }
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
        for (Pattern inlinePattern : INLINE_PATTERNS) {
            Matcher m = inlinePattern.matcher(content);
            content = m.replaceAll("$1");
        }
        sentence.setContent(content);
    }

    private void extractLinks(Sentence sentence) {
        StringBuilder modContent = new StringBuilder();
        int start = 0;
        Matcher m = LINK_PATTERN.matcher(sentence.getContent());
        while (m.find()) {
            String[] tagInternal = m.group(1).split("\\|");
            String tagURL = null;
            if (tagInternal.length == 1) {
                tagURL = tagInternal[0].trim();
                modContent.append(sentence.getContent().substring(
                        start, m.start())).append(tagURL.trim());
            } else if (tagInternal.length == 0) {
                LOG.warn("Invalid link block: vacant block");
                tagURL = "";
            } else {
                if (tagInternal.length > 2) {
                    LOG.warn(
                            "Invalid link block: there are more than two link blocks at line "
                                    + sentence.getLineNumber());
                }
                tagURL = tagInternal[1].trim();
                StringBuilder buffer = new StringBuilder();
                buffer.append(sentence.getContent().substring(start, m.start()));
                buffer.append(tagInternal[0].trim());
                modContent.append(buffer);
            }
            sentence.addLink(tagURL);
            start = m.end();
        }

        if (start > 0) {
            modContent.append(sentence.getContent().substring(
                    start, sentence.getContent().length()));
            sentence.setContent(modContent.toString());
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

    private int extractListLevel(String listPrefix) {
        return listPrefix.length();
    }

    /**
     * List of elements used in wiki format.
     */
    private enum LinePattern {
        SENTENCE, LIST, NUM_LIST, VOID, HEADER, COMMENT
    }
}
