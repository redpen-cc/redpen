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

import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.parser.BaseDocumentParser;
import cc.redpen.parser.SentenceExtractor;
import cc.redpen.parser.common.Line;
import cc.redpen.parser.common.Model;
import cc.redpen.tokenizer.RedPenTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Erasing parser for the AsciiDoc format<br>
 * <p>
 * One of the requirements for RedPen is that the line &amp; column position (ie: offset) for
 * each character in the parsed text be preserved throughout parsing and validation.
 * <p>
 * This AsciiDoc parser attempts to solve this requirement by maintaining a model of the source
 * document's characters and their original position, and then logically 'erasing' the parts of
 * that model - usually the markup - that should not be presented to RedPen for validation.
 * <p>
 * The remaining "un-erased" text is transformed into RedPen's document model.
 * <p>
 * AsciiDoc's syntax and grammar is documented at @see http://asciidoc.org/
 */
public class AsciiDocParser extends BaseDocumentParser {

    private static final Logger LOG = LoggerFactory.getLogger(AsciiDocParser.class);

    /**
     * An array of AsciiDoctor macros to erase
     */
    private static final String[] MACROS = {
            "ifdef::",
            "ifndef::",
            "ifeval::",
            "endif::",
    };

    /**
     * An array of AsciiDoc admonitions to erase
     */
    private static final String[] ADMONITIONS = {
            "NOTE: ",
            "TIP: ",
            "IMPORTANT: ",
            "CAUTION: ",
            "WARNING: "
    };

    /**
     * An array of AsciiDoc external link prefixes
     */
    private static final String[] EXTERNAL_LINK_PREFIXES = {
            "link:",
            "http://",
            "https://",
            "image:",
            "include:"
    };

    /**
     * current parser state
     */
    private class State {
        // are we in a block
        public boolean inBlock = false;
        // are we in a list?
        public boolean inList = false;
        // should we erase lines within the current block?
        public boolean eraseBlock = true;
        // the sort of block we are in
        public char blockMarker = 0;
        // length of the lead block marker
        public int blockMarkerLength = 0;
    }


    @Override
    public Document parse(InputStream inputStream, Optional<String> fileName, SentenceExtractor sentenceExtractor,
                          RedPenTokenizer tokenizer) throws RedPenException {
        Document.DocumentBuilder documentBuilder = Document.builder(tokenizer);
        fileName.ifPresent(documentBuilder::setFileName);

        Model model = new Model(sentenceExtractor);

        // add the lines from the input to the model
        populateModel(model, inputStream);

        // convert the model to a redpen document
        convertModel(model, documentBuilder);

        return documentBuilder.build();
    }

    /**
     * populate the erasable model with the text from the inputstream
     * @param model model to populate
     * @param io stream to read
     */
    protected void populateModel(Model model, InputStream io) {
        BufferedReader reader = createReader(io);

        int lineno = 0;
        try {
            // add the lines to the model
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                lineno++;
                model.add(new AsciiDocLine(line, lineno));
            }
            reader.close();

            // preocess each line of the model
            State state = new State();
            for (model.rewind(); model.isMore(); model.getNextLine()) {
                processLine(model.getCurrentLine(), model, state);
            }

            processHeader(model);

        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Exception when parsing AsciiDoc file", e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("AsciiDoc parser model (X=erased line,[=block,section-listlevel-lineno,*=list item):\n" + model.toString());
        }
    }

    /**
     * Conver the parser's model to the RedPen document model
     *
     * @param model model to convert
     * @param builder doc builder
     */
    protected void convertModel(Model model, Document.DocumentBuilder builder) {
        model.rewind();

        // add a header if there isn't one in the model
        if ((model.getCurrentLine() != null) && (model.getCurrentLine().getSectionLevel() == 0)) {
            builder.addSection(0);
        }

        while (model.isMore()) {

            // skip blank lines
            while (model.isMore() && model.getCurrentLine().isEmpty()) {
                model.getNextLine();
            }

            if (model.isMore()) {
                // check for new sections
                if (model.getCurrentLine().getSectionLevel() > 0) {
                    builder.addSection(
                            model.getCurrentLine().getSectionLevel(),
                            model.convertToSentences(model.getCurrentLine())
                    );
                    model.getNextLine();
                }
                // check for a list item
                else if (model.getCurrentLine().isListStart()) {
                    List<Line> listElementLines = new ArrayList<>();
                    int listLevel = model.getCurrentLine().getListLevel();

                    // add the list start line
                    listElementLines.add(model.getCurrentLine());

                    // test the following lines to see if they continue this list item
                    model.getNextLine();
                    while (model.isMore() &&
                            !model.getCurrentLine().isListStart() &&
                            (model.getCurrentLine().getListLevel() == listLevel)) {
                        listElementLines.add(model.getCurrentLine());
                        model.getNextLine();
                    }
                    builder.addListElement(listLevel, model.convertToSentences(listElementLines));
                }
                // process a paragraph
                else {
                    List<Line> paragraphLines = new ArrayList<>();
                    // current line can't be empty, so this loop will enter at least once
                    while (model.isMore() && !model.getCurrentLine().isEmpty()) {
                        paragraphLines.add(model.getCurrentLine());
                        model.getNextLine();
                    }
                    builder.addParagraph();
                    model.convertToSentences(paragraphLines).forEach(builder::addSentence);
                }
            }
        }
    }

