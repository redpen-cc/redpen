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

import cc.redpen.model.Sentence;
import cc.redpen.validator.ValidationError;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class KatakanaEndHyphenValidatorTest {
    @Test
    public void testEmptyString() {
        KatakanaEndHyphenValidator validator
                = new KatakanaEndHyphenValidator();
        Sentence str = new Sentence("", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(str.toString(), 0, errors.size());
    }

    @Test
    public void testSingleHiragana() {
        KatakanaEndHyphenValidator validator
                = new KatakanaEndHyphenValidator();
        Sentence str = new Sentence("あ", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(str.toString(), 0, errors.size());
    }

    @Test
    public void testSingleKatakana() {
        KatakanaEndHyphenValidator validator
                = new KatakanaEndHyphenValidator();
        Sentence str = new Sentence("ア", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(str.toString(), 0, errors.size());
    }

    @Test
    public void testKatakanaOfLength2() {
        KatakanaEndHyphenValidator validator
                = new KatakanaEndHyphenValidator();
        Sentence str = new Sentence("ドア", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(str.toString(), 0, errors.size());
    }

    @Test
    public void testKatakanaOfLength3andHyphen() {
        KatakanaEndHyphenValidator validator
                = new KatakanaEndHyphenValidator();
        Sentence str = new Sentence("ミラー", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(str.toString(), 0, errors.size());
    }

    @Test
    public void testKatakanaOfLength4andHyphen() {
        KatakanaEndHyphenValidator validator
                = new KatakanaEndHyphenValidator();
        Sentence str = new Sentence("コーヒー", 0); // This is an error.
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(str.toString(), 1, errors.size());
    }

    @Test
    public void testSentenceBeginningWithKatakanaWithHypen() {
        KatakanaEndHyphenValidator validator
                = new KatakanaEndHyphenValidator();
        Sentence str = new Sentence("コンピューターが壊れた。", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(str.toString(), 1, errors.size());
    }

    @Test
    public void testSentenceBeginningWithKatakanaWithoutHypen() {
        KatakanaEndHyphenValidator validator
                = new KatakanaEndHyphenValidator();
        Sentence str = new Sentence("コンピュータが壊れた。", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(str.toString(), 0, errors.size());
    }

    @Test
    public void testSentenceContainKatakanaWithHypen() {
        KatakanaEndHyphenValidator validator
                = new KatakanaEndHyphenValidator();
        Sentence str = new Sentence("僕のコンピューターが壊れた。", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(str.toString(), 1, errors.size());
    }

    @Test
    public void testSentenceContainKatakanaWitouthHypen() {
        KatakanaEndHyphenValidator validator
                = new KatakanaEndHyphenValidator();
        Sentence str = new Sentence("僕のコンピュータが壊れた。", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(str.toString(), 0, errors.size());
    }

    @Test
    public void testSentenceEndingWithKatakanaWithHypen() {
        KatakanaEndHyphenValidator validator
                = new KatakanaEndHyphenValidator();
        Sentence str = new Sentence("僕のコンピューター", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(str.toString(), 1, errors.size());
    }

    @Test
    public void testSentenceEndingWithKatakanaWithoutHypen() {
        KatakanaEndHyphenValidator validator
                = new KatakanaEndHyphenValidator();
        Sentence str = new Sentence("僕のコンピュータ", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(str.toString(), 0, errors.size());
    }

    @Test
    public void testSentenceContainWithKatakanaMiddleDot() {
        KatakanaEndHyphenValidator validator
                = new KatakanaEndHyphenValidator();
        Sentence str = new Sentence("コーヒー・コンピューター", 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(str);
        assertEquals(str.toString(), 2, errors.size());
    }
}
