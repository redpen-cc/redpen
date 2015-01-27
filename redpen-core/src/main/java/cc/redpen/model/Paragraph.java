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
package cc.redpen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent a paragraph of text.
 */
public final class Paragraph {
    private final List<Sentence> sentences;

    /**
     * Constructor.
     */
    public Paragraph() {
        super();
        sentences = new ArrayList<>();
    }

    /**
     * Get the iterator of sentences.
     *
     * @return sentences in the paragraph
     */
    public List<Sentence> getSentences() {
        return sentences;
    }

    /**
     * Get the specified sentence.
     *
     * @param sentenceNumber sentence number
     * @return sentence with specified id
     */
    public Sentence getSentence(int sentenceNumber) {
        return sentences.get(sentenceNumber);
    }

    /**
     * Append a sentence to a paragraph.
     *
     * @param content sentence
     * @param lineNum line number of sentence
     */
    public void appendSentence(String content, int lineNum) {
        sentences.add(new Sentence(content, lineNum));
    }

    /**
     * Append a sentence.
     *
     * @param sentence a sentence to be added to paragraph
     */
    public void appendSentence(Sentence sentence) {
        sentences.add(sentence);
    }

    /**
     * Get the number of sentences in the paragraph.
     *
     * @return sentences number
     */
    public int getNumberOfSentences() {
        return sentences.size();
    }
}
