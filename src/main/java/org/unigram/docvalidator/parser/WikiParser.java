/**
 * DocumentValidator
 * Copyright (c) 2013-, Takahiko Ito, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package org.unigram.docvalidator.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.unigram.docvalidator.store.FileContent;
import org.unigram.docvalidator.store.Paragraph;
import org.unigram.docvalidator.store.Section;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.util.StringUtils;

import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parser for wiki formatted file.
 */
public final class WikiParser extends BasicDocumentParser {
  /**
   * Constructor.
   */
  public WikiParser() {
    super();
  }

  public FileContent generateDocument(String fileName)
      throws DocumentValidatorException {
    InputStream inputStream = this.loadStream(fileName);
    return this.generateDocument(inputStream);
  }

  public FileContent generateDocument(InputStream is) {
    BufferedReader br = null;
    try {
      br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      LOG.error(e.getMessage());
      return null;
    }
    FileContent doc = new FileContent();
    //for sentences right below the begging of document
    Section currentSection = new Section(0, "");
    doc.appendSection(currentSection);
    LinePattern prevPattern = LinePattern.VOID;
    LinePattern currentPattern = LinePattern.VOID;
    try {
      String line;
      int lineNum = 0;
      String remain = "";
      while ((line = br.readLine()) != null) {
        prevPattern = currentPattern;
        Vector<String> head = new Vector<String>();
        if (check(HEADER_PATTERN, line, head)) {
          currentPattern = LinePattern.HEADER;
          Integer level = Integer.valueOf(head.get(0));
          Section tmpSection =  new Section(level, head.get(1));
          doc.appendSection(tmpSection);
          if (!addChild(currentSection, tmpSection)) {
            LOG.warn("Failed to add parent for a Seciotn: "
                + tmpSection.getHeaderContent());
          }
          currentSection = tmpSection;
        } else if (check(LIST_PATTERN, line, head)) {
          currentPattern = LinePattern.LIST;
          if (prevPattern != LinePattern.LIST) {
            currentSection.appendListBlock();
          }
          currentSection.appendListElement(extractListLevel(head.get(0)),
              head.get(1));
        } else if (line.equals("")) { // new paragraph content
          currentSection.appendParagraph(new Paragraph());
        } else { // usual sentence.
          currentPattern = LinePattern.SENTENCE;
          remain = extractSentences(lineNum, remain + line, currentSection);
        }
        prevPattern = currentPattern;
        lineNum++;
      }
      if (remain.length() > 0) {
        doc.getLastSection().appendSentence(remain, lineNum);
      }
    } catch (IOException e) {
      LOG.error("Failed to parse input document: " + e.getMessage());
      return null;
    }
    return doc;
  }

  private boolean addChild(Section candidate, Section child) {
    if (candidate.getLevel() < child.getLevel()) {
      candidate.appendSection(child);
      child.setParent(candidate);
    } else { // search parent
      Section parent = candidate.getParent();
      while (parent != null) {
        if (parent.getLevel() < child.getBlockID()) {
          parent.appendSection(child);
          child.setParent(parent);
          candidate = child;
          break;
        }
        parent = parent.getParent();
      }
      if (parent == null) {
        return false;
      }
    }
    return true;
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

  private static boolean check(Pattern p, String target, Vector<String> head) {
    Matcher m = p.matcher(target);
    if (m.matches()) {
      for (int i = 1; i <= m.groupCount(); i++) {
        head.add(m.group(i));
      }
      return true;
    } else {
      return false;
    }
  }

  private int extractListLevel(String listPrefix) {
    return listPrefix.length();
  }

  private static Logger LOG = LoggerFactory.getLogger(WikiParser.class);

  private static final Pattern HEADER_PATTERN
    = Pattern.compile("^h([1-6])\\.(.*)$");

  private static final Pattern LIST_PATTERN = Pattern.compile("^(-+) (.*)$");

  //private static final Pattern numberedListPattern
  // = Pattern.compile("(#+).(.*)$");

  /**
   * List of elements used in wiki format.
   */
  private enum LinePattern {
    SENTENCE, LIST, NUM_LIST, VOID, HEADER
  }
}
