package cc.redpen.parser.markdown;

import java.util.ArrayList;
import java.util.List;

public class MergedCandidateSentence {

    private int lineNum;

    private String contents;

    private List<String> links; // candidateSentenceId -> link

    private List<CandidateSentence.LineOffset> offsetMap;

    public MergedCandidateSentence(int lineNum, String contents,
            List<String> links, List<CandidateSentence.LineOffset> offsetMap) {
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

        List<String> links = new ArrayList<>(); // candidateSentenceId -> link

        List<CandidateSentence.LineOffset> offsetMap = new ArrayList<>();

        for (CandidateSentence sentence : candidateSentences) {
            if (offsetMap.size() > 0) {
                int rt = sentence.getLineNum() - offsetMap.get(offsetMap.size() - 1).lineNum;
                for (int i = 0; i < rt; i++) {
                    contents.append(" ");
                    offsetMap.add(new CandidateSentence.LineOffset(sentence.getLineNum() + i, 0));
                }
            }
            contents.append(sentence.getSentence());
            offsetMap.addAll(sentence.getOffsetMap());
            if (sentence.getLink() != null) {
                links.add(sentence.getLink());
            }
        }
        return new MergedCandidateSentence(lineNum, contents.toString(), links, offsetMap);
    }

    public String getContents() {
        return contents;
    }

    public List<String> getLinks() {
        return links;
    }

    public List<CandidateSentence.LineOffset> getOffsetMap() {
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
