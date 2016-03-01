package cc.redpen.validator;

import cc.redpen.RedPenException;
import cc.redpen.util.DictionaryLoader;

import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.emptySet;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Base class for dictionary-based validators.
 */
public abstract class DictionaryValidator extends Validator {
  protected DictionaryLoader<Set<String>> loader = WORD_LIST;
  protected Set<String> dictionary = emptySet();
  private String dictionaryPrefix;

  public DictionaryValidator() {
    super("list", new HashSet<>(), "dict", "");
  }

  public DictionaryValidator(Object...attributes) {
    this();
    addDefaultAttributes(attributes);
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

    String confFile = getStringAttribute("dict");
    if (isNotEmpty(confFile)) {
      getSetAttribute("list").addAll(loader.loadCachedFromFile(findFile(confFile), getClass().getSimpleName() + " user dictionary"));
    }
  }
}
