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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MergedCandidateSentence {
    private static final Logger LOG =
            LoggerFactory.getLogger(MergedCandidateSentence.class);

    private int lineNum;

    private String contents;

    private Map<LineOffset, String> links;

    private List<LineOffset> offsetMap;

    public MergedCandidateSentence(int lineNum, String contents,
            Map<LineOffset, String> links, List<LineOffset> offsetMap) {
        this.lineNum = lineNum;
        this.contents = contents;
        this.links = links;
        this.offsetMap = offsetMap;
    }

    public static Optional<MergedCandidateSentence> merge(List<CandidateSentence> candidateSentences) {
        if (candidateSentences.size() == 0) {
            return Optional.empty();
        }
        int lineNum = candidateSentences.get(0).getLineNum();
        StringBuilder contents = new StringBuilder();
        Map<LineOffset, String> links = new HashMap<>();
        List<LineOffset> offsetMap = new ArrayList<>();

        for (CandidateSentence sentence : candidateSentences) {
            contents.append(sentence.getContent());
            offsetMap.addAll(sentence.getOffsetMap());
            if (sentence.getLink() != null) {
                links.put(sentence.getOffsetMap().get(0), sentence.getLink());
            }
        }
        if (offsetMap.size() != contents.length()) {
            LOG.warn("Size of offset map (\"{}\") is not the same as sentence length \"{}\":  ",
                    offsetMap.size(), contents.length(), contents);
        }
        return Optional.of(new MergedCandidateSentence(lineNum, contents.toString(), links, offsetMap));
    }

    public List<String> getRangedLinks(int startPosition, int endPosition) {
        if (startPosition >= offsetMap.size() || endPosition >= offsetMap.size()) {
            return new ArrayList<>();
        }
        List<String> output = new ArrayList<>();
        for (LineOffset linkPosition : this.links.keySet()) {
            if (linkPosition.compareTo(offsetMap.get(startPosition)) >= 0
                    && linkPosition.compareTo(offsetMap.get(endPosition)) <= 0) {
                output.add(this.getLinks().get(linkPosition));
            }
        }
        return output;
    }

    public String getContents() {
        return contents;
    }

    public Map<LineOffset, String> getLinks() {
        return links;
    }

    public List<LineOffset> getOffsetMap() {
        return offsetMap;
    }

    public int getLineNum() {
        return lineNum;
    }

    @Override
    public String toString() {
        return "MergedCandidateSentence{" +
                "lineNum=" + lineNum +
                ", contents='" + contents + '\'' +
                ", links=" + links +
                ", offsetMap=" + offsetMap +
                '}';
    }
}
