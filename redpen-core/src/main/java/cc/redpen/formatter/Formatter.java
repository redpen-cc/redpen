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

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ResultDistributor flush the errors reported from Validators.
 */
public abstract class Formatter {
    private static final Logger LOG = LoggerFactory.getLogger(Formatter.class);

    public void format(PrintWriter pw, Map<Document, List<ValidationError>> docErrorsMap) {
        BufferedWriter writer = new BufferedWriter(new PrintWriter(pw));

        writeHeader(writer);

        docErrorsMap.keySet().forEach(document -> {
            List<ValidationError> errors = docErrorsMap.get(document);
            for (ValidationError error : errors) {
                writeError(writer, document, error);
            }
        });
        writeFooter(writer);
        try {
            writer.flush();
        } catch (IOException e) {
            LOG.error("failed to flush", e);
        }
    }

    public void format(OutputStream os, Map<Document, List<ValidationError>> docErrorsMap) {
        format(new PrintWriter(os), docErrorsMap);
    }

    public String format(Map<Document, List<ValidationError>> docErrorsMap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        format(new PrintWriter(baos), docErrorsMap);
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * Output given validation error.
     *
     * @param err validation error
     */
    private void writeError(Writer writer, Document document, ValidationError err) {
        try {
            writer.write(format(document, err));
            writer.write("\n");
        } catch (IOException | RedPenException e) {
            LOG.error("failed to weite error", e);
        }
    }

    private void writeHeader(Writer writer) {
        if (header().isPresent()) {
            try {
                writer.write(header().get());
                writer.write("\n");
            } catch (IOException e) {
                LOG.error("failed to write header", e);
            }
        }
    }

    private void writeFooter(Writer writer) {
        if (footer().isPresent()) {
            try {
                writer.write(footer().get());
                writer.write("\n");
            } catch (IOException e) {
                LOG.error("failed to write footer", e);
            }
        }
    }

    /**
     * Convert ValidationError into a string to flush a error message.
     *
     * @param document document associated with the validation error
     * @param error    object containing file and line number information.
     * @return error message
     */
    abstract String format(Document document, ValidationError error) throws RedPenException;

    /**
     * Return the header block of semi-structured format. Returns empty by default.
     *
     * @return header block
     */
    protected Optional<String> header() {
        return Optional.empty();
    }

    /**
     * Return the footer block of semi-structured format. Returns empty by default.
     *
     * @return footer block
     */
    protected Optional<String> footer() {
        return Optional.empty();
    }
}
