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
import cc.redpen.util.DictionaryLoader;
import cc.redpen.util.SpellingUtils;
import cc.redpen.validator.Validator;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Ensure that known (dictionary) words are not used too frequently within the document
 */
public class WordFrequencyValidator extends Validator {

    private static final String DEFAULT_RESOURCE_PATH = "default-resources/word-frequency";

    // reference set of word frequencies
    private Map<String, Double> referenceWordFrequencies = null;
    // how the reference words deviate from their average use
    private Map<String, Double> referenceWordDeviations = null;
    // the occurance of words in the document
    private Map<String, Integer> documentWordOccurances = new HashMap<>();
    // the number of words in the document
    private int wordCount = 0;
    // the minimum number of words in the document before this validator activates
    private int minWordCount = 200;
    // the standard deviation of the reference words
    private double referenceStdDeviation = 0;
    // the maximum deviation from the reference frequency permitted before a validation error is created
    private double deviationFactor = 3f;
    // one ugly side-effect of using forEach...
    private Sentence lastSentence;

    @Override
    protected void init() throws RedPenException {
        super.init();

        deviationFactor = getConfigAttributeAsDouble("deviation_factor", deviationFactor);
        minWordCount = getConfigAttributeAsInt("min_word_count", minWordCount);

        String defaultDictionaryFile = DEFAULT_RESOURCE_PATH + "/word-frequency-" + getSymbolTable().getLang() + ".dat";
        referenceWordDeviations = new HashMap<>();
        try {
            referenceWordFrequencies =
                    new DictionaryLoader<Map<String, Double>>(HashMap::new, (set, line) -> {
                        String[] fields = line.split(" ");
                        set.put(fields[1], Double.valueOf(fields[0]));
                    }).loadCachedFromResource(defaultDictionaryFile, "word frequencies");
            referenceStdDeviation = getDeviations(referenceWordFrequencies, referenceWordDeviations);

        } catch (Exception ignored) {
            referenceWordFrequencies = new HashMap<>();
        }
    }

    /**
     * Add the words in the sentence to the word frequency histogram
     *
     * @param sentence
     */
    private void processSentence(Sentence sentence) {
        for (TokenElement token : sentence.getTokens()) {
            String word = token.getSurface().toLowerCase();
            if (referenceWordDeviations.get(word) != null) {
                Integer occurances = documentWordOccurances.get(word);
                if (occurances == null) {
                    documentWordOccurances.put(word, 1);
                } else {
                    documentWordOccurances.put(word, occurances + 1);
                }
                wordCount++;
            } else if (SpellingUtils.getDictionary(getSymbolTable().getLang()).contains(word)) {
                wordCount++;
            }
        }
    }

    /**
     * Return the standard deviation and full the deviations map with root of each word's variance
     *
     * @param histogram  word frequency histogram
     * @param deviations a map that gets filled with each words variance
     * @return the standard deviation of the histogram
     */
    private double getDeviations(Map<String, Double> histogram, Map<String, Double> deviations) {
        double sum = 0;
        for (String word : histogram.keySet()) {
            sum += histogram.get(word);
        }
        double size = 100.0;
        double mean = sum / size;
        sum = 0;

        for (String word : histogram.keySet()) {
            double diff = histogram.get(word) - mean;
            sum += diff * diff;
        }
        double stddev = Math.sqrt(sum / size);

        for (String word : histogram.keySet()) {
            deviations.put(word, Math.abs(histogram.get(word) - mean));
        }
        return stddev;
    }

    @Override
    public void validate(Document document) {

        // process each sentence in the document
        lastSentence = null;
        for (int i = 0; i < document.size(); i++) {
            for (Paragraph para : document.getSection(i).getParagraphs()) {
                for (Sentence sentence : para.getSentences()) {
                    processSentence(sentence);
                    lastSentence = sentence;
                }
            }
        }

        // don't validate if the document is too short
        if (wordCount >= minWordCount) {
            Map<String, Double> documentWordFrequencies = new HashMap<>();

            documentWordOccurances.forEach((word, count) -> {
                documentWordFrequencies.put(word, 100.0 * (double) count / (double) wordCount);
            });

            DecimalFormat df = new DecimalFormat("0.00");

            Map<String, Double> documentDeviations = new HashMap<>();
            double stddev = getDeviations(documentWordFrequencies, documentDeviations);
            documentDeviations.forEach((word, deviation) -> {
                Double referenceDeviation = referenceWordDeviations.get(word);
                if (referenceDeviation != null) {
                    double devRatio = deviation / stddev;
                    double docPercentage = documentWordFrequencies.get(word);
                    double referencePercentage = referenceWordFrequencies.get(word);

                    // if the word deviates significantly from the norm and also from the reference percentage,
                    // then raise an error
                    if ((devRatio > 1) && (docPercentage > referencePercentage * deviationFactor)) {
                        addLocalizedError(
                                "WordUsedTooFrequently",
                                lastSentence,
                                word,
                                df.format(docPercentage),
                                df.format(referencePercentage));
                    }
                }
            });
        }
    }
}