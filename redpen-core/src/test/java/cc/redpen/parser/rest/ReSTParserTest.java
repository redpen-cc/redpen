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
package cc.redpen.parser.rest;

import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.model.Document;
import cc.redpen.model.Section;
import cc.redpen.parser.DocumentParser;
import cc.redpen.parser.SentenceExtractor;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ReSTParserTest {

    @Test
    public void testSections() {
        String sampleText = "" +
                "sub section\n" +
                "-----------\n" +
                "\n" +
                "blah\n" +
                "\n" +
                "subsub section\n" +
                "~~~~~~~~~~~~~~\n" +
                "\n" +
                "blah blah\n" +
                "\n" +
                "subsubsub section\n" +
                "^^^^^^^^^^^^^^^^^\n" +
                "\n" +
                "blah blah blah";

        Document doc = createFileContent(sampleText);
        assertNotNull("doc is null", doc);
        assertEquals(3, doc.size());

        final Section firstSection = doc.getSection(0);
        assertEquals(1, firstSection.getHeaderContentsListSize());
        assertEquals("sub section", firstSection.getHeaderContent(0).getContent());
        assertEquals("blah", firstSection.getParagraph(0).getSentence(0).getContent());

        final Section secondSection = doc.getSection(1);
        assertEquals(1, firstSection.getHeaderContentsListSize());
        assertEquals("subsub section", secondSection.getHeaderContent(0).getContent());
        assertEquals("blah blah", secondSection.getParagraph(0).getSentence(0).getContent());

        final Section thirdSection = doc.getSection(2);
        assertEquals(1, thirdSection.getHeaderContentsListSize());
        assertEquals("subsubsub section", thirdSection.getHeaderContent(0).getContent());
        assertEquals("blah blah blah", thirdSection.getParagraph(0).getSentence(0).getContent());
    }

    private Document createFileContent(String inputDocumentString) {
        DocumentParser parser = DocumentParser.REST;
        Document doc = null;
        try {
            Configuration configuration = Configuration.builder().build();
            doc = parser.parse(
                    inputDocumentString,
                    new SentenceExtractor(configuration.getSymbolTable()),
                    configuration.getTokenizer());
        } catch (RedPenException e) {
            e.printStackTrace();
            fail();
        }
        return doc;
    }


}
