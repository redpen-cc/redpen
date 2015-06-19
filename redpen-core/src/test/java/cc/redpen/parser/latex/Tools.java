package cc.redpen.parser.latex;

import java.util.List;
import java.util.ArrayList;

import org.pegdown.ast.Node;
import org.pegdown.ast.SuperNode;
import org.pegdown.ast.HeaderNode;
import org.pegdown.ast.TextNode;
import org.apache.commons.lang3.StringUtils;

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

    public static String summary(final Node node) {
        final List<String> tokens = new ArrayList<>();
        tokens.add(typeCode(node));
        if (node instanceof TextNode) {
            tokens.add("'" + ((TextNode)node).getText() + "'");
        }
        if (node instanceof SuperNode) {
            final List<Node> children = ((SuperNode)node).getChildren();
            if (!children.isEmpty()) {
                for (Node n : children) {
                    tokens.add(summary(n));
                }
            } else {
                tokens.add("()");
            }
        }
        return "(" + StringUtils.join(tokens, " ") + ")";
    }

    private static String typeCode(final Node node) {
        final String type = node.toString().split(" ", 2)[0];
        if (! (node instanceof HeaderNode) ) {
            return type;
        } else {
            return String.format("%s#%d", type, ((HeaderNode)node).getLevel());
        }
    }
}
