package cc.redpen.validator;

import cc.redpen.RedPenException;
import cc.redpen.util.DictionaryLoader;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;
import static java.util.stream.Stream.concat;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Base class for dictionary-based validators.
 */
public abstract class DictionaryValidator extends Validator {
  protected DictionaryLoader<Set<String>> loader = WORD_LIST;
  private String dictionaryPrefix;
  private Set<String> dictionary = emptySet();

  public DictionaryValidator() {
    super("list", new HashSet<>(), "dict", "");
  }

  public DictionaryValidator(Object...keyValues) {
    this();
    addDefaultProperties(keyValues);
  }

  public DictionaryValidator(String dictionaryPrefix) {
    this();
    this.dictionaryPrefix = dictionaryPrefix;
  }

  public DictionaryValidator(DictionaryLoader<Set<String>> loader, String dictionaryPrefix) {
    this(dictionaryPrefix);
    this.loader = loader;
  }

  @Override protected void init() throws RedPenException {
    if (dictionaryPrefix != null) {
      String defaultDictionaryFile = "default-resources/" + dictionaryPrefix + "-" + getSymbolTable().getLang() + ".dat";
      dictionary = loader.loadCachedFromResource(defaultDictionaryFile, getClass().getSimpleName() + " default dictionary");
    }

    String confFile = getString("dict");
    if (isNotEmpty(confFile)) {
      getSet("list").addAll(loader.loadCachedFromFile(findFile(confFile), getClass().getSimpleName() + " user dictionary"));
    }
  }

  protected boolean inDictionary(String word) {
    Set<String> customDictionary = getSet("list");
    return dictionary.contains(word) || customDictionary != null && customDictionary.contains(word);
  }

  protected Stream<String> streamDictionary() {
    return concat(dictionary.stream(), getSet("list").stream());
  }
}
