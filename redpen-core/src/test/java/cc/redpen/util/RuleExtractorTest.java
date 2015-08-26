package cc.redpen.util;

import junit.framework.TestCase;

public class RuleExtractorTest extends TestCase {

    public void testSplit() throws Exception {
        String[] segments = RuleExtractor.split("This:n + is:v");
        assertEquals(2, segments.length);
        assertEquals("This:n", segments[0]);
        assertEquals("is:v", segments[1]);
    }

    public void testSplitWithoutSpaces() throws Exception {
        String[] segments = RuleExtractor.split("This:n+is:v");
        assertEquals(2, segments.length);
        assertEquals("This:n", segments[0]);
        assertEquals("is:v", segments[1]);
    }

    public void testSplitWithoutBeforeSpaces() throws Exception {
        String[] segments = RuleExtractor.split("This:n +is:v");
        assertEquals(2, segments.length);
        assertEquals("This:n", segments[0]);
        assertEquals("is:v", segments[1]);
    }
}
