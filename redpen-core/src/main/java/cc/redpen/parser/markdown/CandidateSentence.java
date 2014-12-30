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
package cc.redpen.parser.markdown;

/**
 * Buffer list of to candidate sentence.
 */
final class CandidateSentence {
    private int lineNum;

    private String sentence;

    private String link;

    private int startPositionOffset;

    CandidateSentence(int lineNum,
                      String content, String link) {
        this(lineNum, content, link, 0);
    }

    CandidateSentence(int lineNum, String sentence, String link,
            int positionOffset) {
        this.lineNum = lineNum;
        this.sentence = sentence;
        this.link = link;
        this.startPositionOffset = positionOffset;
    }

    public int getLineNum() {
        return lineNum;
    }

    public int getStartPositionOffset() { return startPositionOffset; }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String text) {
        this.sentence = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String url) {
        this.link = url;
    }

    @Override
    public String toString() {
        return "CandidateSentence{"
                + "lineNum=" + lineNum
                + ", sentence='" + sentence + '\''
                + ", link='" + link + '\''
                + ", startPositionOffset='" + startPositionOffset + '\''
                + '}';
    }
}
