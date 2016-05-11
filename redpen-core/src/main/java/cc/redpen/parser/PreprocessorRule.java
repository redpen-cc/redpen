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

import cc.redpen.model.Document;
import cc.redpen.model.ListBlock;
import cc.redpen.model.ListElement;
import cc.redpen.model.Paragraph;
import cc.redpen.model.Section;
import cc.redpen.model.Sentence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An preprocessor 'rule'. Supported rule types are:
 *
 * SUPPRESS: Suppress the output of validation errors for a section, block or paragraph
 */
public class PreprocessorRule {

    public enum RuleType {
        SUPPRESS
    }

    private int lineNumber = 0;
    private List<String> parameters = new ArrayList<>();
    private RuleType ruleType;

    public PreprocessorRule(RuleType ruleType, int lineNumber) {
        this.ruleType = ruleType;
        this.lineNumber = lineNumber + 1; // applies the to line after the line the rule was detected
    }

    public RuleType getRuleType() {
        return ruleType;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void addParameter(String parameter) {
        parameters.add(parameter.toLowerCase());
    }

    /**
     * Return true if the rule is triggered by the given line and name given the structure
     * of the supplied document
     *
     * @param document
     * @param otherLineNumber
     * @param name
     * @return
     */
    public boolean isTriggeredBy(Document document, int otherLineNumber, String name) {
        if (parameters.isEmpty() || parameters.contains(name.toLowerCase())) {
            // find out if the rule line number is in the same section as the other line number
            for (Section section : document) {
                List<Sentence> allBlockSentences = new ArrayList<>();
                allBlockSentences.addAll(section.getHeaderContents());
                if (isInsideSentences(section.getHeaderContents(), lineNumber, otherLineNumber)) {
                    return true;
                }
                for (Paragraph paragraph : section.getParagraphs()) {
                    allBlockSentences.addAll(paragraph.getSentences());
                    if (isInsideSentences(paragraph.getSentences(), lineNumber, otherLineNumber)) {
                        return true;
                    }
                }
                for (ListBlock listBlock : section.getListBlocks()) {
                    for (ListElement element : listBlock.getListElements()) {
                        allBlockSentences.addAll(element.getSentences());
                        if (isInsideSentences(element.getSentences(), lineNumber, otherLineNumber)) {
                            return true;
                        }
                    }
                }
                if (isInsideSentences(allBlockSentences, lineNumber, otherLineNumber)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInsideSentences(Collection<Sentence> sentences, int ruleLineNumber, int errorLineNumber) {
        if (ruleLineNumber <= errorLineNumber) {
            int startLine = Integer.MAX_VALUE;
            int endLine = 0;
            for (Sentence sentence : sentences) {
                startLine = Math.min(startLine, sentence.getLineNumber());
                endLine = Math.max(endLine, sentence.getLineNumber());
            }

            // if we are in this paragraph, and the other line number is
            if ((startLine <= ruleLineNumber) && (endLine >= ruleLineNumber) &&
                (startLine <= errorLineNumber) && (endLine >= errorLineNumber)
                ) {
                return true;
            }
        }
        return false;
    }
}
