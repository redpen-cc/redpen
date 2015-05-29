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
import cc.redpen.model.ListBlock;
import cc.redpen.model.Paragraph;
import cc.redpen.model.Section;
import cc.redpen.parser.DocumentParser;
import cc.redpen.parser.LineOffset;
import cc.redpen.parser.SentenceExtractor;
import cc.redpen.validator.ValidationError;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cc.redpen.config.SymbolType.COMMA;
import static cc.redpen.config.SymbolType.FULL_STOP;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class LaTeXParserTest {

    @Before
    public void setup() {
    }

    @Test
    public void testBasicDocument() throws UnsupportedEncodingException {
        String sampleText = ""
            + "\\documentclass[a4paper]{jsarticle}\n"
            + "\\author{asdasd}\n"
            + "\\title{LaTeX Parser Test}\n"
            + "\\maketitle\n"
            + "\\begin{document}\n"
            + "\\section{About Gekioko.}\n"
            + "Gekioko pun pun maru means very very angry.\n"
            + "\n"
            + "The word also have posive meaning.\n"
            + "\\subsection{About Gunma.}\n"
            + "\n"
            + "Gunma is located at west of Saitama.\n"
            + "\n"
            + "Features:\n"
            + "\\begin{enumerate}\n"
            + "\\item Main City: Gumma City\n"
            + "\\item Capital: 200 Millon\n"
            + "\\end{enumerate}\n"
            + "\n"
            + "Location:\n"
            + "\\begin{itemize}\n"
            + "\\item Japan\n"
            + "\\end{itemize}\n"
            + "\n"
            + "The word also have posive meaning. Hower it is a bit wired.\n"
            + "\\end{document}\n";

        Document doc = createFileContent(sampleText);

        assertNotNull("doc is null", doc);
        assertEquals(3, doc.size());
        // first section
        final Section firstSection = doc.getSection(0);
        assertEquals(1, firstSection.getHeaderContentsListSize());
        assertEquals("", firstSection.getHeaderContent(0).getContent());
        assertEquals(0, firstSection.getNumberOfLists());
        assertEquals(0, firstSection.getNumberOfParagraphs());
        assertEquals(1, firstSection.getNumberOfSubsections());

        // 2nd section
        final Section secondSection = doc.getSection(1);
        assertEquals(1, secondSection.getHeaderContentsListSize());
        assertEquals("About Gekioko.", secondSection.getHeaderContent(0).getContent());
        assertEquals(6, secondSection.getHeaderContent(0).getLineNumber());
        assertEquals(10, secondSection.getHeaderContent(0).getStartPositionOffset());
        assertEquals(0, secondSection.getNumberOfLists());
        assertEquals(2, secondSection.getNumberOfParagraphs());
        assertEquals(1, secondSection.getNumberOfSubsections());
        assertEquals(firstSection, secondSection.getParentSection());

        // validate paragraph in 2nd section
        assertEquals(1, secondSection.getParagraph(0).getNumberOfSentences());
        assertEquals(true, secondSection.getParagraph(0).getSentence(0).isFirstSentence());
        assertEquals(2, secondSection.getParagraph(0).getSentence(0).getLineNumber());
        assertEquals(0, secondSection.getParagraph(0).getSentence(0).getStartPositionOffset());
        assertEquals(1, secondSection.getParagraph(1).getNumberOfSentences());
        assertEquals(true, secondSection.getParagraph(1).getSentence(0).isFirstSentence());
        assertEquals(4, secondSection.getParagraph(1).getSentence(0).getLineNumber());
        assertEquals(0, secondSection.getParagraph(1).getSentence(0).getStartPositionOffset());

        // 3rd section
        Section lastSection = doc.getSection(doc.size() - 1);
        // TBD: RE-based LaTeX parser cannot handle lists yet
        //assertEquals(1, lastSection.getNumberOfLists());
        //assertEquals(5, lastSection.getListBlock(0).getNumberOfListElements());
        assertEquals(2, lastSection.getNumberOfParagraphs());
        assertEquals(1, lastSection.getHeaderContentsListSize());
        assertEquals(0, lastSection.getNumberOfSubsections());
        assertEquals("About Gunma.", lastSection.getHeaderContent(0).getContent());
        assertEquals(3, lastSection.getHeaderContent(0).getStartPositionOffset());
        assertEquals(secondSection, lastSection.getParentSection());

        // validate paragraphs in last section
        assertEquals(3, lastSection.getParagraph(0).getNumberOfSentences());
        assertEquals(true, lastSection.getParagraph(0).getSentence(0).isFirstSentence());
        assertEquals(7, lastSection.getParagraph(0).getSentence(0).getLineNumber());
        assertEquals(0, lastSection.getParagraph(0).getSentence(0).getStartPositionOffset());
        assertEquals(2, lastSection.getParagraph(1).getNumberOfSentences());
        assertEquals(true, lastSection.getParagraph(1).getSentence(0).isFirstSentence());
        assertEquals(15, lastSection.getParagraph(1).getSentence(0).getLineNumber());
        assertEquals(false, lastSection.getParagraph(1).getSentence(1).isFirstSentence());
        assertEquals(15, lastSection.getParagraph(1).getSentence(1).getLineNumber());
        assertEquals(36, lastSection.getParagraph(1).getSentence(1).getStartPositionOffset());
    }

    @Test
    public void testGenerateDocumentWithOneLineComment() {
        String sampleText = ""
            + "\\documentclass[a4paper]{jsarticle}\n"
            + "\\author{asdasd}\n"
            + "\\title{LaTeX Parser Test}\n"
            + "\\maketitle\n"
            + "\\begin{document}\n"
            + "There are various tests.\n"
            + "% The following should be exmples\n"
            + "Most common one is unit test.\n"
            + "Integration test is also common.\n"
            + "\\end{document}\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(3, firstParagraph.getNumberOfSentences());
    }

    @Test
    public void testGenerateDocumentWithVoidComment() {
        String sampleText = ""
            + "\\documentclass[a4paper]{jsarticle}\n"
            + "\\author{asdasd}\n"
            + "\\title{LaTeX Parser Test}\n"
            + "\\maketitle\n"
            + "\\begin{document}\n"
            + "There are various tests.\n"
            + "%\n"
            + "Most common one is unit test.\n"
            + "Integration test is also common.\n"
            + "\\end{document}\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(3, firstParagraph.getNumberOfSentences());
    }

    @Test
    public void testGenerateDocumentWithVoidComment2() {
        String sampleText = ""
            + "\\documentclass[a4paper]{jsarticle}\n"
            + "\\author{asdasd}\n"
            + "\\title{LaTeX Parser Test}\n"
            + "\\maketitle\n"
            + "\\begin{document}\n"
            + "There are various tests.%\n"
            + "Most common one is unit test.\n"
            + "Integration test is also common.\n"
            + "\\end{document}\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(3, firstParagraph.getNumberOfSentences());
    }

    @Test
    public void testGenerateDocumentWithMultipleSentenceInOneSentence() {
        String sampleText = ""
            + "\\documentclass[a4paper]{jsarticle}\n"
            + "\\author{asdasd}\n"
            + "\\title{LaTeX Parser Test}\n"
            + "\\maketitle\n"
            + "\\begin{document}\n"
            + "Tokyu is a good railway company. The company is reliable. In addition it is rich.\n"
            + "\\end{document}\n";
        String[] expectedResult = {"Tokyu is a good railway company.",
                " The company is reliable.", " In addition it is rich."};
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(3, firstParagraph.getNumberOfSentences());
        for (int i = 0; i < expectedResult.length; i++) {
            assertEquals(expectedResult[i], firstParagraph.getSentence(i).getContent());
        }
    }

    @Test
    public void testGenerateDocumentWithMultipleSentences() {
        String sampleText = ""
            + "\\documentclass[a4paper]{jsarticle}\n"
            + "\\author{asdasd}\n"
            + "\\title{LaTeX Parser Test}\n"
            + "\\maketitle\n"
            + "\\begin{document}\n"
            + "Tokyu is a good railway company. The company is reliable. In addition it is rich.\n"
            + "I like the company. Howerver someone does not like it.\n"
            + "\\end{document}\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(5, firstParagraph.getNumberOfSentences());
    }

    @Test
    public void testGenerateDocumentWithMultipleSentencesWithVaraiousStopCharacters() {
        String sampleText = ""
            + "\\documentclass[a4paper]{jsarticle}\n"
            + "\\author{asdasd}\n"
            + "\\title{LaTeX Parser Test}\n"
            + "\\maketitle\n"
            + "\\begin{document}\n"
            + "Is Tokyu a good railway company? The company is reliable. In addition it is rich!\n"
            + "\\end{document}\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(3, firstParagraph.getNumberOfSentences());
        assertEquals("Is Tokyu a good railway company?", doc.getSection(0).getParagraph(0).getSentence(0).getContent());
        assertEquals(" The company is reliable.", doc.getSection(0).getParagraph(0).getSentence(1).getContent());
        assertEquals(" In addition it is rich!", doc.getSection(0).getParagraph(0).getSentence(2).getContent());
    }

    @Test
    public void testGenerateDocumentWitVoidContent() {
        String sampleText = "";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        assertEquals(0, firstSections.getParagraphs().size());
//    assertEquals(false, firstSections.getParagraphs().hasNext());
    }

    @Test
    public void testGenerateDocumentWithPeriodInSuccession() {
        String sampleText = ""
            + "\\documentclass[a4paper]{jsarticle}\n"
            + "\\author{asdasd}\n"
            + "\\title{LaTeX Parser Test}\n"
            + "\\maketitle\n"
            + "\\begin{document}\n"
            + "...\n"
            + "\\end{document}\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(1, firstParagraph.getNumberOfSentences());
    }

    @Test
    public void testGenerateDocumentWitoutPeriodInLastSentence() {
        String sampleText = ""
            + "\\documentclass[a4paper]{jsarticle}\n"
            + "\\author{asdasd}\n"
            + "\\title{LaTeX Parser Test}\n"
            + "\\maketitle\n"
            + "\\begin{document}\n"
            + "Hongo is located at the west of Tokyo. Saitama is located at the north\n"
            + "\\end{document}\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(2, firstParagraph.getNumberOfSentences());
    }

    @Test
    public void testGenerateDocumentWithSentenceLongerThanOneLine() {
        String sampleText = ""
            + "\\documentclass[a4paper]{jsarticle}\n"
            + "\\author{asdasd}\n"
            + "\\title{LaTeX Parser Test}\n"
            + "\\maketitle\n"
            + "\\begin{document}\n"
            + "This is a good day.\n"
            + "Hongo is located at the west of Tokyo "
            + "which is the capital of Japan "
            + "which is not located in the south of the earth."
            + "\\end{document}\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(2, firstParagraph.getNumberOfSentences());
    }

    // TBD: The LaTeX parser has not interest in hyperlinks yet.

    @Test
    public void testDocumentWithItalicWord() {
        String sampleText = ""
            + "\\documentclass[a4paper]{jsarticle}\n"
            + "\\author{asdasd}\n"
            + "\\title{LaTeX Parser Test}\n"
            + "\\maketitle\n"
            + "\\begin{document}\n"
            + "This is a {\\emph good} day.\n"
            + "\\end{document}\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals("This is a good day.", firstParagraph.getSentence(0).getContent());
    }

    @Test
    public void testDocumentWithMultipleItalicWords() {
        String sampleText = ""
            + "\\documentclass[a4paper]{jsarticle}\n"
            + "\\author{asdasd}\n"
            + "\\title{LaTeX Parser Test}\n"
            + "\\maketitle\n"
            + "\\begin{document}\n"
            + "\\emph{This} is a {\\emph good} day.\n"
            + "\\end{document}\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals("This is a good day.", firstParagraph.getSentence(0).getContent());
    }

    @Test
    public void testDocumentWithMultipleNearItalicWords() {
        String sampleText = ""
            + "\\documentclass[a4paper]{jsarticle}\n"
            + "\\author{asdasd}\n"
            + "\\title{LaTeX Parser Test}\n"
            + "\\maketitle\n"
            + "\\begin{document}\n"
            + "This is \\emph{a} \\emph{good} day.\n"
            + "\\end{document}\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals("This is a good day.", firstParagraph.getSentence(0).getContent());
    }

    @Test
    public void testDocumentWithItalicExpression() {
        String sampleText = ""
            + "\\documentclass[a4paper]{jsarticle}\n"
            + "\\author{asdasd}\n"
            + "\\title{LaTeX Parser Test}\n"
            + "\\maketitle\n"
            + "\\begin{document}\n"
            + "This is {\\emph a good} day.\n"
            + "\\end{document}\n";
        Document doc = createFileContent(sampleText);
        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals("This is a good day.", firstParagraph.getSentence(0).getContent());
    }

    @Test
    public void testDocumentWithHeaderCotainingMultipleSentences()
            throws UnsupportedEncodingException {
        String sampleText = ""
            + "\\documentclass[a4paper]{jsarticle}\n"
            + "\\author{asdasd}\n"
            + "\\title{LaTeX Parser Test}\n"
            + "\\maketitle\n"
            + "\\begin{document}\n"
            + "\\section{About Gunma. About Saitama.}\n"
            + "Gunma is located at west of Saitama.\n"
            + "The word also have posive meaning. Hower it is a bit wired."
            + "\\end{document}\n";
        Document doc = createFileContent(sampleText);
        Section lastSection = doc.getSection(doc.size() - 1);
        assertEquals(2, lastSection.getHeaderContentsListSize());
        assertEquals("About Gunma.", lastSection.getHeaderContent(0).getContent());
        assertEquals(" About Saitama.", lastSection.getHeaderContent(1).getContent());
    }

    @Test
    public void testDocumentWithHeaderWitoutPeriod()
            throws UnsupportedEncodingException {
        String sampleText = ""
            + "\\documentclass[a4paper]{jsarticle}\n"
            + "\\author{asdasd}\n"
            + "\\title{LaTeX Parser Test}\n"
            + "\\maketitle\n"
            + "\\begin{document}\n"
            + "\\section{About Gunma}\n"
            + "Gunma is located at west of Saitama.\n"
            + "The word also have posive meaning. Hower it is a bit wired.\n"
            + "\\end{document}\n";
        Document doc = createFileContent(sampleText);
        Section lastSection = doc.getSection(doc.size() - 1);
        assertEquals(1, lastSection.getHeaderContentsListSize());
        assertEquals("About Gunma", lastSection.getHeaderContent(0).getContent());
    }

    @Test
    public void testDocumentWithMultipleSections()
            throws UnsupportedEncodingException {
        String sampleText = ""
            + "\\documentclass[a4paper]{jsarticle}\n"
            + "\\author{asdasd}\n"
            + "\\title{LaTeX Parser Test}\n"
            + "\\maketitle\n"
            + "\\begin{document}\n"
            + "\\section{Prefectures in Japan.}\n"
            + "There are 47 prefectures in Japan.\n"
            + "\n"
            + "Each prefectures has its features.\n"
            + "\\subsection{Gunma }"
            + "Gumma is very beautiful\n"
            + "\n"
            + "\\end{document}\n";
        Document doc = createFileContent(sampleText);
        assertEquals(3, doc.size());
        Section rootSection = doc.getSection(0);
        Section h1Section = doc.getSection(1);
        Section h2Section = doc.getSection(2);

        assertEquals(0, rootSection.getLevel());
        assertEquals(1, h1Section.getLevel());
        assertEquals(2, h2Section.getLevel());

        assertEquals(rootSection.getSubSection(0), h1Section);
        assertEquals(h1Section.getParentSection(), rootSection);
        assertEquals(h2Section.getParentSection(), h1Section);
        assertEquals(rootSection.getParentSection(), null);

        assertEquals(1, rootSection.getHeaderContent(0).getLineNumber());
        assertEquals(0, rootSection.getNumberOfParagraphs());

        assertEquals(6, h1Section.getHeaderContent(0).getLineNumber());
        assertEquals(2, h1Section.getNumberOfParagraphs());
        assertEquals(1, h1Section.getParagraph(0).getNumberOfSentences());
        assertEquals(7, h1Section.getParagraph(0).getSentence(0).getLineNumber());
        assertEquals(1, h1Section.getParagraph(1).getNumberOfSentences());
        assertEquals(9, h1Section.getParagraph(1).getSentence(0).getLineNumber());

        assertEquals(10, h2Section.getHeaderContent(0).getLineNumber());
        assertEquals(1, h2Section.getNumberOfParagraphs());
        assertEquals(1, h2Section.getParagraph(0).getNumberOfSentences());
        assertEquals(11, h2Section.getParagraph(0).getSentence(0).getLineNumber());
    }

    @Test
    public void testDocumentWithoutLastPeriod()
            throws UnsupportedEncodingException {
        String sampleText = ""
            + "\\documentclass[a4paper]{jsarticle}\n"
            + "\\author{asdasd}\n"
            + "\\title{LaTeX Parser Test}\n"
            + "\\maketitle\n"
            + "\\begin{document}\n"
            + "\\section{Prefectures in Japan.}\n"
            + "There are 47 prefectures in Japan\n"
            + "\\end{document}\n";

        Document doc = createFileContent(sampleText);
        assertEquals(2, doc.size());
        Section rootSection = doc.getSection(0);
        Section h1Section = doc.getSection(1);

        assertEquals(0, rootSection.getLevel());
        assertEquals(1, h1Section.getLevel());

        assertEquals(rootSection.getSubSection(0), h1Section);
        assertEquals(h1Section.getParentSection(), rootSection);
        assertEquals(rootSection.getParentSection(), null);

        assertEquals(1, rootSection.getHeaderContent(0).getLineNumber());
        assertEquals(0, rootSection.getNumberOfParagraphs());

        assertEquals(1, h1Section.getHeaderContent(0).getLineNumber());
        assertEquals(1, h1Section.getNumberOfParagraphs());
        assertEquals(1, h1Section.getParagraph(0).getNumberOfSentences());
        assertEquals(7, h1Section.getParagraph(0).getSentence(0).getLineNumber());
    }

    @Test
    public void testGenerateJapaneseDocument() {
        String sampleText = ""
            + "\\documentclass[a4paper]{jsarticle}\n"
            + "\\author{asdasd}\n"
            + "\\title{LaTeX Parser Test}\n"
            + "\\maketitle\n"
            + "\\begin{document}\n"
            + "埼玉は東京の北に存在する。\n"
            + "大きなベッドタウンであり、多くの人が住んでいる。"
            + "\\end{document}\n";
        Configuration config =
                new Configuration.ConfigurationBuilder().setLanguage("ja").build();
        Document doc = createFileContent(sampleText, config);

        Section firstSections = doc.getSection(0);
        Paragraph firstParagraph = firstSections.getParagraph(0);
        assertEquals(2, firstParagraph.getNumberOfSentences());
    }

    @Test
    public void testErrorPositionOfMarkdownParser() throws RedPenException {
        String sampleText = ""
            + "\\documentclass[a4paper]{jsarticle}\n"
            + "\\author{asdasd}\n"
            + "\\title{LaTeX Parser Test}\n"
            + "\\maketitle\n"
            + "\\begin{document}\n"
            + "This is a good day。\n" // invalid end of sentence symbol
            + "\\end{document}\n";
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
        assertEquals(1, errors.size());
        assertEquals("InvalidSymbol", errors.get(0).getValidatorName());
        assertEquals(19, errors.get(0).getSentence().getContent().length());
        assertEquals(Optional.of(new LineOffset(6, 18)), errors.get(0).getStartPosition());
        assertEquals(Optional.of(new LineOffset(6, 19)), errors.get(0).getEndPosition());
    }

    private Document createFileContent(String inputDocumentString, Configuration conf) {
        DocumentParser parser = DocumentParser.LATEX;
        try {
            return parser.parse(inputDocumentString, new SentenceExtractor(conf.getSymbolTable()), conf.getTokenizer());
        } catch (RedPenException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Document createFileContent(String inputDocumentString) {
        Configuration conf = new Configuration.ConfigurationBuilder().build();
        DocumentParser parser = DocumentParser.LATEX;
        Document doc = null;
        try {
            doc = parser.parse(inputDocumentString, new SentenceExtractor(conf.getSymbolTable()), conf.getTokenizer());
        } catch (RedPenException e) {
            e.printStackTrace();
        }
        return doc;
    }

}
