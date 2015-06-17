package cc.redpen.validator;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.model.Section;
import cc.redpen.model.Sentence;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Created by yusuke on 6/12/15.
 * <p>
 * Copyright 2015 yusuke
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
public class JavaScriptValidatorTest extends JavaScriptValidator {
    @Test
    public void testLoadFile() throws Exception {
        File file = File.createTempFile("test", "txt");
        String content = "hello\nred\npen.";
        Files.write(Paths.get(file.getAbsolutePath()), content.getBytes(Charset.forName("UTF-8")));
        String loadCached = JavaScriptValidator.loadCached(file);
        assertEquals(content, loadCached);

        String content2 = "hello\nred\npen.\nmodified\n";
        Files.write(Paths.get(file.getAbsolutePath()), content2.getBytes(Charset.forName("UTF-8")));
        // ensure the modified date differs
        file.setLastModified(System.currentTimeMillis() + 2000);

        String loadCached2 = JavaScriptValidator.loadCached(file);
        assertEquals(content2, loadCached2);
    }

    @Test
    public void testFileJSValidator() throws RedPenException, IOException {
        File javaScriptValidatorsDir = File.createTempFile("test", "js");
        // delete the temporary file, make a directory, and store JavaScript validator in it
        javaScriptValidatorsDir.delete();
        javaScriptValidatorsDir.mkdirs();
        File validatorJS = new File(javaScriptValidatorsDir.getAbsolutePath() + File.separator + "MyValidator.js");
        String content2 = "function validateSentence(validator, errors, sentence) {\n" +
                "errors.add(validator.createValidationError(sentence, 'validation error in JavaScript Validator'));}";
        Files.write(Paths.get(validatorJS.getAbsolutePath()), content2.getBytes(Charset.forName("UTF-8")));
        validatorJS.deleteOnExit();

        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("JavaScript").addAttribute("validator-path", javaScriptValidatorsDir.getAbsolutePath()))
                .build();

        Document document = new Document.DocumentBuilder()
                .addSection(1)
                .addParagraph()
                .addSentence("the good item is a good example.", 1)
                .build();

        RedPen redPen = new RedPen(config);
        List<ValidationError> errors = redPen.validate(document);
        assertEquals(1, errors.size());
        assertEquals("JavaScript validator validation error in JavaScript Validator", errors.get(0).getMessage());
    }

    private List<String> calledFunctions;

    @Test
    public void testJSLiteralValidator() throws RedPenException, IOException {
        JavaScriptValidator.JSValidator validator = new JavaScriptValidator.JSValidator(
                "function preValidateSentence(validator, sentence) { validator.markCalled('preValidateSentence');}" +
                        "function preValidateSection(validator, section) {validator.markCalled('preValidateSection');}" +
                        "function validateDocument(validator, errors, document) {validator.markCalled('validateDocument');" +
                        "errors.add(validator.createValidationError(document.getSection(0).getHeaderContent(0), 'doc'));}" +
                        "function validateSentence(validator, errors, sentence) {validator.markCalled('validateSentence');" +
                        "errors.add(validator.createValidationError(sentence, 'sentence'));}" +
                        "function validateSection(validator, errors, section) {validator.markCalled('validateSection');" +
                        "errors.add(validator.createValidationError(section.getHeaderContent(0), 'section'));}");
        calledFunctions = new ArrayList<>();
        Document document = new Document.DocumentBuilder()
                .addSection(1)
                .addParagraph()
                .addSentence("the good item is a good example.", 1)
                .build();
        Section section = document.getSection(0);
        Sentence sentence = section.getHeaderContent(0);
        validator.preValidate(this, sentence);
        validator.preValidate(this, section);
        validator.validate(this, errors, document);
        validator.validate(this, errors, sentence);
        validator.validate(this, errors, section);
        assertEquals(5, calledFunctions.size());
        assertEquals("preValidateSentence", calledFunctions.get(0));
        assertEquals("preValidateSection", calledFunctions.get(1));
        assertEquals("validateDocument", calledFunctions.get(2));
        assertEquals("validateSentence", calledFunctions.get(3));
        assertEquals("validateSection", calledFunctions.get(4));

        assertEquals(3, errors.size());
        assertEquals("js validator doc", errors.get(0).getMessage());
        assertEquals("js validator sentence", errors.get(1).getMessage());
        assertEquals("js validator section", errors.get(2).getMessage());
    }

    ArrayList<ValidationError> errors = new ArrayList<>();

    public void markCalled(String msg) {
        calledFunctions.add(msg);
    }
}
