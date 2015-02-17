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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * Format errors as string messages, collated by sentence
 */
public final class PlainBySentenceFormatter extends Formatter {
    private static final Logger LOG = LoggerFactory.getLogger(PlainBySentenceFormatter.class);

    @Override
    public void format(PrintWriter pw, Map<Document, List<ValidationError>> docErrorsMap) throws RedPenException, IOException {

        BufferedWriter writer = new BufferedWriter(new PrintWriter(pw));

        for (Document document : docErrorsMap.keySet()) {
            if (document.getFileName().isPresent()) {
                writer.write("Document: " + document.getFileName().get() + "\n");
            }

            List<ValidationError> errors = docErrorsMap.get(document);
            errors.sort(JSONBySentenceFormatter.BY_SENTENCE_COMPARATOR);
            ValidationError lastError = null;
            for (ValidationError error : errors) {
                if (JSONBySentenceFormatter.BY_SENTENCE_COMPARATOR.compare(lastError, error) != 0) {
                    writer.write("\tLine: " + error.getSentence().getLineNumber() + ", Offset: " + error.getSentence().getStartPositionOffset() + "\n");
                    writer.write("\t\tSentence: " + error.getSentence().getContent() + "\n");
                    lastError = error;
                }
                writer.write("\t\t\t" + formatError(document, error));
            }
        }
        try {
            writer.flush();
        } catch (IOException e) {
            LOG.error("failed to flush", e);
        }
    }

    @Override
    public String formatError(Document document, ValidationError error) {
        StringBuilder str = new StringBuilder();
        str.append(error.getValidatorName()).append(": ");
        str.append(error.getMessage());
        str.append("\n");
        return str.toString();
    }
}
