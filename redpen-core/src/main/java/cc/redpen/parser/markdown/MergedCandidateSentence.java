package cc.redpen.parser.markdown;

import cc.redpen.parser.LineOffset;

import java.util.*;

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
        return Optional.of(new MergedCandidateSentence(lineNum, contents.toString(), links, offsetMap));
    }

    public List<String> getRangedLinks(int startPosition, int endPosition) {
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
