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

import cc.redpen.parser.PreprocessingReader;
import cc.redpen.parser.common.Line;
import cc.redpen.parser.common.LineParser;
import cc.redpen.parser.common.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cc.redpen.parser.rest.MultiLineProcessUtils.processMultiLineMatch;

public class ReSTParser extends LineParser {
    private static final Logger LOG = LoggerFactory.getLogger(ReSTParser.class);

    static Pattern DIGIT_PATTERN = Pattern.compile("^\\s*[0-9#]+\\.");
    static Pattern NORMAL_TABLE_PATTERN = Pattern.compile("^[+][-+]+[+]$");
    static Pattern CSV_TABLE_PATTERN = Pattern.compile("^=+[= ]+=$");
    static Pattern DIRECTIVE_PATTERN = Pattern.compile("^[.][.] [a-z]+::");
    static Pattern INLINE_COMMENT_PATTERN = Pattern.compile("^[.][.] [^\\[][^:]+$");

    /**
     * current parser state
     */
    private class State {
        // are we in table
        public boolean inTable = false;
        // are we in a block such as directives, comments and so on
        public boolean inBlock = false;
        // are we in a list?
        public boolean inList = false;
        // should we erase lines within the current block?
        public boolean eraseDirective = true;
        // the sort of directives we are in
        public String type;
    }

