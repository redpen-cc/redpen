package cc.redpen.parser;

import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.WhiteSpaceTokenizer;
import org.junit.Test;

import java.util.List;

import static java.util.stream.IntStream.of;
import static java.util.stream.IntStream.range;
import static org.junit.Assert.assertEquals;

public class PropertiesParserTest extends BaseParserTest {
  PropertiesParser parser = new PropertiesParser();

  @Test
  public void keyEqualsValue() throws Exception {
    Document doc = parse("hello=world");
    Sentence sentence = doc.getLastSection().getParagraph(0).getSentence(0);
    assertEquals("world", sentence.getContent());
  }

  @Test
  public void keyColonValue() throws Exception {
    Document doc = parse("hello:world");
    Sentence sentence = doc.getLastSection().getParagraph(0).getSentence(0);
    assertEquals("world", sentence.getContent());
  }

  @Test
  public void keySpaceValue() throws Exception {
    Document doc = parse("hello world");
    Sentence sentence = doc.getLastSection().getParagraph(0).getSentence(0);
    assertEquals("world", sentence.getContent());
  }

  @Test
  public void extraSpaces() throws Exception {
    Document doc = parse("hello = world ");
    List<Sentence> sentences = doc.getSection(0).getParagraph(0).getSentences();
    assertEquals(1, sentences.size());
    Sentence sentence = sentences.get(0);
    assertEquals("world ", sentence.getContent());
    assertEquals(1, sentence.getLineNumber());
    assertEquals(offsets(1, range(8, 14)), sentence.getOffsetMap());
  }

  @Test
  public void threeLines() throws Exception {
    Document doc = parse("hello = world\r\nworld:earth\n  key=val");

    Sentence sentence = doc.getSection(0).getParagraph(0).getSentence(0);
    assertEquals("world", sentence.getContent());
    assertEquals(1, sentence.getLineNumber());
    assertEquals(offsets(1, range(8, 13)), sentence.getOffsetMap());

    sentence = doc.getSection(1).getParagraph(0).getSentence(0);
    assertEquals("earth", sentence.getContent());
    assertEquals(2, sentence.getLineNumber());
    assertEquals(offsets(2, range(6, 11)), sentence.getOffsetMap());

    sentence = doc.getSection(2).getParagraph(0).getSentence(0);
    assertEquals("val", sentence.getContent());
    assertEquals(3, sentence.getLineNumber());
    assertEquals(offsets(3, range(6, 9)), sentence.getOffsetMap());
  }

  @Test
  public void manyWhitespaces() throws Exception {
    Document doc = parse("hi there the first hi is a key");
    Sentence sentence = doc.getSection(0).getParagraph(0).getSentence(0);
    assertEquals("there the first hi is a key", sentence.getContent());
    assertEquals(offsets(1, range(3, 30)), sentence.getOffsetMap());
  }

  @Test
  public void whitespaceInKey() throws Exception {
    Document doc = parse("two\\ words=value");
    Sentence sentence = doc.getSection(0).getParagraph(0).getSentence(0);
    assertEquals("value", sentence.getContent());
    assertEquals(offsets(1, range(11, 16)), sentence.getOffsetMap());
  }

  @Test
  public void multipleDelimiters() throws Exception {
    Document doc = parse("key=:value");
    Sentence sentence = doc.getSection(0).getParagraph(0).getSentence(0);
    assertEquals(":value", sentence.getContent());
    assertEquals(offsets(1, range(4, 10)), sentence.getOffsetMap());
  }

  @Test
  public void emptyLines() throws Exception {
    Document doc = parse("\nkey=value\n\n\n   \n\n");
    assertEquals(doc.getSection(0), doc.getLastSection());
    Sentence sentence = doc.getSection(0).getParagraph(0).getSentence(0);
    assertEquals("value", sentence.getContent());
  }

  @Test
  public void comments() throws Exception {
    Document doc = parse(" #Hello World\n!Another comment\n#");
    Sentence sentence = doc.getSection(0).getParagraph(0).getSentence(0);
    assertEquals("Hello World", sentence.getContent());
    sentence = doc.getSection(1).getParagraph(0).getSentence(0);
    assertEquals("Another comment", sentence.getContent());
  }

  @Test
  public void valuesAreUnescaped() throws Exception {
    Document doc = parse("hello=Hello\\ W\\u00F6rld\\t");
    Sentence sentence = doc.getSection(0).getParagraph(0).getSentence(0);
    assertEquals("Hello Wörld\t", sentence.getContent());
    assertEquals(offsets(1, range(6, 12), range(13, 15), range(20, 24)), sentence.getOffsetMap());
  }

  @Test
  public void multilineValue() throws Exception {
    Document doc = parse("hello=Hello\\\nWorld\\\n\\\nfoo");
    Sentence sentence = doc.getSection(0).getParagraph(0).getSentence(0);
    assertEquals("Hello\nWorld\n\nfoo", sentence.getContent());
    List<LineOffset> offsets = offsets(1, range(6, 11));
    offsets.addAll(offsets(2, of(0), range(0, 5)));
    offsets.addAll(offsets(3, of(0)));
    offsets.addAll(offsets(4, of(0), range(0, 3)));
    assertEquals(offsets, sentence.getOffsetMap());
  }

  @Test
  public void multipleSentences() throws Exception {
    Document doc = parse("hello=One sentence. Second one! Third one");

    Sentence sentence = doc.getSection(0).getParagraph(0).getSentence(0);
    assertEquals("One sentence.", sentence.getContent());
    assertEquals(offsets(1, range(6, 19)), sentence.getOffsetMap());

    sentence = doc.getSection(0).getParagraph(0).getSentence(1);
    assertEquals(" Second one!", sentence.getContent());
    assertEquals(offsets(1, range(19, 31)), sentence.getOffsetMap());

    sentence = doc.getSection(0).getParagraph(0).getSentence(2);
    assertEquals(" Third one", sentence.getContent());
    assertEquals(offsets(1, range(31, 41)), sentence.getOffsetMap());
  }

  private Document parse(String content) throws RedPenException {
    return parser.parse(content, new SentenceExtractor('.', '!'), new WhiteSpaceTokenizer());
  }

  // todo detect UTF-8 vs ISO-8859-1 files

  // todo test global errors
}