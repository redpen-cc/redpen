package cc.redpen.tokenizer;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TokenElementTest {
    @Test(expected = UnsupportedOperationException.class)
    public void testTagsImmutable() {
        TokenElement token = new TokenElement("foobar", "tag");
        List<String> tags = token.getTags();
        tags.add("baz");
    }

    @Test
    public void testSurfaceImmutable() {
        TokenElement token = new TokenElement("foobar", "tag");
        String surface = token.getSurface();
        surface = "baz";
        assertEquals("foobar", token.getSurface());
    }
}
