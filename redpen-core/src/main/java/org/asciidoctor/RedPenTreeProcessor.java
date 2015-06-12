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

import cc.redpen.model.Section;
import cc.redpen.model.Sentence;
import cc.redpen.parser.LineOffset;
import cc.redpen.parser.SentenceExtractor;
import org.apache.commons.lang3.StringEscapeUtils;
import org.asciidoctor.ast.AbstractBlock;
import org.asciidoctor.ast.BlockImpl;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.SectionImpl;
import org.asciidoctor.extension.Treeprocessor;
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

    private cc.redpen.model.Document.DocumentBuilder documentBuilder;
    private SentenceExtractor sentenceExtractor;

    private int lineNumber = 1;

    public RedPenTreeProcessor(cc.redpen.model.Document.DocumentBuilder documentBuilder, SentenceExtractor sentenceExtractor, Map<String, Object> config) {
        super(config);
        this.documentBuilder = documentBuilder;
        this.sentenceExtractor = sentenceExtractor;
    }

    @Override
    public Document process(Document document) {
        List<Sentence> headers = new ArrayList<>();
        headers.add(new Sentence(document.doctitle() != null ? document.doctitle() : "", 0));
        documentBuilder.appendSection(new Section(0, headers));
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
                processParagraph(block.convert().trim());
            } else if (item instanceof SectionImpl) {
                SectionImpl section = (SectionImpl) item;
                List<Sentence> headers = new ArrayList<>();
                headers.add(new Sentence(section.title() != null ? section.title() : "", 0));
                documentBuilder.appendSection(new Section(section.number(), headers));
                traverse(section.blocks(), indent + 1);
            } else if (item != null) {
                AbstractBlock block = (AbstractBlock) item;
                traverse(block.blocks(), indent + 1);
            } else {
                LOG.error("Unhandled AsciiDoctor Block class " + item.getClass().getSimpleName());
            }
        }
    }

    private void processParagraph(String paragraph) {
        String sentenceText = "";
        int offset = 0;
        String[] sublines = paragraph.split("\001");
        for (String subline : sublines) {
            int lineNumberEndPos = subline.indexOf('\002');
            if (lineNumberEndPos != -1) {
                try {
                    lineNumber = Integer.valueOf(subline.substring(0, lineNumberEndPos));
                } catch (Exception e) {
                    LOG.error("Error when parsing line number from converted AsciiDoc", e);
                }
                subline = subline.substring(lineNumberEndPos + 1);
            }
            subline = StringEscapeUtils.unescapeHtml4(subline);

            sentenceText += subline;

            while (true) {
                int periodPosition = sentenceExtractor.getSentenceEndPosition(sentenceText);
                if (periodPosition != -1) {
                    String completeSentence = sentenceText.substring(0, periodPosition + 1);
                    LineOffset lineOffset = addSentence(completeSentence, lineNumber, offset);
                    lineNumber = lineOffset.lineNum;
                    offset = lineOffset.offset;
                    sentenceText = sentenceText.substring(periodPosition + 1);
                } else {
                    break;
                }
            }
            lineNumber++;
        }

        if (!sentenceText.trim().isEmpty()) {
            addSentence(sentenceText, lineNumber, offset);
        }
    }

    public LineOffset addSentence(String rawSentenceText, int lineNumber, int offset) {
        List<LineOffset> offsetMap = new ArrayList<>();
        String normalizedSentence = "";
        for (int i = 0; i < rawSentenceText.length(); i++) {
            char ch = rawSentenceText.charAt(i);
            if (ch == '\n') {
                if (!sentenceExtractor.getBrokenLineSeparator().isEmpty()) {
                    offsetMap.add(new LineOffset(lineNumber, offset));
                    normalizedSentence += sentenceExtractor.getBrokenLineSeparator();
                }
                lineNumber++;
                offset = 0;
            } else {
                normalizedSentence += ch;
                offsetMap.add(new LineOffset(lineNumber, offset));
                offset++;
            }
        }
        Sentence sentence = new Sentence(normalizedSentence, lineNumber, offset);
        sentence.setOffsetMap(offsetMap);
        documentBuilder.addSentence(sentence);

        return new LineOffset(lineNumber, offset);
    }

}