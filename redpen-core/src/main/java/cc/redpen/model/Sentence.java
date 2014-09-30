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

import cc.redpen.tokenizer.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Sentence block in a Document.
 */
public final class Sentence {
    /**
     * Links (including internal and external ones).
     */
    public final List<String> links;
    /**
     * Content of string.
     */
    public String content;

    /**
     * Sentence position in a file.
     */
    public int position;

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
    public List<Token> tokens;

    /**
     * Constructor.
     *
     * @param sentenceContent  content of sentence
     * @param sentencePosition sentence position
     */
    public Sentence(String sentenceContent, int sentencePosition) {
        super();
        this.content = sentenceContent;
        this.position = sentencePosition;
        this.isFirstSentence = false;
        this.links = new ArrayList<>();
        this.tokens = new ArrayList<>();
    }
}
