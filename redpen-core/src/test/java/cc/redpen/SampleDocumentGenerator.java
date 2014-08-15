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
package cc.redpen;

import cc.redpen.config.Configuration;
import cc.redpen.model.DocumentCollection;
import cc.redpen.parser.DocumentParserFactory;
import cc.redpen.parser.Parser;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;

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
     * @param type      document syntax: wiki, markdown or plain
     * @return DocumentCollection object
     */
    public static DocumentCollection generateOneFileDocument(String docString,
                                                             Parser.Type type) throws RedPenException {
        Configuration configuration = new Configuration.Builder()
                .setSymbolTable("en").build();
        DocumentCollection.Builder builder = new DocumentCollection.Builder();
        Parser parser = DocumentParserFactory.generate(type, configuration, builder);
        InputStream stream = IOUtils.toInputStream(docString);
        parser.generateDocument(stream);
        return builder.build();
    }
}
