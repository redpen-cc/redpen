package cc.redpen.validator.sentence;

import cc.redpen.RedPenException;
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.TokenElement;
import cc.redpen.util.WordListExtractor;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class SpellingValidator extends Validator {

    private static final String DEFAULT_RESOURCE_PATH = "default-resources/spellchecker";
    private static final Logger LOG =
            LoggerFactory.getLogger(SpellingValidator.class);
    private static String skipCharacters = "+~-(),\".";
    // TODO: replace more memory efficient data structure
    private Set<String> validWords = new HashSet<>();

    @Override
    protected void init() throws RedPenException {
        String lang = getSymbolTable().getLang();
        WordListExtractor extractor = new WordListExtractor();
        extractor.setToLowerCase();

        LOG.info("Loading default invalid expression dictionary for " +
                "\"" + lang + "\".");
        String defaultDictionaryFile = DEFAULT_RESOURCE_PATH
                + "/spellchecker-" + lang + ".dat";
        try {
            extractor.loadFromResource(defaultDictionaryFile);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            LOG.info("Failed to load default dictionary.");
            throw new RedPenException(e);
        }
        LOG.info("Succeeded to load default dictionary.");

        Optional<String> listStr = getConfigAttribute("list");
        listStr.ifPresent(f -> {
            LOG.info("User defined valid word list found.");
            validWords.addAll(Arrays.asList(f.split(",")));
            LOG.info("Succeeded to add elements of user defined list.");
        });

        Optional<String> userDictionaryFile = getConfigAttribute("dict");
        userDictionaryFile.ifPresent(f -> {
            LOG.info("user dictionary file is " + f);
            try {
                extractor.load(new FileInputStream(f));
            } catch (IOException e) {
                LOG.error("Failed to load user dictionary.");
                return;
            }
            LOG.info("Succeeded to load specified user dictionary.");
        });
        validWords.addAll(extractor.get());
    }

    @Override
    public void validate(List<ValidationError> errors, Sentence sentence) {
        for (TokenElement token : sentence.getTokens()) {
            String surface = normalize(token.getSurface());
            if (surface.length() == 0) {
                continue;
            }

            if (!this.validWords.contains(surface)) {
                errors.add(createValidationError(sentence, surface));
            }
        }
    }

    private String normalize(String line) {
        StringBuilder builder = new StringBuilder(line.length());
        for (char ch : line.toCharArray()) {
            if (skipCharacters.indexOf(ch) == -1) {
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

    @Override
    public String toString() {
        return "SpellingValidator{" +
                "validWords=" + validWords +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpellingValidator that = (SpellingValidator) o;

        return !(validWords != null ? !validWords.equals(that.validWords) : that.validWords != null);

    }

    @Override
    public int hashCode() {
        return validWords != null ? validWords.hashCode() : 0;
    }
}
