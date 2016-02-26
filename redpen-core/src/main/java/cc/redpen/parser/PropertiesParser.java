package cc.redpen.parser;

import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.RedPenTokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

/**
 * Parser for Java properties file format.
 */
class PropertiesParser extends BaseDocumentParser {

    @Override
    public Document parse(InputStream inputStream, Optional<String> fileName, SentenceExtractor sentenceExtractor, RedPenTokenizer tokenizer) throws RedPenException {
        Document.DocumentBuilder builder = Document.builder(tokenizer);
        fileName.ifPresent(builder::setFileName);

        try (BufferedReader reader = createReader(inputStream)) {
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                int keyStart = skipWhitespace(line, 0);
                int valueStart = valueOffset(line, keyStart);
                List<LineOffset> offsets = offsets(lineNum, valueStart, line.length());
                Sentence sentence = new Sentence(line.substring(valueStart, line.length()), offsets, emptyList());
                builder.addSection(0).addParagraph().addSentence(sentence);
            }
        }
        catch (IOException e) {
            throw new RedPenException(e);
        }

        return builder.build();
    }

    private int skipWhitespace(String line, int start) {
        for (int i = start; i < line.length(); i++)
            if (line.charAt(i) != ' ') return i;
        return line.length();
    }

    private int valueOffset(String line, int start) {
        int result = -1;
        for (int i = start; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\\') i++;
            else if (c == ' ') result = i;
            else if (c == ':' || c == '=') result = i;
            else if (result >= 0) break;
        }
        return result + 1;
    }

    static List<LineOffset> offsets(int lineNum, int startInclusive, int endExclusive) {
        return range(startInclusive, endExclusive).mapToObj(p -> new LineOffset(lineNum, p)).collect(toList());
    }
}
