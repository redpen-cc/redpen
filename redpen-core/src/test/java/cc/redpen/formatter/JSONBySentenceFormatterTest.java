package cc.redpen.formatter;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.model.Sentence;
import cc.redpen.parser.DocumentParser;
import cc.redpen.parser.SentenceExtractor;
import cc.redpen.tokenizer.WhiteSpaceTokenizer;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JSONBySentenceFormatterTest extends Validator {
    @Test
    public void testFormat() throws JSONException {
        JSONFormatter formatter = new JSONBySentenceFormatter();
        List<ValidationError> errors = new ArrayList<>();
        errors.add(createValidationError(new Sentence("testing JSONFormatter", 1)));
        Document document = new Document.DocumentBuilder().setFileName("docName").build();
        String result = formatter.format(document, errors);

        JSONObject jsonObject = new JSONObject(result);
        String docName = jsonObject.getString("document");
        assertEquals("docName", docName);
        JSONArray jsonErrors = jsonObject.getJSONArray("errors");
        assertNotNull(jsonErrors);
        assertEquals(1, jsonErrors.length());
        JSONObject sentenceErrors = jsonErrors.getJSONObject(0);
        assertEquals("testing JSONFormatter", sentenceErrors.getString("sentence"));
        assertEquals(0, sentenceErrors.getJSONObject("position").getJSONObject("start").getInt("offset"));
        assertEquals(1, sentenceErrors.getJSONObject("position").getJSONObject("start").getInt("line"));
        assertEquals(20, sentenceErrors.getJSONObject("position").getJSONObject("end").getInt("offset"));
        assertEquals(1, sentenceErrors.getJSONObject("position").getJSONObject("end").getInt("line"));
        assertNotNull(sentenceErrors.getJSONArray("errors"));
        assertEquals(1, sentenceErrors.getJSONArray("errors").length());
        JSONObject error = sentenceErrors.getJSONArray("errors").getJSONObject(0);
        assertEquals("json by sentence test error", error.getString("message"));
        assertEquals("JSONBySentenceFormatterTest", error.getString("validator"));
    }

    @Test
    public void testFormatErrorsFromMarkdownParser() throws RedPenException, JSONException {
        String sampleText = "This is a good day。"; // invalid end of sentence symbol
        Configuration conf = new Configuration.ConfigurationBuilder()
                .setLanguage("en")
                .build();
        Configuration configuration = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(
                        new ValidatorConfiguration("InvalidSymbol"))
                .build();

        List<Document> documents = new ArrayList<>();
        DocumentParser parser = DocumentParser.MARKDOWN;
        documents.add(parser.parse(sampleText,
                new SentenceExtractor(conf.getSymbolTable()), conf.getTokenizer()));
        RedPen redPen = new RedPen(configuration);
        List<ValidationError> errors = redPen.validate(documents).get(documents.get(0));

        JSONFormatter formatter = new JSONBySentenceFormatter();
        String resultString = formatter.format(new cc.redpen.model.Document.DocumentBuilder(
                new WhiteSpaceTokenizer()).build(), errors);
        JSONObject jsonObject = new JSONObject(resultString);
        JSONArray jsonErrors = jsonObject.getJSONArray("errors");
        assertEquals(1, jsonErrors.length());
        JSONObject sentenceErrors = jsonErrors.getJSONObject(0);
        assertEquals("This is a good day。", sentenceErrors.getString("sentence"));
        assertEquals(0, sentenceErrors.getJSONObject("position").getJSONObject("start").getInt("offset"));
        assertEquals(1, sentenceErrors.getJSONObject("position").getJSONObject("start").getInt("line"));
        assertEquals(18, sentenceErrors.getJSONObject("position").getJSONObject("end").getInt("offset"));
        assertEquals(1, sentenceErrors.getJSONObject("position").getJSONObject("end").getInt("line"));
        assertNotNull(sentenceErrors.getJSONArray("errors"));
        assertEquals(1, sentenceErrors.getJSONArray("errors").length());
        JSONObject error = sentenceErrors.getJSONArray("errors").getJSONObject(0);
        assertEquals("Found invalid symbol \"。\".", error.getString("message"));
        assertEquals("InvalidSymbol", error.getString("validator"));

    }
}
