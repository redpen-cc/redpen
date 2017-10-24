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
    private Map<Document, Map<String, List<CandidateTokenInfo>>> words = new HashMap<>();
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
                if (!this.words.get(document).containsKey(reading)) {
                    continue;
                }
                generateErrors(document, sentence, token, reading);
                this.words.get(document).remove(reading);
            }
        }
    }

    private void generateErrors(Document document, Sentence sentence, TokenElement token, String reading) {
        List<CandidateTokenInfo> tokens = this.words.get(document).get(reading);
        Map<String, List<CandidateTokenInfo>> candidateMap = new HashMap<>();
        for (CandidateTokenInfo candidate : tokens) {
            if (candidate.element != token && !token.getSurface().equals(candidate.element.getSurface())) {
                if (!candidateMap.containsKey(candidate.element.getSurface())) {
                    candidateMap.put(candidate.element.getSurface(), new LinkedList<>());
                }
                candidateMap.get(candidate.element.getSurface()).add(candidate);
            }
        }

        for (String surface : candidateMap.keySet()) {
            StringBuilder candidates = new StringBuilder();
            candidates.append(surface);
            List<CandidateTokenInfo> candidateTokenList = candidateMap.get(surface);
            candidates.append("(");
            candidates.append(candidateTokenList.get(0).element.getTags().get(0));
            candidates.append(")");
            if (document.getFileName().orElse("").length() > 0) {
                candidates.append(" in ");
                candidates.append(document.getFileName().orElse(""));
            }
            candidates.append(" at ");
            candidates.append(addTokenInfo(candidateTokenList));
            addLocalizedErrorFromToken(sentence, token, candidates.toString());
        }
    }

    private String addTokenInfo(List<CandidateTokenInfo> candidateTokenList) {
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
            if (!this.words.containsKey(document)) {
                this.words.put(document, new HashMap<>());
            }
            if (!this.words.get(document).containsKey(reading)) {
                this.words.get(document).put(reading, new LinkedList<>());
            }
            this.words.get(document).get(reading).add(new CandidateTokenInfo(token, sentence));
        }
    }

    private String getReading(TokenElement token) {
        String reading = token.getReading() != null ? token.getReading() : token.getSurface();
        return reading.toLowerCase();
    }

    private List<Sentence> extractSentences(Document document) {
        List<Sentence> sentences = new ArrayList<>();
        for (Section section : document) {
            for (Paragraph paragraph : section.getParagraphs()) {
                sentences.addAll(paragraph.getSentences());
            }
            sentences.addAll(section.getHeaderContents());
            for (ListBlock listBlock : section.getListBlocks()) {
                for (ListElement listElement : listBlock.getListElements()) {
                    sentences.addAll(listElement.getSentences());
                }
            }
        }
        return sentences;
    }

    @Override
    public List<String> getSupportedLanguages() {
        return singletonList(Locale.JAPANESE.getLanguage());
    }
}
