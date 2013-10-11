package org.unigram.docvalidator.util;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class FileLoaderTest {
  @Test
  public void testCreateWordList() {
    String sampleWordSet = new String("Saitama\n");
    sampleWordSet += "Gumma\n";
    sampleWordSet += "Gifu\n";

    InputStream inputStream = IOUtils.toInputStream(sampleWordSet);
    WordListExtractor ex = new WordListExtractor();
    FileLoader fileLoader = new FileLoader(ex);
    assertEquals(0, fileLoader.loadFile(inputStream));
    Set<String> result = ex.get();
    assertEquals(3, result.size());
  }
  
  @Test
  public void testCreateVacantWordList() {
    String sampleWordSet = new String("");

    InputStream inputStream = IOUtils.toInputStream(sampleWordSet);
    WordListExtractor ex = new WordListExtractor();
    FileLoader fileLoader = new FileLoader(ex);
    assertEquals(0, fileLoader.loadFile(inputStream));
    Set<String> result = ex.get();
    assertEquals(0, result.size());
  }
  
  @Test
  public void testCreateKeyValueList() {
    String sampleWordSet = new String("Saitama\t100\n");
    sampleWordSet += "Gumma\t530000\n";
    sampleWordSet += "Gifu\t1200\n";

    InputStream inputStream = IOUtils.toInputStream(sampleWordSet);
    KeyValueDictionaryExtractor ex = new KeyValueDictionaryExtractor();
    FileLoader fileLoader = new FileLoader(ex);
    assertEquals(0, fileLoader.loadFile(inputStream));
    Map<String, String> result = ex.get();
    assertEquals(3, result.size());
    assertEquals("100", result.get("Saitama"));
    assertEquals("530000", result.get("Gumma"));
    assertEquals("1200", result.get("Gifu"));
  }
  
  @Test
  public void testCreateVacantKeyValueList() {
    String sampleWordSet = new String("");
    InputStream inputStream = IOUtils.toInputStream(sampleWordSet);
    KeyValueDictionaryExtractor ex = new KeyValueDictionaryExtractor();
    FileLoader fileLoader = new FileLoader(ex);
    assertEquals(0, fileLoader.loadFile(inputStream));
    Map<String, String> result = ex.get();
    assertEquals(0, result.size());
  }
  
  @Test
  public void testNullStream() {
    InputStream inputStream = null;
    WordListExtractor ex = new WordListExtractor();
    FileLoader fileLoader = new FileLoader(ex);
    assertEquals(1, fileLoader.loadFile(inputStream));
  }
}
