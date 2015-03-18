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

import cc.redpen.parser.LineOffset;
import cc.redpen.tokenizer.TokenElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Sentence block in a Document.
 */
public final class Sentence implements Serializable {
    private static final long serialVersionUID = 3761982769692999924L;
    /**
     * Links (including internal and external ones).
     */
    private final List<String> links;
    /**
     * Sentence position in a file.
     */
    private int lineNumber;
    /**
     * Content of string.
     */
    private String content;
    /**
     * Position which the sentence starts with.
     */
    private int startPositionOffset;
    /**
     * Flag for knowing if the sentence is the first sentence
     * of a block, such as paragraph, list, header.
     */
    private boolean isFirstSentence;
    /**
     * A list of tokens.
     * <p/>
     * Note: the contents of the tokens are added in DocumentCollectionBuilder
     */
    private List<TokenElement> tokens;
    /**
     * Combinations of line Number and the position offset
     */
    private List<LineOffset> offsetMap;

    /**
     * Constructor.
     *
     * @param sentenceContent content of sentence
     * @param lineNum         line number of sentence
     */
    public Sentence(String sentenceContent, int lineNum) {
        this(sentenceContent, lineNum, 0);
    }

    /**
     * Constructor.
     *
     * @param sentenceContent  content of sentence
     * @param sentencePosition sentence position
     * @param startOffset      offset of the start position in the line
     */
    public Sentence(String sentenceContent, int sentencePosition, int startOffset) {
        super();
        this.content = sentenceContent;
        this.lineNumber = sentencePosition;
        this.isFirstSentence = false;
        this.links = new ArrayList<>();
        this.tokens = new ArrayList<>();
        this.startPositionOffset = startOffset;
        this.offsetMap = new ArrayList<>();
    }


    public Sentence(String content, List<LineOffset> offsetMap, List<String> links) {
        this.content = content;
        this.offsetMap = offsetMap;
        this.startPositionOffset = offsetMap.get(0).offset;
        this.lineNumber = offsetMap.get(0).lineNum;
        this.isFirstSentence = false;
        this.tokens = new ArrayList<>();
        this.links = links;
    }

    /**
     * Get line number where the sentence starts.
     *
     * @return line number
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Set line number where the sentence starts.
     *
     * @param lineNumber line number
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Return links the sentence contains.
     *
     * @return a set of links
     */
    public List<String> getLinks() {
        return links;
    }

    /**
     * Add a link to Sentence
     *
     * @param link link url
     */
    public void addLink(String link) {
        this.links.add(link);
    }

    /**
     * Get content of sentence.
     *
     * @return sentence
     */
    public String getContent() {
        return content;
    }

    /**
     * Set content of sentence.
     *
     * @param content sentence
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Get start column offset where the sentence starts.
     *
     * @return column offset of start sentence
     */
    public int getStartPositionOffset() {
        return startPositionOffset;
    }


    /**
     * Detect the sentence is the first sentence of a paragraph.
     *
     * @return true when the first sentence of a paragraph. false otherwise.
     */
    public boolean isFirstSentence() {
        return isFirstSentence;
    }

    /**
     * Set a flag to detect if the sentence is a first sentence of a paragraph.
     *
     * @param isFirstSentence a flag to detect if the sentence exists in the begging of a paragraph
     */
    public void setIsFirstSentence(boolean isFirstSentence) {
        this.isFirstSentence = isFirstSentence;
    }

    /**
     * Get a set of tokenized words in the sentence.
     *
     * @return list of tokenized words
     */
    public List<TokenElement> getTokens() {
        return tokens;
    }

    /**
     * Set a set of tokenized words.
     *
     * @param tokens tokenized words
     */
    public void setTokens(List<TokenElement> tokens) {
        this.tokens = tokens;
    }

    /**
     * Set the offset mapping table which contains character position to column offset in line.
     *
     * @param offsetMap position mapping table
     */
    public void setOffsetMap(List<LineOffset> offsetMap) {
        this.offsetMap = offsetMap;
    }


    /**
     * Get offset position for specified character position.
     *
     * @param position character position in a sentence
     * @return offset position
     */
    public Optional<LineOffset> getOffset(int position) {
        if (position >= 0) {
            if (offsetMap.size() > position) {
                return Optional.of(offsetMap.get(position));
            } else if ((position > 0) && (offsetMap.size() == position)) {
                LineOffset prev = offsetMap.get(position - 1);
                return Optional.of(new LineOffset(prev.lineNum, prev.offset + 1));
            }
            return Optional.of(new LineOffset(lineNumber, position));
        }
        return Optional.empty();
    }

    /**
     * Get the position of the supplied offset (ie: the position in the source text) in this sentence's normalized content
     *
     * @param offset the position in the source text
     * @return the position in the setence's content
     */
    public int getOffsetPosition(LineOffset offset) {
        int position = offsetMap.indexOf(offset);
        return position < 0 ? 0 : position;
    }

    /**
     * Get size of offset mapping table (the size should be same as the content length).
     *
     * @return size of position mapping table
     */
    public int getOffsetMapSize() {
        return this.offsetMap.size();
    }


    @Override
    public String toString() {
        return "Sentence{" +
                "links=" + links +
                ", lineNumber=" + lineNumber +
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
        if (lineNumber != sentence.lineNumber) return false;
        if (startPositionOffset != sentence.startPositionOffset) return false;
        if (content != null ? !content.equals(sentence.content) : sentence.content != null) return false;
        if (links != null ? !links.equals(sentence.links) : sentence.links != null) return false;
        if (tokens != null ? !tokens.equals(sentence.tokens) : sentence.tokens != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = links != null ? links.hashCode() : 0;
        result = 31 * result + lineNumber;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + startPositionOffset;
        result = 31 * result + (isFirstSentence ? 1 : 0);
        result = 31 * result + (tokens != null ? tokens.hashCode() : 0);
        return result;
    }
}
