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

import cc.redpen.model.*;
import cc.redpen.tokenizer.TokenElement;
import cc.redpen.validator.Validator;

import java.util.*;

import static java.util.Collections.singletonList;

public class JapaneseExpressionVariationValidator extends Validator {
    private Map<Document, Map<String, List<CandidateTokenInfo>>> readingMap = new HashMap<>();
    private Map<Document, List<Sentence>> sentenceMap = new HashMap<>();

    class CandidateTokenInfo {
        public CandidateTokenInfo(TokenElement element, Sentence sentence) {
            this.element = element;
            this.sentence = sentence;
        }
        public TokenElement element;
        public Sentence sentence;
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
        Map<String, List<CandidateTokenInfo>> variationMap = generateVariationMap(document, targetToken, reading);

        for (String surface : variationMap.keySet()) {
            List<CandidateTokenInfo> variationList = variationMap.get(surface);
            String variation = generateErrorMessage(variationList, surface);
            String positionList = addVariationPositions(variationList);
            addLocalizedErrorFromToken(sentence, targetToken, variation, positionList);
        }
    }

    private String generateErrorMessage(List<CandidateTokenInfo> variationList, String surface) {
        StringBuilder variation = new StringBuilder();
        variation.append(surface);
        variation.append("(");
        variation.append(variationList.get(0).element.getTags().get(0));
        variation.append(")");
        return variation.toString();
    }

    private Map<String, List<CandidateTokenInfo>> generateVariationMap(Document document, TokenElement targetToken, String reading) {
        List<CandidateTokenInfo> tokens = this.readingMap.get(document).get(reading);
        Map<String, List<CandidateTokenInfo>> variationMap = new HashMap<>();
        for (CandidateTokenInfo candidate : tokens) {
            if (candidate.element != targetToken && !targetToken.getSurface().equals(candidate.element.getSurface())) {
                if (!variationMap.containsKey(candidate.element.getSurface())) {
                    variationMap.put(candidate.element.getSurface(), new LinkedList<>());
                }
                variationMap.get(candidate.element.getSurface()).add(candidate);
            }
        }
        return variationMap;
    }

    private String addVariationPositions(List<CandidateTokenInfo> candidateTokenList) {
        StringBuilder builder = new StringBuilder();
        for (CandidateTokenInfo candidateToken : candidateTokenList) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(getTokenString(candidateToken));
        }
        return builder.toString();
    }

    private String getTokenString(CandidateTokenInfo token) {
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
            this.readingMap.get(document).get(reading).add(new CandidateTokenInfo(token, sentence));
        }
    }

    private String getReading(TokenElement token) {
        String reading = token.getReading() != null ? token.getReading() : token.getSurface();
        return reading.toLowerCase();
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
    public List<String> getSupportedLanguages() {
        return singletonList(Locale.JAPANESE.getLanguage());
    }
}
