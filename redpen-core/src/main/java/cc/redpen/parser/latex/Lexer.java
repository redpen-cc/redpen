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
package cc.redpen.parser.latex;

import java.util.List;
import java.util.ArrayList;

/**
 * Experimental lexer for LaTeX.
 */
public class Lexer {
    private static final String SPECIALS = "[-=;:'\"<>,.?%!#^&()\\/{}[]$+| \r\n\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\u0008\u0009\u000b\u000c\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f]";
    private static final String WHITESPACE = " \r\n\t";

    private char[] mTarget;
    private String mMode = "TEXTILE";
    private List<Character> mRegister = new ArrayList<>();
    private Character mDelimiter = null;
    private Position mPos = new Position(1, -1);
    private Position mModeFrom = new Position(1, -1);

    private Lexer(final char[] target) {
        mTarget = target;
    }

    public static Lexer on(final char[] target) {
        return new Lexer(target);
    }

    public static Lexer on(final String target) {
        return new Lexer(target.toCharArray());
    }


    public List<Token> parse() {
        final List<Token> ret = new ArrayList<>();
        for (int i=0; i<mTarget.length; ++i) {
            ++mPos.col;
            doParse(ret, mTarget[i]);
        }
        flush(ret);
        return ret;
    }

    private void doParse(final List<Token> o, final char c) {
        switch (mMode) {
        case "TEXTILE":
            doParseTextile(o, c);
            break;
        case "FORMULA":
            doParseFormula(o, c);
            break;
        case "COMMENT":
            doParseComment(o, c);
            break;
        case "CONTROL":
            doParseControl(o, c);
            break;
        case "VERBATIM":
            doParseVerbatim(o, c);
            break;
        case "ESCAPING":
            doParseEscaping(o, c);
            break;
        }
    }

    private void doParseTextile(final List<Token> o, final char c) {
        switch (c) {
        case '%':
            mMode = "COMMENT";
            break;
        case '\\':
            flush(o);
            savePosition();
            mMode = "ESCAPING";
            break;
        case '{':
            flush(o);
            savePositionWithOffset(0, 1);
            synthesize(o, "GROUP1_BEGIN", c);
            break;
        case '}':
            flush(o);
            savePositionWithOffset(0, 1);
            synthesize(o, "GROUP1_END", c);
            break;
        case '[':
            flush(o);
            savePositionWithOffset(0, 1);
            synthesize(o, "GROUP2_BEGIN", c);
            break;
        case ']':
            flush(o);
            savePositionWithOffset(0, 1);
            synthesize(o, "GROUP2_END", c);
            break;
        case '$':
            mMode = "FORMULA";
            break;
        default:
            mRegister.add(c);
            if (c == '\n') {
                ++mPos.row;
                mPos.col = -1;
            }
        }
    }

    private void doParseFormula(final List<Token> o, final char c) {
        if (c == '$') {
            mMode = "TEXTILE";
        }
    }

    private void doParseComment(final List<Token> o, final char c) {
        if (c == '\n') {
            mMode = "TEXTILE";
        }
    }

    private void doParseControl(final List<Token> o, final char c) {
        if (SPECIALS.indexOf(c) < 0) {
            mRegister.add(c);
        } else {
            final String word = Flusher.on(mRegister).flush();
            if ("verb".equals(word)) {
                savePosition();
                mMode = "VERBATIM";
                mDelimiter = c;
            } else {
                if (word.length() > 0) {
                    o.add(new Token(mMode, word, mModeFrom));
                    savePosition();
                    mMode = "TEXTILE";
                    if (WHITESPACE.indexOf(c) < 0) {
                        doParse(o, c);
                    }
                }
            }
        }
    }

    private void doParseVerbatim(final List<Token> o, final char c) {
        if (c != mDelimiter) {
            mRegister.add(c);
        } else {
            flush(o);
            savePosition();
            mMode = "TEXTILE";
        }
    }

    private void doParseEscaping(final List<Token> o, final char c) {
        if (SPECIALS.indexOf(c) < 0) {
            flush(o);
            savePosition();
            mMode = "CONTROL";
            doParse(o, c);
        } else {
            mMode = "TEXTILE";
            mRegister.add(c);
            if (c == '\n') {
                ++mPos.row;
                mPos.col = -1;
            }
        }
    }

    private void synthesize(final List<Token> l, final String mode, final char on) {
        l.add(new Token(mode, on, mPos));
    }

    private void flush(final List<Token> l) {
        if (!mRegister.isEmpty()) {
            l.add(new Token(mMode, Flusher.on(mRegister).flush(), mModeFrom));
        }
    }

    private void savePosition() {
        savePositionWithOffset(0, 0);
    }

    private void savePositionWithOffset(final int rowOffset, final int colOffset) {
        mModeFrom = new Position(mPos);
        mModeFrom.row += rowOffset;
        mModeFrom.col += colOffset;
    }
}
