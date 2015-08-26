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
package cc.redpen.validator.section;

import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.model.Paragraph;
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.TokenElement;
import cc.redpen.validator.Validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Ensure that there are candidates for expanded versions of acronyms. That is, if there exists an
 * acronym ABC then there must exist a sequence of capitalized words such as Axxx Bxx Cxxx.
 */
public class UnexpandedAcronymValidator extends Validator {

    private static final int MIN_ACRONYM_LENGTH_DEFAULT = 3; // TLA

    private int minAcronymLength = MIN_ACRONYM_LENGTH_DEFAULT;
    private Set<String> smallWords = new HashSet<>();
    private Set<String> expandedAcronyms = new HashSet<>();
    private Set<String> contractedAcronyms = new HashSet<>();

    @Override
    protected void init() throws RedPenException {
        super.init();
        minAcronymLength = getConfigAttributeAsInt("min_acronym_length", MIN_ACRONYM_LENGTH_DEFAULT);
        smallWords.add("of");
        smallWords.add("the");
        smallWords.add("for");
        smallWords.add("in");
    }

    private void processSentence(Sentence sentence) {
        List<String> sequence = new ArrayList<>();
        for (TokenElement token : sentence.getTokens()) {
            String word = token.getSurface();
            if (!word.trim().isEmpty()) {
                if (isAllCapitals(word)) {
                    if (word.length() >= minAcronymLength) {
                        contractedAcronyms.add(word);
                    }
                } else if (isCapitalized(word)) {
                    sequence.add(word);
                } else if (!smallWords.contains(word) && !sequence.isEmpty()) {
                    String acronym = "";
                    for (String s : sequence) {
                        acronym += s.charAt(0);
                    }
                    if (acronym.length() >= minAcronymLength) {
                        expandedAcronyms.add(acronym);
                        if (acronym.length() >= minAcronymLength + 1) {
                            expandedAcronyms.add(acronym.substring(1));
                        }
                    }
                    sequence.clear();
                }
            }
        }
    }

    private boolean isAllCapitals(String word) {
        if (word.length() > 1) {
            for (int i = 0; i < word.length(); i++) {
                if (!Character.isAlphabetic(word.charAt(i)) || Character.isLowerCase(word.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean isCapitalized(String word) {
        if (!word.isEmpty()) {
            if (Character.isAlphabetic(word.charAt(0)) && Character.isUpperCase(word.charAt(0))) {
                if (word.length() == 1) {
                    return true;
                } else {
                    int lowerCaseCount = 0;
                    for (int i = 1; i < word.length(); i++) {
                        if (Character.isAlphabetic(word.charAt(i)) && Character.isLowerCase(word.charAt(i))) {
                            lowerCaseCount++;
                        }
                    }
                    return lowerCaseCount > 0;
                }
            }
        }
        return false;
    }

    @Override
    public void validate(Document document) {

        Sentence lastSentence = null;
        for (int i = 0; i < document.size(); i++) {
            for (Paragraph para : document.getSection(i).getParagraphs()) {
                for (Sentence sentence : para.getSentences()) {
                    processSentence(sentence);
                    lastSentence = sentence;
                }
            }
        }

        // TODO: permit errors to be added that are not bound to a sentence
        for (String acronym : contractedAcronyms) {
            if (!expandedAcronyms.contains(acronym)) {
                addValidationError("UnexpandedAcronym", lastSentence, acronym);
            }
        }
    }
}