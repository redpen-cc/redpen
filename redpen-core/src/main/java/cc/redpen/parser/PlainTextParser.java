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
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.RedPenTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Parser for plain text file.
 */
final public class PlainTextParser extends BaseDocumentParser implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(PlainTextParser.class);
    private static final long serialVersionUID = -4343255148183552844L;

    /**
     * Constructor.
     */
    PlainTextParser() {
        super();
    }

    @Override
    public Document parse(InputStream is, Optional<String> fileName, SentenceExtractor sentenceExtractor, RedPenTokenizer tokenizer)
            throws RedPenException {
        Document.DocumentBuilder documentBuilder = new Document.DocumentBuilder(tokenizer);
        fileName.ifPresent(documentBuilder::setFileName);

        List<Sentence> headers = new ArrayList<>();
        headers.add(new Sentence("", 0));
        documentBuilder.addSection(0, headers);
        documentBuilder.addParagraph();

        BufferedReader br = createReader(is);
        String line;
        int linesRead = 0;
        int startLine = 1;
        String paragraph = "";
        try {
            while ((line = br.readLine()) != null) {
                linesRead++;
                if (line.equals("")) {
                    if (!paragraph.isEmpty()) {
                        this.extractSentences(startLine, paragraph, sentenceExtractor, documentBuilder);
                    }
                    startLine = linesRead + 1;
                    documentBuilder.addParagraph();
                    paragraph = "";
                } else {
                    paragraph += (paragraph.isEmpty() ? "" : "\n") + line;
                }
            }
        } catch (IOException e) {
            throw new RedPenException(e);
        }

        if (!paragraph.isEmpty()) {
            this.extractSentences(startLine, paragraph, sentenceExtractor, documentBuilder);
        }
        return documentBuilder.build();
    }


    /* Add a sentence with offsets */
    public static LineOffset addSentence(LineOffset lineOffset, String rawSentenceText, SentenceExtractor sentenceExtractor, Document.DocumentBuilder builder) {

        int lineNum = lineOffset.lineNum;
        int offset = lineOffset.offset;

        int sentenceStartLineNum = lineNum;
        int sentenceStartLineOffset = offset;

        List<LineOffset> offsetMap = new ArrayList<>();
        String normalizedSentence = "";
        int i;
        // skip leading line breaks to find the start line of the sentence
        for (i = 0; i < rawSentenceText.length(); i++) {
            char ch = rawSentenceText.charAt(i);
            if (ch == '\n') {
                sentenceStartLineNum++;
                lineNum++;
                sentenceStartLineOffset = 0;
                offset = 0;
            } else {
                break;
            }
        }
        for (; i < rawSentenceText.length(); i++) {
            char ch = rawSentenceText.charAt(i);
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
        }
        Sentence sentence = new Sentence(normalizedSentence, sentenceStartLineNum, sentenceStartLineOffset);
        sentence.setOffsetMap(offsetMap);
        builder.addSentence(sentence);

        return new LineOffset(lineNum, offset);
    }

    /* Extract sentences from a paragraph */
    private void extractSentences(int lineNum, String paragraphText, SentenceExtractor sentenceExtractor, Document.DocumentBuilder builder) {
        int periodPosition = sentenceExtractor.getSentenceEndPosition(paragraphText);
        LineOffset lineOffset = new LineOffset(lineNum, 0);
        if (periodPosition == -1) {
            addSentence(lineOffset, paragraphText, sentenceExtractor, builder);
        } else {
            while (true) {
                lineOffset = addSentence(lineOffset, paragraphText.substring(0, periodPosition + 1), sentenceExtractor, builder);
                paragraphText = paragraphText.substring(periodPosition + 1, paragraphText.length());
                periodPosition = sentenceExtractor.getSentenceEndPosition(paragraphText);
                if (periodPosition == -1) {
                    if (!paragraphText.isEmpty()) {
                        addSentence(lineOffset, paragraphText, sentenceExtractor, builder);
                    }
                    return;
                }
            }
        }
    }

    @Override
    public String toString() {
        return "PlainTextParser{}";
    }
}
