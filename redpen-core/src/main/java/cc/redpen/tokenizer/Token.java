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
package cc.redpen.tokenizer;

import java.util.ArrayList;
import java.util.List;

public class Token {
    final private String content;

    final private List<String> tags;

    public Token(String word) {
        content = word;
        tags = new ArrayList<>();
    }

    public Token(String word, String tag) {
        this(word);
        tags.add(tag);
    }

    public Token(String word, List<String> tagList) {
        this(word);
        tags.addAll(tagList);
    }

    public String getContent() {
        return content;
    }

    public List<String> getTags() {
        return tags;
    }
}
