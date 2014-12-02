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
package cc.redpen.distributor;

import cc.redpen.RedPenException;
import cc.redpen.formatter.Formatter;
import cc.redpen.formatter.PlainFormatter;
import cc.redpen.formatter.XMLFormatter;
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

/**
 * ResultDistributor flush the errors reported from Validators.
 */
public class ResultDistributor {
    private static final Logger LOG = LoggerFactory.getLogger(ResultDistributor.class);
    public static Formatter XML = new XMLFormatter();
    public static Formatter PLAIN = new PlainFormatter();

    /**
     * Constructor.
     */
    public ResultDistributor() {
    }

    public void distribute(PrintWriter pw, Formatter formatter, Map<Document, List<ValidationError>> docErrorsMap) {
        BufferedWriter writer = new BufferedWriter(new PrintWriter(pw));

        flushHeader(writer, formatter);

        docErrorsMap.keySet().forEach(document -> {
            List<ValidationError> errors = docErrorsMap.get(document);
            for (ValidationError error : errors) {
                flushError(writer, formatter, document, error);
            }
        });
        flushFooter(writer, formatter);

    }

    public void distribute(OutputStream os, Formatter formatter, Map<Document, List<ValidationError>> docErrorsMap) {
        distribute(new PrintWriter(os), formatter, docErrorsMap);
    }

    public String distribute(Formatter formatter, Map<Document, List<ValidationError>> docErrorsMap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        distribute(new PrintWriter(baos), formatter, docErrorsMap);
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * Output given validation error.
     *
     * @param err validation error
     */
    private void flushError(Writer writer, Formatter formatter, Document document, ValidationError err) {
        try {
            writer.write(formatter.format(document, err));
            writer.write("\n");
            writer.flush();
        } catch (IOException | RedPenException e) {
            LOG.error("failed to flush error", e);
        }
    }

    private void flushHeader(Writer writer, Formatter formatter) {
        if (formatter.header().isPresent()) {
            try {
                writer.write(formatter.header().get());
                writer.write("\n");
            } catch (IOException e) {
                LOG.error("failed to flush header", e);
            }
        }
    }

    private void flushFooter(Writer writer, Formatter formatter) {
        if (formatter.footer().isPresent()) {
            try {
                writer.write(formatter.footer().get());
                writer.write("\n");
            } catch (IOException e) {
                LOG.error("failed to flush header", e);
            }
        }
    }
}
