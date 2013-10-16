package org.unigram.docvalidator.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.unigram.docvalidator.ConfigurationLoader;
import org.unigram.docvalidator.store.FileContent;
import org.unigram.docvalidator.store.Paragraph;
import org.unigram.docvalidator.store.Section;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.Configuration;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.DocumentValidatorException;

public class WikiParserTest {

  private Parser loadParser(DVResource resource) {
    Parser parser = null;
    try {
      parser = DocumentParserFactory.generate("w", resource);
    } catch (DocumentValidatorException e1) {
      fail();
      e1.printStackTrace();
    }
    return parser;
  }

  @Before
  public void setup() {
  }

  @Test
  public void testGenerateDocument() throws UnsupportedEncodingException {
    Configuration conf = new Configuration("dummy");
    Parser parser = loadParser(new DVResource(conf));
    String sampleText = "";
    sampleText += "h1. About Gekioko.\n";
    sampleText += "Gekioko pun pun maru means very very angry.\n";
    sampleText += "\n";
    sampleText += "The word also have posive meaning.\n";
    sampleText += "h1. About Gunma \n";
    sampleText += "\n";
    sampleText += "Gunma is located at west of Saitama.\n";
    sampleText += "- Features\n";
    sampleText += "-- Main City: Gumma City\n";
    sampleText += "-- Capical: 200 Millon\n";
    sampleText += "- Location\n";
    sampleText += "-- Japan\n";
    sampleText += "\n";
    sampleText += "The word also have posive meaning. Hower it is a bit wired.";

    InputStream is = new ByteArrayInputStream(sampleText.getBytes("utf-8")); 
    try {
      FileContent doc = parser.generateDocument(is);
      Iterator<Section> sections = doc.getSections();
      int sectionNum = 0;
      Vector<Section> sectionBlocks =new Vector<Section>();
      while(sections.hasNext()) {
        sectionNum += 1;
        sectionBlocks.add(sections.next());
      }
      assertEquals(3, sectionNum);
      assertEquals(1,doc.getLastSection().getSizeofLists());
      assertEquals(5,doc.getLastSection().getLastListBlock().getNumberOfListElements());
      Iterator<Paragraph> paragraph = doc.getLastSection().getParagraph();
      int pcount = 0;
      while (paragraph.hasNext()) {
        pcount++;
        paragraph.next();
      }
      assertEquals(2,pcount);
    } catch (DocumentValidatorException e) {
      fail();
    }
  }

  @Test
  public void testGenerateDocumentWithMultipleSentenceInOneSentence() {
    Configuration conf = new Configuration("dummy");
    Parser parser = loadParser(new DVResource(conf));
    String sampleText =
        "Tokyu is a good railway company. The company is reliable. In addition it is rich.";
    String[] expectedResult = {"Tokyu is a good railway company.",
        " The company is reliable.", " In addition it is rich."};
    InputStream is = null;
    try {
      is = new ByteArrayInputStream(sampleText.getBytes("utf-8"));
    } catch (UnsupportedEncodingException e1) {
      fail();
    }
    try {
      FileContent doc = parser.generateDocument(is);
      Section firstSections = doc.getSections().next();
      Paragraph firstParagraph = firstSections.getParagraph().next();
      assertEquals(3, firstParagraph.getNumverOfSentences());
      for (int i=0; i<expectedResult.length; i++) {
        assertEquals(expectedResult[i], firstParagraph.getLine(i).content);
      }
    } catch (DocumentValidatorException e) {
      fail();
    }
  }

  @Test
  public void testGenerateDocumentWithMultipleSentenceInMultipleSentences() {
    Configuration conf = new Configuration("dummy");
    Parser parser = loadParser(new DVResource(conf));
    String sampleText = "Tokyu is a good railway company. The company is reliable. In addition it is rich.";
    sampleText += "I like the company. Howerver someone does not like it.";
    InputStream is = null;
    try {
      is = new ByteArrayInputStream(sampleText.getBytes("utf-8"));
    } catch (UnsupportedEncodingException e1) {
      fail();
    }
    try {
      FileContent doc = parser.generateDocument(is);
      Section firstSections = doc.getSections().next();
      Paragraph firstParagraph = firstSections.getParagraph().next();
      assertEquals(5, firstParagraph.getNumverOfSentences());
    } catch (DocumentValidatorException e) {
      fail();
    }
  }

