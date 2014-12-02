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
import cc.redpen.validator.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ResultDistributor flush the errors reported from Validators.
 */
public abstract class Formatter {
    private static final Logger LOG = LoggerFactory.getLogger(Formatter.class);

    public void format(PrintWriter pw, Map<Document, List<ValidationError>> docErrorsMap) throws RedPenException, IOException {
        BufferedWriter writer = new BufferedWriter(new PrintWriter(pw));

        writeHeader(writer);

        for (Document document : docErrorsMap.keySet()) {
            List<ValidationError> errors = docErrorsMap.get(document);
            for (int i = 0; i < errors.size(); i++) {
                ValidationError error = errors.get(i);
                writeError(writer, document, error, i == (errors.size()-1));
            }
        }
        writeFooter(writer);
        try {
            writer.flush();
        } catch (IOException e) {
            LOG.error("failed to flush", e);
        }
    }

    public void format(OutputStream os, Map<Document, List<ValidationError>> docErrorsMap) throws RedPenException, IOException {
        format(new PrintWriter(os), docErrorsMap);
    }

    public String format(Map<Document, List<ValidationError>> docErrorsMap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            format(new PrintWriter(baos), docErrorsMap);
        } catch (RedPenException | IOException e) {
            // writing to ByteArrayOutputStream shouldn't fail with IOException
            throw new RuntimeException(e);
        }
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    public String format(Document document, List<ValidationError> errors) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Map<Document, List<ValidationError>> docErrorsMap = new HashMap<>();
        docErrorsMap.put(document, errors);
        try {
            format(new PrintWriter(baos), docErrorsMap);
        } catch (RedPenException | IOException e) {
            // writing to ByteArrayOutputStream shouldn't fail with IOException
            throw new RuntimeException(e);
        }
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * Output given validation error.
     *
     * @param err validation error
     * @param isLast
     */
    private void writeError(Writer writer, Document document, ValidationError err, boolean isLast) throws RedPenException, IOException {
        writer.write(writeError(document, err, isLast));
    }

    protected void writeHeader(Writer writer) throws IOException {
    }

    protected void writeFooter(Writer writer) throws IOException {
    }

    /**
     * Convert ValidationError into a string to flush a error message.
     *
     * @param document document associated with the validation error
     * @param error    object containing file and line number information.
     * @return error message
     */
    abstract String writeError(Document document, ValidationError error, boolean isLast) throws RedPenException;

}
