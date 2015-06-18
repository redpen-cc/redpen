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

import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.model.Sentence;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FormatterTest extends Validator {
    @Test
    public void testDistributeWithPlainFormatter() throws RedPenException {
        PlainFormatter formatter = new PlainFormatter();
        Map<Document, List<ValidationError>> docErrorsMap = new HashMap<>();
        String result = formatter.format(docErrorsMap);
        assertEquals("", result);
    }

    @Test
    public void testFlushErrorWithPlainFormatter() throws RedPenException {
        PlainFormatter formatter = new PlainFormatter();
        List<ValidationError> errors = new ArrayList<>();
        setErrorList(errors);
        addValidationError(new Sentence("sentence", 1));
        Map<Document, List<ValidationError>> docErrorsMap = new HashMap<>();
        Document document = new Document.DocumentBuilder().build();
        docErrorsMap.put(document, errors);
        String result = formatter.format(docErrorsMap);
        Pattern p = Pattern.compile("foobar");
        Matcher m = p.matcher(result);
        assertTrue(m.find());
    }
}
