/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        String content2 = "function validateSentence(sentence) {\n" +
                "addValidationError(sentence, 'validation error in JavaScript Validator');}";
        Files.write(Paths.get(validatorJS.getAbsolutePath()), content2.getBytes(Charset.forName("UTF-8")));
        validatorJS.deleteOnExit();

        Configuration config = new Configuration.ConfigurationBuilder()
                .addValidatorConfig(new ValidatorConfiguration("JavaScript").addAttribute("script-path", javaScriptValidatorsDir.getAbsolutePath()))
                .build();

        Document document = new Document.DocumentBuilder()
                .addSection(1)
                .addParagraph()
                .addSentence("the good item is a good example.", 1)
                .build();

        RedPen redPen = new RedPen(config);
        List<ValidationError> errors = redPen.validate(document);
        assertEquals(1, errors.size());
        assertEquals("[MyValidator.js] JavaScript validator validation error in JavaScript Validator", errors.get(0).getMessage());
    }

    public static List<String> calledFunctions;

    @Test
    public void testJSLiteralValidator() throws RedPenException, IOException {
        JavaScriptValidator validator = new JavaScriptValidator();
        validator.scripts.add(new Script(validator, "testScript.js",
                "function preValidateSentence(sentence) {" +
                        // add function names to "calledFunctions" list upon function calls for the later assertions
                        // the following script is using Nashorn's lobal object "Java".type to access static member:
                        // http://docs.oracle.com/javase/8/docs/technotes/guides/scripting/nashorn/api.html
                        "Java.type('cc.redpen.validator.JavaScriptValidatorTest').calledFunctions.add('preValidateSentence');}" +
                        "function preValidateSection(section) {" +
                        "Java.type('cc.redpen.validator.JavaScriptValidatorTest').calledFunctions.add('preValidateSection');}" +
                        "function validateDocument(document) {" +
                        "Java.type('cc.redpen.validator.JavaScriptValidatorTest').calledFunctions.add('validateDocument');" +
                        // add ValidationError
                        "addValidationError(document.getSection(0).getHeaderContent(0), 'doc');}" +
                        "function validateSentence(sentence) {" +
                        "Java.type('cc.redpen.validator.JavaScriptValidatorTest').calledFunctions.add('validateSentence');" +
                        // add ValidationError
                        "addValidationError(sentence, 'sentence');}" +
                        "function validateSection(section) {" +
                        "Java.type('cc.redpen.validator.JavaScriptValidatorTest').calledFunctions.add('validateSection');" +
                        // add ValidationError
                        "addValidationError(section.getHeaderContent(0), 'section');}"));
        Document document = new Document.DocumentBuilder()
                .addSection(1)
                .addParagraph()
                .addSentence("the good item is a good example.", 1)
                .build();
        Section section = document.getSection(0);
        Sentence sentence = section.getHeaderContent(0);

        calledFunctions = new ArrayList<>();
        validator.setErrorList(errors);
        validator.preValidate(sentence);
        validator.preValidate(section);
        validator.validate(document);
        validator.validate(sentence);
        validator.validate(section);
        assertEquals(5, calledFunctions.size());
        assertEquals("preValidateSentence", calledFunctions.get(0));
        assertEquals("preValidateSection", calledFunctions.get(1));
        assertEquals("validateDocument", calledFunctions.get(2));
        assertEquals("validateSentence", calledFunctions.get(3));
        assertEquals("validateSection", calledFunctions.get(4));

        assertEquals(3, errors.size());
        assertEquals("[testScript.js] JavaScript validator doc", errors.get(0).getMessage());
        assertEquals("[testScript.js] JavaScript validator sentence", errors.get(1).getMessage());
        assertEquals("[testScript.js] JavaScript validator section", errors.get(2).getMessage());
    }

    @Test
    public void testEmbeddedmessage() throws RedPenException, IOException {
        JavaScriptValidator validator = new JavaScriptValidator();
        validator.scripts.add(new Script(validator, "testScript.js",
                "var message = 'embedded message {0}';" +
                        "function validateSentence(sentence) {" +
                        // add ValidationError
                        "addValidationError(sentence, '[placeholder]');}"));
        Document document = new Document.DocumentBuilder()
                .addSection(1)
                .addParagraph()
                .addSentence("the good item is a good example.", 1)
                .build();
        Section section = document.getSection(0);
        Sentence sentence = section.getHeaderContent(0);

        calledFunctions = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(sentence);
        assertEquals(1, errors.size());
        assertEquals("[testScript.js] embedded message [placeholder]", errors.get(0).getMessage());
    }

    ArrayList<ValidationError> errors = new ArrayList<>();

    public void markCalled(String msg) {
        calledFunctions.add(msg);
    }
}
