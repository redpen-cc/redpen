package cc.redpen.parser.markdown;

import cc.redpen.parser.LineOffset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MergedCandidateSentence {

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

    public static MergedCandidateSentence merge(List<CandidateSentence> candidateSentences) {
        if (candidateSentences.size() == 0) {
            return null;
        }
        int lineNum = candidateSentences.get(0).getLineNum();
        StringBuilder contents = new StringBuilder();
        Map<LineOffset, String> links = new HashMap<>();
        List<LineOffset> offsetMap = new ArrayList<>();

        for (CandidateSentence sentence : candidateSentences) {
            if (offsetMap.size() > 0) {
                int rt = sentence.getLineNum() - offsetMap.get(offsetMap.size() - 1).lineNum;
                for (int i = 0; i < rt; i++) {
                    contents.append(" ");
                    offsetMap.add(new LineOffset(sentence.getLineNum() + i, 0));
                }
            }
            contents.append(sentence.getContent());
            offsetMap.addAll(sentence.getOffsetMap());
            if (sentence.getLink() != null) {
                links.put(sentence.getOffsetMap().get(0), sentence.getLink());
            }
        }
        return new MergedCandidateSentence(lineNum, contents.toString(), links, offsetMap);
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
