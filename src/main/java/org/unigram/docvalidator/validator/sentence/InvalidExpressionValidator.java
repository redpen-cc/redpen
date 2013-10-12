package org.unigram.docvalidator.validator.sentence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.util.Configuration;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.util.FileLoader;
import org.unigram.docvalidator.util.WordListExtractor;
import org.unigram.docvalidator.validator.SentenceValidator;

/**
 * Validate input sentences contain invalid expression.
 */
public class InvalidExpressionValidator implements SentenceValidator {

  public InvalidExpressionValidator() {
    invalidExpressions = new HashSet<String>();
  }

  public List<ValidationError> check(Sentence line) {
    List<ValidationError> result = new ArrayList<ValidationError>();
    String str = line.content;
    for (Iterator<String> iter = invalidExpressions.iterator();
        iter.hasNext();) {
      String w = iter.next();
      if (str.indexOf(w) != -1) {
        result.add(new ValidationError(line.position,
            "Found invalid expression: \""
            + w + "\" in line: " + str));
      }
    }
    return result;
  }

  public boolean initialize(Configuration conf, CharacterTable characterTable)
      throws DocumentValidatorException {
    String confFile = conf.getAttribute("dictionary");
    LOG.info("dictionary file is " + confFile);
    if (confFile == null || confFile.equals("")) {
      LOG.error("dictionary file is not specified");
      return false;
    }
    WordListExtractor extractor = new WordListExtractor();
    FileLoader loader = new FileLoader(extractor);
    if (loader.loadFile(confFile) != 0) {
      return false;
    }
    invalidExpressions = extractor.get();
    return true;
  }

  private Set<String> invalidExpressions;

  private static Logger LOG =
      LoggerFactory.getLogger(InvalidExpressionValidator.class);
}