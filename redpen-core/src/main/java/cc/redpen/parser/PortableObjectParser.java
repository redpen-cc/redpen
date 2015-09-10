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
 * Parser for Portable Object format (well known as .po)
 */
final class PortableObjectParser extends BaseDocumentParser {

    private static final Logger LOG = LoggerFactory.getLogger(PortableObjectParser.class);

    private static final Pattern MSGID_PATTERN
            = Pattern.compile("^msgid \".*");
    private static final Pattern MSGSTR_PATTERN
            = Pattern.compile("^msgstr \".*");
    private static final Pattern MSGBODY_PATTERN
            = Pattern.compile("^\".+\"$");
    private static final Pattern COMMENT_PATTERN
            = Pattern.compile("^#.*$");

    PortableObjectParser() {
        super();
    }

    @Override
    public String toString() {
        return "PortableObjectParser{}";
    }

    private static boolean check(Pattern p, String target) {
        Matcher m = p.matcher(target);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Document parse(InputStream inputStream, Optional<String> fileName, SentenceExtractor sentenceExtractor, RedPenTokenizer tokenizer)
            throws RedPenException {
        Document.DocumentBuilder documentBuilder = new Document.DocumentBuilder(tokenizer);
        fileName.ifPresent(documentBuilder::setFileName);

        // add dummy section
        List<Sentence> headers = new ArrayList<>();
        headers.add(new Sentence("", 0));
        documentBuilder.addSection(0, headers);

        StringBuilder msgid = new StringBuilder();
        StringBuilder msgstr = new StringBuilder();
        String line;
        int lineNum = 1;
        int lineNumMsgstr = 1;
        boolean isMsgidBlock = false;
        BufferedReader br = createReader(inputStream);

        documentBuilder.addParagraph();

        try {
            while ((line = br.readLine()) != null) {
                if (check(COMMENT_PATTERN, line)) {
                    // do nothing
                } else if (check(MSGID_PATTERN, line)) {
                    msgid.delete(0, msgid.length());
                    msgid.append(line.substring(7, line.length() - 1));
                    isMsgidBlock = true;
                } else if (check(MSGSTR_PATTERN, line)) {
                    msgstr.delete(0, msgstr.length());
                    msgstr.append(line.substring(8, line.length() - 1));
                    isMsgidBlock = false;
                    lineNumMsgstr = lineNum;
                } else if (check(MSGBODY_PATTERN, line)) {
                    if (isMsgidBlock) {
                        msgid.append(line.substring(1, line.length() - 1));
                    } else {
                        msgstr.append(line.substring(1, line.length() - 1));
                    }
                } else if (line.equals("")) {
                    if (msgid.length() > 0) {
                        this.extractSentences(lineNumMsgstr, msgstr.toString(), sentenceExtractor, documentBuilder);
                        documentBuilder.addParagraph();
                    }
                    msgid.delete(0, msgid.length());
                    msgstr.delete(0, msgstr.length());
                }
                lineNum++;
            }
            if (msgstr.length() > 0) {
                this.extractSentences(lineNumMsgstr, msgstr.toString(), sentenceExtractor, documentBuilder);
                documentBuilder.addParagraph();
            }
        } catch (IOException e) {
            throw new RedPenException(e);
        }

        return documentBuilder.build();
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
}
