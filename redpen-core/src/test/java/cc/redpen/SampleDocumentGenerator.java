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
package cc.redpen;

import cc.redpen.config.Configuration;
import cc.redpen.model.Document;
import cc.redpen.parser.DocumentParser;
import cc.redpen.parser.SentenceExtractor;

import java.util.ArrayList;
import java.util.List;

/**
 * Generate DocumentCollection objects from String. This class are applied
 * only for testing purpose.
 */
public class SampleDocumentGenerator {
    /**
     * Given a string and the syntax type, build a DocumentCollection object.
     * This build method is made to write test easily, but this generator
     * class does not supports the configurations if the configurations are
     * needed please use DocumentGenerator class.
     *
     * @param docString input document string
     * @param parser    document syntax: wiki, markdown or plain
     * @return DocumentCollection object
     */
    public static List<Document> generateOneFileDocument(String docString,
                                                             DocumentParser parser) throws RedPenException {
        Configuration configuration = new Configuration.ConfigurationBuilder()
                .setLanguage("en").build();
        List<Document> docs = new ArrayList<>();
        docs.add(parser.parse(docString, new SentenceExtractor(configuration.getSymbolTable()), configuration.getTokenizer()));
        return docs;
    }
}
