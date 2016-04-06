/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.redpen.parser.asciidoc;

import cc.redpen.model.Sentence;
import cc.redpen.parser.LineOffset;
import cc.redpen.parser.SentenceExtractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A model of the original document, represented as an array of lines
 */
public class Model {
    private static final AsciiDocLine EMPTY_LINE = new AsciiDocLine("", 0);

    private List<AsciiDocLine> lines = new ArrayList<>();

    private int lineIndex = 0;
    private SentenceExtractor sentenceExtractor;

    /**
     * Construct a model. The sentence extract will be used delimit sentences and
     * join lines together when adding the model to the document builder
     *
     * @param sentenceExtractor sentence extractor
     */
    public Model(SentenceExtractor sentenceExtractor) {
        this.sentenceExtractor = sentenceExtractor;
    }

    /**
     * Return the offset string from the model at the given line number
     *
     * @param lineNumber line number
     * @return the line
     */
    public AsciiDocLine getLine(int lineNumber) {
        int index = lineNumber - 1;
        if ((index >= 0) && (index < lines.size())) {
            return lines.get(index);
        }
        return EMPTY_LINE;
    }

    /**
     * Add a line to the model
     *
     * @param line line to be added
     */
    public void add(AsciiDocLine line) {
        lines.add(line);
    }

    /**
     * Return the number of lines in the model
     *
     * @return line count
     */
    public int lineCount() {
        return lines.size();
    }

    /**
     * Set the line pointer to the first  line
     */
    public void rewind() {
        lineIndex = 0;
    }

    /**
     * Get the next line from the model, or null if there are no more lines
     *
     * @return next line
     */
    public AsciiDocLine getNextLine() {
        if (lineIndex < lines.size()) {
            AsciiDocLine line = lines.get(lineIndex);
            lineIndex++;
            return line;
        }
        return null;
    }

    /**
     * Return the current line from the model, or null if we've run out of lines
     *
     * @return line from the model
     */
    public AsciiDocLine getCurrentLine() {
        if (lineIndex < lines.size()) {
            AsciiDocLine line = lines.get(lineIndex);
            return line;
        }
        return null;
    }

    /**
     * Are there more lines to iterate through?
     *
     * @return has more lines
     */
    public boolean isMore() {
        return lineIndex < lines.size();
    }

    /**
     * Convert the single line into an array of sentences
     *
     * @param line line to be converted to sentence
     * @return list of converted sentence
     */
    public List<Sentence> convertToSentences(AsciiDocLine line) {
        List<AsciiDocLine> lines = new ArrayList<>();
        lines.add(line);
        return convertToSentences(lines);
    }

    /**
     * Convert a list of Lines into sentences by breaking them up appropriately, whilst
     * tracking their original line number and offset
     *
     * @param lines lines to be converted
     * @return list of converted sentence
     */
    public List<Sentence> convertToSentences(List<AsciiDocLine> lines) {
        List<Sentence> sentences = new ArrayList<>();

        String content = "";
        List<LineOffset> offsets = new ArrayList<>();
        for (int ln = 0; ln < lines.size(); ln++) {
            AsciiDocLine line = lines.get(ln);

            for (int i = 0; i < line.length(); i++) {
                if (line.isValid(i)) {
                    content += line.rawCharAt(i);
                    offsets.add(new LineOffset(line.getLineNo(), line.getOffset(i)));
                    // check for end of sentence
                    if (sentenceExtractor.getSentenceEndPosition("" + line.rawCharAt(i)) != -1) {
                        sentences.add(new Sentence(content, offsets, Collections.EMPTY_LIST));
                        content = "";
                        offsets = new ArrayList<>();
                    }
                }
            }
            // join lines
            if ((lines.size() > 1) && (ln != lines.size() - 1)) {
                for (char c : sentenceExtractor.getBrokenLineSeparator().toCharArray()) {
                    content += c;
                    offsets.add(new LineOffset(line.getLineNo(), line.getOffset(line.length())));
                }
            }
        }
        // add remaining line
        if (!content.trim().isEmpty()) {
            sentences.add(new Sentence(content, offsets, Collections.EMPTY_LIST));
        }
        return sentences;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (AsciiDocLine ostring : lines) {
            sb.append(ostring.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

}

