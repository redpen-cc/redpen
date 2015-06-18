package cc.redpen.parser.latex;

import java.util.List;

public class Tools {
    public static Token token(final String type, final String value) {
        return new Token(type, value, new Position(0,0));
    }

    public static Token token(final String type, final String value, final Position pos) {
        return new Token(type, value, pos);
    }

    public static Token token(final String type, final String value, final List<Token> params) {
        final Token t = new Token(type, value, new Position(0,0));
        t.p = params;
        return t;
    }
}
