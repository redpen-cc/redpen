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
import cc.redpen.model.Document;
import cc.redpen.parser.DocumentParser;
import cc.redpen.parser.SentenceExtractor;
import cc.redpen.validator.ValidationError;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

public class AsciiDocParserTest {

    @Before
    public void setup() {
    }

//    @Test(expected = NullPointerException.class)
//    public void testNullDocument() throws Exception {
//        Configuration configuration = new Configuration.ConfigurationBuilder().build();
//        DocumentParser parser = DocumentParser.ASCIIDOC;
//        InputStream is = null;
//        parser.parse(is, new SentenceExtractor(configuration.getSymbolTable()), configuration.getTokenizer());
//    }

    @Test
    public void testBasicDocument() throws UnsupportedEncodingException {
        String sampleText = "";
        sampleText += "= About Gekioko.\n";
        sampleText += "Gekioko pun pun maru means _very very_ angry.\n";
        sampleText += "\n";
        sampleText += "The word also has a positive meaning.\n";
        sampleText += "== About Gunma.\n";
        sampleText += "\n";
        sampleText += "Gunma is located at west of http://en.wikipedia.org/wiki/Saitama,_Saitama[Saitama].\n";
        sampleText += "\n";
        sampleText += "* Features\n";
        sampleText += "   ** Main City: Gumma City\n";
        sampleText += "   ** Capical: 200 Millon\n";
        sampleText += "* Location\n";
        sampleText += "    ** Japan\n";
        sampleText += "\n";
        sampleText += "The word also have positive meaning. However it is a bit weird.";

        Document doc = createFileContent(sampleText);

//        assertNotNull("doc is null", doc);
//        assertEquals(3, doc.size());
//        // first section
//        final Section firstSection = doc.getSection(0);
//        assertEquals(1, firstSection.getHeaderContentsListSize());
//        assertEquals("", firstSection.getHeaderContent(0).getContent());
//        assertEquals(0, firstSection.getNumberOfLists());
//        assertEquals(0, firstSection.getNumberOfParagraphs());
//        assertEquals(1, firstSection.getNumberOfSubsections());
//
//        // 2nd section
//        final Section secondSection = doc.getSection(1);
//        assertEquals(1, secondSection.getHeaderContentsListSize());
//        assertEquals("About Gekioko.", secondSection.getHeaderContent(0).getContent());
//        assertEquals(1, secondSection.getHeaderContent(0).getLineNumber());
//        assertEquals(2, secondSection.getHeaderContent(0).getStartPositionOffset());
//        assertEquals(0, secondSection.getNumberOfLists());
//        assertEquals(2, secondSection.getNumberOfParagraphs());
//        assertEquals(1, secondSection.getNumberOfSubsections());
//        assertEquals(firstSection, secondSection.getParentSection());
//
//        // validate paragraph in 2nd section
//        assertEquals(1, secondSection.getParagraph(0).getNumberOfSentences());
//        assertEquals(true, secondSection.getParagraph(0).getSentence(0).isFirstSentence());
//        assertEquals(2, secondSection.getParagraph(0).getSentence(0).getLineNumber());
//        assertEquals(0, secondSection.getParagraph(0).getSentence(0).getStartPositionOffset());
//        assertEquals(1, secondSection.getParagraph(1).getNumberOfSentences());
//        assertEquals(true, secondSection.getParagraph(1).getSentence(0).isFirstSentence());
//        assertEquals(4, secondSection.getParagraph(1).getSentence(0).getLineNumber());
//        assertEquals(0, secondSection.getParagraph(1).getSentence(0).getStartPositionOffset());
//
//        // 3rd section
//        Section lastSection = doc.getSection(doc.size() - 1);
//        assertEquals(1, lastSection.getNumberOfLists());
//        assertEquals(5, lastSection.getListBlock(0).getNumberOfListElements());
//        assertEquals(2, lastSection.getNumberOfParagraphs());
//        assertEquals(1, lastSection.getHeaderContentsListSize());
//        assertEquals(0, lastSection.getNumberOfSubsections());
//        assertEquals("About Gunma.", lastSection.getHeaderContent(0).getContent());
//        assertEquals(3, lastSection.getHeaderContent(0).getStartPositionOffset());
//        assertEquals(secondSection, lastSection.getParentSection());
//
//        // validate paragraphs in last section
//        assertEquals(1, lastSection.getParagraph(0).getNumberOfSentences());
//        assertEquals(true, lastSection.getParagraph(0).getSentence(0).isFirstSentence());
//        assertEquals(7, lastSection.getParagraph(0).getSentence(0).getLineNumber());
//        assertEquals(0, lastSection.getParagraph(0).getSentence(0).getStartPositionOffset());
//        assertEquals(2, lastSection.getParagraph(1).getNumberOfSentences());
//        assertEquals(true, lastSection.getParagraph(1).getSentence(0).isFirstSentence());
//        assertEquals(15, lastSection.getParagraph(1).getSentence(0).getLineNumber());
//        assertEquals(false, lastSection.getParagraph(1).getSentence(1).isFirstSentence());
//        assertEquals(15, lastSection.getParagraph(1).getSentence(1).getLineNumber());
//        assertEquals(36, lastSection.getParagraph(1).getSentence(1).getStartPositionOffset());
    }

    @Test
    public void testErrorPositionOfAsciiDocParser() throws RedPenException {
        String sampleText = "This is a good day。\n"; // invalid end of sentence symbol
        Configuration conf = new Configuration.ConfigurationBuilder()
                .setLanguage("en")
                .build();
        List<Document> documents = new ArrayList<>();
        documents.add(createFileContent(sampleText, conf));

        Configuration configuration = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(
                        new ValidatorConfiguration("InvalidSymbol"))
                .build();

        RedPen redPen = new RedPen(configuration);
        List<ValidationError> errors = redPen.validate(documents).get(documents.get(0));
//        assertEquals(1, errors.size());
//        assertEquals("InvalidSymbol", errors.get(0).getValidatorName());
//        assertEquals(19, errors.get(0).getSentence().getContent().length());
//        assertEquals(new LineOffset(1, 18), errors.get(0).getStartPosition().get());
//        assertEquals(new LineOffset(1, 19), errors.get(0).getEndPosition().get());
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


}
