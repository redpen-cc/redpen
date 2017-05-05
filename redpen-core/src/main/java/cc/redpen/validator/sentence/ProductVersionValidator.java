package cc.redpen.validator.sentence;

import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.TokenElement;
import cc.redpen.validator.Validator;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validator to reduce validation error about duplicated period
 * (for example version number) in sentence.
 */
public class ProductVersionValidator extends Validator {
    private static final Logger LOG = LoggerFactory.getLogger(ProductVersionValidator.class);
    private static final Pattern PRODUCT_VERSION = Pattern.compile("\\s(\\d*\\.\\d+)(\\.\\d+)?");

    public ProductVersionValidator() {}

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList(Locale.JAPANESE.getLanguage());
    }

    @Override
    public void validate(Sentence sentence) {
        String content = sentence.getContent();
        Matcher matcher = PRODUCT_VERSION.matcher(content);
        List<TokenElement> tokens = sentence.getTokens();
        int tokenIndex = 0;
        while(matcher.find()) {
            int tokenStartOffset = matcher.start() + 1;
            String word = content.substring(matcher.start() + 1, matcher.end());
            tokenIndex = searchTokenIndexByOffset(tokens, tokenStartOffset);
            List<String> tags = tokens.get(tokenIndex).getTags();
            TokenElement mergedToken = new TokenElement(word, tags, tokenStartOffset);
            for (int i = 0; i < word.length(); i++) {
                tokens.remove(tokenIndex);
            }
            tokens.add(tokenIndex, mergedToken);
        }
        sentence.setTokens(tokens);
    }

    private int searchTokenIndexByOffset(List<TokenElement> tokens, int offset) {
        int index = 0;
        int i = 0;
        for (TokenElement token : tokens) {
            if (token.getOffset() == offset) {
                index = i;
                break;
            }
            i++;
        }
        return index;
    }
}
