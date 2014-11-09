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
import cc.redpen.model.DocumentCollection;

import java.io.File;
import java.io.InputStream;

/**
 * DocumentParser generates Document from input.
 */
public interface DocumentParser {
    /**
     * Given input stream, return Document instance from a stream.
     *
     * @param io input stream containing input content
     * @return a generated file content
     * @throws cc.redpen.RedPenException if Parser failed to parse input.
     */
    Document parse(InputStream io, SentenceExtractor sentenceExtractor, DocumentCollection.Builder documentBuilder)
            throws RedPenException;

    /**
     * Given content, return Document instance for the specified file.
     *
     * @param content input content
     * @return a generated file content
     * @throws cc.redpen.RedPenException if Parser failed to parse input.
     */
    Document parse(String content, SentenceExtractor sentenceExtractor, DocumentCollection.Builder documentBuilder)
            throws RedPenException;

    /**
     * Given input file name, return Document instance for the specified file.
     *
     * @param file input file
     * @return a generated file content
     * @throws cc.redpen.RedPenException if Parser failed to parse input.
     */
    Document parse(File file, SentenceExtractor sentenceExtractor, DocumentCollection.Builder documentBuilder)
            throws RedPenException;

    public static final DocumentParser PLAIN = new PlainTextParser();
    public static final DocumentParser WIKI = new WikiParser();
    public static final DocumentParser MARKDOWN = new MarkdownParser();

    static DocumentParser of(String parserType) {
        switch (parserType.toUpperCase()){
            case "PLAIN" :
                return PLAIN;
            case "WIKI" :
                return WIKI;
            case "MARKDOWN":
                return MARKDOWN;
            default :
                throw new IllegalArgumentException("no such parser for :" + parserType);
        }
    }
}
