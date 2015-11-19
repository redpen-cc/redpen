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

package cc.redpen.parser;

import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.RedPenTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.*;


/**
 * Erasing parser for the AsciiDoc format<br/>
 * <p>
 * One of the requirements for RedPen is that the line & column position (ie: offset) for
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
    private static final Line EMPTY_LINE = new Line("", 0);

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
     * The different ways embedded inline markers can be erased
     */
    private enum EraseStyle {
        All,
        None,
        Markers,
        InlineMarkup,
        PreserveLabel,
        CloseMarkerContainsDelimiters
    }


    /**
     * An 'erasing' string utility class that stores the original offset for each preserved character
     */
    private static class Line {
        // value returned for comparison if a character is escaped
        static final char ESCAPED_CHARACTER_VALUE = 'ø';
        static final String INLINE_MARKUP_DELIMITERS = " _*`#^~.,";

        // a list of offsets for each character
        List<Integer> offsets = new ArrayList<>();
        // the text for the line
        List<Character> text = new ArrayList<>();
        // marks erased characters as invalid
        List<Boolean> valid = new ArrayList<>();
        // remembers which characters were escaped in the original string
        List<Boolean> escaped = new ArrayList<>();

        private int lineNo = 0;
        private boolean allSameCharacter = false;
        private boolean erased = false;
        private boolean inBlock = false;

        private int sectionLevel = 0;
        private int listLevel = 0;
        private boolean listStart = false;

        /**
         * Construct a line using the supplied string
         *
         * @param str    the text of the line
         * @param lineno the original line number
         */
        public Line(String str, int lineno) {
            this.lineNo = lineno;
            if (!str.isEmpty()) {
                allSameCharacter = true;
                char lastCh = 0;
                for (int i = 0; i < str.length(); i++) {
                    char ch = str.charAt(i);

                    if ((i < str.length() - 1) && (ch == '\\')) {
                        i++;
                        ch = str.charAt(i);
                        escaped.add(true);
                    }
                    else {
                        escaped.add(false);
                    }

                    offsets.add(i);
                    text.add(ch);
                    valid.add(true);

                    if ((lastCh != 0) && (lastCh != ch)) {
                        allSameCharacter = false;
                    }
                    lastCh = ch;
                }
            }

            // trim the end
            while (!text.isEmpty() &&
                    Character.isWhitespace(text.get(text.size() - 1))) {
                text.remove(text.size() - 1);
            }
        }

        /**
         * Is this line a repeating set of the same character?
         *
         * @return
         */
        public boolean isAllSameCharacter() {
            return allSameCharacter;
        }

        /**
         * Has this line been completely erased?
         *
         * @return
         */
        public boolean isErased() {
            return erased;
        }

        /**
         * Is this line in a block?
         *
         * @return
         */
        public boolean isInBlock() {
            return inBlock;
        }


        /**
         * Is this line the start of a new list item?
         *
         * @return
         */
        public boolean isListStart() {
            return listStart;
        }

        /**
         * Return the list level for this line, or zero if it is not in a list.
         *
         * @return the list level, or zero if not in a list
         */
        public int getListLevel() {
            return listLevel;
        }

        /**
         * If the line is a section header, return the section level,
         * or zero if the line is not a section header
         *
         * @return the section level or zero if not a section header
         */
        public int getSectionLevel() {
            return sectionLevel;
        }

        public void setInBlock(boolean inBlock) {
            this.inBlock = inBlock;
        }

        public void setSectionLevel(int newSectionLevel) {
            this.sectionLevel = newSectionLevel;
        }

        public void setListLevel(int newListLevel) {
            this.listLevel = newListLevel;
        }

        public void setListStart(boolean listStart) {
            this.listStart = listStart;
        }

        /**
         * Erase length characters in the line, starting at pos
         *
         * @param pos
         * @param length
         */
        public void erase(int pos, int length) {
            if ((pos >= 0) && (pos < valid.size())) {
                for (int i = pos; (i < valid.size()) && (i < pos + length); i++) {
                    valid.set(i, false);
                }
            }
        }

        /**
         * Erase the whole line
         */
        public void erase() {
            for (int i = 0; i < valid.size(); i++) {
                valid.set(i, false);
            }
            erased = true;
        }

        /**
         * Erase all occurances of the given string
         *
         * @param segment
         */
        public void erase(String segment) {
            for (int i = 0; i < text.size(); i++) {
                boolean found = true;
                for (int j = 0; j < segment.length(); j++) {
                    if (charAt(j + i) != segment.charAt(j)) {
                        found = false;
                        break;
                    }
                }
                if (found) {
                    erase(i, segment.length());
                    i += segment.length();
                }
            }
        }

        /**
         * Erase the open and close markers, and optionally all the text inside them
         * Returns the position of the first enclosure or -1 if no enclosure was found
         *
         * @param open
         * @param close
         * @param eraseStyle
         * @return position of first enclosure
         */
        public int eraseEnclosure(String open,
                                  String close,
                                  EraseStyle eraseStyle) {
            boolean inEnclosure = false;
            int firstEnclosurePosition = -1;
            int lastCommaPosition = -1;
            int enclosureStart = 0;
            for (int i = 0; i < length(); i++) {
                if (valid.get(i)) {
                    if (!inEnclosure) {
                        // look for the open string
                        boolean foundOpen = true;
                        for (int j = 0; j < open.length(); j++) {
                            if (charAt(i + j) != open.charAt(j)) {
                                foundOpen = false;
                                break;
                            }
                        }
                        // inline requires start of line or a space before the marker
                        if (foundOpen && (eraseStyle == EraseStyle.InlineMarkup)) {
                            if ((i != 0) &&
                                    (INLINE_MARKUP_DELIMITERS.indexOf(charAt(i - 1)) == -1)) {
                                foundOpen = false;
                            }
                        }
                        if (foundOpen) {
                            enclosureStart = i;
                            inEnclosure = true;
                            firstEnclosurePosition = i;
                        }
                    }
                    else {
                        // look for the close string
                        boolean foundClose = true;
                        if (eraseStyle == EraseStyle.CloseMarkerContainsDelimiters) {
                            foundClose = (close.indexOf(charAt(i)) != -1);
                        }
                        else {
                            for (int j = 0; j < close.length(); j++) {
                                if (charAt(i + j) != close.charAt(j)) {
                                    foundClose = false;
                                    break;
                                }
                            }
                        }

                        if (foundClose && (eraseStyle == EraseStyle.InlineMarkup)) {
                            if ((i != length() - 1) &&
                                    (INLINE_MARKUP_DELIMITERS.indexOf(charAt(i + close.length())) == -1)) {
                                foundClose = false;
                            }
                        }

                        if (foundClose) {
                            switch (eraseStyle) {
                                case All:
                                    erase(enclosureStart, (i - enclosureStart) + close.length());
                                    break;
                                case Markers:
                                case InlineMarkup:
                                    erase(enclosureStart, open.length());
                                    erase(i, close.length());
                                    break;
                                case PreserveLabel:
                                    if (lastCommaPosition != -1) {
                                        erase(enclosureStart, (lastCommaPosition + 1) - enclosureStart);
                                        erase(i, close.length());
                                    }
                                    else {
                                        erase(enclosureStart, open.length());
                                        erase(i, close.length());
                                    }
                                    break;
                                case CloseMarkerContainsDelimiters:
                                    erase(enclosureStart, (i - enclosureStart));
                                    break;
                                case None:
                                    break;
                            }
                            inEnclosure = false;
                            lastCommaPosition = -1;
                        }
                        else if (charAt(i) == ',') {
                            lastCommaPosition = i;
                        }
                    }
                }
            }
            return firstEnclosurePosition;
        }

        /**
         * Return the length of the line
         *
         * @return
         */
        public int length() {
            return text.size();
        }

        /**
         * Return the character at the given position. Erase characters will return 0 rather
         * than the actual character
         *
         * @param i
         * @return
         */
        public char charAt(int i) {
            return charAt(i, false);
        }

        /**
         * Return the character at the given position, optionally including erased characters
         *
         * @param i
         * @param includeInvalid
         * @return
         */
        public char charAt(int i, boolean includeInvalid) {
            if ((i >= 0) && (i < text.size())) {
                if (escaped.get(i)) {
                    return ESCAPED_CHARACTER_VALUE;
                }
                if (includeInvalid || valid.get(i)) {
                    return text.get(i);
                }
            }
            return 0;
        }

        /**
         * Return the offset for the character at the given position
         *
         * @param i
         * @return
         */
        public int getOffset(int i) {
            if (i >= 0) {
                if (i < offsets.size()) {
                    return offsets.get(i);
                }
                else {
                    return offsets.size();
                }
            }
            return 0;
        }

        /**
         * Return the original line number for this line
         *
         * @return
         */
        public int getLineNo() {
            return lineNo;
        }

        /**
         * Return the raw character at the specified position, ignoring its validity.
         *
         * @param i
         * @return
         */
        public char rawCharAt(int i) {
            if ((i >= 0) && (i < text.size())) {
                return text.get(i);
            }
            return ' ';
        }

        /**
         * Is the character at the given position valid?
         *
         * @param i
         * @return
         */
        public boolean isValid(int i) {
            if ((i >= 0) && (i < text.size())) {
                return valid.get(i);
            }
            return false;
        }

        /**
         * Is the character at the given position empty (ie: whitespace or invalid)
         *
         * @return
         */
        public boolean isEmpty() {
            for (int i = 0; i < text.size(); i++) {
                if (!Character.isWhitespace(text.get(i)))
                    if (valid.get(i)) {
                        return false;
                    }
            }

            return true;
        }

        /**
         * Does the line start with the given string?
         *
         * @param s
         * @return
         */
        public boolean startsWith(String s) {
            for (int i = 0; i < s.length(); i++) {
                if (charAt(i) != s.charAt(i)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public String toString() {
            String result = "";
            for (int i = 0; i < text.size(); i++) {
                if (valid.get(i)) {
                    result += text.get(i);
                }
                else {
                    result += "·" + text.get(i);
                }
            }
            return (erased ? "X" : " ") +
                    (inBlock ? "[" : " ") +
                    sectionLevel + "-" +
                    listLevel + "-" +
                    String.format("%03d", lineNo) +
                    (listStart ? "*" : ":") +
                    " " +
                    result;
        }
    }

    /**
     * A model of the original document, represented as an array of lines
     */
    private class Model {
        private List<Line> lines = new ArrayList<>();

        private int lineIndex = 0;
        private SentenceExtractor sentenceExtractor;

        /**
         * Construct a model. The sentence extract will be used delimit sentences and
         * join lines together when adding the model to the document builder
         *
         * @param sentenceExtractor
         */
        public Model(SentenceExtractor sentenceExtractor) {
            this.sentenceExtractor = sentenceExtractor;
        }

        /**
         * Return the offset string from the model at the given line number
         *
         * @param lineNumber
         * @return
         */
        public Line getLine(int lineNumber) {
            int index = lineNumber - 1;
            if ((index >= 0) && (index < lines.size())) {
                return lines.get(index);
            }
            return EMPTY_LINE;
        }

        /**
         * Add a line to the model
         *
         * @param line
         */
        public void add(Line line) {
            lines.add(line);
        }

        /**
         * Return the number of lines in the model
         *
         * @return
         */
        public int lineCount() {
            return lines.size();
        }

        /**
         * Set the line pointer to the first  line
         */
        public void rewind() {
            lineIndex = 0;
        }

        /**
         * Get the next line from the model, or null if there are no more lines
         *
         * @return
         */
        public Line getNextLine() {
            if (lineIndex < lines.size()) {
                Line line = lines.get(lineIndex);
                lineIndex++;
                return line;
            }
            return null;
        }

        /**
         * Return the current line from the model, or null if we've run out of lines
         *
         * @return
         */
        public Line getCurrentLine() {
            if (lineIndex < lines.size()) {
                Line line = lines.get(lineIndex);
                return line;
            }
            return null;
        }

        /**
         * Are there more lines to iterate through?
         *
         * @return
         */
        public boolean isMore() {
            return lineIndex < lines.size();
        }

        /**
         * Convert the single line into an array of sentences
         *
         * @param line
         * @return
         */
        public List<Sentence> convertToSentences(Line line) {
            List<Line> lines = new ArrayList<>();
            lines.add(line);
            return convertToSentences(lines);
        }

        /**
         * Convert a list of Lines into sentences by breaking them up appropriately, whilst
         * tracking their original line number and offset
         *
         * @param lines
         * @return
         */
        public List<Sentence> convertToSentences(List<Line> lines) {
            List<Sentence> sentences = new ArrayList<>();

            String content = "";
            List<LineOffset> offsets = new ArrayList<>();
            for (int ln = 0; ln < lines.size(); ln++) {
                Line line = lines.get(ln);

                for (int i = 0; i < line.length(); i++) {
                    if (line.isValid(i)) {
                        content += line.rawCharAt(i);
                        offsets.add(new LineOffset(line.getLineNo(), line.getOffset(i)));
                        // check for end of sentence
                        if (sentenceExtractor.getSentenceEndPosition("" + line.rawCharAt(i)) != -1) {
                            sentences.add(new Sentence(content, offsets, Collections.EMPTY_LIST));
                            content = "";
                            offsets = new ArrayList<>();
                        }
                    }
                }
                // join lines
                if ((lines.size() > 1) && (ln != lines.size() - 1)) {
                    for (char c : sentenceExtractor.getBrokenLineSeparator().toCharArray()) {
                        content += c;
                        offsets.add(new LineOffset(line.getLineNo(), line.getOffset(line.length())));
                    }
                }
            }
            // add remaining line
            if (!content.trim().isEmpty()) {
                sentences.add(new Sentence(content, offsets, Collections.EMPTY_LIST));
            }
            return sentences;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Line ostring : lines) {
                sb.append(ostring.toString());
                sb.append("\n");
            }
            return sb.toString();
        }

    }

    @Override
    public Document parse(InputStream io, Optional<String> fileName, SentenceExtractor sentenceExtractor, RedPenTokenizer tokenizer) throws RedPenException {
        Document.DocumentBuilder documentBuilder = new Document.DocumentBuilder(tokenizer);
        fileName.ifPresent(documentBuilder::setFileName);

        State state = new State();
        Model model = new Model(sentenceExtractor);

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
                model.add(new Line(line, lineno));
            }
            reader.close();

            // process the model
            for (Line offsetLine : model.lines) {
                processLine(offsetLine, model, state);
            }
            processHeader(model);

        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Exception when parsing AsciiDoc file", e);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("AsciiDoc parser model (X=erased line,[=block,section-listlevel-lineno,*=list item):\n" + model.toString());
        }

        convertModel(model, documentBuilder);

        return documentBuilder.build();
    }

    /**
     * Conver the parser's model to the RedPen document model
     *
     * @param model
     * @param builder
     */
    private void convertModel(Model model, Document.DocumentBuilder builder) {
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
     * @param line
     * @param state the current state
     */
    private void processLine(Line line, Model model, State state) {

        if (!line.isErased()) {

            Line previousLine = model.getLine(line.lineNo - 1);
            Line nextLine = model.getLine(line.lineNo + 1);

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
                    state.inBlock = false;
                }
                else if ((line.length() >= 4) && (firstChar == '|') && (secondChar == '=')) {
                    // end a table
                    line.erase();
                    state.inBlock = false;
                }
                // erase the block content
                line.setInBlock(true);
                if (state.eraseBlock) {
                    line.erase();
                }
                return;
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

            // see if we are starting other blocks
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
                    case '_':
                    case '*':
                        // blocks whose innards are preserved
                        state.inBlock = true;
                        state.eraseBlock = false;
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

            // attributes
            if (line.eraseEnclosure(":", ":", EraseStyle.None) == 0) {
                line.erase();
            }
            if (line.eraseEnclosure("[", "]", EraseStyle.None) == 0) {
                line.erase();
            }

            // check for a title
            if ((firstChar == '.') && (" .".indexOf(secondChar) == -1)) {
                line.erase(0, 1);
            }

            // erase urls and links
            line.eraseEnclosure("link:", " ,[", EraseStyle.CloseMarkerContainsDelimiters);
            line.eraseEnclosure("http://", " ,[", EraseStyle.CloseMarkerContainsDelimiters);

            // other directives (image, include etc)
            line.eraseEnclosure("image:", " ,[", EraseStyle.CloseMarkerContainsDelimiters);
            line.eraseEnclosure("include:", " ,[", EraseStyle.CloseMarkerContainsDelimiters);

            // enclosed directives
            line.eraseEnclosure("+++", "+++", EraseStyle.All);
            line.eraseEnclosure("[[", "]]", EraseStyle.All);

            line.eraseEnclosure("<<", ">>", EraseStyle.PreserveLabel);

            line.eraseEnclosure("{", "}", EraseStyle.Markers); // NOTE: should we make substitutions?
            line.eraseEnclosure("[", "]", EraseStyle.Markers);

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
            if (isListElement(line, nextLine)) {
                state.inList = true;
            }

            // continuation markers
            if (line.charAt(line.length() - 1) == '+') {
                if (line.length() == 0) {
                    line.erase();
                }
                else {
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
    }


    /**
     * Erase all inline markup (bold, italics, special tokens etc)
     *
     * @param line
     */
    private void eraseInlineMarkup(Line line) {
        // inline markup (bold, italics etc)
        line.eraseEnclosure("__", "__", EraseStyle.Markers);
        line.eraseEnclosure("**", "**", EraseStyle.Markers);
        line.eraseEnclosure("``", "``", EraseStyle.Markers);
        line.eraseEnclosure("##", "##", EraseStyle.Markers);
        line.eraseEnclosure("^", "^", EraseStyle.Markers);
        line.eraseEnclosure("~", "~", EraseStyle.Markers);

        line.eraseEnclosure("_", "_", EraseStyle.InlineMarkup);
        line.eraseEnclosure("*", "*", EraseStyle.InlineMarkup);
        line.eraseEnclosure("`", "`", EraseStyle.InlineMarkup);
        line.eraseEnclosure("#", "#", EraseStyle.InlineMarkup);

        line.erase("'`");
        line.erase("`'");
        line.erase("\"`");
        line.erase("`\"");
        line.erase("(C)");
        line.erase("(R)");
        line.erase("(TM)");
    }
}

