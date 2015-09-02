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
import cc.redpen.util.DictionaryLoader;
import cc.redpen.validator.Validator;

import java.util.HashMap;
import java.util.Map;

/**
 * Ensure that known (dictionary) words are not used too frequently within the document
 */
public class WordFrequencyValidator extends Validator {

    private Map<String, Float> wordHistogram = null;
    private static final String DEFAULT_RESOURCE_PATH = "default-resources/word-frequency";
    private double frequencyFactor = 2f;

    @Override
    protected void init() throws RedPenException {
        super.init();

        frequencyFactor = getConfigAttributeAsDouble("frequency_factor", frequencyFactor);

        String defaultDictionaryFile = DEFAULT_RESOURCE_PATH + "/word-frequency-" + getSymbolTable().getLang() + ".dat";
        try {
            wordHistogram = new DictionaryLoader<Map<String, Float>>(
                    HashMap::new, (set, line) -> {
                String[] fields = line.split(" ");
                set.put(fields[1], Float.valueOf(fields[0]));
            }
            ).loadCachedFromResource(defaultDictionaryFile, "spell dictionary");
            System.err.println("Loaded " + wordHistogram.size() + " word frequencies");
        } catch (Exception ignored) {
            wordHistogram = new HashMap<>();
        }
    }

    private void processSentence(Sentence sentence) {
    }

    @Override
    public void validate(Document document) {

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

//        addValidationError("WordUsedTooFrequently", lastSentence, word, frequency, standardFrequency);
    }
}