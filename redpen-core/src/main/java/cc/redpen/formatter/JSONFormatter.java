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
import cc.redpen.validator.ValidationError;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;

/**
 * XML Output formatter.
 */
public class JSONFormatter extends Formatter {

    private static final Logger LOG = LoggerFactory.getLogger(JSONFormatter.class);

    @Override
    protected String writeError(cc.redpen.model.Document document, ValidationError error,
                              boolean isLast) throws RedPenException {
        JSONObject jsonError = new JSONObject();
        try {
            jsonError.put("sentence", error.getSentence().content);
            jsonError.put("message", error.getMessage());
        } catch (JSONException e) {
            throw new RedPenException(e);
        }
        return jsonError.toString()+ (isLast ? "": ",");
    }

    @Override
    protected void writeHeader(Writer writer) throws IOException {
        writer.write("{\"document\":\"foobar\",\"errors\":[");
//        writer.write("{\"errors\":[");
    }

    @Override
    protected void writeFooter(Writer writer) throws IOException {
        writer.write("]}");
//        writer.write("]}");
    }
}