    /**
     * Does the give line start a list?
     *
     * @param line
     * @param nextLine the subsequent line
     * @return
     */
    private boolean isListElement(Line line, Line nextLine) {

        int pos = 0;
        while (Character.isWhitespace(line.charAt(pos))) {
            pos++;
        }

        // is the first non-space character a suitable list marker?
        if (".-*".indexOf(line.charAt(pos)) != -1) {
            char listMarker = line.charAt(pos);
            int level = 1;
            pos++;
            // count the list marker's size
            while (line.charAt(pos) == listMarker) {
                pos++;
                level++;
            }
            // we need a whitespace
            if (Character.isWhitespace(line.charAt(pos))) {
                // remember the list level
                line.setListLevel(level);
                line.setListStart(true);
                // remove the list markup
                line.erase(0, pos);
                // remove whitespace
                while (Character.isWhitespace(line.charAt(pos))) {
                    line.erase(pos, 1);
                    pos++;
                }
                return true;
            }
        }

        // test for labelled lists
        if ((line.charAt(line.length() - 1) == ':') &&
                (line.charAt(line.length() - 2) == ':')) {
            int level = 1;
            pos = line.length() - 3;
            while ((pos > 0) && line.charAt(pos) == ':') {
                pos--;
                level++;
            }
            nextLine.setListLevel(level);
            nextLine.setListStart(true);
            line.erase();
            return true;
        }

        return false;
    }

    /**
     * Process the header, erasing the annotations that can follow it
     *
     * @param model
     */
    private void processHeader(Model model) {
        // look for "= Text of header" or "Text of header\n==========="
        if (model.lineCount() > 1) {
            boolean haveHeader = false;
            if ((model.getLine(1).charAt(0, true) == '=') &&
                    (model.getLine(1).charAt(1, true) == ' ')) {
                haveHeader = true;
            }
            else if ((model.getLine(1).length() == model.getLine(2).length()) &&
                    model.getLine(2).isAllSameCharacter() &&
                    (model.getLine(2).charAt(0, true) == '=')) {
                haveHeader = true;
            }

            if (haveHeader) {
                model.getLine(1).setSectionLevel(1);
                // erase lines up until an empty line
                for (int i = 2; i <= model.lineCount(); i++) {
                    if (!model.getLine(i).isErased() && model.getLine(i).isEmpty()) {
                        break;
                    }
                    model.getLine(i).erase();
                }
            }
        }
    }

