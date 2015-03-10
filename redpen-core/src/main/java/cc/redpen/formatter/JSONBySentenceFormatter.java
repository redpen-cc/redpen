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
import cc.redpen.parser.LineOffset;
import cc.redpen.validator.ValidationError;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * JSON error formatter, collating errors by sentence
 */
public class JSONBySentenceFormatter extends JSONFormatter {
    private static final Logger LOG = LoggerFactory.getLogger(JSONBySentenceFormatter.class);

    /**
     * Reusable comparator to compare errors by their position in the document, starting with the line number, followed by the offset within the line, and finally the sentence content
     */
    public static final Comparator<ValidationError> BY_SENTENCE_COMPARATOR = new Comparator<ValidationError>() {
        @Override
        public int compare(ValidationError error1, ValidationError error2) {
            if ((error1 == null) || (error2 == null)) {
                return -1;
            }
            Sentence sentence1 = error1.getSentence();
            Sentence sentence2 = error2.getSentence();
            int lineComp = sentence1.getLineNumber() - sentence2.getLineNumber();
            if (lineComp == 0) {
                int offsetComp = sentence1.getStartPositionOffset() - sentence2.getStartPositionOffset();
                if (offsetComp == 0) {
                    return sentence1.getContent().compareTo(sentence2.getContent());
                }
                return offsetComp;
            }
            return lineComp;
        }
    };

    /**
     * Render a single redpen error as JSON
     *
     * @param error the redpen error
     * @return a JSON object representing the redpen error
     * @throws org.json.JSONException
     */
    @Override
    protected JSONObject asJSON(ValidationError error) throws JSONException {
        JSONObject jsonError = new JSONObject();

        jsonError.put("message", error.getMessage());
        jsonError.put("validator", error.getValidatorName());

        LineOffset startOffset;
        LineOffset endOffset;
        if (error.getStartPosition().isPresent()) {
            startOffset = error.getStartPosition().get();
        } else {
            startOffset = new LineOffset(error.getSentence().getLineNumber(), error.getSentence().getStartPositionOffset());
        }
        if (error.getEndPosition().isPresent()) {
            endOffset = error.getEndPosition().get();
        } else {
            endOffset = new LineOffset(error.getSentence().getLineNumber(), error.getSentence().getStartPositionOffset());
        }
        jsonError.put("position", asJSON(startOffset, endOffset));

        // add the error position relative to the sentence's content
        jsonError.put("subsentence", asJSON(error.getSentence(), startOffset, endOffset));

        return jsonError;
    }

    /**
     * Render as a JSON object a list of errors for a given document
     *
     * @param document the document that has the errors
     * @param errors   a list of errors
     * @return a JSON object representing the errors
     */
    protected JSONObject asJSON(Document document, List<ValidationError> errors) {
        List<ValidationError> sortedErrors = new ArrayList<>(errors);
        sortedErrors.sort(BY_SENTENCE_COMPARATOR);

        JSONObject jsonErrors = new JSONObject();
        try {
            if (document.getFileName().isPresent()) {
                jsonErrors.put("document", document.getFileName().get());
            }
            JSONArray documentErrors = new JSONArray();
            jsonErrors.put("errors", documentErrors);

            JSONArray sentenceErrors = new JSONArray();
            ValidationError lastError = null;
            for (ValidationError error : sortedErrors) {
                if (BY_SENTENCE_COMPARATOR.compare(lastError, error) != 0) {
                    JSONObject sentenceError = new JSONObject();
                    sentenceError.put("sentence", error.getSentence().getContent());
                    Sentence sentence = error.getSentence();
                    //NOTE: last position is optional
                    LineOffset lastPosition = sentence.getOffset(sentence.getContent().length() - 1)
                            .orElse(new LineOffset(sentence.getLineNumber(), sentence.getStartPositionOffset() + sentence.getContent().length()));
                    sentenceError.put("position", asJSON(
                            sentence.getLineNumber(),
                            sentence.getStartPositionOffset(),
                            lastPosition.lineNum,
                            lastPosition.offset)
                    );
                    sentenceErrors = new JSONArray();
                    sentenceError.put("errors", sentenceErrors);

                    documentErrors.put(sentenceError);
                    lastError = error;
                }
                sentenceErrors.put(asJSON(error));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonErrors;
    }
}
