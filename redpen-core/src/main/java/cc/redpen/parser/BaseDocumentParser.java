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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Abstract Parser class containing common procedures to
 * implements the concrete Parser classes.
 */
public abstract class BaseDocumentParser implements DocumentParser {
    private static final Logger LOG = LoggerFactory.getLogger(
            BaseDocumentParser.class);


    @Override
    public Document parse(String content, SentenceExtractor sentenceExtractor, DocumentCollection.Builder documentBuilder) throws RedPenException{
        try {
            return parse(new ByteArrayInputStream(content.getBytes("UTF-8")), sentenceExtractor, documentBuilder);
        } catch (UnsupportedEncodingException shouldNeverHappen) {
            throw new RuntimeException(shouldNeverHappen);
        }
    }

    @Override
    public Document parse(File file, SentenceExtractor sentenceExtractor, DocumentCollection.Builder documentBuilder) throws RedPenException {
        Document document ;
        try (InputStream inputStream = new FileInputStream(file)) {
            document = this.parse(inputStream, sentenceExtractor, documentBuilder);
                if (document != null) {
                    document.setFileName(file.getName());
                }
        } catch (IOException e) {
            throw new RedPenException(e);
        }
        return document;
    }

    /**
     * create BufferedReader from InputStream is.
     *
     * @param is InputStream using to parse
     * @return BufferedReader created from InputStream
     * @throws cc.redpen.RedPenException if InputStream is not
     *                                   supported UTF-8 encoding
     */
    protected BufferedReader createReader(InputStream is)
            throws RedPenException {
        if (is == null) {
            throw new RedPenException("input stream is null");
        }
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RedPenException(
                    "does not support UTF-8 encoding", e);
        }
        return br;
    }

}
