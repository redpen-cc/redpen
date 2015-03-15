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
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class MergedCandidateSentenceTest {
    @Test
    public void testMerge() {
        List<CandidateSentence> candidateSentences = initializeCandidateSentences(
                new CandidateSentence(1, "first ", "", 0),
                new CandidateSentence(1, "second ", "", 6),
                new CandidateSentence(1, "third", "", 13));

        MergedCandidateSentence mergedSentence = MergedCandidateSentence.merge(candidateSentences).get();
        assertTrue(mergedSentence != null);
        assertEquals("first second third", mergedSentence.getContents());
        List<LineOffset> expectedOffsets = initializeMappingTable(
                new LineOffset(1, 0), // f
                new LineOffset(1, 1), // i
                new LineOffset(1, 2), // r
                new LineOffset(1, 3), // s
                new LineOffset(1, 4), // t
                new LineOffset(1, 5), // ' '
                new LineOffset(1, 6), // s
                new LineOffset(1, 7), // e
                new LineOffset(1, 8), // c
                new LineOffset(1, 9), // o
                new LineOffset(1, 10), // n
                new LineOffset(1, 11), // d
                new LineOffset(1, 12), // ' '
                new LineOffset(1, 13), // t
                new LineOffset(1, 14), // h
                new LineOffset(1, 15), // i
                new LineOffset(1, 16), // r
                new LineOffset(1, 17)); // d

        assertEquals(expectedOffsets.size(), mergedSentence.getOffsetMap().size());
        for (int i = 0; i < expectedOffsets.size() ; i++) {
            assertEquals(expectedOffsets.get(i),
                    mergedSentence.getOffsetMap().get(i));
        }

    }


    private static List<CandidateSentence> initializeCandidateSentences(CandidateSentence... candidateSentences) {
        List<CandidateSentence> candidateSentenceList = new ArrayList<>();
        for (CandidateSentence candidateSentence : candidateSentences) {
            candidateSentenceList.add(candidateSentence);
        }
        return candidateSentenceList;
    }

    private static List<LineOffset> initializeMappingTable(LineOffset... offsets) {
        List<LineOffset> offsetTable = new ArrayList<>();
        for (LineOffset offset : offsets) {
            offsetTable.add(offset);
        }
        return offsetTable;
    }
}
