package cc.redpen.validator.sentence;

import cc.redpen.RedPenException;
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.TokenElement;
import cc.redpen.util.ResourceLoader;
import cc.redpen.util.WordListExtractor;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class SpellingValidator extends Validator<Sentence> {

    private static final String DEFAULT_RESOURCE_PATH = "default-resources/spellchecker";
    private static final Logger LOG =
            LoggerFactory.getLogger(SpellingValidator.class);
    private static Character[] skipChars =
            new Character[]{'+', '~', '-', '(', ')', ',', '\"', '.'};
    private static Set<Character> skipCharacters =
            new HashSet<>(Arrays.asList(skipChars));
    // TODO: replace more memory efficient data structure
    private Set<String> validWords = new HashSet<>();

    @Override
    protected void init() throws RedPenException {
        String lang = getSymbolTable().getLang();
        WordListExtractor extractor = new WordListExtractor();
        extractor.setToLowerCase();
        ResourceLoader loader = new ResourceLoader(extractor);

        LOG.info("Loading default invalid expression dictionary for " +
                "\"" + lang + "\".");
        String defaultDictionaryFile = DEFAULT_RESOURCE_PATH
                + "/spellchecker-" + lang + ".dat";
        try {
            loader.loadInternalResource(defaultDictionaryFile);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            LOG.info("Failed to load default dictionary.");
            throw new RedPenException(e);
        }
        LOG.info("Succeeded to load default dictionary.");

        Optional<String> userDictionaryFile = getConfigAttribute("dict");
        userDictionaryFile.ifPresent(f -> {
            LOG.info("user dictionary file is " + f);
            try {
                loader.loadExternalFile(f);
            } catch (IOException e) {
                LOG.error("Failed to load user dictionary.");
                return;
            }
            LOG.info("Succeeded to load specified user dictionary.");
        });
        validWords = extractor.get();
    }

    @Override
    public List<ValidationError> validate(Sentence line) {
        List<ValidationError> validationErrors = new ArrayList<>();
        for (TokenElement token : line.tokens) {
            String surface = normalize(token.getSurface());
            if (surface.length() == 0) {
                continue;
            }

            if (!this.validWords.contains(surface)) {
                validationErrors.add(createValidationError(line));
            }
        }
        return validationErrors;
    }

    private String normalize(String line) {
        StringBuilder builder = new StringBuilder(line.length());
        for (Character ch : line.toCharArray()) {
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
