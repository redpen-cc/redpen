/**
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JSONFormatterTest extends Validator {

    @Test
    public void testFormatErrors() throws RedPenException, JSONException {
        JSONFormatter formatter = new JSONFormatter();
        List<ValidationError> errors = new ArrayList<>();
        errors.add(createValidationError(new Sentence("testing JSONFormatter", 1)));
        Document document = new Document.DocumentBuilder().build();
        String result = formatter.format(document, errors);
        JSONObject jsonObject = new JSONObject(result);
        String jsonDoc = jsonObject.getString("document");
        assertNotNull(jsonDoc);
        JSONArray jsonErrors = jsonObject.getJSONArray("errors");
        assertNotNull(jsonErrors);
        assertEquals(1, jsonErrors.length());
        assertEquals("testing JSONFormatter", jsonErrors.getJSONObject(0).getString("sentence"));
        assertEquals("json test error", jsonErrors.getJSONObject(0).getString("message"));
        assertEquals("{\"document\":\"foobar\",\"errors\":[{\"sentence\":\"testing JSONFormatter\",\"message\":\"json test error\"}]}", result);
    }
}
