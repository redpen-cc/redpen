package org.unigram.docvalidator.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.store.FileContent;
import org.unigram.docvalidator.store.Paragraph;
import org.unigram.docvalidator.store.Section;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.util.StringUtils;

/**
 * Parser for plain text file.
 */
public final class PlainTextParser extends AbstractDocumentParser {
  /**
   * Constructor.
   */
  public PlainTextParser() {
    super();
  }

  public FileContent generateDocument(String fileName)
      throws DocumentValidatorException {
    InputStream iStream = this.loadStream(fileName);
    FileContent content = this.generateDocument(iStream);
    content.setFileName(fileName);
    return content;
  }

  public FileContent generateDocument(InputStream is) {
    BufferedReader br = null;
    try {
      br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    } catch (UnsupportedEncodingException e1) {
      e1.printStackTrace();
    }
    FileContent fileContent = new FileContent();
    fileContent.appendSection(new Section(0, ""));
    Section currentSection = fileContent.getLastSection();
    currentSection.appendParagraph(new Paragraph());
    try {
      String remain = new String("");
      String line;
      int lineNum = 0;
      while ((line = br.readLine()) != null) {
        int periodPosition =
            StringUtils.getSentenceEndPosition(line, this.period);
        if (line.equals("")) {
          currentSection.appendParagraph(new Paragraph());
        } else if (periodPosition == -1) {
          remain = remain + line;
        } else {
          remain =
              this.extractSentences(lineNum, remain + line, currentSection);
        }
        lineNum++;
      }
      if (remain.length() > 0) {
        currentSection.appendSentence(remain, lineNum);
      }
    } catch (IOException e) {
      LOG.error("Failed to parse: " + e.getMessage());
      return null;
    }
    return fileContent;
  }

  private String extractSentences(int lineNum, String line,
      Section currentSection) {
    int periodPosition = StringUtils.getSentenceEndPosition(line, this.period);
    if (periodPosition == -1) {
      return line;
    } else {
      while (true) {
        currentSection.appendSentence(
            line.substring(0, periodPosition + 1), lineNum);
        line = line.substring(periodPosition + 1, line.length());
        periodPosition = StringUtils.getSentenceEndPosition(line, this.period);
        if (periodPosition == -1) {
          return line;
        }
      }
    }
  }

  private static Logger LOG = LoggerFactory.getLogger(PlainTextParser.class);
}
