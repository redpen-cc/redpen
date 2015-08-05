package cc.redpen.util;

import cc.redpen.tokenizer.TokenElement;
import cc.redpen.validator.ExpressionRule;

import java.util.Arrays;

public class RuleExtractor {
    /**
     * Create a rule from input sentence.
     *
     * @param line input rule
     * @return Rule for matching
     */
    public static ExpressionRule run(String line) {
        String[] expressionSegments = split(line);
        ExpressionRule rule = new ExpressionRule();
        for (String segment : expressionSegments) {
            String[] wordSegments = segment.split(":");
            String surface = wordSegments[0];
            String tagStr = "";
            if (wordSegments.length > 1) {
                tagStr = wordSegments[1];
            }
            rule.addElement(new TokenElement(surface, Arrays.asList(tagStr.split(","))));
        }
        return rule;
    }

    static String[] split(String line) {
        String[] segments = line.split(" *[+] *");
        return segments;
    }
}
