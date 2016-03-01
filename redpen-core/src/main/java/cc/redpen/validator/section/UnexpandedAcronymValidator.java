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
import cc.redpen.validator.sentence.SpellingDictionaryValidator;

import java.util.*;

import static java.util.Collections.singletonList;

/**
 * Ensure that there are candidates for expanded versions of acronyms. That is, if there exists an
 * acronym ABC then there must exist a sequence of capitalized words such as Axxx Bxx Cxxx.
 */
public class UnexpandedAcronymValidator extends SpellingDictionaryValidator {

    // a set of small words used to join acronyms, such as 'of', 'the' and 'for'
    private Set<String> acronymJoiningWords = new HashSet<>();
    // the set of acronyms we've deduced from sequences of capitalized words
    private Set<String> expandedAcronyms = new HashSet<>();
    // the set of acronyms we found literally within the document
    private Set<String> contractedAcronyms = new HashSet<>();

    public UnexpandedAcronymValidator() {
        setDefaultAttributes("min_acronym_length", 3); // ignore uppercase words smaller than this length
    }

    @Override public List<String> getSupportedLanguages() {
        return singletonList(Locale.ENGLISH.getLanguage());
    }

    @Override
    protected void init() throws RedPenException {
        super.init();
        acronymJoiningWords.add("of");
        acronymJoiningWords.add("the");
        acronymJoiningWords.add("for");
        acronymJoiningWords.add("in");
        acronymJoiningWords.add("and");
        acronymJoiningWords.add("&");
    }

    private void processSentence(Sentence sentence) {
        List<String> sequence = new ArrayList<>();
        for (TokenElement token : sentence.getTokens()) {
            String word = token.getSurface();
            if (!word.trim().isEmpty()) {
                int minAcronymLength = getIntAttribute("min_acronym_length");
                if (isAllCapitals(word)) {
                    if ((word.length() >= minAcronymLength) && !inDictionary(word.toLowerCase())) {
                        contractedAcronyms.add(word);
                    }
                } else if (isCapitalized(word)) {
                    sequence.add(word);
                } else if (!acronymJoiningWords.contains(word) && !sequence.isEmpty()) {
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

    /**
     * Return true of the supplied word is all in capitals
     */
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

    /**
     * Return true if the word only starts with a capital letter
     */
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

        // process all sentences and remember the last sentence
        Sentence lastSentence = null;
        for (int i = 0; i < document.size(); i++) {
            for (Paragraph para : document.getSection(i).getParagraphs()) {
                for (Sentence sentence : para.getSentences()) {
                    processSentence(sentence);
                    lastSentence = sentence;
                }
            }
        }

        // if the contracted acronyms aren't in the expanded acronyms, generate an error
        for (String acronym : contractedAcronyms) {
            if (!expandedAcronyms.contains(acronym)) {
                addLocalizedError("UnexpandedAcronym", lastSentence, acronym);
            }
        }
    }
}