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
package cc.redpen.tokenizer;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class TokenElement implements Serializable {
    private static final long serialVersionUID = -9055285891555999514L;

    // the surface form of the token
    final private String surface;

    // token metadata (POS, etc)
    final private List<String> tags;

    // the character position of the token in the sentence
    final private int offset;

    // token reading
    final private String reading;

    // the base form of the token
    final private String baseForm;

    public TokenElement(String word, List<String> tagList, int offset, String reading, String baseForm) {
        surface = word;
        tags = Collections.unmodifiableList(tagList);
        this.offset = offset;
        this.reading = reading;
        this.baseForm = baseForm;
    }

    public TokenElement(String word, List<String> tagList, int offset, String reading) {
        this(word, tagList, offset, reading, null);
    }

    public TokenElement(String word, List<String> tagList, int offset) {
        this(word, tagList, offset, word);
    }

    public String getSurface() {
        return surface;
    }

    public List<String> getTags() {
        return tags;
    }

    public int getOffset() {
        return offset;
    }

    public String getReading() { return reading; }

    public String getBaseForm() { return (baseForm!=null) ? baseForm : surface; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TokenElement)) return false;

        TokenElement that = (TokenElement) o;

        if (offset != that.offset) return false;
        if (!surface.equals(that.surface)) return false;
        if (!tags.equals(that.tags)) return false;
        return reading.equals(that.reading);
    }

    @Override
    public int hashCode() {
        int result = surface.hashCode();
        result = 31 * result + tags.hashCode();
        result = 31 * result + offset;
        result = 31 * result + reading.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TokenElement{" +
                "surface='" + surface + '\'' +
                ", tags=" + tags +
                ", offset=" + offset +
                ", reading='" + reading + '\'' +
                '}';
    }
}
