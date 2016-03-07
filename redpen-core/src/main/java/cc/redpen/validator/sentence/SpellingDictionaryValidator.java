package cc.redpen.validator.sentence;

import cc.redpen.validator.DictionaryValidator;

/**
 * Base class for Validators using the spelling dictionary.
 */
public abstract class SpellingDictionaryValidator extends DictionaryValidator {
  public SpellingDictionaryValidator() {
    super(WORD_LIST_LOWERCASED, "spellchecker/spellchecker");
  }
}
