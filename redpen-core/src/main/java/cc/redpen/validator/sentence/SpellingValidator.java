package cc.redpen.validator.sentence;

import cc.redpen.RedPenException;
import cc.redpen.ValidationError;
import cc.redpen.model.Sentence;
import cc.redpen.util.ResourceLoader;
import cc.redpen.util.WordListExtractor;
import cc.redpen.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

        Optional<String> userDictionaryFile = getConfigAttribute("dictionary");
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
        for (Character ch : line.content.toCharArray()) {
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
}
