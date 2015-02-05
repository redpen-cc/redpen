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

import java.util.List;

/**
 * Element of List in semi-structured text format such as wiki.
 */
public final class ListElement {
    private final List<Sentence> contents;
    private final int level;

    /**
     * Constructor.
     *
     * @param listLevel    indentation level
     * @param listContents content of list element
     */
    public ListElement(int listLevel, List<Sentence> listContents) {
        super();
        this.level = listLevel;
        this.contents = listContents;
    }

    /**
     * Get content of list element.
     *
     * @return all contents of list element
     */
    public List<Sentence> getSentences() {
        return contents;
    }

    /**
     * Given sentence id, return the content of sentence.
     *
     * @param id sentence id
     * @return content of list element
     */
    public Sentence getSentence(int id) {
        return contents.get(id);
    }

    /**
     * Get the number of content sentence.
     *
     * @return the number of sentences in the list item
     */
    public int getNumberOfSentences() {
        return contents.size();
    }

    /**
     * Get indentation level.
     *
     * @return indentation level
     */
    public int getLevel() {
        return level;
    }
}
