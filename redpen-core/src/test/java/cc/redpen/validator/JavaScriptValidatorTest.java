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
import cc.redpen.parser.DocumentParser;
import cc.redpen.parser.SentenceExtractor;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JavaScriptValidatorTest extends JavaScriptValidator {
    @Test
    public void doNotCrashIfJsDirectoryDoesNotExist() throws Exception {
        JavaScriptValidator validator = new JavaScriptValidator();
        validator.preInit(new ValidatorConfiguration("JavaScript"), Configuration.builder().build());
        validator.init();
        assertTrue(validator.scripts.isEmpty());
    }

    @Test
    public void testLoadFile() throws Exception {
        File file = File.createTempFile("test", "txt");
        String content = "hello\nred\npen.";
        Files.write(Paths.get(file.getAbsolutePath()), content.getBytes(UTF_8));
        String loadCached = JavaScriptValidator.loadCached(file);
        assertEquals(content, loadCached);

        String content2 = "hello\nred\npen.\nmodified\n";
        Files.write(Paths.get(file.getAbsolutePath()), content2.getBytes(UTF_8));
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
        System.setProperty("REDPEN_HOME", javaScriptValidatorsDir.getAbsolutePath());
        File validatorJS = new File(javaScriptValidatorsDir.getAbsolutePath() + File.separator + "MyValidator.js");
        String content2 = "function validateSentence(sentence) {\n" +
                "addLocalizedError(sentence, 'validation error in JavaScript Validator');}";
        Files.write(Paths.get(validatorJS.getAbsolutePath()), content2.getBytes(UTF_8));
        validatorJS.deleteOnExit();

        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("JavaScript").addProperty("script-path", javaScriptValidatorsDir.getAbsolutePath()))
                .build();

        Document document = Document.builder()
                .addSection(1)
                .addParagraph()
                .addSentence(new Sentence("the good item is a good example.", 1))
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
                        "_JavaScriptValidatorTest.calledFunctions.add('preValidateSentence');}" +
                        "function preValidateSection(section) {" +
                        "_JavaScriptValidatorTest.calledFunctions.add('preValidateSection');}" +
                        "function validateDocument(document) {" +
                        "_JavaScriptValidatorTest.calledFunctions.add('validateDocument');" +
                        // add ValidationError
                        "addError('validation error', document.getSection(0).getHeaderContent(0));" +
                        // add ValidationError
                        "addLocalizedError(document.getSection(0).getHeaderContent(0), 'doc');}" +
                        "function validateSentence(sentence) {" +
                        "_JavaScriptValidatorTest.calledFunctions.add('validateSentence');" +
                        // add ValidationError
                        "addLocalizedError(sentence, 'sentence');}" +
                        "function validateSection(section) {" +
                        "_JavaScriptValidatorTest.calledFunctions.add('validateSection');" +
                        // add ValidationError
                        "addLocalizedError(section.getHeaderContent(0), 'section');}"));
        Document document = Document.builder()
                .addSection(1)
                .addParagraph()
                .addSentence(new Sentence("the good item is a good example.", 1))
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

        assertEquals(4, errors.size());
        assertEquals("[testScript.js] validation error", errors.get(0).getMessage());
        assertEquals("[testScript.js] JavaScript validator doc", errors.get(1).getMessage());
        assertEquals("[testScript.js] JavaScript validator sentence", errors.get(2).getMessage());
        assertEquals("[testScript.js] JavaScript validator section", errors.get(3).getMessage());
    }

    @Test
    public void testEmbeddedmessage() throws RedPenException, IOException {
        JavaScriptValidator validator = new JavaScriptValidator();
        validator.scripts.add(new Script(validator, "testScript.js",
                "var message = 'embedded message {0}';" +
                        "function validateSentence(sentence) {" +
                        // add ValidationError
                        "addLocalizedError(sentence, '[placeholder]');}"));
        Document document = Document.builder()
                .addSection(1)
                .addParagraph()
                .addSentence(new Sentence("the good item is a good example.", 1))
                .build();
        Section section = document.getSection(0);
        Sentence sentence = section.getHeaderContent(0);

        calledFunctions = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(sentence);
        assertEquals(1, errors.size());
        assertEquals("[testScript.js] embedded message [placeholder]", errors.get(0).getMessage());
    }

    @Test
    public void testErrorSuppressionErrorFromJavaScriptValidator() throws RedPenException, IOException {
        File javaScriptValidatorsDir = File.createTempFile("test", "js");
        javaScriptValidatorsDir.delete();
        javaScriptValidatorsDir.mkdirs();
        System.setProperty("REDPEN_HOME", javaScriptValidatorsDir.getAbsolutePath());
        File validatorJS = new File(javaScriptValidatorsDir.getAbsolutePath() + File.separator + "MyValidator.js");
        String content2 = "function validateSentence(sentence) {\n" +
                "addLocalizedError(sentence, 'validation error in JavaScript Validator');}";
        Files.write(Paths.get(validatorJS.getAbsolutePath()), content2.getBytes(UTF_8));
        validatorJS.deleteOnExit();

        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("JavaScript").addProperty("script-path", javaScriptValidatorsDir.getAbsolutePath()))
                .build();

        String sampleAsciiDocShortText =
                "[suppress]\n" +
                "the good item is a good example.\n";
        Document doc = createFileContent(sampleAsciiDocShortText, DocumentParser.ASCIIDOC);
        RedPen redPen = new RedPen(config);
        List<ValidationError> errors = redPen.validate(doc);
        assertEquals(0, errors.size());
        assertEquals("[MyValidator.js] JavaScript validator validation error in JavaScript Validator", errors.get(0).getMessage());
    }

