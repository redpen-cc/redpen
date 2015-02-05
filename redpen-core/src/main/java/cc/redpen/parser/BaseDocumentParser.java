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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Abstract Parser class containing common procedures to
 * implements the concrete Parser classes.
 */
public abstract class BaseDocumentParser implements DocumentParser {
    private static final Logger LOG = LoggerFactory.getLogger(
            BaseDocumentParser.class);


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
     * create BufferedReader from InputStream is.
     *
     * @param is InputStream using to parse
     * @return BufferedReader created from InputStream
     */
    protected BufferedReader createReader(InputStream is) {
        return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
    }

}
