package cc.redpen.parser.rest;

import cc.redpen.parser.common.LineParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MultiLineProcessUtilsTest {
    @Test
    void testDetectThreeLineHeader() {
        LineParser.TargetLine target = new LineParser.TargetLine(
                new ReSTLine("This is a part", 1),
                new ReSTLine("#############", 0),
                new ReSTLine("#############", 2));
        assertEquals(true, MultiLineProcessUtils.processMultiLineMatch('#', '#', target));
    }

    @Test
    void testDetectTwoLineHeader() {
        LineParser.TargetLine target = new LineParser.TargetLine(
                new ReSTLine("This is a part", 1),
                new ReSTLine("", 0),
                new ReSTLine("#############", 2));
        assertEquals(true, MultiLineProcessUtils.processMultiLineMatch(null, '#', target));
    }
}
