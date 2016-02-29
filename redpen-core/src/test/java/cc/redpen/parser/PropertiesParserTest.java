package cc.redpen.parser;

import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.WhiteSpaceTokenizer;
import org.junit.Test;

import java.util.List;

import static cc.redpen.parser.PropertiesParser.offsets;
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
    assertEquals(offsets(1, 8, 14), sentence.getOffsetMap());
  }

  @Test
  public void threeLines() throws Exception {
    Document doc = parse("hello = world\r\nworld:earth\n  key=val");

    Sentence sentence = doc.getSection(0).getParagraph(0).getSentence(0);
    assertEquals("world", sentence.getContent());
    assertEquals(1, sentence.getLineNumber());
    assertEquals(offsets(1, 8, 13), sentence.getOffsetMap());

    sentence = doc.getSection(1).getParagraph(0).getSentence(0);
    assertEquals("earth", sentence.getContent());
    assertEquals(2, sentence.getLineNumber());
    assertEquals(offsets(2, 6, 11), sentence.getOffsetMap());

    sentence = doc.getSection(2).getParagraph(0).getSentence(0);
    assertEquals("val", sentence.getContent());
    assertEquals(3, sentence.getLineNumber());
    assertEquals(offsets(3, 6, 9), sentence.getOffsetMap());
  }

  @Test
  public void manyWhitespaces() throws Exception {
    Document doc = parse("hi there the first hi is a key");
    Sentence sentence = doc.getSection(0).getParagraph(0).getSentence(0);
    assertEquals("there the first hi is a key", sentence.getContent());
    assertEquals(offsets(1, 3, 30), sentence.getOffsetMap());
  }

  @Test
  public void whitespaceInKey() throws Exception {
    Document doc = parse("two\\ words=value");
    Sentence sentence = doc.getSection(0).getParagraph(0).getSentence(0);
    assertEquals("value", sentence.getContent());
    assertEquals(offsets(1, 11, 16), sentence.getOffsetMap());
  }

  @Test
  public void multipleDelimiters() throws Exception {
    Document doc = parse("key=:value");
    Sentence sentence = doc.getSection(0).getParagraph(0).getSentence(0);
    assertEquals(":value", sentence.getContent());
    assertEquals(offsets(1, 4, 10), sentence.getOffsetMap());
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
    Document doc = parse(" #Hello World\n");
    Sentence sentence = doc.getSection(0).getParagraph(0).getSentence(0);
    assertEquals("Hello World", sentence.getContent());
  }

  private Document parse(String content) throws RedPenException {
    return parser.parse(content, new SentenceExtractor('.'), new WhiteSpaceTokenizer());
  }

  // todo detect UTF-8 vs ISO-8859-1 files

  // todo test global errors
}