    @Override
    protected void populateModel(Model model, InputStream io) {
        State state = new State();
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
                model.add(new ReSTLine(line, lineno));
            }
            reader.close();
            model.setPreprocessorRules(reader.getPreprocessorRules());
            for (model.rewind(); model.isMore(); model.getNextLine()) {
                processLine(model.getCurrentLine(), model, state);
            }
        } catch (Exception e) {
            LOG.error("Exception when parsing reST file", e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("reST parser model (X=erased line,[=block,section-listlevel-lineno,*=list item):\n" + model.toString());
        }
    }

    private void processLine(Line line, Model model, State state) {
        if (line.isErased()) { return; }
        TargetLine target = new TargetLine(line,
                model.getLine(line.getLineNo() - 1),
                model.getLine(line.getLineNo() + 1));

        // handle section
        int level = extractSectionLevel(target);
        if (level > 0) {
            line.setSectionLevel(level);
            reset(state);
        }

        // handle inline markups
        this.eraseInlineMarkup(line);

        // handle list (bullets, definition...)
        if (!state.inBlock && isListElement(target, state)) { state.inList = true; }

        // handle table (normal, csv)
        if (!state.inBlock && isTable(target, state)) { state.inTable = true; }

        // handle directives (image, raw, contents...)
        if (isDirective(target, state)) { state.inBlock = true; }

        // handle source codes (literal blocks)
        if (isLiteral(target, state)) { state.inBlock = true; }

        // handle comments
        if (isComment(target, state)) { state.inBlock = true; }

        // handle line block
        handlelineBlock(line); //NOTE: line block does not effect upcoming lines

        // handle footnotes

        // a blank line will reset any blocks element we are in
        if (isEndBlock(target)) { reset(state); }

        // lines in block is not checked
        if (state.inBlock) { line.erase();  }
    }

    private void handlelineBlock(Line line) {
        if (line.charAt(0) == '|' && line.charAt(1) == ' ') {
            line.erase();
        } else if (line.startsWith(">>>")) {
            line.erase();
        }
    }

    private boolean isEndBlock(TargetLine target) {
        if (target.line.length() == 0 &&
                ((target.nextLine.charAt(0) != ' ' && target.nextLine.charAt(1) != ' ')
                        && target.nextLine.charAt(0) != '\t')) { // not continue indent in the next line
            return true;
        }
        return false;
    }

    private boolean isLiteral(TargetLine target, State state) {
        Line line = target.line;
        if (line.length() == 2 && (line.charAt(0) == ':' && line.charAt(1) == ':')
                && target.nextLine.length() == 0) {
            return true;
        }
        return false;
    }

    private boolean isComment(TargetLine target, State state) {
        Line line = target.line;

        // check if inline comment start?
        Matcher m = INLINE_COMMENT_PATTERN.matcher(target.line.getText());
        if (m.find()) {
            target.line.erase();
            return true;
        }

        // check if block comment start?
        if (line.length() == 2 && (line.charAt(0) == '.' && line.charAt(1) == '.')) {
            return true;
        }
        return false;
    }

    private boolean isDirective(TargetLine target, State state) {
        Matcher m = DIRECTIVE_PATTERN.matcher(target.line.getText());
        if (m.find()) {
            target.line.erase();
            return true;
        }
        return false;
    }

    private void reset(State state) {
        state.inList = false;
        state.inBlock = false;
        state.inTable = false;
        state.eraseDirective = false;
        state.type = "";
    }

    private boolean isTable(TargetLine target, State state) {
        Line line = target.line;
        if (state.inTable) {
            line.erase();
            return true;
        }

        if (isNormalTable(target, state)) return true;
        if (isCSVTable(target, state)) return true;
        return false;
    }

    private boolean isNormalTable(TargetLine target, State state) {
        Matcher m = NORMAL_TABLE_PATTERN.matcher(target.line.getText());
        if (m.find()) {
            target.line.erase();
            return true;
        }
        return false;
    }

    private boolean isCSVTable(TargetLine target, State state) {
        Matcher m = CSV_TABLE_PATTERN.matcher(target.line.getText());
        if (m.find()) {
            target.line.erase();
            return true;
        }
        return false;
    }


    // FIXME: current implmentation does not extract list level...
    private boolean isListElement(TargetLine line, State state) {
        if (isNormalList(line, state)) return true;
        if (isDigitList(line, state)) return true;
        if (isDefinitionList(line, state)) return true;
        return false;
    }

    private boolean isNormalList(TargetLine target, State state) {
        Line line = target.line;
        int spacePos = 0;
        while(' ' == line.charAt(spacePos)) { spacePos++; }
        if (line.charAt(spacePos) == '*') {
            line.setListLevel(1);
            line.setListStart(true);
            line.erase(0, spacePos+1);
            while (Character.isWhitespace(line.charAt(++spacePos))) {
                line.erase(spacePos, 1);
                spacePos++;
            }
            return true;
        }
        return false;
    }

    private boolean isDefinitionList(TargetLine target, State state) {
        Line line = target.line;
        Line nextLine = target.nextLine;

        // handling block tag
        if (
                (line.charAt(0) != ' ' && line.charAt(1) != ' ' && line.charAt(0) != '\t') && // not start from indents
                (line.charAt(line.length() - 1) != ':' && line.charAt(line.length() - 2) != ':') && // not source code
                        (nextLine.charAt(0) == ' ' && (nextLine.charAt(1) == ' ') || nextLine.charAt(0) == '\t') // have indentation in next line
                )
        {
            line.erase();
            return true;
        }

        // handling list contents
        if (state.inList && ((line.charAt(0) == ' ' && line.charAt(1) == ' ')  || (line.charAt(0) == '\t'))) {
            line.setListLevel(1);
            line.setListStart(true);
            int spacePos = 0;
            while (Character.isWhitespace(line.charAt(spacePos))) {
                line.erase(spacePos, 1);
                spacePos++;
            }
            return true;
        }
        return false;
    }

    private boolean isDigitList(TargetLine target, State state) {
        Line line = target.line;
        Matcher m = DIGIT_PATTERN.matcher(target.line.getText());
        if (m.find()) {
            line.setListLevel(1);
            line.setListStart(true);
            int dotPos = target.line.getText().indexOf(".");
            line.erase(0, ++dotPos);
            while (Character.isWhitespace(target.line.charAt(dotPos))) {
                line.erase(dotPos, 1);
                dotPos++;
            }
            return true;
        }
        return false;
    }

    private int extractSectionLevel(TargetLine target) {
        if (processMultiLineMatch('#', '#', target)) {
            return 1;
        } else if (processMultiLineMatch('*', '*', target)) {
            return 2;
        } else if (processMultiLineMatch('=', '=', target)) {
            return 3;
        } else if (processMultiLineMatch(null, '=', target)) {
            return 4;
        } else if (processMultiLineMatch('-', '-', target)) { // this is a subtitle?
            return 0;
        } else if (processMultiLineMatch(null, '-', target)) {
            return 5;
        } else if (processMultiLineMatch('~', '~', target)) {
            return 6;
        } else if (processMultiLineMatch(null, '~', target)) {
            return 7;
        } else if (processMultiLineMatch('^', '^', target)) {
            return 8;
        } else if (processMultiLineMatch(null, '^', target)) {
            return 9;
        }
        return -1;
    }

    /**
     * Erase all inline markup (bold, italics, special tokens etc)
     *
     * @param line
     */
    private void eraseInlineMarkup(Line line) {
        // inline markup (bold, italics etc)
        line.eraseEnclosure(":ref:`", "`", ReSTLine.EraseStyle.InlineMarkup); // inline cross section reference
        line.eraseEnclosure("`", "`:sup:", ReSTLine.EraseStyle.InlineMarkup); // superscript
        line.eraseEnclosure("`", "`sub:", ReSTLine.EraseStyle.InlineMarkup); // subscript
        line.eraseEnclosure("*", "*", ReSTLine.EraseStyle.InlineMarkup); // emphasis
        line.eraseEnclosure("**", "**", ReSTLine.EraseStyle.InlineMarkup); // strong emphasis
        line.eraseEnclosure("`", "`", ReSTLine.EraseStyle.InlineMarkup); // interpreted text
        line.eraseEnclosure("``", "``", ReSTLine.EraseStyle.InlineMarkup); // inline literal
        line.eraseEnclosure("`", "`_", ReSTLine.EraseStyle.InlineMarkup); // phrase reference
        line.eraseEnclosure("_`", "`", ReSTLine.EraseStyle.InlineMarkup); // inline literal target
        line.eraseEnclosure("[", "]_", ReSTLine.EraseStyle.InlineMarkup); // footnote reference
        line.eraseEnclosure("|", "|", ReSTLine.EraseStyle.InlineMarkup); // inline figure

        // FIXME: inline annotation with reference_ and anonymous__ not covered yet.
    }
}
