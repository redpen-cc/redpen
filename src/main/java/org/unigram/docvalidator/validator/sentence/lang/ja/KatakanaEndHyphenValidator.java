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
package org.unigram.docvalidator.validator.sentence.lang.ja;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.util.ValidatorConfiguration;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.validator.SentenceValidator;

/**
 * Validate the end hyphens of Katakana words in Japanese documents.
 * Japanese Katakana words have variations in end hyphen.
 * For example, "computer" is written in Katakana by 
 * "コンピュータ (without hyphen) ", and "コンピューター (with hypen) ".
 * This validator check if Katakana words ending format is match
 * the predefined standard. See JIS Z8301, G.6.2.2 b) G.3.
 *
 * The rules in JIS Z8301 are as follows:
 *
 * a) Words of 3 characters or more can not have the end hyphen.
 * b) Words of 2 characters or less can have the end hyphen.
 * c) A compound word applies a) and b) for each component.
 * d) In the cases from a) to c), the length of a syllable
 *    which are represented as a hyphen, flip syllable,
 *    and stuffed syllable is 1 except for Youon.
 *
 * Note that KatakanaEndHyphenValidator only checks the rules a) and b).
 * Default "with hyphen" mode is false, which follows a) and b).
 */
public class KatakanaEndHyphenValidator implements SentenceValidator {
  /**
   * Default "with hypen" mode.
   */
  private static final boolean DEFAULT_WITH_HYPEN = false;
  /**
   * Default Katakana limit length.
   */
  private static final int DEFAULT_KATAKANA_LIMIT_LENGTH = 2;
  /**
   * Katakana end hyphen character.
   */
  private static final char HYPHEN = 'ー';

  public List<ValidationError> check(Sentence sentence) {
    List<ValidationError> errors = new ArrayList<ValidationError>();
    if (withHypen) {
      return errors;
    }
    char c;
    int sentenceLength = sentence.content.length();
    StringBuffer katakana = new StringBuffer("");
    for (int i = 0; i < sentenceLength; i++) {
      c = sentence.content.charAt(i);
      if (isKatakana(c)) {
        katakana.append(c);
      } else {
        if (DEFAULT_KATAKANA_LIMIT_LENGTH < katakana.length()
            && c == HYPHEN) {
          katakana.append(c);
          errors.add(new ValidationError(sentence.position,
              "Invalid Katakana end hypen found: " + katakana.toString()
              + " in \"" + sentence.content + "\""));
        }
        katakana.delete(0, katakana.length());
      }
    }
    return errors;
  }

  public static boolean isKatakana(char c) {
    return Character.UnicodeBlock.of(c) == Character.UnicodeBlock.KATAKANA;
  }

  public KatakanaEndHyphenValidator() {
    super();
    withHypen = DEFAULT_WITH_HYPEN;
  }

  public boolean initialize(ValidatorConfiguration conf, CharacterTable characterTable)
        throws DocumentValidatorException {
    if (conf.getAttribute("katakana_end_hyphen").equals("true")) {
      withHypen = true;
    }
    return true;
  }

  private static Logger LOG =
      LoggerFactory.getLogger(KatakanaEndHyphenValidator.class);

  private boolean withHypen;
}
