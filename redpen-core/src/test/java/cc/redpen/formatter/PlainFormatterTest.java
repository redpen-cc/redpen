package cc.redpen.formatter;

import cc.redpen.model.Document;
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.WhiteSpaceTokenizer;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PlainFormatterTest extends Validator {
    @Test
    public void testConvertValidationError() {
        ValidationError error = createValidationError(new Sentence("This is a sentence", 0));
        Formatter formatter = new PlainFormatter();
        Document document = new cc.redpen.model.Document.DocumentBuilder(new WhiteSpaceTokenizer())
                .setFileName("foobar.md").build();
        List<ValidationError> validationErrors = Arrays.asList(error);
        String resultString = formatter.format(document, validationErrors);
        assertEquals("foobar.md:0: ValidationError[PlainFormatterTest], plain test error at line: This is a sentence\n", resultString);
    }

    @Test
    public void testConvertValidationErrorWithoutFileName() {
        ValidationError error = createValidationError(new Sentence("This is a sentence", 0));
        Formatter formatter = new PlainFormatter();
        Document document = new cc.redpen.model.Document.DocumentBuilder(new WhiteSpaceTokenizer()).build();
        List<ValidationError> validationErrors = Arrays.asList(error);
        String resultString = formatter.format(document, validationErrors);
        assertEquals("0: ValidationError[PlainFormatterTest], plain test error at line: This is a sentence\n", resultString);
    }
}
