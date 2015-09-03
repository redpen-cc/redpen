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
import cc.redpen.validator.Validator;

import java.util.HashMap;
import java.util.Map;

/**
 * Check that too many sentences don't start with the same words
 */
public class FrequentSentenceStartValidator extends Validator {

    private Map<String, Integer> sentenceStartHistogram = new HashMap<>(); // histogram of sentence starts
    private int leadingWordLimit = 3; // number of words starting each sentence to consider
    private int percentageThreshold = 25; // maximum percentage of sentences that can start with the same words
    private int minimumSentenceCount = 5; // must have at least this number of sentences

    @Override
    protected void init() throws RedPenException {
        super.init();
        leadingWordLimit = getConfigAttributeAsInt("leading_word_limit", leadingWordLimit);
        percentageThreshold = getConfigAttributeAsInt("percentage_threshold", percentageThreshold);
        minimumSentenceCount = getConfigAttributeAsInt("min_sentence_count", minimumSentenceCount);
    }

    /**
     * Add sequences of tokens, up to leadingWordLimit, in the histogram
     *
     * @param sentence
     */
    private void processSentence(Sentence sentence) {
        if (sentence.getTokens().size() > leadingWordLimit) {
            String leadingPhrase = "";
            for (int i = 0; i < leadingWordLimit; i++) {
                leadingPhrase += (leadingPhrase.isEmpty() ? "" : " ") + sentence.getTokens().get(i).getSurface();
                Integer count = sentenceStartHistogram.get(leadingPhrase);
                if (sentenceStartHistogram.get(leadingPhrase) == null) {
                    count = 0;
                }
                sentenceStartHistogram.put(leadingPhrase, count + 1);
            }
        }
    }

    @Override
    public void validate(Document document) {

        // remember the last sentence since we can't add an error without a sentence
        Sentence lastSentence = null;
        int sentenceCount = 0;
        for (int i = 0; i < document.size(); i++) {
            for (Paragraph para : document.getSection(i).getParagraphs()) {
                for (Sentence sentence : para.getSentences()) {
                    processSentence(sentence);
                    sentenceCount++;
                    lastSentence = sentence;
                }
            }
        }

        // make sure we have enough sentences to make this validation worthwhile
        if (sentenceCount >= minimumSentenceCount) {
            for (String start : sentenceStartHistogram.keySet()) {
                int count = sentenceStartHistogram.get(start);
                int percentage = (int) ((100.0 * (float) count / (float) sentenceStartHistogram.size()));
                if (percentage > percentageThreshold) {
                    addLocalizedError("SentenceStartTooFrequent", lastSentence, percentage, start);
                }
            }
        }
    }
}