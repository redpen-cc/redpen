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
import cc.redpen.model.*;
import cc.redpen.parser.DocumentParser;
import cc.redpen.parser.LineOffset;
import cc.redpen.parser.SentenceExtractor;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

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
        String sampleText = "The Article Title\n" +
                "=================\n" +
                "Author's Name <authors@email.address>\n" +
                "v1.0, 2003-12\n" +
                "\n" +
                "\n" +
                "This is the optional preamble (an untitled section body). Useful for\n" +
                "writing simple sectionless documents consisting only of a preamble.\n" +
                "\n" +
                "NOTE: The abstract, preface, appendix, bibliography, glossary and\n" +
                "index section titles are significant ('specialsections').\n" +
                "\n" +
                "\n" +
                ":numbered!:\n" +
                "[abstract]\n" +
                "Example Abstract\n" +
                "----------------\n" +
                "The optional abstract (one or more paragraphs) goes here.\n" +
                "\n" +
                "This document is an AsciiDoc article skeleton containing briefly\n" +
                "annotated element placeholders plus a couple of example index entries\n" +
                "and footnotes.\n" +
                "\n" +
                "This is a new paragraph that consists of this line. And also of this line.\nWith this, it would be three lines in total.\nBut this line spoils that and makes it four.\n" +
                "\n" +
                ":numbered:\n" +
                "\n" +
                "The First Section\n" +
                "-----------------\n" +
                "Article sections start at level 1 & can be nested up to four levels\n" +
                "deep. Note that < and > are encoded by AsciiDoctor.\n" +
                "footnote:[An example footnote.]\n" +
                "indexterm:[Example index entry]\n" +
                "\n" +
                "And now for something completely different: ((monkeys)), lions and\n" +
                "tigers (Bengal and Siberian) using the alternative syntax index\n" +
                "entries.\n" +
                "(((Big cats,Lions)))\n" +
                "(((Big cats,Tigers,Bengal Tiger)))\n" +
                "(((Big cats,Tigers,Siberian Tiger)))\n" +
                "Note that multi-entry terms generate separate index entries.\n" +
                "\n" +
                "Here are a couple of image examples: an image:images/smallnew.png[]\n" +
                "example inline image followed by an example block image:\n" +
                "\n" +
                ".Tiger block image\n" +
                "image::images/tiger.png[Tiger image]\n" +
                "\n" +
                "Followed by an example table:\n" +
                "\n" +
                ".An example table\n" +
                "[width=\"60%\",options=\"header\"]\n" +
                "|==============================================\n" +
                "| Option          | Description\n" +
                "| -a 'USER GROUP' | Add 'USER' to 'GROUP'.\n" +
                "| -R 'GROUP'      | Disables access to 'GROUP'.\n" +
                "|==============================================\n" +
                "\n" +
                ".An example example\n" +
                "===============================================\n" +
                "Lorum ipum...\n" +
                "===============================================\n" +
                "\n" +
                "[[X1]]\n" +
                "Sub-section with Anchor\n" +
                "~~~~~~~~~~~~~~~~~~~~~~~\n" +
                "Sub-section at level 2.\n" +
                "\n" +
                "A Nested Sub-section\n" +
                "^^^^^^^^^^^^^^^^^^^^\n" +
                "Sub-section at level 3.\n" +
                "\n" +
                "Yet another nested Sub-section\n" +
                "++++++++++++++++++++++++++++++\n" +
                "Sub-section at level 4.\n" +
                "\n" +
                "This is the maximum sub-section depth supported by the distributed\n" +
                "AsciiDoc configuration.\n" +
                "footnote:[A second example footnote.]\n" +
                "\n" +
                "\n" +
                "二 セクション\n" +
                "-------\n" +
                "Article sections are at level 1 and can contain sub-sections nested up\n" +
                "to four deep.\n" +
                "\n" +
                "An example link to anchor at start of the <<X1,first sub-section>>.\n" +
                "indexterm:[Second example index entry]\n" +
                "\n" +
                "An example link to a bibliography entry <<taoup>>.\n" +
                "\n" +
                "\n" +
                ":numbered!:\n" +
                "\n" +
                "[appendix]\n" +
                "Example Appendix\n" +
                "----------------\n" +
                "AsciiDoc article appendices are just just article sections with\n" +
                "'specialsection' titles.\n" +
                "\n" +
                "Appendix Sub-section\n" +
                "~~~~~~~~~~~~~~~~~~~~\n" +
                "Appendix sub-section at level 2.\n" +
                "\n" +
                "\n" +
                "[bibliography]\n" +
                "Example Bibliography\n" +
                "--------------------\n" +
                "The bibliography list is a style of AsciiDoc bulleted list.\n" +
                "\n" +
                "[bibliography]\n" +
                "- [[[taoup]]] Eric Steven Raymond. 'The Art of Unix\n" +
                "  Programming'. Addison-Wesley. ISBN 0-13-142901-9.\n" +
                "- [[[walsh-muellner]]] Norman Walsh & Leonard Muellner.\n" +
                "  'DocBook - The Definitive Guide'. O'Reilly & Associates. 1999.\n" +
                "  ISBN 1-56592-580-7.\n" +
                "\n" +
                "\n" +
                "[glossary]\n" +
                "Example Glossary\n" +
                "----------------\n" +
                "Glossaries are optional. Glossaries entries are an example of a style\n" +
                "of AsciiDoc labeled lists.\n" +
                "\n" +
                "[glossary]\n" +
                "A glossary term::\n" +
                "  The corresponding (indented) definition.\n" +
                "\n" +
                "A second glossary term::\n" +
                "  The corresponding (indented) definition.\n" +
                "\n" +
                "\n" +
                "ifdef::backend-docbook[]\n" +
                "[index]\n" +
                "Example Index\n" +
                "-------------\n" +
                "////////////////////////////////////////////////////////////////\n" +
                "The index is normally left completely empty, it's contents being\n" +
                "generated automatically by the DocBook toolchain.\n" +
                "////////////////////////////////////////////////////////////////\n" +
                "endif::backend-docbook[]";

        sampleText  = "Instances Overview\n====================\nAuthor's Name <person@email.address>\nv1.2, 2015-08\n" +
                "\nThis is the optional preamble (an untitled section body). Useful for\n" +
                "writing simple sectionless documents consisting only of a preamble.\n\n" +
                "NOTE: The abstract, preface, appendix, bibliography, glossary and\nindex section titles are significant ('specialsections').\n" +
                "\n\n:numbered!:\n[abstract]\n" +
                "Instances\n" +
                "---------\n" +
                "In this article, we'll call a computer server that works as a member of a cluster an _instance_. for example, each instance in distributed search engines stores the the fractions of data.\n" +
                "\n Such distriubuted systems need a component to merge the preliminary results from member instnaces.\n\n\n" +
                ".Instance image\n" +
                "image::images/tiger.png[Instance image]\n\n" +
                "A sample table:\n\n" +
                ".A sample table\n" +
                "[width=\"60%\",options=\"header\"]\n" +
                "|==============================================\n" +
                "| Option     | Description\n" +
                "| NAME       | The name of the instance\n" +
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

        assertNotNull("doc is null", doc);
//        assertEquals(11, doc.size());

//        final Section firstSection = doc.getSection(0);
//        assertEquals(2, firstSection.getHeaderContentsListSize());
//        assertEquals("", firstSection.getHeaderContent(0).getContent());
//        assertEquals(0, firstSection.getNumberOfLists());
//        assertEquals(1, firstSection.getNumberOfParagraphs());
//        assertEquals(0, firstSection.getNumberOfSubsections());
//
//        Configuration configuration = new Configuration.ConfigurationBuilder()
//                .addValidatorConfig(new ValidatorConfiguration("SentenceLength"))
//                .addValidatorConfig(new ValidatorConfiguration("InvalidSymbol")).build();
//
//        RedPen redPen = new RedPen(configuration);
//        List<ValidationError> errors = redPen.validate(doc);
//        for (ValidationError error : errors) {
//            System.out.println(error.getMessage());
//        }

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
            doc = parser.parse(inputDocumentString, new SentenceExtractor(configuration.getSymbolTable()),
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
