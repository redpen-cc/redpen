package cc.redpen.validator.sentence;

import cc.redpen.model.Sentence;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HankakuKanaValidator extends Validator {
    static Pattern pattern = Pattern.compile("[\\uFF65-\\uFF9F\\s-]");

    public HankakuKanaValidator() {}

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList(Locale.JAPANESE.getLanguage());
    }

    @Override
    public void validate(List<ValidationError> errors, Sentence sentence) {
        Matcher matcher = pattern.matcher(sentence.getContent());
        while (matcher.find()) {
            errors.add(createValidationError(sentence,
                    sentence.getContent().charAt(matcher.start())));
        }
    }
}
