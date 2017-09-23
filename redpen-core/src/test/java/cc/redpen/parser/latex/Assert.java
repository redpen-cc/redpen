package cc.redpen.parser.latex;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
public class Assert {
    public static void assertTokensLike(final List<Token> expected, final List<Token> actual) {
        assertEquals(expected.size(), actual.size(), "token streams differ in size");
        for (int i=0; i<expected.size(); ++i) {
            final Token p = expected.get(i);
            final Token q = actual.get(i);
            assertTrue(isTokenLikeTo(p, q), String.format("token streams do not look like at index %d: expected: <%s>, got: <%s>", i, p, q));
        }
    }

    public static void assertTokensEqual(final List<Token> expected, final List<Token> actual) {
        assertEquals(expected.size(), actual.size(),"token streams differ in size");
        for (int i=0; i<expected.size(); ++i) {
            final Token p = expected.get(i);
            final Token q = actual.get(i);
            assertTrue(isTokenEqualTo(p, q), String.format("token streams differ at index %d: expected: <%s>, got: <%s>", i, p, q));
        }
    }

    public static boolean isTokenLikeTo(final Token p, final Token q) {
        return (p.t.equals(q.t) && p.v.equals(q.v));
    }

    public static boolean isTokenEqualTo(final Token p, final Token q) {
        return String.valueOf(p).equals(String.valueOf(q));
    }
}
