package org.unigram.docvalidator.validator.sentence;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.Configuration;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.validator.SentenceValidator;

/**
 * Validate input sentences have more words than specified.
 */
public class MaxWordNumberValidator implements SentenceValidator {
  /**
   * Default maximum number of words in one sentence.
   */
  public static final int DEFAULT_MAXIMUM_WORDS_IN_A_SENTENCE = 30;

  public MaxWordNumberValidator() {
    super();
    this.maxWordNumber = DEFAULT_MAXIMUM_WORDS_IN_A_SENTENCE;
  }

  public List<ValidationError> check(Sentence sentence) {
    List<ValidationError> result = new ArrayList<ValidationError>();
    String content = sentence.content;
    String[] wordList = content.split(" ");
    int wordNum = wordList.length;
    if (wordNum > maxWordNumber) {
      result.add(new ValidationError(sentence.position,
          "The number of the words exceeds the maximum "
          + String.valueOf(wordNum) + " in line: " + sentence.content));
    }
    return result;
  }

  public boolean initialize(Configuration conf, CharacterTable characterTable)
      throws DocumentValidatorException {
    if (conf.getAttribute("max_word_num") == null) {
      this.maxWordNumber = DEFAULT_MAXIMUM_WORDS_IN_A_SENTENCE;
      LOG.info("max_length was not set.");
      LOG.info("Using the default value of max_length.");
    } else {
      this.maxWordNumber = Integer.valueOf(conf.getAttribute("max_word_num"));
    }
    return true;
  }
  private static Logger LOG =
      LoggerFactory.getLogger(MaxWordNumberValidator.class);

  private int maxWordNumber;
}
