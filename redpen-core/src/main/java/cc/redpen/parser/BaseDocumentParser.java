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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.Character.isWhitespace;

/**
 * Abstract Parser class containing common procedures to
 * implements the concrete Parser classes.
 */
public abstract class BaseDocumentParser implements DocumentParser {

    @Override
    public Document parse(InputStream is, SentenceExtractor sentenceExtractor, RedPenTokenizer tokenizer)
        throws RedPenException {
        return parse(is, Optional.empty(), sentenceExtractor, tokenizer);
    }

    @Override
    public Document parse(String content, SentenceExtractor sentenceExtractor, RedPenTokenizer tokenizer) throws RedPenException {
        return parse(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), sentenceExtractor, tokenizer);
    }

    @Override
    public Document parse(File file, SentenceExtractor sentenceExtractor, RedPenTokenizer tokenizer) throws RedPenException {
        try (InputStream inputStream = new FileInputStream(file)) {
            return parse(inputStream, Optional.of(file.getName()), sentenceExtractor, tokenizer);
        } catch (IOException e) {
            throw new RedPenException(e);
        }
    }

    /**
     * Given input stream, return Document instance from a stream.
     *
     * @param io                input stream containing input content
     * @param fileName          file name
     * @param sentenceExtractor SentenceExtractor object
     * @param tokenizer         tokenizer
     * @return a generated file content
     * @throws cc.redpen.RedPenException if Parser failed to parse input.
     */
    protected abstract Document parse(InputStream io, Optional<String> fileName, SentenceExtractor sentenceExtractor, RedPenTokenizer tokenizer)
        throws RedPenException;


    /**
     * create BufferedReader from InputStream is.
     *
     * @param is                InputStream using to parse
     * @return BufferedReader created from InputStream
     */
    protected PreprocessingReader createReader(InputStream is) {
        return new PreprocessingReader(new InputStreamReader(is, StandardCharsets.UTF_8), this);
    }

    protected static class ValueWithOffsets extends Sentence {
        public ValueWithOffsets() {
            super("", 0);
        }

        public ValueWithOffsets(String content, List<LineOffset> offsetMap) {
            super(content, offsetMap, new ArrayList<>());
        }

        public boolean isEmpty() {
            return "".equals(getContent());
        }

        public ValueWithOffsets append(String line, List<LineOffset> offsets) {
            setContent(getContent() + line);
            getOffsetMap().addAll(offsets);
            return this;
        }

        public ValueWithOffsets extract(int start, int end) {
            if (start == end) return new ValueWithOffsets();
            return new ValueWithOffsets(getContent().substring(start, end), getOffsetMap().subList(start, end));
        }
    }

    protected int skipWhitespace(String line, int start) {
        for (int i = start; i < line.length(); i++)
            if (!isWhitespace(line.charAt(i))) return i;
        return line.length();
    }
}
