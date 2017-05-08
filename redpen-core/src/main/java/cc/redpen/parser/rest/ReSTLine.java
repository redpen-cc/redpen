package cc.redpen.parser.rest;

import cc.redpen.parser.common.Line;

public class ReSTLine extends Line {
    /**
     * Construct a line using the supplied string
     *
     * @param str    the text of the line
     * @param lineno the original line number
     */
    public ReSTLine(String str, int lineno) {
        super(str, lineno);
        this.lineNo = lineno;
        this.inlineMarkupDelimiters = " _*`#^~.,";
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
                characters.add(ch);
                valid.add(true);

                if ((lastCh != 0) && (lastCh != ch)) {
                    allSameCharacter = false;
                }
                lastCh = ch;
            }
        }

        // trim the end
        while (!characters.isEmpty() &&
                Character.isWhitespace(characters.get(characters.size() - 1))) {
            characters.remove(characters.size() - 1);
        }
    }
}
