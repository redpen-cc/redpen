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
package cc.redpen.validator.document;

import cc.redpen.RedPenException;
import cc.redpen.model.*;
import cc.redpen.tokenizer.TokenElement;
import cc.redpen.validator.KeyValueDictionaryValidator;

import java.util.*;

import static java.util.Collections.singletonList;

public class JapaneseExpressionVariationValidator extends KeyValueDictionaryValidator {
    private Map<Document, Map<String, List<TokenInfo>>> readingMap;
    private Map<Document, List<Sentence>> sentenceMap;

    class TokenInfo {
        public TokenInfo(TokenElement element, Sentence sentence) {
            this.element = element;
            this.sentence = sentence;
        }
        public TokenElement element;
        public Sentence sentence;
    }

    public JapaneseExpressionVariationValidator() {
        super("japanese-spelling-variation/spelling-variation");
    }

    @Override
    public void validate(Document document) {
        if (!sentenceMap.containsKey(document)) {
            throw new IllegalStateException("Document " + document.getFileName() + " does not have any sentence");
        }
        for (Sentence sentence : sentenceMap.get(document)) {
            for (TokenElement token : sentence.getTokens()) {
                String reading = getReading(token);
                if (!this.readingMap.get(document).containsKey(reading)) {
                    continue;
                }
                generateErrors(document, sentence, token, reading);
                this.readingMap.get(document).remove(reading);
            }
        }
    }

    private void generateErrors(Document document, Sentence sentence, TokenElement targetToken, String reading) {
        Map<String, List<TokenInfo>> variationMap = generateVariationMap(document, targetToken, reading);
        for (String surface : variationMap.keySet()) {
            List<TokenInfo> variationList = variationMap.get(surface);
            String variation = generateErrorMessage(variationList, surface);
            String positionList = addVariationPositions(variationList);
            addLocalizedErrorFromToken(sentence, targetToken, variation, positionList);
        }
    }

    private String generateErrorMessage(List<TokenInfo> variationList, String surface) {
        StringBuilder variation = new StringBuilder();
        variation.append(surface);
        variation.append("(");
        variation.append(variationList.get(0).element.getTags().get(0));
        variation.append(")");
        return variation.toString();
    }

    private Map<String, List<TokenInfo>> generateVariationMap(Document document, TokenElement targetToken, String reading) {
        List<TokenInfo> tokens = this.readingMap.get(document).get(reading);
        Map<String, List<TokenInfo>> variationMap = new HashMap<>();
        for (TokenInfo variation : tokens) {
            if (variation.element != targetToken && !targetToken.getSurface().equals(variation.element.getSurface())) {
                if (!variationMap.containsKey(variation.element.getSurface())) {
                    variationMap.put(variation.element.getSurface(), new LinkedList<>());
                }
                variationMap.get(variation.element.getSurface()).add(variation);
            }
        }
        return variationMap;
    }

    private String addVariationPositions(List<TokenInfo> tokenList) {
        StringBuilder builder = new StringBuilder();
        for (TokenInfo variation : tokenList) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(getTokenString(variation));
        }
        return builder.toString();
    }

    private String getTokenString(TokenInfo token) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        stringBuilder.append(token.sentence.getLineNumber());
        stringBuilder.append(",");
        stringBuilder.append(token.element.getOffset());
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    @Override
    public void preValidate(Document document) {
        sentenceMap.put(document, extractSentences(document));
        List<Sentence> sentences = sentenceMap.get(document);
        for (Sentence sentence : sentences) {
            extractTokensFromSentence(document, sentence);
        }
    }

    private void extractTokensFromSentence(Document document, Sentence sentence) {
        for (TokenElement token : sentence.getTokens()) {
            if (token.getSurface().equals(" ")) {
                continue;
            }
            String reading = getReading(token);
            if (!this.readingMap.containsKey(document)) {
                this.readingMap.put(document, new HashMap<>());
            }
            if (!this.readingMap.get(document).containsKey(reading)) {
                this.readingMap.get(document).put(reading, new LinkedList<>());
            }
            this.readingMap.get(document).get(reading).add(new TokenInfo(token, sentence));
        }
    }

    private String getReading(TokenElement token) {
        String surface = token.getSurface().toLowerCase();
        if (inDictionary(surface)) {
            return getValue(surface);
        }
        String reading = token.getReading() != null ? token.getReading() : surface;
        return reading;
    }

    private List<Sentence> extractSentences(Document document) {
        List<Sentence> sentences = new ArrayList<>();
        for (Section section : document) {
            sentences.addAll(extractSentencesFromSection(section));
        }
        return sentences;
    }

    private List<Sentence> extractSentencesFromSection(Section section) {
        List<Sentence> sentencesInSection = new ArrayList<>();
        for (Paragraph paragraph : section.getParagraphs()) {
            sentencesInSection.addAll(paragraph.getSentences());
        }
        sentencesInSection.addAll(section.getHeaderContents());
        for (ListBlock listBlock : section.getListBlocks()) {
            for (ListElement listElement : listBlock.getListElements()) {
                sentencesInSection.addAll(listElement.getSentences());
            }
        }
        return sentencesInSection;
    }

    @Override
    protected void init() throws RedPenException {
        super.init();
        this.readingMap = new HashMap<>();
        this.sentenceMap = new HashMap<>();
    }

    @Override
    public List<String> getSupportedLanguages() {
        return singletonList(Locale.JAPANESE.getLanguage());
    }
}
