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
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.StringUtils;
import org.unigram.docvalidator.util.LevenshteinDistance;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.util.ValidatorConfiguration;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.validator.SentenceValidator;

/**
 * Validate the correctness of Katakana word spelling.
 * Japanese Katakana words have orthographical variations.
 * For example, "index" is written in Katakana by 
 * "インデクス" or "インデックス".
 * Unfortunately, we can not detect whether is correct or not
 * because there is no specific rule.
 * So our strategy is to validate whether a Katakana word is
 * very similar to the other Katakana word in previous sentences
 * or not.
 * The similarity is defined as Levenshtein distance.
 * We heuristically detecit a pair as similar if the distance is
 * smaller than the 30% of the length of the target string.
 * Moreover, if the length of Katakana word is small,
 * mostly the pairs are detected as similar.
 * To avoid the noisy detection, we set a threshold of the
 * length of Katakana word. And if the length of a Katakana
 * word is smaller than the threshold, we do not detect
 * the similarity.
 */
public class KatakanaSpellCheckValidator implements SentenceValidator {
  /**
   * The default similarity ratio between the length and the distance.
   */
  private static final float SIMILARITY_RATIO = 0.3f;
  /**
   * The default threshold value for the length of Katakana word
   * to ignore.
   */
  private static final int MAX_IGNORE_KATAKANA_LENGTH = 3;
  /**
   * Katakana word dic with line number.
   */
  private HashMap<String, Integer> dic = new HashMap<String, Integer>();

  public List<ValidationError> check(Sentence sentence) {
    List<ValidationError> errors = new ArrayList<ValidationError>();
    List<ValidationError> result;
    StringBuilder katakana = new StringBuilder("");
    for (int i = 0; i < sentence.content.length(); i++) {
      char c = sentence.content.charAt(i);
      if (StringUtils.isKatakana(c)) {
        katakana.append(c);
      } else {
        result = this.checkKatakanaSpell(sentence, katakana.toString());
        if (result != null) {
          errors.addAll(result);
        }
        katakana.delete(0, katakana.length());
      }
    }
    result = checkKatakanaSpell(sentence, katakana.toString());
    if (result != null) {
      errors.addAll(result);
    }
    return errors;
  }

  private List<ValidationError> checkKatakanaSpell(Sentence sentence,
      String katakana) {
    if (katakana.length() <= MAX_IGNORE_KATAKANA_LENGTH) {
      return null;
    }
    if (dic.containsKey(katakana)) {
      return null;
    }
    final int minLsDistance =
      Math.round(katakana.length() * SIMILARITY_RATIO);
    boolean found = false;
    List<ValidationError> errors = new ArrayList<ValidationError>();
    for (String key : dic.keySet()) {
      if (LevenshteinDistance.getDistance(key, katakana) <= minLsDistance) {
        found = true;
        errors.add(new ValidationError(
          "Found a Katakana word: \"" + katakana + "\""
          + ", which is similar to \"" + key + "\""
          + " at postion " + dic.get(key).toString() + ".",
          sentence));
      }
    }
    if (!found) {
      dic.put(katakana, Integer.valueOf(sentence.position));
    }
    return errors;
  }

  public KatakanaSpellCheckValidator() {
    super();
  }

  public boolean initialize(ValidatorConfiguration conf,
      CharacterTable characterTable)
      throws DocumentValidatorException {
    //TODO: support the exception word list.
    //TODO: configurable SIMILARITY_RATIO.
    //TODO: configurable MAX_IGNORE_KATAKANA_LENGTH.
    return true;
  }

  private static Logger LOG =
      LoggerFactory.getLogger(KatakanaSpellCheckValidator.class);
}
