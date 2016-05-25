package cc.redpen.parser;

import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.RedPenTokenizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Character.isWhitespace;
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

        try (PreprocessingReader reader = createReader(inputStream)) {
            String line;
            AtomicInteger lineNum = new AtomicInteger(0);
            while ((line = reader.readLine()) != null) {
                lineNum.incrementAndGet();
                int keyStart = skipWhitespace(line, 0);
                if (keyStart == line.length()) continue;
                int valueStart = valueOffset(line, keyStart);
                addSentences(builder, sentenceExtractor, section(line, lineNum, valueStart, reader));
            }
            builder.setPreprocessorRules(reader.getPreprocessorRules());
        } catch (IOException e) {
            throw new RedPenException(e);
        }

        return builder.build();
    }

    private void addSentences(Document.DocumentBuilder builder, SentenceExtractor sentenceExtractor, ValueWithOffsets value) {
        if (value == null) return;
        String text = value.getContent();
        List<LineOffset> offsets = value.getOffsetMap();
        builder.addSection(0).addParagraph();
        while (text.length() > 0) {
            int end = sentenceExtractor.getSentenceEndPosition(text) + 1;
            if (end == 0) end = text.length();
            builder.addSentence(new Sentence(text.substring(0, end), offsets.subList(0, end), emptyList()));
            text = text.substring(end, text.length());
            offsets = offsets.subList(end, offsets.size());
        }
    }

    private ValueWithOffsets section(String line, AtomicInteger lineNum, int valueStart, PreprocessingReader reader) throws IOException {
        int length = line.length();
        StringBuilder value = new StringBuilder(length);
        List<LineOffset> offsets = new ArrayList<>(length);
        for (int i = valueStart; i < length; i++) {
            char c = line.charAt(i);
            int offset = i;
            if (c == '\\') {
                if (++i == length) {
                    lineNum.incrementAndGet();
                    line = reader.readLine();
                    valueStart = skipWhitespace(line, 0);
                    offsets.add(new LineOffset(lineNum.get(), valueStart));
                    Sentence nextLine = section(line, lineNum, valueStart, reader);
                    if (nextLine == null) continue;
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
                    c = (char) parseInt(code, 16);
                    i += 4;
                }
            }
            value.append(c);
            offsets.add(new LineOffset(lineNum.get(), offset));
        }
        return value.length() == 0 ? null : new ValueWithOffsets(value.toString(), offsets);
    }

    private int valueOffset(String line, int start) {
        int result = -1;
        for (int i = start; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\\') i++;
            else if (isWhitespace(c)) result = i;
            else if (c == ':' || c == '=' || c == '#' || c == '!') {
                result = i;
                break;
            } else if (result >= 0) break;
        }
        return skipWhitespace(line, result + 1);
    }
}
