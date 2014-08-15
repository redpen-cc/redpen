/**
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.redpen.validator.sentence;

import cc.redpen.DocumentValidatorException;
import cc.redpen.ValidationError;
import cc.redpen.config.SymbolTable;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Sentence;
import cc.redpen.util.StringUtils;
import cc.redpen.validator.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Validate the end hyphens of Katakana words in Japanese documents.
 * Japanese Katakana words have variations in end hyphen.
 * For example, "computer" is written in Katakana by
 * "コンピュータ (without hyphen) ", and "コンピューター (with hypen) ".
 * This validator validate if Katakana words ending format is match
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
public class KatakanaEndHyphenValidator implements Validator<Sentence> {
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

  public KatakanaEndHyphenValidator(ValidatorConfiguration config,
                                    SymbolTable symbolTable)
      throws DocumentValidatorException {
    initialize(config, symbolTable);
  }

  public List<ValidationError> validate(Sentence sentence) {
    List<ValidationError> errors = new ArrayList<>();
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
    List<ValidationError> errors = new ArrayList<>();
    if (isKatakanaEndHyphen(katakana)) {
      errors.add(new ValidationError(
          this.getClass(),
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

  private boolean initialize(
    ValidatorConfiguration conf, SymbolTable symbolTable)
        throws DocumentValidatorException {
    //TODO support exception word list.
    return true;
  }

}
