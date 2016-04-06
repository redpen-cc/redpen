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
package cc.redpen.parser.asciidoc;

import cc.redpen.config.Configuration;
import cc.redpen.parser.SentenceExtractor;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;

public class AsciiDocTest {

    /**
     * Test that ascidoc markup is erased properly
     */
    @Test
    public void testModelErasure() {
        Configuration configuration = Configuration.builder().build();

        String asciidocText =
                "[[purpose]]\n" +
                        ".Purpose\n" +
                        "****\n" +
                        "This contains asciidoc markup that should be {erased}.\n" +
                        "****\n" +
                        "\n" +
                        "TIP: This is a tip.\n" +
                        "\n" +
                        "NOTE: This is a note with a http://my.domain.com/[URL in it].\n" +
                        "\n" +
                        "1. A list\n" +
                        "2. More of a list\n" +
                        "\n" +
                        "* Bulletted\n" +
                        "** Bulleted\n" +
                        "\n" +
                        "[[first,First Steps]]\n" +
                        "== First Steps with http://asciidoc.org[AsciiDoc]\n" +
                        "\n" +
                        ".Inline markup\n" +
                        "* single quotes around a phrase place 'emphasis'\n" +
                        "* astericks around a phrase make the text *bold*\n" +
                        "* double astericks around one or more **l**etters in a word make those letters bold\n" +
                        "* double underscore around a __sub__string in a word emphasize that substring\n" +
                        "* use carrots around characters to make them ^super^script\n" +
                        "\n" +
                        "A table\n" +
                        "|====\n" +
                        "table contents\n" +
                        "|====\n" +
                        "\n" +
                        "Source listing\n" +
                        "[with attributes]\n" +
                        "----\n" +
                        "  $ echo 'foo'\n" +
                        "----\n";

        /* the model's toString() form, showing what has been erased from the input text
            X         = whole line erased
            [         = block
            nn-nnn-nn = section - listlevel - lineno
            *         = list item
            ·         = next character erased
         */
        String modelText =
                "X 0-0-001: ·[·[·p·u·r·p·o·s·e·]·]\n" +
                        "  0-0-002: ·.Purpose\n" +
                        "X 0-0-003: ·*·*·*·*\n" +
                        "  0-0-004: This contains asciidoc markup that should be ·{erased·}.\n" +
                        "X 0-0-005: ·*·*·*·*\n" +
                        "  0-0-006: \n" +
                        "  0-0-007: ·T·I·P·:· This is a tip.\n" +
                        "  0-0-008: \n" +
                        "  0-0-009: ·N·O·T·E·:· This is a note with a ·h·t·t·p·:·/·/·m·y·.·d·o·m·a·i·n·.·c·o·m·/·[URL in it·].\n" +
                        "  0-0-010: \n" +
                        "  0-0-011: 1. A list\n" +
                        "  0-0-012: 2. More of a list\n" +
                        "  0-0-013: \n" +
                        "  0-1-014* ·*· Bulletted\n" +
                        "  0-2-015* ·*·*· Bulleted\n" +
                        "  0-0-016: \n" +
                        "X 0-0-017: ·[·[·f·i·r·s·t·,·F·i·r·s·t· ·S·t·e·p·s·]·]\n" +
                        "  2-0-018: ·=·=· First Steps with ·h·t·t·p·:·/·/·a·s·c·i·i·d·o·c·.·o·r·g·[AsciiDoc·]\n" +
                        "  0-0-019: \n" +
                        "  0-0-020: ·.Inline markup\n" +
                        "  0-1-021* ·*· single quotes around a phrase place 'emphasis'\n" +
                        "  0-1-022* ·*· astericks around a phrase make the text ·*bold·*\n" +
                        "  0-1-023* ·*· double astericks around one or more ·*·*l·*·*etters in a word make those letters bold\n" +
                        "  0-1-024* ·*· double underscore around a ·_·_sub·_·_string in a word emphasize that substring\n" +
                        "  0-1-025* ·*· use carrots around characters to make them ·^super·^script\n" +
                        "  0-0-026: \n" +
                        "  0-0-027: A table\n" +
                        "X[0-0-028: ·|·=·=·=·=\n" +
                        "X[0-0-029: ·t·a·b·l·e· ·c·o·n·t·e·n·t·s\n" +
                        "X[0-0-030: ·|·=·=·=·=\n" +
                        "  0-0-031: \n" +
                        "  0-0-032: Source listing\n" +
                        "X 0-0-033: ·[·w·i·t·h· ·a·t·t·r·i·b·u·t·e·s·]\n" +
                        "X[0-0-034: ·-·-·-·-\n" +
                        "X[0-0-035: · · ·$· ·e·c·h·o· ·'·f·o·o·'\n" +
                        "X[0-0-036: ·-·-·-·-\n";

        Model model = new Model(new SentenceExtractor(configuration.getSymbolTable()));

        // get the parser to populate the model
        new AsciiDocParser().populateModel(model, new ByteArrayInputStream(asciidocText.getBytes()));

        assertEquals(modelText, model.toString());
    }


