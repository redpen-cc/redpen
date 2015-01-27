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
package cc.redpen.parser.markdown;

import cc.redpen.parser.LineOffset;

import java.util.ArrayList;
import java.util.List;

/**
 * Buffer list of to candidate content.
 */
final public class CandidateSentence {

    private int lineNum;

    private String content;

    private String link;

    private int startPositionOffset;

    private List<LineOffset> offsetMap;

    CandidateSentence(int lineNum,
                      String content, String link) {
        this(lineNum, content, link, 0);
    }

    CandidateSentence(int lineNum, String content, String link,
            int positionOffset) {
        this.lineNum = lineNum;
        this.content = content;
        this.link = link;
        this.startPositionOffset = positionOffset;
        this.offsetMap = new ArrayList<>();
        for (int i = 0; i < content.length(); i++) {
            offsetMap.add(new LineOffset(lineNum, positionOffset+i));
        }
    }

    public List<LineOffset> getOffsetMap() {
        return offsetMap;
    }

    public int getLineNum() {
        return lineNum;
    }

    public int getStartPositionOffset() { return startPositionOffset; }

    public String getContent() {
        return content;
    }

    public void setContent(String text) {
        this.content = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String url) {
        this.link = url;
    }

    @Override
    public String toString() {
        return "CandidateSentence{" +
                "lineNum=" + lineNum +
                ", content='" + content + '\'' +
                ", link='" + link + '\'' +
                ", startPositionOffset=" + startPositionOffset +
                ", offsetMap=" + offsetMap +
                '}';
    }
}
