package cc.redpen.docvalidator.validator.sentence;

import cc.redpen.docvalidator.DocumentValidatorException;
import cc.redpen.docvalidator.ValidationError;
import cc.redpen.docvalidator.config.CharacterTable;
import cc.redpen.docvalidator.config.ValidatorConfiguration;
import cc.redpen.docvalidator.model.Sentence;
import cc.redpen.docvalidator.util.ResourceLoader;
import cc.redpen.docvalidator.util.WordListExtractor;
import cc.redpen.docvalidator.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SpellingValidator implements Validator<Sentence> {

  private static final String DEFAULT_RESOURCE_PATH = "default-resources/spellchecker";

  /**
   * Constructor.
   */
  public SpellingValidator() {
    this.validWords = new HashSet<>();
  }

  /**
   * Constructor.
   *
   * @param config Configuration object
   * @param characterTable  Character settings
   * @throws DocumentValidatorException
   */
  public SpellingValidator(ValidatorConfiguration config,
      CharacterTable characterTable)
      throws DocumentValidatorException {
    initialize(config, characterTable);
  }

  private boolean initialize(ValidatorConfiguration config,
      CharacterTable characterTable) {
    String lang = characterTable.getLang();
    WordListExtractor extractor = new WordListExtractor();
    extractor.setToLowerCase();
    ResourceLoader loader = new ResourceLoader(extractor);

    LOG.info("Loading default invalid expression dictionary for " +
        "\"" + lang + "\".");
    String defaultDictionaryFile = DEFAULT_RESOURCE_PATH
        + "/spellchecker-" + lang + ".dat";
    if (loader.loadInternalResource(defaultDictionaryFile)) {
      LOG.info("Succeeded to load default dictionary.");
    } else {
      LOG.info("Failed to load default dictionary.");
    }

    String confFile = config.getAttribute("dictionary");
    if (confFile == null || confFile.equals("")) {
      LOG.error("Dictionary file is not specified.");
    } else {
      LOG.info("user dictionary file is " + confFile);
      if (loader.loadExternalFile(confFile)) {
        LOG.info("Succeeded to load specified user dictionary.");
      } else {
        LOG.error("Failed to load user dictionary.");
      }
    }
    validWords = extractor.get();
    return true;
  }

  @Override
  public List<ValidationError> validate(Sentence line) {
    List<ValidationError> result = new ArrayList<>();
    String str = normalize(line);
    String[] words = str.split(" ");
    for (String word : words) {
      if (word.length() == 0) {
        continue;
      }

      if (!this.validWords.contains(word)) {
        result.add(new ValidationError(
            this.getClass(),
            "Found misspelled word: \"" + word + "\"", line));
      }
    }
    return result;
  }

  private String normalize(Sentence line) {
    StringBuilder builder = new StringBuilder(line.content.length());
    for (Character ch: line.content.toCharArray()) {
      if (!skipCharacters.contains(ch)) {
        builder.append(Character.toLowerCase(ch));
      }
    }
    return builder.toString();
  }

  /**
   * Register a word. This method is for testing purpose.
   *
   * @param word word to register a repelling dictionary
   */
  public void addWord(String word) {
    validWords.add(word);
  }

  // TODO: replace more memory efficient data structure
  private Set<String> validWords;

  private static final Logger LOG =
      LoggerFactory.getLogger(SpellingValidator.class);

  private static Character[] skipChars =
      new Character[] {'+', '~', '-', '(', ')', ',', '\"', '.' };

  private static Set<Character> skipCharacters =
      new HashSet<>(Arrays.asList(skipChars));
}
