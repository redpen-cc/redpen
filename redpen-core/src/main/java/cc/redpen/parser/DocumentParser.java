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
import cc.redpen.parser.asciidoc.AsciiDocParser;
import cc.redpen.parser.rest.ReSTParser;
import cc.redpen.parser.review.ReVIEWParser;
import cc.redpen.tokenizer.RedPenTokenizer;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
    Document parse(InputStream is, SentenceExtractor sentenceExtractor, RedPenTokenizer tokenizer)
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

    DocumentParser PLAIN = new PlainTextParser();
    DocumentParser WIKI = new WikiParser();
    DocumentParser MARKDOWN = new MarkdownParser();
    DocumentParser LATEX = new LaTeXParser();
    DocumentParser ASCIIDOC = new AsciiDocParser();
    DocumentParser PROPERTIES = new PropertiesParser();
    DocumentParser REVIEW = new ReVIEWParser();
    DocumentParser REST = new ReSTParser();

    Map<String, DocumentParser> PARSER_MAP = Collections.unmodifiableMap(
        new HashMap<String, DocumentParser>() {
            {
                put("PLAIN", PLAIN);
                put("WIKI", WIKI);
                put("MARKDOWN", MARKDOWN);
                put("LATEX", LATEX);
                put("ASCIIDOC", ASCIIDOC);
                put("PROPERTIES", PROPERTIES);
                put("REVIEW", REVIEW);
                put("REST", REVIEW);
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
