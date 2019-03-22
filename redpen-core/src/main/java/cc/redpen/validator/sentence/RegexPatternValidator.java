package cc.redpen.validator.sentence;

import cc.redpen.RedPenException;
import cc.redpen.model.Sentence;
import cc.redpen.validator.DictionaryValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexPatternValidator extends DictionaryValidator {
    private final List<Pattern> patterns = new ArrayList<Pattern>();

    @Override
    public void validate(Sentence sentence) {
        for (Pattern pattern : patterns) {
            this.detectPattern(sentence, pattern);
        }
    }

    @Override
    protected void init() throws RedPenException {
        super.init();
        streamDictionary().forEach(value -> {
            patterns.add(Pattern.compile(value));
        });
    }

    private void detectPattern(Sentence sentence, Pattern pattern) {
        Matcher mat = pattern.matcher(sentence.getContent());
        while(mat.find()){
            addLocalizedErrorWithPosition(sentence,
                    mat.start(),
                    mat.end(),
                    mat.group());
        }
    }
}
