package cc.redpen.parser;

import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.RedPenTokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Integer.parseInt;
import static java.util.Collections.emptyList;

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
            AtomicInteger lineNum = new AtomicInteger(0);
            while ((line = reader.readLine()) != null) {
                lineNum.incrementAndGet();
                int keyStart = skipWhitespace(line, 0);
                if (keyStart == line.length()) continue;
                int valueStart = valueOffset(line, keyStart);
                Sentence sentence = sentence(line, lineNum, valueStart, reader);
                builder.addSection(0).addParagraph().addSentence(sentence);
            }
        }
        catch (IOException e) {
            throw new RedPenException(e);
        }

        return builder.build();
    }

    private Sentence sentence(String line, AtomicInteger lineNum, int valueStart, BufferedReader reader) throws IOException {
        int length = line.length();
        StringBuilder value = new StringBuilder(length);
        List<LineOffset> offsets = new ArrayList<>(length);
        for (int i = valueStart; i < length; i++) {
            char c = line.charAt(i);
            int offset = i;
            if (c == '\\') {
                if (++i == length) {
                    lineNum.incrementAndGet();
                    Sentence nextLine = sentence(reader.readLine(), lineNum, 0, reader);
                    value.append('\n').append(nextLine.getContent());
                    offsets.addAll(nextLine.getOffsetMap());
                    continue;
                }

                c = line.charAt(i);
                if (c == 'n') c = '\n';
                else if (c == 't') c = '\t';
                else if (c == 'f') c = '\f';
                else if (c == 'r') c = '\r';
                else if (c == 'u') {
                    String code = line.substring(i + 1, i + 5);
                    c = (char)parseInt(code, 16);
                    i += 4;
                }
            }
            value.append(c);
            offsets.add(new LineOffset(lineNum.get(), offset));
        }
        return new Sentence(value.toString(), offsets, emptyList());
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
            else if (c == ':' || c == '=' || c == '#' || c == '!') {result = i; break;}
            else if (result >= 0) break;
        }
        return skipWhitespace(line, result + 1);
    }
}
