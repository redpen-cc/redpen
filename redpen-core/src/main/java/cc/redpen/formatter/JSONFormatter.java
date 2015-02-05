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
import cc.redpen.validator.ValidationError;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JSON Output formatter.
 */
public class JSONFormatter extends Formatter {
    private static final Logger LOG = LoggerFactory.getLogger(JSONFormatter.class);

    /**
     * format Validation Errors to JSONArray
     * @param pw writer
     * @param docErrorsMap map from document to list of errors
     * @throws RedPenException when failed to write JSON to string
     * @throws IOException when failed to write JSON to string
     */
    @Override
    public void format(PrintWriter pw, Map<Document, List<ValidationError>> docErrorsMap) throws RedPenException, IOException {
        BufferedWriter writer = new BufferedWriter(new PrintWriter(pw));
        try {
            writer.write(toJSONArray(docErrorsMap).toString());
        } catch (JSONException e) {
            throw new RedPenException(e);
        }
        writer.flush();
    }

    /**
     * format Validation Errors to JSON
     * @param document document
     * @param errors list of errors
     * @return JSON formatted errors
     */
    @Override
    public String format(Document document, List<ValidationError> errors) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new PrintWriter(baos));
        Map<Document, List<ValidationError>> docErrorsMap = new HashMap<>();
        docErrorsMap.put(document, errors);
        try {
            writer.write(toJSONArray(docErrorsMap).get(0).toString());
            writer.flush();
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    private JSONArray toJSONArray(Map<Document, List<ValidationError>> docErrorsMap) throws JSONException {
        JSONArray json = new JSONArray();
        for (cc.redpen.model.Document document : docErrorsMap.keySet()) {
            JSONObject docError = new JSONObject();
            if (document.getFileName().isPresent()) {
                docError.put("document", document.getFileName().get());
            }
            JSONArray jsonErrors = new JSONArray();
            List<ValidationError> errors = docErrorsMap.get(document);
            for (ValidationError error : errors) {
                JSONObject jsonError = new JSONObject();
                jsonError.put("sentence", error.getSentence().getContent());
                jsonError.put("message", error.getMessage());
                jsonError.put("lineNum", error.getLineNumber());
                jsonErrors.put(jsonError);
            }
            docError.put("errors", jsonErrors);
            json.put(docError);
        }

        return json;
    }
}
