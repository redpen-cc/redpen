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
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.*;
import cc.redpen.parser.DocumentParser;
import cc.redpen.parser.LineOffset;
import cc.redpen.parser.SentenceExtractor;
import cc.redpen.validator.ValidationError;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class AsciiDocParserTest {

    @Before
    public void setup() {
    }

    @Test(expected = NullPointerException.class)
    public void testNullDocument() throws Exception {
        Configuration configuration = new Configuration.ConfigurationBuilder().build();
        DocumentParser parser = DocumentParser.ASCIIDOC;
        InputStream is = null;
        parser.parse(is, new SentenceExtractor(configuration.getSymbolTable()), configuration.getTokenizer());
    }

    @Test
    public void testBasicDocument() throws UnsupportedEncodingException, RedPenException {
        String sampleText = "Instances Overview\n==================\n" + "Author's Name <person@email.address>\nv1.2, 2015-08\n" +
                "\nThis is the optional preamble (an untitled section body). Useful for " +
                "writing simple sectionless documents consisting only of a preamble.\n\n" +
                "NOTE: The abstract, preface, appendix, bibliography, glossary and index section titles are significant ('specialsections').\n" +
                "\n\n:numbered!:\n[abstract]\n" +
                "Instances\n" +
                "---------\n" +
                "In this article, we'll call a computer server that works as a member of a cluster an _instan3ce_. " +
                "for example, as shown in this http://redpen.ignored.url/[mishpelled link], each instance in distributed search engines stores the the fractions of data.\n" +
                "\nSuch distriubuted systems need a component to merge the preliminary results from member instnaces.\n\n\n" +
                ".Instance image\n" +
                "image::images/tiger.png[Instance image]\n\n" +
                "A sample table:\n\n" +
                ".A sample table\n" +
                "[width=\"60%\",options=\"header\"]\n" +
                "|==============================================\n" +
                "| Option     | Description\n" +
                "| GROUP      | The instance group.\n" +
                "|==============================================\n\n" +
                ".example list\n" +
                "===============================================\n" +
                "Lorum ipum...\n" +
                "===============================================\n\n\n" +
                "[bibliography]\n" +
                "- [[[taoup]]] Eric Steven Raymond. 'The Art of Unix\n" +
                "  Programming'. Addison-Wesley. ISBN 0-13-142901-9.\n" +
                "- [[[walsh-muellner]]] Norman Walsh & Leonard Muellner.\n" +
                "  'DocBook - The Definitive Guide'. O'Reilly & Associates. 1999.\n" +
                "  ISBN 1-56592-580-7.\n\n\n" +
                "[glossary]\n" +
                "Example Glossary\n" +
                "----------------\n" +
                "Glossaries are optional. Glossaries entries are an example of a style\n" +
                "of AsciiDoc labeled lists.\n\n" +
                "[glossary]\n" +
                "A glossary term::\n" +
                "  The corresponding (indented) definition.\n\n" +
                "A second glossary term::\n" +
                "  The corresponding (indented) definition.\n\n\n" +
                "ifdef::backend-docbook[]\n" +
                "[index]\n" +
                "Example Index\n" +
                "-------------\n" +
                "////////////////////////////////////////////////////////////////\n" +
                "The index is normally left completely empty, it's contents being\n" +
                "generated automatically by the DocBook toolchain.\n" +
                "////////////////////////////////////////////////////////////////\n" +
                "endif::backend-docbook[]";


        Document doc = createFileContent(sampleText);

        for (Section section : doc) {
            for (Paragraph paragraph : section.getParagraphs()) {
                paragraph.getSentences().forEach(sentence -> assertNotNull(sentence.getContent()));
                section.getHeaderContents().forEach(sentence -> assertNotNull(sentence.getContent()));
                for (ListBlock listBlock : section.getListBlocks()) {
                    for (ListElement listElement : listBlock.getListElements()) {
                        listElement.getSentences().forEach(sentence -> assertNotNull(sentence.getContent()));
                    }
                }
            }
        }

        assertNotNull("doc is null", doc);
        assertEquals(3, doc.size());

        final Section firstSection = doc.getSection(0);
        assertEquals(1, firstSection.getHeaderContentsListSize());
        assertEquals("Instances Overview", firstSection.getHeaderContent(0).getContent());
        assertEquals(0, firstSection.getNumberOfLists());
        assertEquals(1, firstSection.getNumberOfParagraphs());
        assertEquals(0, firstSection.getNumberOfSubsections());

        Configuration configuration = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("Spelling"))
                .addValidatorConfig(new ValidatorConfiguration("InvalidSymbol")).build();

        RedPen redPen = new RedPen(configuration);
        List<ValidationError> errors = redPen.validate(doc);
    }


    @Test
    public void testRemoveTextDecoration() throws UnsupportedEncodingException {
        String sampleText = "About _Gekioko_.\n";
        Document doc = createFileContent(sampleText);
        assertEquals(1, doc.size());
        assertEquals("About Gekioko.", doc.getSection(0).getParagraph(0).getSentence(0).getContent());
    }

    @Test
    public void testSectionHeader() throws UnsupportedEncodingException {
        String sampleText = "# About _Gekioko_.\n\n" +
                "Gekioko means angry.";

        Document doc = createFileContent(sampleText);

        assertEquals(1, doc.size());
        assertEquals("About Gekioko.", doc.getSection(0).getHeaderContent(0).getContent());
    }

    @Test
    public void testSectionHeaderOffsetPosition() throws UnsupportedEncodingException {
        String sampleText = "= About _Gekioko_.\n\n" +
                "Gekioko means angry.";

        Document doc = createFileContent(sampleText);
        assertEquals(1, doc.size());
        assertEquals(2, doc.getSection(0).getHeaderContent(0).getOffset(0).get().offset);
    }

    private void dumpSentence(Sentence sentence) {
        for (int i = 0; i < sentence.getContent().length(); i++) {
            String offset = sentence.getOffset(i).isPresent() ? sentence.getOffset(i).get().lineNum + "," + sentence.getOffset(i).get().offset : "n/a";
            System.out.print("[" + sentence.getContent().charAt(i) + ":" + offset + "]");
        }
        System.out.println();
    }

    private Document createFileContent(String inputDocumentString,
                                       Configuration config) {
        DocumentParser parser = DocumentParser.ASCIIDOC;

        try {
            return parser.parse(inputDocumentString, new SentenceExtractor(config.getSymbolTable()), config.getTokenizer());
        } catch (RedPenException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Document createFileContent(String inputDocumentString) {
        DocumentParser parser = DocumentParser.ASCIIDOC;
        Document doc = null;
        try {
            Configuration configuration = new Configuration.ConfigurationBuilder().build();
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

    private static List<LineOffset> initializeMappingTable(LineOffset... offsets) {
        List<LineOffset> offsetTable = new ArrayList<>();
        for (LineOffset offset : offsets) {
            offsetTable.add(offset);
        }
        return offsetTable;
    }
}
