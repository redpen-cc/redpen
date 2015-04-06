package cc.redpen.validator.sentence;

import cc.redpen.model.Sentence;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JapaneseStyleValidator extends Validator {
    private static final Pattern futuuPattern = Pattern.compile("のだが|したが|したので|ないかと");
    private static final Pattern teineiPattern = Pattern.compile("でしたが|でしたので|ですので|ですが");
    private static final Pattern futuuEndPattern = Pattern.compile("(だ|である|った|ではない｜ないか)$");
    private static final Pattern teineiEndPattern = Pattern.compile("(です|ます|ました|ません|ですね|でしょうか)$");

    private int futuuCount = 0;
    private int teineiCount = 0;

    @Override
    public void preValidate(Sentence sentence) {
        // match content
        futuuCount += countMatch(sentence, futuuPattern);
        teineiCount+= countMatch(sentence, teineiPattern);

        // match end content
        futuuCount += countEndMatch(sentence, futuuEndPattern);
        teineiCount += countEndMatch(sentence, teineiEndPattern);
    }

    private int countMatch(Sentence sentence, Pattern pattern) {
        String content = sentence.getContent();
        Matcher mat = pattern.matcher(content);
        int count = 0;
        while(mat.find()){
            count +=1;
        }
        return count;
    }

    private int countEndMatch(Sentence sentence, Pattern pattern) {
        String content = sentence.getContent();
        if (content.length() < 2) {
            return 0;
        }
        int count = 0;
        Matcher mat = pattern.matcher(content);
        mat.region(0, content.length()-2);
        while(mat.find()){
            count +=1;
        }
        return count;
    }

    @Override
    public void validate(List<ValidationError> errors, Sentence sentence) {
        if (futuuCount > teineiCount) {
            detectPattern(sentence, teineiPattern, errors);
            detectPattern(sentence, teineiEndPattern, errors);
        } else {
            detectPattern(sentence, futuuPattern, errors);
            detectPattern(sentence, futuuEndPattern, errors
            );
        }
    }

    private void detectPattern(Sentence sentence, Pattern pattern, List<ValidationError> errors) {
        Matcher mat = pattern.matcher(sentence.getContent());
        while(mat.find()){
            errors.add(createValidationErrorWithPosition(sentence,
                    sentence.getOffset(mat.start()),
                    sentence.getOffset(mat.end()),
                    mat.group()));
        }
    }
}
