package cc.redpen.parser.latex;

import java.util.List;
import static org.junit.Assert.*;

public class Assert {
    public static void assertTokensLike(final List<Token> expected, final List<Token> actual) {
        assertEquals("token streams differ in size", expected.size(), actual.size());
        for (int i=0; i<expected.size(); ++i) {
            final Token p = expected.get(i);
            final Token q = actual.get(i);
            assertTrue(String.format("token streams do not look like at index %d: expected: <%s>, got: <%s>", i, p, q), isTokenLikeTo(p, q));
        }
    }

    public static void assertTokensEqual(final List<Token> expected, final List<Token> actual) {
        assertEquals("token streams differ in size", expected.size(), actual.size());
        for (int i=0; i<expected.size(); ++i) {
            final Token p = expected.get(i);
            final Token q = actual.get(i);
            assertTrue(String.format("token streams differ at index %d: expected: <%s>, got: <%s>", i, p, q), isTokenEqualTo(p, q));
        }
    }

    public static boolean isTokenLikeTo(final Token p, final Token q) {
        return (p.t.equals(q.t) && p.v.equals(q.v));
    }

    public static boolean isTokenEqualTo(final Token p, final Token q) {
        return String.valueOf(p).equals(String.valueOf(q));
    }
}