  @Test
  public void testGenerateDocumentWitVoidContent() {
    Configuration conf = new Configuration("dummy");
    Parser parser = loadParser(new DVResource(conf));
    String sampleText = "";
    InputStream is = null;
    try {
      is = new ByteArrayInputStream(sampleText.getBytes("utf-8"));
    } catch (UnsupportedEncodingException e1) {
      fail();
    }
    try {
      FileContent doc = parser.generateDocument(is);
      Section firstSections = doc.getSections().next();
      assertEquals(false, firstSections.getParagraph().hasNext());
    } catch (DocumentValidatorException e) {
      fail();
    }
  }

  @Test
  public void testGenerateDocumentWithPeriodInSuccession() {
    Configuration conf = new Configuration("dummy");
    Parser parser = loadParser(new DVResource(conf));
    String sampleText = "...";
    InputStream is = null;
    try {
      is = new ByteArrayInputStream(sampleText.getBytes("utf-8"));
    } catch (UnsupportedEncodingException e1) {
      fail();
    }
    try {
      FileContent doc = parser.generateDocument(is);
      Section firstSections = doc.getSections().next();
      Paragraph firstParagraph = firstSections.getParagraph().next();
      assertEquals(3, firstParagraph.getNumverOfSentences());
      Iterator<Sentence> siter = firstParagraph.getSentences();
      while(siter.hasNext()) {
        Sentence s= siter.next();
        assertEquals(s.content, ".");
      }
    } catch (DocumentValidatorException e) {
      fail();
    }
  }

  @Test
  public void testGenerateDocumentWitoutPeriodInLastSentence() {
    Configuration conf = new Configuration("dummy");
    Parser parser = loadParser(new DVResource(conf));
    String sampleText = "Hongo is located at the west of Tokyo. Saitama is located at the north";
    InputStream is = null;
    try {
      is = new ByteArrayInputStream(sampleText.getBytes("utf-8"));
    } catch (UnsupportedEncodingException e1) {
      fail();
    }
    try {
      FileContent doc = parser.generateDocument(is);
      Section firstSections = doc.getSections().next();
      Paragraph firstParagraph = firstSections.getParagraph().next();
      assertEquals(2, firstParagraph.getNumverOfSentences());
    } catch (DocumentValidatorException e) {
      fail();
    }
  }

  @Test
  public void testGenerateDocumentWithSentenceLongerThanOneLine() {
    Configuration conf = new Configuration("dummy");
    Parser parser = loadParser(new DVResource(conf));
    String sampleText = "This is a good day.";
    sampleText += "Hongo is located at the west of Tokyo ";
    sampleText += "which is the capital of Japan ";
    sampleText += "which is not located in the south of the earth.";
    InputStream is = null;
    try {
      is = new ByteArrayInputStream(sampleText.getBytes("utf-8"));
    } catch (UnsupportedEncodingException e1) {
      fail();
    }
    try {
      FileContent doc = parser.generateDocument(is);
      Section firstSections = doc.getSections().next();
      Paragraph firstParagraph = firstSections.getParagraph().next();
      assertEquals(2, firstParagraph.getNumverOfSentences());
    } catch (DocumentValidatorException e) {
      fail();
    }
  }

  @Test
  public void testGenerateJapaneseDocument() {
    String japaneseConfiguraitonStr = new String(
        "<?xml version=\"1.0\"?>" +
        "<configuration name=\"Validator\">" +
        "</configuration>");

    String japaneseCharTableStr = new String(
        "<?xml version=\"1.0\"?>" +
        "<character-table>" +
         "<character name=\"FULL_STOP\" value=\"。\" />" +
        "</character-table>");

    ConfigurationLoader loader = new ConfigurationLoader();
    InputStream stream = IOUtils.toInputStream(japaneseConfiguraitonStr);
    Configuration conf = null;
    conf = loader.loadConfiguraiton(stream);
    if (conf == null) {
      fail();
    }

    InputStream char_stream = IOUtils.toInputStream(japaneseCharTableStr);
    CharacterTable characterTable = new CharacterTable(char_stream);

    Parser parser = loadParser(new DVResource(conf, characterTable));
    String sampleText = "埼玉は東京の北に存在する。";
    sampleText += "大きなベッドタウンであり、多くの人が住んでいる。";
    InputStream is = null;
    try {
      is = new ByteArrayInputStream(sampleText.getBytes("utf-8"));
    } catch (UnsupportedEncodingException e1) {
      fail();
    }
    try {
      FileContent doc = parser.generateDocument(is);
      Section firstSections = doc.getSections().next();
      Paragraph firstParagraph = firstSections.getParagraph().next();
      assertEquals(2, firstParagraph.getNumverOfSentences());
    } catch (DocumentValidatorException e) {
      fail();
    }
  }

}