/*
    @Test
    public void testErrorSuppressionErrorFromSPecifiedJavaScriptValidator() throws RedPenException, IOException {
        File javaScriptValidatorsDir = File.createTempFile("test", "js");
        javaScriptValidatorsDir.delete();
        javaScriptValidatorsDir.mkdirs();
        System.setProperty("REDPEN_HOME", javaScriptValidatorsDir.getAbsolutePath());
        File validatorJS = new File(javaScriptValidatorsDir.getAbsolutePath() + File.separator + "MyValidator.js");
        String content2 =
                "function validateSentence(sentence) {\n" +
                "addLocalizedError(sentence, 'validation error in JavaScript Validator');}";
        Files.write(Paths.get(validatorJS.getAbsolutePath()), content2.getBytes(UTF_8));
        validatorJS.deleteOnExit();

        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("JavaScript").
                        addProperty("script-path", javaScriptValidatorsDir.getAbsolutePath()))
                .build();

        String sampleAsciiDocShortText =
                "[suppress='MyValidator']\n" +
                "the good item is a good example.\n";
        Document doc = createFileContent(sampleAsciiDocShortText, DocumentParser.ASCIIDOC);
        RedPen redPen = new RedPen(config);
        List<ValidationError> errors = redPen.validate(doc);
        assertEquals(0, errors.size());
        assertEquals("[MyValidator.js] JavaScript validator validation error in JavaScript Validator", errors.get(0).getMessage());
    }
*/

    @Test
    public void testJSValidatorIsConfinedByDefault() throws RedPenException, IOException {
        JavaScriptValidator validator = new JavaScriptValidator();
        validator.scripts.add(new Script(validator, "testScript.js",
                                         "function validateSentence(sentence) {"
                                         + "if (java || javax || Java || redpenToBeBound || load) {"
                                         + "addLocalizedError(sentence, 'runtime environment is NOT confined');"
                                         + "} else {"
                                         + "addLocalizedError(sentence, 'runtime environment is confined');"
                                         + "}"
                                         + "}"));
        Document document = Document.builder()
                .addSection(1)
                .addParagraph()
                .addSentence(new Sentence("the good item is a good example.", 1))
                .build();
        Section section = document.getSection(0);
        Sentence sentence = section.getHeaderContent(0);

        calledFunctions = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(sentence);
        assertEquals(1, errors.size());
        assertEquals("[testScript.js] JavaScript validator runtime environment is confined", errors.get(0).getMessage());
    }

    ArrayList<ValidationError> errors = new ArrayList<>();


    private Document createFileContent(String inputDocumentString, DocumentParser parser) {
        Document doc = null;
        try {
            Configuration configuration = Configuration.builder().build();
            doc = parser.parse(
                    inputDocumentString,
                    new SentenceExtractor(configuration.getSymbolTable()),
                    configuration.getTokenizer());
        } catch (RedPenException e) {
            e.printStackTrace();
            fail();
        }
        return doc;
    }
}
