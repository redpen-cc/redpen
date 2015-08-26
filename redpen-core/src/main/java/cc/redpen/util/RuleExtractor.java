/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
            rule.addElement(new TokenElement(surface, Arrays.asList(tagStr.split(",")), 0));
        }
        return rule;
    }

    static String[] split(String line) {
        String[] segments = line.split(" *[+] *");
        return segments;
    }
}
