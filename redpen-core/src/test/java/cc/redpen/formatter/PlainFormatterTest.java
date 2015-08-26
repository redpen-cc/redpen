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
package cc.redpen.formatter;

import cc.redpen.model.Document;
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.WhiteSpaceTokenizer;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PlainFormatterTest extends Validator {
    @Test
    public void testConvertValidationError() {
        List<ValidationError> errors = new ArrayList<>();
        setErrorList(errors);
        addValidationError(new Sentence("This is a sentence", 0));
        Formatter formatter = new PlainFormatter();
        Document document = new cc.redpen.model.Document.DocumentBuilder(new WhiteSpaceTokenizer())
                .setFileName("foobar.md").build();
        List<ValidationError> validationErrors = Arrays.asList(errors.get(0));
        String resultString = formatter.format(document, validationErrors);
        assertEquals("foobar.md:0: ValidationError[PlainFormatterTest], plain test error at line: This is a sentence\n", resultString);
    }

    @Test
    public void testConvertValidationErrorWithoutFileName() {
        List<ValidationError> errors = new ArrayList<>();
        setErrorList(errors);
        addValidationError(new Sentence("This is a sentence", 0));
        Formatter formatter = new PlainFormatter();
        Document document = new cc.redpen.model.Document.DocumentBuilder(new WhiteSpaceTokenizer()).build();
        List<ValidationError> validationErrors = Arrays.asList(errors.get(0));
        String resultString = formatter.format(document, validationErrors);
        assertEquals("0: ValidationError[PlainFormatterTest], plain test error at line: This is a sentence\n", resultString);
    }
}
