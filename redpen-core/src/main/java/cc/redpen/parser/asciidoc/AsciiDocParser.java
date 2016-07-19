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

import cc.redpen.parser.PreprocessingReader;
import cc.redpen.parser.common.Line;
import cc.redpen.parser.common.LineParser;
import cc.redpen.parser.common.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;


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
public class AsciiDocParser extends LineParser {

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


    /**
     * populate the erasable model with the text from the inputstream
     * @param model model to populate
     * @param io stream to read
     */
    protected void populateModel(Model model, InputStream io) {
        PreprocessingReader reader = createReader(io);

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

            model.setPreprocessorRules(reader.getPreprocessorRules());

            // process each line of the model
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

        // test for single line labeled list
        // NOTE: single line labeled list must be level 1
        int position = 0;
        if ((position = line.getText().indexOf(":: ")) != -1) {
            line.setListLevel(1);
            line.setListStart(true);
            line.erase(0, position+3);
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
     * @param line
     * @param state the current state
     */
    private void processLine(Line line, Model model, State state) {
        if (line.isErased()) { return; }

        TargetLine target = new TargetLine(line,
                model.getLine(line.getLineNo() - 1),
                model.getLine(line.getLineNo() + 1));

        if (state.inList && (line.getListLevel() == 0)) {
            line.setListLevel(target.previousLine.getListLevel());
        }

        // check for block end
        if (state.inBlock) {
            if (line.isAllSameCharacter() &&
                    (target.firstChar == state.blockMarker) &&
                    (line.length() == state.blockMarkerLength)) {
                // end a regular block
                line.erase();
                line.setInBlock(true);
                state.inBlock = false;
                return;
            } else if ((line.length() >= 4) &&
                    (target.firstChar == state.blockMarker) &&
                    (target.firstChar == '|') &&
                    (target.secondChar == '=')) {
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
                (line.length() == target.previousLine.length()) &&
                ("=-~^+".indexOf(target.firstChar) != -1) &&
                (". [".indexOf(target.previousLine.charAt(0, true)) == -1)) {
            target.previousLine.setSectionLevel(1);
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
                ("_*".indexOf(target.firstChar) != -1)) {
            line.erase();
            return;
        }

        // test for various block starts
        if (!state.inBlock) {
            // fenced block
            if (line.isAllSameCharacter() && (line.length() == 3) && (line.charAt(0) == '`')) {
                state.inBlock = true;
                state.eraseBlock = true;
                state.blockMarker = target.firstChar;
                state.blockMarkerLength = line.length();
                line.setInBlock(true);
                line.erase();
                return;
            }

            // see if we are starting other types of blocks
            if (line.isAllSameCharacter() && (line.length() >= 4)) {
                switch (target.firstChar) {
                    case '-':
                    case '=':
                    case '&':
                    case '/':
                    case '+':
                    case '.':
                        // blocks that have their innards erased
                        state.inBlock = true;
                        state.eraseBlock = true;
                        state.blockMarker = target.firstChar;
                        state.blockMarkerLength = line.length();
                        line.setInBlock(true);
                        line.erase();
                        return;
                }
            }

            // see if this is a table marker
            if ((line.length() >= 4) && (target.firstChar == '|') && (target.secondChar == '=')) {
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
        if (!state.inList && (target.firstChar == ' ')) {
            line.erase();
            return;
        }

        // maybe we have a comment?
        if ((target.firstChar == '/') && (target.secondChar == '/')) {
            line.erase();
            return;
        }

        // 'open' block marker
        if ((target.firstChar == '-') && (target.secondChar == '-')) {
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
        if ((target.firstChar == '.') && (" .".indexOf(target.secondChar) == -1)) {
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

        int headerIndent = 0;
        while (line.charAt(headerIndent) == '=') {
            headerIndent++;
        }
        if ((headerIndent > 0) && (line.charAt(headerIndent) == ' ')) {
            line.erase(0, headerIndent + 1);
            line.setSectionLevel(headerIndent);
        }

        // lists!
        if (!state.inBlock && isListElement(line, target.nextLine)) {
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
    private void eraseInlineMarkup(Line line) {
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
