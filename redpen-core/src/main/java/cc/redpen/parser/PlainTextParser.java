/**
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser for plain text file.
 */
public final class PlainTextParser extends BasicDocumentParser {
    private static final Logger LOG =
            LoggerFactory.getLogger(PlainTextParser.class);

    /**
     * Constructor.
     */
    PlainTextParser() {
        super();
    }

    @Override
    public Document generateDocument(InputStream is)
            throws RedPenException {
        builder.addDocument("");

        List<Sentence> headers = new ArrayList<>();
        headers.add(new Sentence("", 0));
        builder.addSection(0, headers);
        builder.addParagraph();
        BufferedReader br = createReader(is);
        String remain = "";
        String line;
        int lineNum = 0;
        try {
            while ((line = br.readLine()) != null) {
                int periodPosition =
                        this.getSentenceExtractor().getSentenceEndPosition(line);
                if (line.equals("")) {
                    builder.addParagraph();
                } else if (periodPosition == -1) {
                    remain = remain + line;
                } else {
                    remain =
                            this.extractSentences(lineNum, remain + line);
                }
                lineNum++;
            }
        } catch (IOException e) {
            throw new RedPenException(e);
        }
        if (remain.length() > 0) {
            builder.addSentence(remain, lineNum);
        }
        return builder.getLastDocument();
    }

    private String extractSentences(int lineNum, String line) {
        int periodPosition = getSentenceExtractor().getSentenceEndPosition(line);
        if (periodPosition == -1) {
            return line;
        } else {
            while (true) {
                builder.addSentence(
                        line.substring(0, periodPosition + 1), lineNum);
                line = line.substring(periodPosition + 1, line.length());
                periodPosition = getSentenceExtractor().getSentenceEndPosition(line);
                if (periodPosition == -1) {
                    return line;
                }
            }
        }
    }
}
