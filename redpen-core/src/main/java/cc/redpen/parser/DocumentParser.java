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
import cc.redpen.tokenizer.RedPenTokenizer;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * DocumentParser generates Document from input.
 */
public interface DocumentParser {
    /**
     * Given input stream, return Document instance from a stream.
     *
     * @param is                input stream containing input content
     * @param sentenceExtractor SentenceExtractor object
     * @param tokenizer         tokenizer
     * @return a generated file content
     * @throws cc.redpen.RedPenException if Parser failed to parse input.
     */
    public default Document parse(InputStream is, SentenceExtractor sentenceExtractor, RedPenTokenizer tokenizer)
            throws RedPenException {
        return this.parse(is, Optional.empty(), sentenceExtractor, tokenizer);
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
    Document parse(InputStream io, Optional<String> fileName, SentenceExtractor sentenceExtractor, RedPenTokenizer tokenizer)
            throws RedPenException;

    /**
     * Given content, return Document instance for the specified file.
     *
     * @param content           input content
     * @param sentenceExtractor SentenceExtractor object
     * @param tokenizer         tokenizer
     * @return a generated file content
     * @throws cc.redpen.RedPenException if Parser failed to parse input.
     */
    Document parse(String content, SentenceExtractor sentenceExtractor, RedPenTokenizer tokenizer)
            throws RedPenException;

    /**
     * Given input file name, return Document instance for the specified file.
     *
     * @param file              input file
     * @param sentenceExtractor SentenceExtractor object
     * @param tokenizer         tokenizer
     * @return a generated file content
     * @throws cc.redpen.RedPenException if Parser failed to parse input.
     */
    Document parse(File file, SentenceExtractor sentenceExtractor, RedPenTokenizer tokenizer)
            throws RedPenException;

    public static final DocumentParser PLAIN = new PlainTextParser();
    public static final DocumentParser WIKI = new WikiParser();
    public static final DocumentParser MARKDOWN = new MarkdownParser();

    public static final Map<String, DocumentParser> PARSER_MAP = Collections.unmodifiableMap(
            new HashMap<String, DocumentParser>() {
                {
                    put("PLAIN", PLAIN);
                    put("WIKI", WIKI);
                    put("MARKDOWN", MARKDOWN);
                }
            });

    static DocumentParser of(String parserType) {
        DocumentParser parser = PARSER_MAP.get(parserType.toUpperCase());
        if (parser == null) {
            throw new IllegalArgumentException("no such parser for :" + parserType);
        }
        return parser;
    }
}
