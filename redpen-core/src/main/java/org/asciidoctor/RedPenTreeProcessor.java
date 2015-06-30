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

package org.asciidoctor;

import cc.redpen.model.Sentence;
import cc.redpen.parser.LineOffset;
import cc.redpen.parser.SentenceExtractor;
import org.apache.commons.lang3.StringEscapeUtils;
import org.asciidoctor.ast.AbstractBlock;
import org.asciidoctor.ast.BlockImpl;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.SectionImpl;
import org.asciidoctor.extension.Treeprocessor;
import org.jruby.RubyArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * AsciiDoctor tree processor, for use with the "redpen" AsciiDoctor backend, used to populate a RedPen document
 * <p/>
 * This (unfortunately) has to be under an 'org' or 'com' package, rather than a 'cc' package,
 * due to the way JRuby and AsciiDoctor handle the registration of this class.
 */
public class RedPenTreeProcessor extends Treeprocessor {
    private static final Logger LOG = LoggerFactory.getLogger(RedPenTreeProcessor.class);

    private static final char REDPEN_ASCIIDOCTOR_BACKEND_LINE_START = '\001';
    private static final char REDPEN_ASCIIDOCTOR_BACKEND_LINENUMBER_DELIM = '\002';
    private static final char REDPEN_ASCIIDOCTOR_BACKEND_SUBSTITUTION_START = '\003';
    private static final char REDPEN_ASCIIDOCTOR_BACKEND_SUBSTITUTION_END = '\004';

    private cc.redpen.model.Document.DocumentBuilder documentBuilder;
    private SentenceExtractor sentenceExtractor;

    private int lineNumber = 1;

    private int headerNumber = 0;
    private RubyArray headerLinesSource = null;
    private RubyArray headerLinesLineNos = null;

    public RedPenTreeProcessor(cc.redpen.model.Document.DocumentBuilder documentBuilder, SentenceExtractor sentenceExtractor, Map<String, Object> config) {
        super(config);
        this.documentBuilder = documentBuilder;
        this.sentenceExtractor = sentenceExtractor;
    }

    private String getHeaderSource(int headerId) {
        if ((headerLinesSource != null) && (headerId < headerLinesSource.size())) {
            return String.valueOf(headerLinesSource.get(headerId));
        }
        return "";
    }

