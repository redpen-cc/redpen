/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.redpen.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

/**
 * This class wraps a buffered reader. It looks for preprocessor instructions in the input text and
 * converts this to a list of PreprocessorRules
 */
public class PreprocessingReader implements AutoCloseable {

    private BufferedReader reader;
    private Set<PreprocessorRule> preprocessorRules = new HashSet<>();
    private int lineNumber = 0;
    PreprocessorRule lastRule = null;

    public PreprocessingReader(Reader reader) {
        this.reader = new BufferedReader(reader);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    public String readLine() throws IOException {
        String line = reader.readLine();
        lineNumber++;
        if (line != null) {
            String ruleText = line
                .replaceAll("^#@#", "")
                .replaceAll("^%", "")
                .replaceAll("^ *\\[!--","")
                .replaceAll("^ *<!--","")
                .trim();
            if (ruleText.toLowerCase().startsWith("[suppress")) {
                addSuppressRule(ruleText);
                return "";
            }
        }
        return line;
    }

    private void addSuppressRule(String ruleString) {
        PreprocessorRule rule = new PreprocessorRule(PreprocessorRule.RuleType.SUPPRESS, lineNumber);
        if (lastRule != null) {
            lastRule.setLineNumberLimit(lineNumber);
        }
        lastRule = rule;
        String[] parts = ruleString.split("=");
        if (parts.length > 1) {
            String[] parameters = parts[1].replaceAll("[\"'\\]]", "").split(" ");
            for (String parameter : parameters) {
                if (!parameter.isEmpty()) {
                    rule.addParameter(parameter);
                }
            }
        }
        preprocessorRules.add(rule);
    }

    public Set<PreprocessorRule> getPreprocessorRules() {
        return preprocessorRules;
    }
}