    /**
     * Process the current line, removing asciidoc tags and markup and setting the current state
     *
     * @param pline
     * @param state the current state
     */
    private void processLine(Line pline, Model model, State state) {
        AsciiDocLine line = (AsciiDocLine) pline;

        if (line.isErased()) { return; }

        Line previousLine = model.getLine(line.getLineNo() - 1);
        Line nextLine = model.getLine(line.getLineNo() + 1);

        if (state.inList && (line.getListLevel() == 0)) {
            line.setListLevel(previousLine.getListLevel());
        }

        char firstChar = line.charAt(0);
        char secondChar = line.charAt(1);

        // check for block end
        if (state.inBlock) {
            if (line.isAllSameCharacter() &&
                    (firstChar == state.blockMarker) &&
                    (line.length() == state.blockMarkerLength)) {
                // end a regular block
                line.erase();
                line.setInBlock(true);
                state.inBlock = false;
                return;
            } else if ((line.length() >= 4) &&
                    (firstChar == state.blockMarker) &&
                    (firstChar == '|') &&
                    (secondChar == '=')) {
                // end a table
                line.erase();
                line.setInBlock(true);
                state.inBlock = false;
                return;
            }
            // erase the block content
            line.setInBlock(true);
            if (state.eraseBlock) {
                line.erase();
                return;
            }
        }

        // check for old style heading (line followed by single-char-line of same length)
        if (line.isAllSameCharacter() &&
                (line.length() == previousLine.length()) &&
                ("=-~^+".indexOf(firstChar) != -1) &&
                (". [".indexOf(previousLine.charAt(0, true)) == -1)) {
            previousLine.setSectionLevel(1);
            line.erase();
            return;
        }

        // horizontal rule
        if (line.isAllSameCharacter() && (line.length() == 3) && (line.charAt(0) == '\'')) {
            line.erase();
            return;
        }

        // block markers in which we process the internal text
        if (line.isAllSameCharacter() && (line.length() >= 4) &&
                ("_*".indexOf(firstChar) != -1)) {
            line.erase();
            return;
        }

        // test for various block starts
        if (!state.inBlock) {
            // fenced block
            if (line.isAllSameCharacter() && (line.length() == 3) && (line.charAt(0) == '`')) {
                state.inBlock = true;
                state.eraseBlock = true;
                state.blockMarker = firstChar;
                state.blockMarkerLength = line.length();
                line.setInBlock(true);
                line.erase();
                return;
            }

            // see if we are starting other types of blocks
            if (line.isAllSameCharacter() && (line.length() >= 4)) {
                switch (firstChar) {
                    case '-':
                    case '=':
                    case '&':
                    case '/':
                    case '+':
                    case '.':
                        // blocks that have their innards erased
                        state.inBlock = true;
                        state.eraseBlock = true;
                        state.blockMarker = firstChar;
                        state.blockMarkerLength = line.length();
                        line.setInBlock(true);
                        line.erase();
                        return;
                }
            }

            // see if this is a table marker
            if ((line.length() >= 4) && (firstChar == '|') && (secondChar == '=')) {
                line.erase();
                state.inBlock = true;
                state.eraseBlock = true;
                state.blockMarker = '|';
                state.blockMarkerLength = 1;
                line.setInBlock(true);
                return;
            }

        }

        // check for a single-line literal sentence
        if (!state.inList && (firstChar == ' ')) {
            line.erase();
            return;
        }

        // maybe we have a comment?
        if ((firstChar == '/') && (secondChar == '/')) {
            line.erase();
            return;
        }

        // 'open' block marker
        if ((firstChar == '-') && (secondChar == '-')) {
            line.erase();
            return;
        }

        // attributes (at position == 0)
        if (line.eraseEnclosure(":", ":", AsciiDocLine.EraseStyle.None) == 0) {
            line.erase();
            return;
        }
        if (line.eraseEnclosure("[", "]", AsciiDocLine.EraseStyle.None) == 0) {
            line.erase();
            return;
        }

        // check for a title
        if ((firstChar == '.') && (" .".indexOf(secondChar) == -1)) {
            line.erase(0, 1);
        }

        // erase urls and links
        for (String prefix : EXTERNAL_LINK_PREFIXES) {
            line.eraseEnclosure(prefix, " ,[", AsciiDocLine.EraseStyle.CloseMarkerContainsDelimiters);
        }

        // enclosed directives
        line.eraseEnclosure("+++", "+++", AsciiDocLine.EraseStyle.All);
        line.eraseEnclosure("[[", "]]", AsciiDocLine.EraseStyle.All);
        line.eraseEnclosure("<<", ">>", AsciiDocLine.EraseStyle.PreserveLabel);
        line.eraseEnclosure("{", "}", AsciiDocLine.EraseStyle.Markers); // NOTE: should we make substitutions?
        line.eraseEnclosure("[", "]", AsciiDocLine.EraseStyle.Markers);

        // headers
        int headerIndent = 0;
        while (line.charAt(headerIndent) == '=') {
            headerIndent++;
        }
        if ((headerIndent > 0) && (line.charAt(headerIndent) == ' ')) {
            line.erase(0, headerIndent + 1);
            line.setSectionLevel(headerIndent);
        }

        // lists!
        if (!state.inBlock && isListElement(line, nextLine)) {
            state.inList = true;
        }

        // continuation markers
        if (line.charAt(line.length() - 1) == '+') {
            if (line.length() == 0) {
                line.erase();
            } else {
                line.erase(line.length() - 1, 1);
            }
        }

        // a blank line will cancel any list element we are in
        if (state.inList && (line.length() == 0)) {
            state.inList = false;
            line.setListLevel(0);
        }

        // macros
        for (String macro : MACROS) {
            if (line.startsWith(macro)) {
                line.erase();
                break;
            }
        }

        // admonitions
        for (String admonition : ADMONITIONS) {
            if (line.startsWith(admonition)) {
                line.erase(0, admonition.length());
                break;
            }
        }

        eraseInlineMarkup(line);
    }


    /**
     * Erase all inline markup (bold, italics, special tokens etc)
     *
     * @param line
     */
    private void eraseInlineMarkup(AsciiDocLine line) {
        // inline markup (bold, italics etc)
        line.eraseEnclosure("__", "__", AsciiDocLine.EraseStyle.Markers);
        line.eraseEnclosure("**", "**", AsciiDocLine.EraseStyle.Markers);
        line.eraseEnclosure("``", "``", AsciiDocLine.EraseStyle.Markers);
        line.eraseEnclosure("##", "##", AsciiDocLine.EraseStyle.Markers);
        line.eraseEnclosure("^", "^", AsciiDocLine.EraseStyle.Markers);
        line.eraseEnclosure("~", "~", AsciiDocLine.EraseStyle.Markers);

        line.eraseEnclosure("_", "_", AsciiDocLine.EraseStyle.InlineMarkup);
        line.eraseEnclosure("*", "*", AsciiDocLine.EraseStyle.InlineMarkup);
        line.eraseEnclosure("`", "`", AsciiDocLine.EraseStyle.InlineMarkup);
        line.eraseEnclosure("#", "#", AsciiDocLine.EraseStyle.InlineMarkup);

        line.erase("'`");
        line.erase("`'");
        line.erase("\"`");
        line.erase("`\"");
        line.erase("(C)");
        line.erase("(R)");
        line.erase("(TM)");
    }
}

