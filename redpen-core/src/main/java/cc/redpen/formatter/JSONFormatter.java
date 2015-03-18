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
import cc.redpen.parser.LineOffset;
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
import java.util.List;
import java.util.Map;

/**
 * JSON error formatter
 */
public class JSONFormatter extends Formatter {
    private static final Logger LOG = LoggerFactory.getLogger(JSONFormatter.class);

    @Override
    public void format(PrintWriter pw, Map<Document, List<ValidationError>> docErrorsMap) throws RedPenException, IOException {
        BufferedWriter writer = new BufferedWriter(new PrintWriter(pw));
        JSONArray errors = new JSONArray();
        docErrorsMap.forEach((doc, errorList) -> errors.put(asJSON(doc, errorList)));
        writer.write(errors.toString());
        writer.flush();
    }

    @Override
    public String format(Document document, List<ValidationError> errors) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new PrintWriter(baos));
        try {
            writer.write(asJSON(document, errors).toString());
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    @Override
    public String formatError(Document document, ValidationError error) throws RedPenException {
        try {
            return asJSON(error).toString();
        } catch (Exception e) {
            throw new RedPenException(e);
        }
    }


    /**
     * Render a single redpen error as JSON
     *
     * @param error the redpen error
     * @return a JSON object representing the redpen error
     * @throws JSONException
     */
    protected JSONObject asJSON(ValidationError error) throws JSONException {
        JSONObject jsonError = new JSONObject();

        jsonError.put("sentence", error.getSentence().getContent());
        jsonError.put("lineNum", error.getLineNumber());
        jsonError.put("message", error.getMessage());
        jsonError.put("validator", error.getValidatorName());
        jsonError.put("sentenceStartColumnNum", error.getStartColumnNumber());
        if (error.getStartPosition().isPresent()) {
            jsonError.put("startPosition", asJSON(error.getStartPosition().get()));
        }
        if (error.getEndPosition().isPresent()) {
            jsonError.put("endPosition", asJSON(error.getEndPosition().get()));
        }

        return jsonError;
    }

    /**
     * Render a line offset as JSON
     *
     * @param lineOffset the line offset
     * @return a JSON object representing the line offset
     */
    protected JSONObject asJSON(LineOffset lineOffset) throws JSONException {
        JSONObject offset = new JSONObject();
        offset.put("lineNum", lineOffset.lineNum);
        offset.put("offset", lineOffset.offset);
        return offset;
    }

    /**
     * Render a start and end line offset as a 'position'
     *
     * @param startLineOffset the line offset denoting the start position
     * @param endLineOffset   the line offset denoting the end position
     * @return a JSON representation of this 'position'
     * @throws JSONException
     */
    protected JSONObject asJSON(LineOffset startLineOffset, LineOffset endLineOffset) throws JSONException {
        JSONObject json = asJSON(startLineOffset.lineNum, startLineOffset.offset, endLineOffset.lineNum, endLineOffset.offset);
        return json;
    }

    /**
     * Render a start and end coordinates as a 'position'
     *
     * @param startLine   the line this position starts
     * @param startOffset the offset within startLine the position starts
     * @param endLine     the line offset denoting the end position
     * @param endOffset   the offset within endLine the position ends
     * @return a JSON representation of this 'position'
     * @throws JSONException
     */
    protected JSONObject asJSON(int startLine, int startOffset, int endLine, int endOffset) throws JSONException {
        JSONObject position = new JSONObject();
        JSONObject offset = new JSONObject();
        offset.put("line", startLine);
        offset.put("offset", startOffset);
        position.put("start", offset);
        offset = new JSONObject();
        offset.put("line", endLine);
        offset.put("offset", endOffset);
        position.put("end", offset);
        return position;
    }

    /**
     * Render a start and end line offsets (ie: source text coordinates) as a description of a section of the sentence.getContent() string
     *
     * @param sentence        the sentence in which the offsets are located
     * @param startLineOffset the line offset denoting the start position
     * @param endLineOffset   the line offset denoting the end position
     * @return a JSON representation of the offsets describing a section within the sentence's getContent() string
     * @throws JSONException
     */
    protected JSONObject asJSON(Sentence sentence, LineOffset startLineOffset, LineOffset endLineOffset) throws JSONException {
        JSONObject json = new JSONObject();
        int start = sentence.getOffsetPosition(startLineOffset);
        int length = Math.max(sentence.getOffsetPosition(endLineOffset) - start, 0);
        json.put("offset", start);
        json.put("length", length);
        return json;
    }

    /**
     * Render as a JSON object a list of errors for a given document
     *
     * @param document the document that has the errors
     * @param errors   a list of errors
     * @return a JSON object representing the errors
     */
    protected JSONObject asJSON(Document document, List<ValidationError> errors) {

        JSONObject jsonErrors = new JSONObject();
        try {
            if (document.getFileName().isPresent()) {
                jsonErrors.put("document", document.getFileName().get());
            }
            JSONArray documentErrors = new JSONArray();
            for (ValidationError error : errors) {
                documentErrors.put(asJSON(error));
            }
            jsonErrors.put("errors", documentErrors);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonErrors;
    }


}
