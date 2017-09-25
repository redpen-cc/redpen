package cc.redpen.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RuleExtractorTest  {

    @Test
    void testSplit() throws Exception {
        String[] segments = RuleExtractor.split("This:n + is:v");
        assertEquals(2, segments.length);
        assertEquals("This:n", segments[0]);
        assertEquals("is:v", segments[1]);
    }

    @Test
    void testSplitWithoutSpaces() throws Exception {
        String[] segments = RuleExtractor.split("This:n+is:v");
        assertEquals(2, segments.length);
        assertEquals("This:n", segments[0]);
        assertEquals("is:v", segments[1]);
    }

    @Test
    void testSplitWithoutBeforeSpaces() throws Exception {
        String[] segments = RuleExtractor.split("This:n +is:v");
        assertEquals(2, segments.length);
        assertEquals("This:n", segments[0]);
        assertEquals("is:v", segments[1]);
    }
}
