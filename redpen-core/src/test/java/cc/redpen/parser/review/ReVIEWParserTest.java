/*
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
package cc.redpen.parser.review;

import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.model.Document;
import cc.redpen.parser.DocumentParser;
import cc.redpen.parser.SentenceExtractor;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;

public class ReVIEWParserTest {
    @Test
    public void ParseBlock() throws Exception {
        String sample = "//list[yml_sample][my.yml]{";
        ReVIEWParser parser = new ReVIEWParser();
        ReVIEWParser.ReVIEWBlock block = parser.parseBlock(new ReVIEWLine(sample, 0));
        assertEquals("list", block.type);
        assertEquals(2, block.properties.size());
        assertEquals("yml_sample", block.properties.get(0));
        assertEquals("my.yml", block.properties.get(1));
        assertTrue(block.isOpen);
    }

    @Test
    public void ParseBlockWithoutProperties() throws Exception {
        String sample = "//lead{";
        ReVIEWParser parser = new ReVIEWParser();
        ReVIEWParser.ReVIEWBlock block = parser.parseBlock(new ReVIEWLine(sample, 0));
        assertEquals("lead", block.type);
        assertEquals(0, block.properties.size());
        assertTrue(block.isOpen);
    }

    @Test
    public void testRemoveTextDecoration() throws UnsupportedEncodingException {
        String sampleText = "About @<b>{Gekioko}.\n";
        Document doc = createFileContent(sampleText);
        assertEquals(1, doc.size());
        assertEquals("About Gekioko.", doc.getSection(0).getParagraph(0).getSentence(0).getContent());
    }

    private Document createFileContent(String inputDocumentString) {
        DocumentParser parser = DocumentParser.REVIEW;
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