    /**
     * Test the line class
     */
    @Test
    public void testLine() {
        String testLine = "A line *with* an [enclosure] and one with a [enclosure,with a description] " +
                "and then an URL like http://fred.fish/";

        String[] expectedResults = new String[]{
                "  0-0-001: A line *with* an [enclosure] and one with a [enclosure,with a description] and then an URL like http://fred.fish/",
                "  0-0-001: A line *with* an ·[·e·n·c·l·o·s·u·r·e·] and one with a ·[·e·n·c·l·o·s·u·r·e·,·w·i·t·h· ·a· ·d·e·s·c·r·i·p·t·i·o·n·] and then an URL like http://fred.fish/",
                "  0-0-001: A line *with* an [enclosure] and one with a [enclosure,with a description] and then an URL like ·h·t·t·p·:·/·/·f·r·e·d·.·f·i·s·h·/",
                "  0-0-001: A line ·*with·* an [enclosure] and one with a [enclosure,with a description] and then an URL like http://fred.fish/",
                "  0-0-001: A line *with* an ·[enclosure·] and one with a ·[enclosure,with a description·] and then an URL like http://fred.fish/",
                "  0-0-001: A line *with* an ·[enclosure·] and one with a ·[·e·n·c·l·o·s·u·r·e·,with a description·] and then an URL like http://fred.fish/"
        };
        AsciiDocLine line;

        // this erases nothing since the close tag is not found
        line = new AsciiDocLine(testLine, 1);
        line.eraseEnclosure("[", "]]", AsciiDocLine.EraseStyle.All);
        assertEquals(expectedResults[0], line.toString());

        // すべてが消去されますか？
        line = new AsciiDocLine(testLine, 1);
        line.eraseEnclosure("[", "]", AsciiDocLine.EraseStyle.All);
        assertEquals(expectedResults[1], line.toString());

        // Test a string of delimiters as an ending tag
        line = new AsciiDocLine(testLine, 1);
        line.eraseEnclosure("http://", " ,[", AsciiDocLine.EraseStyle.CloseMarkerContainsDelimiters);
        assertEquals(expectedResults[2], line.toString());

        // Test inline markup erasure
        line = new AsciiDocLine(testLine, 1);
        line.eraseEnclosure("*", "*", AsciiDocLine.EraseStyle.InlineMarkup);
        assertEquals(expectedResults[3], line.toString());

        // Test just removing the markers
        line = new AsciiDocLine(testLine, 1);
        line.eraseEnclosure("[", "]", AsciiDocLine.EraseStyle.Markers);
        assertEquals(expectedResults[4], line.toString());

        // Test just removing the delimter tags
        line = new AsciiDocLine(testLine, 1);
        line.eraseEnclosure("[", "]", AsciiDocLine.EraseStyle.PreserveLabel);
        assertEquals(expectedResults[5], line.toString());
    }
}
