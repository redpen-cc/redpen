package cc.redpen.parser.rest;

import cc.redpen.parser.common.LineParser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MultiLineProcessUtilsTest {
    @Test
    public void testDetectThreeLineHeader() {
        LineParser.TargetLine target = new LineParser.TargetLine(
                new ReSTLine("This is a part", 1),
                new ReSTLine("#############", 0),
                new ReSTLine("#############", 2));
        assertEquals(true, MultiLineProcessUtils.processMultiLineMatch('#', '#', target));
    }

    @Test
    public void testDetectTwoLineHeader() {
        LineParser.TargetLine target = new LineParser.TargetLine(
                new ReSTLine("This is a part", 1),
                new ReSTLine("", 0),
                new ReSTLine("#############", 2));
        assertEquals(true, MultiLineProcessUtils.processMultiLineMatch(null, '#', target));
    }
}
