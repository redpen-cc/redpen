package cc.redpen.validator.sentence;

import cc.redpen.model.Sentence;
import cc.redpen.validator.Validator;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.singletonList;

public class HankakuKanaValidator extends Validator {
    static Pattern pattern = Pattern.compile("[\\uFF65-\\uFF9F\\s-]");

    public HankakuKanaValidator() {}

    @Override
    public List<String> getSupportedLanguages() {
        return singletonList(Locale.JAPANESE.getLanguage());
    }

    @Override
    public void validate(Sentence sentence) {
        Matcher matcher = pattern.matcher(sentence.getContent());
        while (matcher.find()) {
            addLocalizedError(sentence,
                    sentence.getContent().charAt(matcher.start()));
        }
    }
}
