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
    private Map<String, List<TokenElement>> words = new HashMap<>();
    private Map<Document, List<Sentence>> sentenceMap = new HashMap<>();

    @Override
    public void validate(Document document) {
        if (!sentenceMap.containsKey(document)) {
            throw new IllegalStateException("Document " + document.getFileName() + " does not have any sentence");
        }
        for (Sentence sentence : sentenceMap.get(document)) {
            for (TokenElement token : sentence.getTokens()) {
                String reading = getReading(token);
                if (!this.words.containsKey(reading)) {
                    continue;
                }

                String candidates = generateCandidates(token, reading);
                if (candidates.length() > 0) {
                    addLocalizedErrorFromToken(sentence, token, candidates);
                }
                this.words.remove(reading);
            }
        }
    }

    private String generateCandidates(TokenElement token, String reading) {
        List<TokenElement> tokens = this.words.get(reading);
        StringBuilder stringBuilder = new StringBuilder();
        for (TokenElement candidate : tokens) {
            if (candidate != token && !token.getSurface().equals(candidate.getSurface())) {
                String candidateStr = getTokenString(candidate);
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(candidateStr);
            }
        }
        return stringBuilder.toString();
    }

    private String getTokenString(TokenElement token) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(token.getSurface());
        return stringBuilder.toString();
    }


    @Override
    public void preValidate(Document document) {
        sentenceMap.put(document, extractSentences(document));
        List<Sentence> sentences = sentenceMap.get(document);
        for (Sentence sentence : sentences) {
            for (TokenElement token : sentence.getTokens()) {
                if (token.getSurface().equals(" ")) {
                    continue;
                }
                String reading = getReading(token);
                if (!this.words.containsKey(reading)) {
                    this.words.put(reading, new LinkedList<TokenElement>());
                }
                this.words.get(reading).add(token);
            }
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