    private int getHeaderLineNo(int headerId) {
        if ((headerLinesLineNos != null) && (headerId < headerLinesLineNos.size())) {
            return Integer.valueOf((String) headerLinesLineNos.get(headerId));
        }
        return lineNumber;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Document process(Document document) {
        List<Sentence> headers = new ArrayList<>();

        headerLinesSource = (RubyArray) document.getAttributes().get("header_lines_source");
        headerLinesLineNos = (RubyArray) document.getAttributes().get("header_lines_lineNos");

        lineNumber = getHeaderLineNo(headerNumber);
        processParagraph(document.doctitle(), getHeaderSource(headerNumber), headers);

        if (headers.isEmpty()) {
            headers.add(new Sentence(document.doctitle() != null ? document.doctitle() : "", 0));
        }
        documentBuilder.addSection(0, headers);

        headerNumber++;

        traverse(document.blocks(), 0);
        return document;
    }

    @SuppressWarnings("unchecked")
    private void traverse(List<AbstractBlock> blocks, int indent) {
        for (int i = 0; i < blocks.size(); i++) {
            Object item = blocks.get(i);
            if (item instanceof BlockImpl) {
                BlockImpl block = (BlockImpl) item;
                documentBuilder.addParagraph();
                List<Sentence> sentences = new ArrayList<>();
                processParagraph(block.convert(), block.source(), sentences);
                for (Sentence sentence : sentences) {
                    documentBuilder.addSentence(sentence);
                }
            } else if (item instanceof SectionImpl) {
                SectionImpl section = (SectionImpl) item;
                List<Sentence> headers = new ArrayList<>();
                lineNumber = getHeaderLineNo(headerNumber);
                processParagraph(section.title(), getHeaderSource(headerNumber), headers);
                if (headers.isEmpty()) {
                    headers.add(new Sentence(section.title() != null ? section.title() : "", 0));
                }
                documentBuilder.addSection(section.number(), headers);
                headerNumber++;
                traverse(section.blocks(), indent + 1);
            } else if (item != null) {
                AbstractBlock block = (AbstractBlock) item;
                traverse(block.blocks(), indent + 1);
            } else {
                LOG.error("Unhandled AsciiDoctor Block class " + item.getClass().getSimpleName());
            }
        }
    }

    private void processParagraph(String paragraph, String sourceText, List<Sentence> sentences) {
        paragraph = paragraph == null ? "" : paragraph;
        sourceText = sourceText == null ? "" : sourceText;
        int offset = 0;
        String[] sublines = paragraph.split(String.valueOf(REDPEN_ASCIIDOCTOR_BACKEND_LINE_START));
        for (String subline : sublines) {
            int lineNumberEndPos = subline.indexOf(REDPEN_ASCIIDOCTOR_BACKEND_LINENUMBER_DELIM);
            if (lineNumberEndPos != -1) {
                try {
                    lineNumber = Integer.valueOf(subline.substring(0, lineNumberEndPos));
                } catch (Exception e) {
                    LOG.error("Error when parsing line number from converted AsciiDoc", e);
                }
                subline = subline.substring(lineNumberEndPos + 1);
            }
            subline = StringEscapeUtils.unescapeHtml4(subline);

            while (true) {
                int periodPosition = sentenceExtractor.getSentenceEndPosition(subline);
                if (periodPosition != -1) {
                    String candidateSentence = subline.substring(0, periodPosition + 1);
                    subline = subline.substring(periodPosition + 1);
                    periodPosition = sentenceExtractor.getSentenceEndPosition(sourceText);
                    String sourceSentence = "";
                    if (periodPosition != -1) {
                        sourceSentence = sourceText.substring(0, periodPosition + 1);
                        sourceText = sourceText.substring(periodPosition + 1);
                    }
                    LineOffset lineOffset = addSentence(new LineOffset(lineNumber, offset), sourceSentence, candidateSentence, sentenceExtractor, sentences);
                    lineNumber = lineOffset.lineNum;
                    offset = lineOffset.offset;
                } else {
                    break;
                }
            }
            if (!subline.trim().isEmpty()) {
                addSentence(new LineOffset(lineNumber, offset), sourceText, subline, sentenceExtractor, sentences);
            }
        }

        lineNumber++;
    }

    /**
     * Add a processed asciidoc sentence, using the raw source sentence to guide the character offsets
     *
     * @param lineOffset
     * @param source
     * @param processed
     * @param sentenceExtractor
     * @param sentences
     * @return
     */
    private LineOffset addSentence(LineOffset lineOffset, String source, String processed, SentenceExtractor sentenceExtractor, List<Sentence> sentences) {

        List<LineOffset> offsetMap = new ArrayList<>();
        String normalizedSentence = "";

        int lineNum = lineOffset.lineNum;
        int offset = lineOffset.offset;

        int sourceOffset = 0;
        int window = 0;
        int matchLength = 4;
        for (int i = 0; i < processed.length(); i++) {
            char ch = processed.charAt(i);
            switch (ch) {
                case REDPEN_ASCIIDOCTOR_BACKEND_SUBSTITUTION_START:
                    window += 4;
                    break;
                case REDPEN_ASCIIDOCTOR_BACKEND_SUBSTITUTION_END:
                    window = Math.max(0, window - 4);
                    break;
                default:
                    // catch up with the source string using the window and match length
                    if ((sourceOffset < source.length()) && (source.charAt(sourceOffset) != ch)) {
                        String match = processed.substring(i, Math.min(processed.length(), i + matchLength));
                        int pos = match.indexOf(REDPEN_ASCIIDOCTOR_BACKEND_SUBSTITUTION_START);
                        if (pos != -1) {
                            match = match.substring(0, pos);
                        }
                        pos = match.indexOf(REDPEN_ASCIIDOCTOR_BACKEND_SUBSTITUTION_END);
                        if (pos != -1) {
                            match = match.substring(0, pos);
                        }
                        for (int j = 0; (sourceOffset < source.length()); j++, sourceOffset++, offset++) {
                            if (source.substring(sourceOffset).startsWith(match)) {
                                break;
                            }
                        }
                    }

                    if (ch == '\n') {
                        if (!sentenceExtractor.getBrokenLineSeparator().isEmpty()) {
                            offsetMap.add(new LineOffset(lineNum, offset));
                            normalizedSentence += sentenceExtractor.getBrokenLineSeparator();
                        }
                        lineNum++;
                        offset = 0;
                    } else {
                        normalizedSentence += ch;
                        offsetMap.add(new LineOffset(lineNum, offset));
                        offset++;
                    }

                    sourceOffset++;
                    break;
            }
        }
        Sentence sentence = new Sentence(normalizedSentence, lineOffset.lineNum, lineOffset.offset);
        sentence.setOffsetMap(offsetMap);
        sentences.add(sentence);
        return new LineOffset(lineNum, offset);
    }
}