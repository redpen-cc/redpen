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
package cc.redpen.validator.sentence;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.model.Sentence;
import cc.redpen.validator.ValidationError;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class KatakanaEndHyphenValidatorTest {
    private KatakanaEndHyphenValidator validator = new KatakanaEndHyphenValidator();

    @Test
    public void testEmptyString() {
        Sentence str = new Sentence("", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(str.toString(), 0, errors.size());
    }

    @Test
    public void testSingleHiragana() {
        Sentence str = new Sentence("あ", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(str.toString(), 0, errors.size());
    }

    @Test
    public void testSingleKatakana() {
        Sentence str = new Sentence("ア", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(str.toString(), 0, errors.size());
    }

    @Test
    public void testKatakanaOfLength2() {
        Sentence str = new Sentence("ドア", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(str.toString(), 0, errors.size());
    }

    @Test
    public void testKatakanaOfLength3andHyphen() {
        Sentence str = new Sentence("ミラー", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(str.toString(), 0, errors.size());
    }

    @Test
    public void testKatakanaOfLength4andHyphen() {
        Sentence str = new Sentence("コーヒー", 0); // This is an error.
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(str.toString(), 1, errors.size());
    }

    @Test
    public void testSentenceBeginningWithKatakanaWithHypen() {
        Sentence str = new Sentence("コンピューターが壊れた。", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(str.toString(), 1, errors.size());
    }

    @Test
    public void testSentenceBeginningWithKatakanaWithoutHypen() {
        Sentence str = new Sentence("コンピュータが壊れた。", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(str.toString(), 0, errors.size());
    }

    @Test
    public void testSentenceContainKatakanaWithHypen() {
        Sentence str = new Sentence("僕のコンピューターが壊れた。", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(str.toString(), 1, errors.size());
    }

    @Test
    public void testSentenceContainKatakanaWitouthHypen() {
        Sentence str = new Sentence("僕のコンピュータが壊れた。", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(str.toString(), 0, errors.size());
    }

    @Test
    public void testSentenceEndingWithKatakanaWithHypen() {
        Sentence str = new Sentence("僕のコンピューター", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(str.toString(), 1, errors.size());
    }

    @Test
    public void testSentenceEndingWithKatakanaWithoutHypen() {
        Sentence str = new Sentence("僕のコンピュータ", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(str.toString(), 0, errors.size());
    }

    @Test
    public void testSentenceContainWithKatakanaMiddleDot() {
        Sentence str = new Sentence("コーヒー・コンピューター", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(str.toString(), 2, errors.size());
    }

    @Test
    public void testHonorsSkipWordList() throws RedPenException {
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("KatakanaEndHyphen").addProperty("list", "コーヒー"))
                .build();

        List<Document> documents = new ArrayList<>();documents.add(
                Document.builder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("濃いコーヒーは胃にわるい。", 1))
                        .build());

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.get(0)).size());
    }

    @Test
    public void testHonorsSkipWordDict() throws RedPenException {
        Configuration config = Configuration.builder("ja")
                .addValidatorConfig(new ValidatorConfiguration("KatakanaEndHyphen")
                        .addProperty("dict", "src/test/resources/cc/redpen/validator/KatakanaEndHyphenValidatorTest-skipworddict.txt")) // XXX
                .build();

        List<Document> documents = new ArrayList<>();documents.add(
                Document.builder()
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("コーヒーと紅茶と、どちらがお好きですか。", 1))
                        .build());

        System.setProperty("REDPEN_HOME",new File("src/test/resources/").getAbsolutePath());
        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(documents);
        assertEquals(0, errors.get(documents.get(0)).size());
    }
}
