package org.unigram.docvalidator.validator.sentence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.util.Configuration;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.util.FileLoader;
import org.unigram.docvalidator.util.KeyValueDictionaryExtractor;
import org.unigram.docvalidator.validator.SentenceValidator;

public class SuggestExpressionValidator implements SentenceValidator {

  public SuggestExpressionValidator() {
    super();
    synonms = new HashMap<String, String>();
  }

  public List<ValidationError> check(Sentence line) {
    String str = line.content;
    Set<String> invalidWords = synonms.keySet();
    for (Iterator<String> iter = invalidWords.iterator(); iter.hasNext();) {
      String w = iter.next();
      if (str.indexOf(w) != -1) {
        List<ValidationError> result = new ArrayList<ValidationError>();
        result.add(new ValidationError(
            line.position, "Found invalid word, \""
                + w + "\"in line: " + str + "Use the synonym of the word \""
                + synonms.get(w) + "\" instead."));
        return result;
      }
    }
    return null;
  }

  public boolean initialize(Configuration conf, CharacterTable characterTable)
      throws DocumentValidatorException {
    String confFile = conf.getAttribute("invalid_word_file");
    LOG.info("dictionary file is " + confFile);
    if (confFile == null || confFile.equals("")) {
      LOG.error("dictionary file is not specified");
      return false;
    }
    KeyValueDictionaryExtractor extractor = new KeyValueDictionaryExtractor();
    FileLoader loader = new FileLoader(extractor);
    if (loader.loadFile(confFile) != 0) {
      return false;
    }
    synonms = extractor.get();
    return true;
  }

  private static Logger LOG =
      LoggerFactory.getLogger(SuggestExpressionValidator.class);

  protected Map<String, String> synonms;
}
