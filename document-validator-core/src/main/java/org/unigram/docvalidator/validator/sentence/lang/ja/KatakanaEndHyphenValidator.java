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
import org.unigram.docvalidator.util.StringUtils;
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
 */
public class KatakanaEndHyphenValidator implements SentenceValidator {
  /**
   * Default Katakana limit length without hypen.
   */
  private static final int DEFAULT_KATAKANA_LIMIT_LENGTH = 3;
  /**
   * Katakana end hyphen character.
   */
  private static final char HYPHEN = 'ー';
  /**
   * Katakana middle dot character.
   */
  private static final char KATAKANA_MIDDLE_DOT = '・';

  public List<ValidationError> check(Sentence sentence) {
    List<ValidationError> errors = new ArrayList<ValidationError>();
    List<ValidationError> result;
    StringBuffer katakana = new StringBuffer("");
    for (int i = 0; i < sentence.content.length(); i++) {
      char c = sentence.content.charAt(i);
      if (StringUtils.isKatakana(c) && c != KATAKANA_MIDDLE_DOT) {
        katakana.append(c);
      } else {
        result = this.checkKatakanaEndHyphen(sentence, katakana);
        if (result != null) {
          errors.addAll(result);
        }
        katakana.delete(0, katakana.length());
      }
    }
    result = this.checkKatakanaEndHyphen(sentence, katakana);
    if (result != null) {
      errors.addAll(result);
    }
    return errors;
  }

  private List<ValidationError> checkKatakanaEndHyphen(Sentence sentence,
      StringBuffer katakana) {
    List<ValidationError> errors = new ArrayList<ValidationError>();
    if (isKatakanaEndHyphen(katakana)) {
      errors.add(new ValidationError(
          "Invalid Katakana end hypen found \"" + katakana.toString() + "\"",
          sentence));
    }
    return errors;
  }
  public static boolean isKatakanaEndHyphen(StringBuffer katakana) {
    return (DEFAULT_KATAKANA_LIMIT_LENGTH < katakana.length()
            && katakana.charAt(katakana.length() - 1) == HYPHEN);
  }

  public KatakanaEndHyphenValidator() {
    super();
  }

  public boolean initialize(ValidatorConfiguration conf, CharacterTable characterTable)
        throws DocumentValidatorException {
    //TODO support exception word list.
    return true;
  }

  private static Logger LOG =
      LoggerFactory.getLogger(KatakanaEndHyphenValidator.class);
}
