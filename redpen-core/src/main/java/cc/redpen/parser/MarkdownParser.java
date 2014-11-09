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
import cc.redpen.model.Sentence;
import cc.redpen.parser.markdown.ToFileContentSerializer;
import org.pegdown.Extensions;
import org.pegdown.ParsingTimeoutException;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.RootNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser for Markdown format.<br/>
 * <p>
 * Markdown Syntax @see http://daringfireball.net/projects/markdown/
 */
public class MarkdownParser extends BaseDocumentParser {

    private PegDownProcessor pegDownProcessor = new PegDownProcessor(
            Extensions.HARDWRAPS
                    + Extensions.AUTOLINKS
                    + Extensions.FENCED_CODE_BLOCKS
                    + Extensions.TABLES);

    MarkdownParser() {
        super();
    }

    @Override
    public Document parse(InputStream inputStream, SentenceExtractor sentenceExtractor, DocumentCollection.Builder documentBuilder)
            throws RedPenException {
        documentBuilder.addDocument("");

        StringBuilder sb = new StringBuilder();
        String line;
        int charCount = 0;
        List<Integer> lineList = new ArrayList<>();
        BufferedReader br = createReader(inputStream);

        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
                // TODO surrogate pair ?
                charCount += line.length() + 1;
                lineList.add(charCount);
            }
        } catch (IOException e) {
            throw new RedPenException(e);
        }

        try {
            List<Sentence> headers = new ArrayList<>();
            headers.add(new Sentence("", 0));
            documentBuilder.addSection(0, headers);

            // TODO create document after parsing... overhead...
            RootNode rootNode =
                    pegDownProcessor.parseMarkdown(sb.toString().toCharArray());
            ToFileContentSerializer serializer =
                    new ToFileContentSerializer(documentBuilder,
                            lineList, sentenceExtractor);
            serializer.toFileContent(rootNode);
        } catch (ParsingTimeoutException e) {
            throw new RedPenException("Failed to parse timeout: ", e);
        }
        return documentBuilder.getLastDocument();
    }
}
