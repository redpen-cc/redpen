package cc.redpen.validator.section;

import cc.redpen.model.Paragraph;
import cc.redpen.model.Section;
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.TokenElement;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;

import java.util.*;

/**
 * DuplicatedSectionValidator check if there are highly similar section pairs.
 */
final public class DuplicatedSectionValidator extends Validator {

    static final private double SIMILARITY_THRESHOLD = 0.9d;
    private List<SectionVector> sectionVectors = new ArrayList<>();

    class SectionVector {
        public final Sentence header;
        public final Map<String, Integer> sectionVector;

        public SectionVector(Sentence header, Map<String, Integer> vector) {
            this.header = header;
            this.sectionVector = vector;
        }
    }

    @Override
    public void preValidate(Section section) {
        Map<String, Integer> sectionVector = extractWordFrequency(section);
        this.sectionVectors.add(new SectionVector(section.getHeaderContent(0), sectionVector));
    }

    private Map<String, Integer> extractWordFrequency(Section section) {
        Map<String, Integer> sectionVector = new HashMap<>();
        for (Paragraph paragraph : section.getParagraphs()) {
            for (Sentence sentence : paragraph.getSentences()) {
                addWords(sectionVector, sentence);
            }
        }
        // apply to sentences in section header
        for (Sentence headerSentence : section.getHeaderContents()) {
            addWords(sectionVector, headerSentence);
        }
        return sectionVector;
    }

    @Override
    public void validate(List<ValidationError> errors, Section section) {
        Map<String, Integer> targetVector = extractWordFrequency(section);
        for (SectionVector sectionVector : sectionVectors) {
            Map<String, Integer> candidateVector = sectionVector.sectionVector;
            // NOTE: not header.equals() since the we need check if the references are identical
            if (sectionVector.header != section.getHeaderContent(0) &&
                    calcCosine(targetVector, candidateVector) > SIMILARITY_THRESHOLD) {
                Optional<Sentence> header = Optional.ofNullable(section.getHeaderContent(0));
                errors.add(createValidationError(header.orElse(section.getParagraph(0).getSentence(0)),
                        sectionVector.header.position));
            }
        }
    }

    private double calcCosine(Map<String, Integer> targetVector,
            Map<String, Integer> argumentVector) {
        int innerProduct = 0;
        int targetVecLen = calcLength(targetVector);
        int argumentVecLen = calcLength(argumentVector);
        for (String word : targetVector.keySet()) {
            if (argumentVector.containsKey(word)) {
                innerProduct += targetVector.get(word) * argumentVector.get(word);
            }
        }
        return innerProduct / (Math.sqrt(targetVecLen) * Math.sqrt(argumentVecLen));
    }

    private int calcLength(Map<String, Integer> targetVector) {
        int length = 0;
        for (String word : targetVector.keySet()) {
            length += targetVector.get(word) * targetVector.get(word);
        }
        return length;
    }

    private void addWords(Map<String, Integer> sectionVector, Sentence sentence) {
        for (TokenElement token : sentence.tokens) {
            String surface = token.getSurface();
            if (!sectionVector.containsKey(surface)){
                sectionVector.put(surface, 0);
            }
            Integer currentNum = sectionVector.get(surface);
            sectionVector.put(surface, ++currentNum);
        }
    }

    @Override
    public String toString() {
        return "DuplicatedSectionValidator{" +
                "sectionVectors=" + sectionVectors +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DuplicatedSectionValidator that = (DuplicatedSectionValidator) o;

        if (sectionVectors != null ? !sectionVectors.equals(that.sectionVectors) : that.sectionVectors != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return sectionVectors != null ? sectionVectors.hashCode() : 0;
    }
}
