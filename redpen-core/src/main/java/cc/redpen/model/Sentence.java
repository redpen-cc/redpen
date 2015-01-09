/**
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
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

import cc.redpen.parser.LineOffset;
import cc.redpen.tokenizer.TokenElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Sentence block in a Document.
 */
public final class Sentence implements Serializable {
    private static final long serialVersionUID = -3019057510527995111L;

    /**
     * Links (including internal and external ones).
     */
    private final List<String> links;

    /**
     * Sentence position in a file.
     */
    private int lineNum;
    /**
     * Content of string.
     */
    public String content;
    /**
     * Position which the sentence starts with.
     */
    public int startPositionOffset;
    /**
     * Flag for knowing if the sentence is the first sentence
     * of a block, such as paragraph, list, header.
     */
    public boolean isFirstSentence;
    /**
     * A list of tokens.
     *
     * Note: the contents of the tokens are added in DocumentCollectionBuilder
     */
    public List<TokenElement> tokens;

    /**
     * Combinations of line Number and the position offset
     */
    public List<LineOffset> offsetMap;

    /**
     * Constructor.
     *
     * @param sentenceContent  content of sentence
     * @param lineNum line number of sentence
     */
    public Sentence(String sentenceContent, int lineNum) {
        this(sentenceContent, lineNum, 0);
    }

    /**
     * Constructor.
     *
     * @param sentenceContent  content of sentence
     * @param sentencePosition sentence position
     * @param startOffset offset of the start position in the line
     */
    public Sentence(String sentenceContent, int sentencePosition, int startOffset) {
        super();
        this.content = sentenceContent;
        this.lineNum = sentencePosition;
        this.isFirstSentence = false;
        this.links = new ArrayList<>();
        this.tokens = new ArrayList<>();
        this.startPositionOffset = startOffset;
    }

    public int getLineNum() {
        return lineNum;
    }

    // TODO: remove this method for immutability
    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public List<String> getLinks() {
        return links;
    }

    public void addLink(String link) {
        this.links.add(link);
    }

    @Override
    public String toString() {
        return "Sentence{" +
                "links=" + links +
                ", lineNum=" + lineNum +
                ", content='" + content + '\'' +
                ", startPositionOffset=" + startPositionOffset +
                ", isFirstSentence=" + isFirstSentence +
                ", tokens=" + tokens +
                ", offsetMap=" + offsetMap +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sentence sentence = (Sentence) o;

        if (isFirstSentence != sentence.isFirstSentence) return false;
        if (lineNum != sentence.lineNum) return false;
        if (startPositionOffset != sentence.startPositionOffset) return false;
        if (content != null ? !content.equals(sentence.content) : sentence.content != null) return false;
        if (links != null ? !links.equals(sentence.links) : sentence.links != null) return false;
        if (tokens != null ? !tokens.equals(sentence.tokens) : sentence.tokens != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = links != null ? links.hashCode() : 0;
        result = 31 * result + lineNum;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + startPositionOffset;
        result = 31 * result + (isFirstSentence ? 1 : 0);
        result = 31 * result + (tokens != null ? tokens.hashCode() : 0);
        return result;
    }
}
