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
    /**
     * the header source lines, and their respective line numbers,
     * as recorded by the patched parser routing in AsciiDocParser
     */
    private RubyArray headerLinesSource = null;
    private RubyArray headerLinesLineNos = null;

    /**
     * Build a tree processer that uses the supplied documentBuilder and sentenceExtractor
     *
     * @param documentBuilder   redpen document builder
     * @param sentenceExtractor redpen sentence extractor
     * @param config            asciidoctor's configuration map
     */
    public RedPenTreeProcessor(cc.redpen.model.Document.DocumentBuilder documentBuilder, SentenceExtractor sentenceExtractor, Map<String, Object> config) {
        super(config);
        this.documentBuilder = documentBuilder;
        this.sentenceExtractor = sentenceExtractor;
    }

    /**
     * Helper method to get the source text for a header line
     *
     * @param headerNumber the number of the header we're after
     * @return
     */
    private String getHeaderSource(int headerNumber) {
        if ((headerLinesSource != null) && (headerNumber < headerLinesSource.size())) {
            return String.valueOf(headerLinesSource.get(headerNumber));
        }
        return "";
    }

    /**
     * Helper method to get the header line numbers for a particular header line
     *
     * @param headerNumber the number of the header we're after
     * @return
     */
    private int getHeaderLineNo(int headerNumber) {
        if ((headerLinesLineNos != null) && (headerNumber < headerLinesLineNos.size())) {
            try {
                return ((Long)headerLinesLineNos.get(headerNumber)).intValue();
            } catch (Exception ignored) {
            }
        }
        return lineNumber;
    }

    /**
     * Process a AsciiDoctorJ document
     *
     * @param document this is an AsciiDoctorJ document, not a redpen document
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public Document process(Document document) {
        List<Sentence> headers = new ArrayList<>();

        // our patched Parser routine should store the header source lines and their line numbers in these attributes
        headerLinesSource = (RubyArray) document.getAttributes().get("header_lines_source");
        headerLinesLineNos = (RubyArray) document.getAttributes().get("header_lines_linenos");

        lineNumber = getHeaderLineNo(headerNumber);

        // parse the sentences/headers from the document title
        processParagraph(document.doctitle(), getHeaderSource(headerNumber), headers);

        if (headers.isEmpty()) {
            headers.add(new Sentence(document.doctitle() != null ? document.doctitle() : "", 0));
        }
        documentBuilder.addSection(0, headers);

        headerNumber++;

        // traverse all of the blocks in the document
        traverse(document.blocks(), 0);
        return document;
    }

    /**
     * Traverse the list of blocks, recursively traversing any lists of blocks found within each block
     *
     * @param blocks
     * @param indent
     */
    @SuppressWarnings("unchecked")
    private void traverse(List<AbstractBlock> blocks, int indent) {
        for (int i = 0; i < blocks.size(); i++) {
            Object item = blocks.get(i);
            // A standard block - we convert the text and process the sentences inside
            if (item instanceof BlockImpl) {
                BlockImpl block = (BlockImpl) item;
                documentBuilder.addParagraph();
                List<Sentence> sentences = new ArrayList<>();
                processParagraph(block.convert(), block.source(), sentences);
                for (Sentence sentence : sentences) {
                    documentBuilder.addSentence(sentence);
                }
            }
            // A section - this has a header, and contains other blocks
            else if (item instanceof SectionImpl) {
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
            }
            // catchall for all other abstract blocks
            else if (item != null) {
                AbstractBlock block = (AbstractBlock) item;
                traverse(block.blocks(), indent + 1);
            } else {
                LOG.error("Unhandled AsciiDoctor Block class " + item.getClass().getSimpleName());
            }
        }
    }

    /**
     * Process a paragraph of processed text.
     * <p/>
     * This method takes the processed form of the text generated by the Ruby RedPen AsciiDoctor backend found in AsciiDocParser.java
     * If the source form of the paragraph is also provided, it uses this to calculate the RedPen offsets for the characters in the text.
     * <p/>
     * The AsciiDoctor parser does not return the line numbers via the AsciiDoctorJ Block interface. Therefore, the custom RedPen AsciiDoctor backend
     * encodes the line number between ^A and ^B, if known.
     * <p/>
     * Hence the processed text for a paragraph is typically:
     * <p/>
     * ^Alinenumber^Bparagraph_text
     * <p/>
     * This method first breaks the paragraph on these line markers and adjusts the running lineNumber variable,
     * and then processes each sub-paragraph/sentence using standard RedPen sentence-end-position delimiting.
     *
     * @param paragraph  the AsciiDoctor processed text, already converted by the RedPen AsciiDoctor backend
     * @param sourceText the raw source text
     * @param sentences  A list of sentences discovered in the processed text
     */
    protected void processParagraph(String paragraph, String sourceText, List<Sentence> sentences) {
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
                    LineOffset lineOffset = addSentence(
                            new LineOffset(lineNumber, offset),
                            candidateSentence,
                            sourceSentence,
                            sentenceExtractor,
                            sentences);
                    lineNumber = lineOffset.lineNum;
                    offset = lineOffset.offset;
                } else {
                    break;
                }
            }
            if (!subline.trim().isEmpty()) {
                addSentence(
                        new LineOffset(lineNumber, offset),
                        subline,
                        sourceText,
                        sentenceExtractor,
                        sentences);
            }
        }

        lineNumber++;
    }

    /**
     * Add a processed AsciiDoc sentence, using the raw source sentence to guide the character offsets.
     * <p/>
     * Since AsciiDoctor does not return character offset positions for altered/formatted elements, this method
     * attempts to calculate the offsets by comparing the source sentence with the post-processed sentence.
     * <p/>
     * To assist with the calculation, the RedPen AsciiDoctor backend places markers in the text where
     * AsciiDoctor has subsituted or altered the source sentence.
     * <p/>
     * This is a normal function of AsciiDoctor. For example, AsciiDoctor's HTML backend will convert:
     * <p/>
     * This is *bold*.
     * <p/>
     * to:
     * <p/>
     * This is &lt;strong&gt;bold&lt;/strong&gt;.
     * <p/>
     * The RedPen AsciiDoctor backend uses ^C and ^D for all such substitutions, regardless of how many characters the
     * markup originally took. For example, both _ and __ markup notations are replaced by a single ^C or ^D.
     * So after processing using the RedPen AsciiDoctor backend, our sentence would look like:
     * <p/>
     * This is ^Cbold^D.
     * <p/>
     * This method uses these ^C/^D markers to calculate what has been omitted from the source sentence, and
     * therefore what the source sentence character offset should be for the normalized sentence.
     *
     * @param lineOffset        the position of this line
     * @param processed         the RedPen AsciiDoctor backend processed sentence
     * @param source            the source sentence
     * @param sentenceExtractor our sentence extractor
     * @param sentences         a list of sentences to add discovered sentences to
     * @return the new offset position, after we've processed these sentences
     */
    protected LineOffset addSentence(LineOffset lineOffset, String processed, String source, SentenceExtractor sentenceExtractor, List<Sentence> sentences) {

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